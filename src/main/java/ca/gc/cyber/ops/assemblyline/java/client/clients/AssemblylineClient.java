package ca.gc.cyber.ops.assemblyline.java.client.clients;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.AssemblylineAuthenticationMethod;
import ca.gc.cyber.ops.assemblyline.java.client.model.DownloadFileParams;
import ca.gc.cyber.ops.assemblyline.java.client.model.FileInfo;
import ca.gc.cyber.ops.assemblyline.java.client.model.FileResultForService;
import ca.gc.cyber.ops.assemblyline.java.client.model.FileResults;
import ca.gc.cyber.ops.assemblyline.java.client.model.HashSearchResult;
import ca.gc.cyber.ops.assemblyline.java.client.model.IngestResponse;
import ca.gc.cyber.ops.assemblyline.java.client.model.LoginResponse;
import ca.gc.cyber.ops.assemblyline.java.client.model.ResultBlock;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.AsyncBinaryFile;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.BinaryFile;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.IngestBase;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.NonBinaryIngest;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.IngestSubmissionResponse;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.Submission;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.SubmissionFileResults;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.SubmissionFull;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.SubmissionTree;
import ca.gc.cyber.ops.assemblyline.java.client.model.submit.NonBinarySubmit;
import ca.gc.cyber.ops.assemblyline.java.client.model.submit.SubmitMetadata;
import ca.gc.cyber.ops.assemblyline.java.client.responses.AssemblylineApiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class AssemblylineClient implements IAssemblylineClient {

    private static final String SESSION_COOKIE = "session";
    private static final String MULTIPART_MSG_JSON_PART = "json";
    private static final String MULTIPART_MSG_BINARY_PART = "bin";

    @Getter
    protected String session = "";
    protected WebClient webClient;
    protected ObjectMapper mapper;
    protected AssemblylineAuthenticationMethod assemblylineAuthenticationMethod;

    protected ExchangeFilterFunction addSession = (request, next) -> {
        ClientRequest updatedRequest = ClientRequest.from(request)
                .cookie(SESSION_COOKIE, session)
                .build();
        return next.exchange(updatedRequest);
    };

    private String authBearerToken;

    public AssemblylineClient(AssemblylineClientProperties assemblylineClientProperties,
                              HttpClient httpClient, ObjectMapper defaultMapper,
                              AssemblylineAuthenticationMethod assemblylineAuthenticationMethod) {
        this.mapper = defaultMapper.copy();
        this.assemblylineAuthenticationMethod = assemblylineAuthenticationMethod;
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.buildWebClient(assemblylineClientProperties, httpClient);
    }

    /**
     * Makes a copy of an AssemblylineClient that uses the same underlying {@link WebClient}. This is intended for use with methods like {@link #withAuthBearerToken} that set session-specific state.
     */
    private AssemblylineClient(AssemblylineClient original) {
        this.mapper = original.mapper;
        this.assemblylineAuthenticationMethod = original.assemblylineAuthenticationMethod;
        this.webClient = original.webClient;
        this.session = original.session;
    }

    /**
     * Creates a copy of the current AssemblylineClient that will use the given authentication bearer token.
     *
     * @param authBearerToken The authentication bearer token for the new client to use.
     * @return the new client.
     */
    @Override
    public AssemblylineClient withAuthBearerToken(String authBearerToken) {
        AssemblylineClient newClient = new AssemblylineClient(this);
        newClient.authBearerToken = authBearerToken;
        return newClient;
    }

    protected void buildWebClient(AssemblylineClientProperties assemblylineClientProperties,
                                  HttpClient httpClient) {
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(clientCodecConfigurer -> {
                    // toIntExact() will throw an error if the property is more than 2GB (Integer.MAX_VALUE bytes)
                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(Math.toIntExact(
                            assemblylineClientProperties.getMaxInMemorySize().toBytes()));

                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
                })
                .filter(addSession)
                .baseUrl(assemblylineClientProperties.getUrl())
                .build();
    }

    @Override
    public Mono<LoginResponse> login() {

        return webClient.post().uri(LOGIN_URL)
                .body(BodyInserters.fromFormData(this.assemblylineAuthenticationMethod.getAuthBody()))
                .exchangeToMono(cr ->
                        clientResponseToMono(cr, new ParameterizedTypeReference<AssemblylineApiResponse<LoginResponse>>() {
                        })
                                .doOnSuccess(lr -> this.setSession(cr)));
    }

    @Override
    public Mono<Boolean> isSubmissionComplete(String sid) {
        return get(buildUri(SUBMISSION_COMPLETE_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<FileInfo> getFileInfo(String sha256) {
        return get(buildUri(FILE_INFO_URL, sha256), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<FileResults> getFileResults(String sha256) {
        return get(buildUri(FILE_RESULTS_URL, sha256), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<FileResultForService> getFileResultForService(String sha256, String serviceName) {
        return get(buildUri(FILE_RESULT_FOR_SERVICE_URL, sha256, serviceName), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<ResultBlock> getResult(String cacheKey) {
        return get(buildUri(RESULT_URL, cacheKey), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<SubmissionFileResults> getSubmissionFileResults(String sid, String sha256) {
        return get(buildUri(SUBMISSION_FILE_RESULTS_URL, sid, sha256), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<SubmissionTree> getSubmissionTree(String sid) {
        return get(buildUri(SUBMISSION_TREE_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<Submission> getSubmission(String sid) {
        return get(buildUri(SUBMISSION_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<SubmissionFull> getSubmissionFull(String sid) {
        return get(buildUri(SUBMISSION_FULL_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Mono<IngestResponse> ingestUrlOrSha256(NonBinaryIngest ingest) {

        return post(buildUri(INGEST_URL), new ParameterizedTypeReference<>() {
                },
                BodyInserters.fromValue(ingest), MediaType.APPLICATION_JSON);

    }

    @Override
    public Mono<IngestResponse> ingestBinary(BinaryFile<IngestBase> binaryIngest) {

        return Mono.fromCallable(() -> this.multipartInserterFromBinaryIngest(binaryIngest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(multipartInserter -> post(buildUri(INGEST_URL), new ParameterizedTypeReference<>() {
                        },
                        multipartInserter, MediaType.MULTIPART_FORM_DATA));

    }

    @Override
    public Mono<IngestResponse> ingestAsyncBinary(AsyncBinaryFile<IngestBase> asyncBinaryIngest) {
        return Mono.fromCallable(() -> this.multipartInserterFromAsyncBinaryIngest(asyncBinaryIngest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(multipartInserter -> post(buildUri(INGEST_URL), new ParameterizedTypeReference<>() {
                        },
                        multipartInserter, MediaType.MULTIPART_FORM_DATA));
    }

    @Override
    public Flux<IngestSubmissionResponse> getIngestMessageList(String notification) {

        return get(buildUri(INGEST_GET_MESSAGE_LIST_URL, notification),
                new ParameterizedTypeReference<AssemblylineApiResponse<List<IngestSubmissionResponse>>>() {
                })
                .flatMapMany(Flux::fromIterable);

    }

    @Override
    public Mono<Submission> submitUrlOrSha256(NonBinarySubmit submit) {

        return post(buildUri(SUBMIT_URL), new ParameterizedTypeReference<>() {
                },
                BodyInserters.fromValue(submit), MediaType.APPLICATION_JSON);

    }

    @Override
    public Mono<Submission> submitBinary(BinaryFile<SubmitMetadata> binaryIngest) {

        return Mono.fromCallable(() -> this.multipartInserterFromBinaryIngest(binaryIngest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(multipartInserter -> post(buildUri(SUBMIT_URL), new ParameterizedTypeReference<>() {
                        },
                        multipartInserter, MediaType.MULTIPART_FORM_DATA));
    }

    @Override
    public Mono<Submission> submitAsyncBinary(AsyncBinaryFile<SubmitMetadata> binaryIngest) {

        return Mono.fromCallable(() -> this.multipartInserterFromAsyncBinaryIngest(binaryIngest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(multipartInserter -> post(buildUri(SUBMIT_URL), new ParameterizedTypeReference<>() {
                        },
                        multipartInserter, MediaType.MULTIPART_FORM_DATA));
    }

    private Flux<DataBuffer> downloadFileAsFlux(String sha256, DownloadFileParams params) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(FILE_DOWNLOAD_URL)
                            .queryParam("encoding", params.getEncoding().name().toLowerCase());

                    if (!Strings.isBlank(params.getName())) {
                        uriBuilder.queryParam("name", params.getName());
                    }

                    if (!Strings.isBlank(params.getSid())) {
                        uriBuilder.queryParam("sid", params.getSid());
                    }

                    return uriBuilder.build(sha256);
                })
                .headers(this::addAuthBearerHeader)
                .exchangeToFlux(cr ->
                        this.checkForException(cr)
                                .flatMapMany(c -> c.body(BodyExtractors.toDataBuffers())))
                .retryWhen(Retry.max(1)
                        .filter(throwable -> throwable instanceof WebClientResponseException.Unauthorized)
                        .doBeforeRetryAsync(retrySignal ->
                                this.login().then()));
    }

    @Override
    public InputStream downloadFile(String sha256) {
        return downloadFile(sha256, DownloadFileParams.builder().build());
    }

    @Override
    public InputStream downloadFile(String sha256, DownloadFileParams params) {
        PipedOutputStream writablePipeEnd = new PipedOutputStream();
        /* We need an effectively-final variable for use in lambdas, so we need a temporary variable for use in a try
        block that can then be assigned to an effectively-final variable. */
        PipedInputStream tmp;
        try {
            tmp = new PipedInputStream(writablePipeEnd);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to set up piped streams.", e);
        }
        PipedInputStream readablePipeEnd = tmp;

        // This lambda is used twice, so it's a bit nicer to store it in a variable instead of defining it in line.
        Consumer<? super Throwable> handleError = throwable -> {
            log.error("Error encountered while reading response body.", throwable);

            // An error occurred, so we need to close the InputStream to "notify"/un-block the thread reading the InputStream.
            try {
                readablePipeEnd.close();
            } catch (IOException ioe) {
                log.error("Failed to close PipedInputStream.", ioe);
            }
        };

        Flux<DataBuffer> bufferFlux = downloadFileAsFlux(sha256, params)
                .doOnError(handleError)
                .doFinally(signalType -> {
                    //Once all the DataBuffers have been read, close the PipedOutputStream to send EOF to the PipedInputStream.
                    try {
                        writablePipeEnd.close();
                    } catch (IOException ioe) {
                        log.error("Failed to close PipedOutputStream.", ioe);
                    }
                });

        DataBufferUtils.write(bufferFlux, writablePipeEnd)
                .doOnError(handleError)
                /* We need this subscribe() for two reasons:
                 *   1. The write() won't actually start until it is subscribed to.
                 *   2. write() does not release the DataBuffers, so we need to do that ourselves.
                 */
                .subscribe(DataBufferUtils.releaseConsumer());

        return readablePipeEnd;
    }

    @Override
    public Mono<List<String>> getHashSearchDataSources() {
        return get(buildUri(HASH_SEARCH_LIST_DATA_SOURCES), new ParameterizedTypeReference<>() {
        });
    }

    /**
     * Search for a hash in multiple data sources.
     *
     * @param fileHash    Hash to search in the multiple data sources (MD5, SHA1 or SHA256)
     * @param dataSources list of data sources. Set to null to use server default.
     * @param maxTimeout  Maximum execution time for the call in seconds. Set to null to use server default.
     * @return Search results
     * @see AssemblylineClient#hashSearch(String)
     */
    @Override
    public Mono<Map<String, HashSearchResult>> hashSearch(String fileHash, List<String> dataSources,
                                                          Integer maxTimeout) {
        // We're using a LinkedHashMap to preserve ordering, which makes testing easier.
        Map<String, String> params = new LinkedHashMap<>();
        if (dataSources != null) {
            params.put("db", String.join("|", dataSources));
        }
        if (maxTimeout != null) {
            params.put("max_timeout", maxTimeout.toString());
        }
        return get(buildUriWithParams(HASH_SEARCH, params, fileHash), new ParameterizedTypeReference<>() {
        });
    }

    /**
     * Search for a hash in multiple data sources, using default values for optional parameters.
     *
     * @param fileHash Hash to search in the multiple data sources (MD5, SHA1 or SHA256)
     * @return Search results
     * @see AssemblylineClient#hashSearch(String, List, Integer)
     */
    @Override
    public Mono<Map<String, HashSearchResult>> hashSearch(String fileHash) {
        return hashSearch(fileHash, null, null);
    }

    protected void setSession(ClientResponse clientResponse) {
        Optional.ofNullable(clientResponse.cookies().get(SESSION_COOKIE))
                .flatMap(sessionCookie -> sessionCookie.stream().findFirst()
                        .map(HttpCookie::getValue))
                .ifPresent(s -> session = s);
    }

    protected <T> Mono<T> retryWrapper(Mono<T> monoContent) {
        return monoContent
                .retryWhen(Retry.max(1)
                        .filter(throwable -> throwable instanceof WebClientResponseException.Unauthorized)
                        .doBeforeRetryAsync(retrySignal ->
                                this.login().then()));
    }

    private Mono<ClientResponse> checkForException(ClientResponse rc) {
        if (rc.statusCode().isError()){
            return rc.createException()
                    .flatMap(e ->
                        Mono.fromCallable(() -> this.extractApiErrorMessage(e))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(errorMsg ->
                                        WebClientResponseException.create(
                                        e.getStatusCode().value(),
                                        e.getStatusText() + " : " +  errorMsg,
                                        e.getHeaders(),
                                        e.getResponseBodyAsByteArray(),
                                        //No getter for contentType
                                        rc.headers().contentType()
                                                .map(MimeType::getCharset)
                                                .orElse(StandardCharsets.ISO_8859_1),
                                        e.getRequest()))
                    .flatMap(Mono::error)
                    );
        }
        this.setSession(rc);
        return Mono.just(rc);
    }

    private String extractApiErrorMessage(WebClientResponseException exception) {
        var responseExceptionMsg = exception.getResponseBodyAsString();
        try {
        /* We're just reading the error message out of the response, so we don't really care about the type parameter.
        Sometimes it's an empty string, other times it's an empty object/map. */
            AssemblylineApiResponse<Object> response = mapper.readValue(responseExceptionMsg,
                    new TypeReference<>() {});
            return response.getApiErrorMessage();
        } catch (JsonProcessingException e) {
            return responseExceptionMsg;
        }
    }

    /**
     * Helper method to perform a JSON GET request. This helper covers the most common GET requests. For more
     * specialized requests, use webClient directly.
     *
     * @param <T>          The type of data that will be in the response.
     * @param uriBuilder   UriBuilder that will generate the URI to GET.
     * @param responseType A parameterized type reference representing the type of data that will be in the response.
     *                     The declaration of this object must only include concrete type parameters. For example,
     *                     {@code new ParameterizedTypeReference<ALApiResponse<Boolean>>} is OK, but
     *                     {@code new ParameterizedTypeReference<ALApiResponse<T>>} (where {@code T} is a type
     *                     parameter on a method) is not OK.
     * @return The content of the "api_response" section of the response.
     */
    protected <T> Mono<T> get(Function<UriBuilder, URI> uriBuilder,
                              ParameterizedTypeReference<AssemblylineApiResponse<T>> responseType) {
        return this.retryWrapper(webClient.get()
                .uri(uriBuilder)
                .headers(this::addAuthBearerHeader)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(cr -> this.clientResponseToMono(cr, responseType)));
    }

    /**
     * Helper method to perform a JSON POST request. This helper covers the most common POST requests. For more
     * specialized requests, use webClient directly.
     *
     * @param <T>          The type of data that will be in the response.
     * @param uriBuilder   UriBuilder that will generate the URI to POST.
     * @param responseType A parameterized type reference representing the type of data that will be in the response.
     *                     The declaration of this object must only include concrete type parameters. For example,
     *                     {@code new ParameterizedTypeReference<ALApiResponse<Boolean>>} is OK, but
     *                     {@code new ParameterizedTypeReference<ALApiResponse<T>>} (where {@code T} is a type
     *                     parameter on a method) is not OK.
     * @param bodyInserter BodyInserter to construct the body of the Post Request
     * @param contentType  MediaType of the request body
     * @return The content of the "api_response" section of the response.
     */
    protected <T> Mono<T> post(Function<UriBuilder, URI> uriBuilder,
                               ParameterizedTypeReference<AssemblylineApiResponse<T>> responseType,
                               BodyInserter<?, ? super ClientHttpRequest> bodyInserter,
                               MediaType contentType) {
        return this.retryWrapper(webClient.post()
                .uri(uriBuilder)
                .contentType(contentType)
                .headers(this::addAuthBearerHeader)
                .body(bodyInserter)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(cr -> this.clientResponseToMono(cr, responseType)));
    }

    /**
     * Helper to build URIs.
     * <p>
     * Given the return type, the name of this method may be a little surprising, but it should help with readability at call sites.
     *
     * @param pathTemplate Template for the path in the URI.
     * @param args         Values to substitute into the template
     * @return A UriBuilder
     */
    private Function<UriBuilder, URI> buildUri(String pathTemplate, Object... args) {
        return buildUriWithParams(pathTemplate, null, args);
    }

    /**
     * Helper to build URIs.
     * <p>
     * Given the return type, the name of this method may be a little surprising, but it should help with readability at call sites.
     *
     * @param pathTemplate Template for the path in the URI
     * @param params       Query parameters to add to the URL
     * @param templateArgs Values to substitute into the template
     * @return A UriBuilder
     */
    private Function<UriBuilder, URI> buildUriWithParams(String pathTemplate, Map<String, String> params, Object... templateArgs) {
        /* If the path doesn't end with a "/", Assemblyline returns a redirect. If we try to follow the redirect,
        authentication (and by extension the entire request) fails. */
        if (!pathTemplate.endsWith("/")) {
            throw new IllegalArgumentException("Request URI path template does not end with a slash. URI = " + pathTemplate);
        }
        return uriBuilder -> {
            uriBuilder.path(pathTemplate);

            if (params != null) {
                params.forEach((key, value) -> {
                    if (key != null && value != null) {
                        uriBuilder.queryParam(key, value);
                    }
                });
            }

            return uriBuilder.build(templateArgs);
        };
    }

    private BodyInserters.MultipartInserter multipartInserterFromBinaryIngest(BinaryFile<?> binaryFile) throws JsonProcessingException {
        ByteArrayResource bar = new ByteArrayResource(binaryFile.getFile());
        MultipartBodyBuilder mbb = new MultipartBodyBuilder();
        mbb.part(MULTIPART_MSG_BINARY_PART, bar).filename(binaryFile.getFilename());
        mbb.part(MULTIPART_MSG_JSON_PART, mapper.writeValueAsString(binaryFile.getMetadata()));
        return BodyInserters.fromMultipartData(mbb.build());
    }

    private BodyInserters.MultipartInserter multipartInserterFromAsyncBinaryIngest(AsyncBinaryFile<?> binaryFile) throws JsonProcessingException {
        MultipartBodyBuilder mbb = new MultipartBodyBuilder();
        mbb.asyncPart(MULTIPART_MSG_BINARY_PART, binaryFile.getFile(), ByteBuffer.class).filename(binaryFile.getFilename()).contentType(MediaType.APPLICATION_OCTET_STREAM);
        mbb.part(MULTIPART_MSG_JSON_PART, mapper.writeValueAsString(binaryFile.getMetadata()));
        return BodyInserters.fromMultipartData(mbb.build());
    }

    private void addAuthBearerHeader(HttpHeaders httpHeaders) {
        if (authBearerToken != null) {
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + authBearerToken);
        }

    }

    /**
     * Convert ClientResponse to AssemblylineApiResponse content mono.
     * <p>
     *
     * @param clientResponse ClientResponse returned by call
     * @param type           A parameterized type reference representing the type of data that will be in the response.
     *                       The declaration of this object must only include concrete type parameters. For example,
     *                       {@code new ParameterizedTypeReference<ALApiResponse<Boolean>>} is OK, but
     *                       {@code new ParameterizedTypeReference<ALApiResponse<T>>} (where {@code T} is a type
     *                       parameter on a method) is not OK.
     * @param <T>            The type of data that will be in the response.
     * @return Mono of clientResponse body
     */
    protected <T> Mono<T> clientResponseToMono(ClientResponse clientResponse,
                                               ParameterizedTypeReference<AssemblylineApiResponse<T>> type) {
        return this.checkForException(clientResponse)
                .flatMap(c -> c.bodyToMono(type)
                        .map(AssemblylineApiResponse::getApiResponse));
    }

}
