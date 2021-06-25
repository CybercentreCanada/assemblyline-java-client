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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
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
import org.springframework.util.Assert;
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
import reactor.netty.transport.ProxyProvider;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.util.Assert.*;

@Slf4j
public class AssemblylineClient {

    public static final String BASE_URL = "api/v4/";
    public static final String LOGIN_URL = BASE_URL + "auth/login/";

    public static final String FILE_DOWNLOAD_URL = BASE_URL + "file/download/{sha256}/";
    public static final String FILE_INFO_URL = BASE_URL + "file/info/{sha256}/";
    public static final String FILE_RESULTS_URL = BASE_URL + "file/result/{sha256}/";
    public static final String FILE_RESULT_FOR_SERVICE_URL = FILE_RESULTS_URL + "{serviceName}/";

    public static final String HASH_SEARCH = BASE_URL + "hash_search/{hash}/";
    public static final String HASH_SEARCH_LIST_DATA_SOURCES = BASE_URL + "hash_search/list_data_sources/";

    public static final String INGEST_URL = BASE_URL + "ingest/";
    public static final String INGEST_GET_MESSAGE_LIST_URL = BASE_URL + "ingest/get_message_list/{notificationQueue}/";

    public static final String RESULT_URL = BASE_URL + "result/{path:cache_key}/";

    public static final String SUBMISSION_COMPLETE_URL = BASE_URL + "submission/is_completed/{sid}/";
    public static final String SUBMISSION_URL = BASE_URL + "submission/{sid}/";
    public static final String SUBMISSION_FILE_RESULTS_URL = SUBMISSION_URL + "file/{sha256}/";
    public static final String SUBMISSION_FULL_URL = BASE_URL + "submission/full/{sid}/";
    public static final String SUBMISSION_TREE_URL = BASE_URL + "submission/tree/{sid}/";

    public static final String SUBMIT_URL = BASE_URL + "submit/";

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
                              ProxyProperties proxyProperties, ObjectMapper defaultMapper,
                              AssemblylineAuthenticationMethod assemblylineAuthenticationMethod) {
        this.mapper = defaultMapper.copy();
        this.assemblylineAuthenticationMethod = assemblylineAuthenticationMethod;
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.buildWebClient(assemblylineClientProperties, proxyProperties);
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
    public AssemblylineClient withAuthBearerToken(String authBearerToken) {
        AssemblylineClient newClient = new AssemblylineClient(this);
        newClient.authBearerToken = authBearerToken;
        return newClient;
    }

    protected void buildWebClient(AssemblylineClientProperties assemblylineClientProperties,
                                  ProxyProperties proxyProperties) {

        HttpClient httpClient = createHttpClient(proxyProperties);

        webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
                }).filter(addSession).baseUrl(assemblylineClientProperties.getUrl()).build();
    }

    /**
     * Return an HttpClient that uses the proxy setup via the properties, if properties host and port are both set. If
     * no proxy properties are set, a 'secure' Httpclient is returned without proxy configured.
     *
     * @return HttpClient
     * @throws IllegalArgumentException If proxy port is not valid
     */
    private static HttpClient createHttpClient(ProxyProperties proxyProperties) {
        String proxyHost = proxyProperties.getHost();
        Integer proxyPort = proxyProperties.getPort();

        if (proxyHost == null) {
            log.debug("No proxy host set. Assembly line client not configured to go through a proxy.");
            return HttpClient.create().secure();
        }

        hasLength(proxyHost, "Proxy host, when set, must not be empty.");
        notNull(proxyPort, "Proxy port must be set if proxy host is set.");

        log.debug("AssemblylineClient web client is configured to use the proxy %s on port %s.", proxyHost, proxyPort);
        return HttpClient.create().secure()
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(proxyPort));
    }

    public Mono<LoginResponse> login() {

        return webClient.post().uri(LOGIN_URL)
                .body(BodyInserters.fromFormData(this.assemblylineAuthenticationMethod.getAuthBody()))
                .exchangeToMono(cr ->
                        clientResponseToMono(cr, new ParameterizedTypeReference<AssemblylineApiResponse<LoginResponse>>() {
                        })
                                .doOnSuccess(lr -> this.setSession(cr)));
    }

    public Mono<Boolean> isSubmissionComplete(String sid) {
        return get(buildUri(SUBMISSION_COMPLETE_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<FileInfo> getFileInfo(String sha256) {
        return get(buildUri(FILE_INFO_URL, sha256), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<FileResults> getFileResults(String sha256) {
        return get(buildUri(FILE_RESULTS_URL, sha256), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<FileResultForService> getFileResultForService(String sha256, String serviceName) {
        return get(buildUri(FILE_RESULT_FOR_SERVICE_URL, sha256, serviceName), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<ResultBlock> getResult(String cacheKey) {
        return get(buildUri(RESULT_URL, cacheKey), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<SubmissionFileResults> getSubmissionFileResults(String sid, String sha256) {
        return get(buildUri(SUBMISSION_FILE_RESULTS_URL, sid, sha256), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<SubmissionTree> getSubmissionTree(String sid) {
        return get(buildUri(SUBMISSION_TREE_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<Submission> getSubmission(String sid) {
        return get(buildUri(SUBMISSION_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<SubmissionFull> getSubmissionFull(String sid) {
        return get(buildUri(SUBMISSION_FULL_URL, sid), new ParameterizedTypeReference<>() {
        });
    }

    public Mono<IngestResponse> ingestUrlOrSha256(NonBinaryIngest ingest) {

        return post(buildUri(INGEST_URL), new ParameterizedTypeReference<>() {
                },
                BodyInserters.fromValue(ingest), MediaType.APPLICATION_JSON);

    }

    public Mono<IngestResponse> ingestBinary(BinaryFile<IngestBase> binaryIngest) {

        return Mono.fromCallable(() -> this.multipartInserterFromBinaryIngest(binaryIngest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(multipartInserter -> post(buildUri(INGEST_URL), new ParameterizedTypeReference<>() {
                        },
                        multipartInserter, MediaType.MULTIPART_FORM_DATA));

    }

    public Flux<IngestSubmissionResponse> getIngestMessageList(String notification) {

        return get(buildUri(INGEST_GET_MESSAGE_LIST_URL, notification),
                new ParameterizedTypeReference<AssemblylineApiResponse<List<IngestSubmissionResponse>>>() {
                })
                .flatMapMany(Flux::fromIterable);

    }

    public Mono<Submission> submitUrlOrSha256(NonBinarySubmit submit) {

        return post(buildUri(SUBMIT_URL), new ParameterizedTypeReference<>() {
                },
                BodyInserters.fromValue(submit), MediaType.APPLICATION_JSON);

    }

    public Mono<Submission> submitBinary(BinaryFile<SubmitMetadata> binaryIngest) {

        return Mono.fromCallable(() -> this.multipartInserterFromBinaryIngest(binaryIngest))
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

    public InputStream downloadFile(String sha256) {
        return downloadFile(sha256, DownloadFileParams.builder().build());
    }

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
        if (rc.statusCode().is4xxClientError()) {
            return rc.createException().flatMap(Mono::error);
        }
        if (rc.statusCode().is5xxServerError()){
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

    private String extractApiErrorMessage(WebClientResponseException exception) throws JsonProcessingException {
        AssemblylineApiResponse<String> response = mapper.readValue(exception.getResponseBodyAsString(),
                new TypeReference<>() {});
        return response.getApiErrorMessage();
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
     * @return Mono<T>
     */
    protected <T> Mono<T> clientResponseToMono(ClientResponse clientResponse,
                                               ParameterizedTypeReference<AssemblylineApiResponse<T>> type) {
        return this.checkForException(clientResponse)
                .flatMap(c -> c.bodyToMono(type)
                        .map(AssemblylineApiResponse::getApiResponse));
    }

}
