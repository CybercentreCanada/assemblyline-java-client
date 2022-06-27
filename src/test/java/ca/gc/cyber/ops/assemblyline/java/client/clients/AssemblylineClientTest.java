package ca.gc.cyber.ops.assemblyline.java.client.clients;

import ca.gc.cyber.ops.assemblyline.java.client.AssemblylineClientConfig;
import ca.gc.cyber.ops.assemblyline.java.client.model.DownloadFileParams;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringJUnitConfig(AssemblylineClientConfig.class)
@AutoConfigureJson
class AssemblylineClientTest {

    @Autowired
    ObjectMapper defaultMapper;

    @Autowired
    HttpClient httpClient;

    private MockWebServer mockBackEnd;
    private AssemblylineClientProperties assemblylineClientProperties = new AssemblylineClientProperties();
    private AssemblylineClient assemblylineClient;
    private String session = "testSession";

    static {
        /*
         * Blockhound throws exception when NativePRNG.engineNextBytes is called; but it is a false alarm
         * as NativePRNG is created in NONBlocking mode (i.e. it is using /dev/urandom)
         */
        BlockHound.builder()
                .allowBlockingCallsInside("org.springframework.util.MimeTypeUtils",
                        "generateMultipartBoundary").install();

    }

    @BeforeEach
    void initialize() throws IOException {
        // Create a new server for every test to make sure that state doesn't leak between different tests.
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        this.assemblylineClientProperties.setUrl(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        assemblylineClient = new AssemblylineClient(assemblylineClientProperties, httpClient, defaultMapper,
                new AssemblylineAuthenticationTestImpl());

    }

    @AfterEach
    void cleanUp() throws IOException {
        mockBackEnd.shutdown();
    }

    /**
     * Setup mock web server to return the given body. The Content-Type header will be "application/json".
     *
     * @param body The body of the mock response.
     */
    private void mockResponse(String body) {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .addHeader("Set-Cookie", "session=" + session));
    }

    /**
     * Helper method to verify the behaviour of  methods in AssemblylineClient that make HTTP GET requests. It verifies the returned value and the HTTP path that were used.
     *
     * @param actualResult    Output from a method in AssemblylineClient
     * @param expectedPath    Expected path for HTTP request
     * @param expectedResults Expected results
     * @param <T>             The type of data returned by the AssemblylineClient method
     */
    @SafeVarargs
    public final <T> void verifyHttpGet(Publisher<T> actualResult, String expectedPath, T... expectedResults) {
        StepVerifier.Step<T> step = StepVerifier.create(actualResult);
        for (T er : expectedResults) {
            step = step.expectNext(er);
        }
        step
                .expectComplete()
                .verify();

        verifyExpectedPath(expectedPath);

    }


    /**
     * Helper method to verify the HTTP path that was used.
     *
     * @param expectedPath Expected path for HTTP request
     */
    private void verifyExpectedPath(String expectedPath) {

        try {
            RecordedRequest actualRequest = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
            assertEquals(expectedPath, actualRequest.getPath());
        } catch (InterruptedException ie) {
            Assertions.fail(ie);
        }

    }

    private <T> void verifyHttpPostResponse(Mono<T> actualResult, T expectedResult) {
        StepVerifier.create(actualResult)
                .expectNext(expectedResult)
                .expectComplete()
                .verify();
    }

    /**
     * Helper method to verify the behaviour of methods in AssemblylineClient that make HTTP POST requests with a JSON body. It verifies the returned value, the HTTP request path, and the HTTP request body that were used.
     *
     * @param actualResult        Output from a method in AssemblylineClient
     * @param expectedResult      Expected result
     * @param expectedPath        Expected path for HTTP request
     * @param expectedRequestBody Expected body for HTTP request
     * @param <T>                 The type of data returned by the AssemblylineClient method
     */
    private <T> void verifyHttpPostJson(Mono<T> actualResult, T expectedResult, String expectedPath, String expectedRequestBody) {
        this.verifyHttpPostResponse(actualResult, expectedResult);

        try {
            RecordedRequest actualRequest = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
            assertEquals(expectedPath, actualRequest.getPath());
            /* JSONAssert will check the structure of the JSON, which is nicer than comparing strings with irrelevant
            whitespace or ordering differences. */
            JSONAssert.assertEquals(expectedRequestBody, actualRequest.getBody().readUtf8(), false);
        } catch (InterruptedException | JSONException ie) {
            Assertions.fail(ie);
        }
    }

    private <T> void verifyHttpPostJsonMultiformData(Mono<T> actualResult, T expectedResult, String expectedPath,
                                                        String expectedJsonRequest, byte[] binData) {

        this.verifyHttpPostResponse(actualResult, expectedResult);
        try {

            RecordedRequest actualRequest = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
            assertEquals(expectedPath, actualRequest.getPath());

            MockHttpServletRequest requestContext = new MockHttpServletRequest("POST", actualRequest.getRequestUrl().uri().toString());
            requestContext.setCharacterEncoding(actualRequest.getHeader("Content-Type"));
            requestContext.setContent(actualRequest.getBody().readByteArray());
            actualRequest.getHeaders().toMultimap().forEach((e, v) -> requestContext.addHeader(e, v.get(0)));
            final FileItemFactory factory = new DiskFileItemFactory();
            final ServletFileUpload upload = new ServletFileUpload(factory);
            final List<FileItem> items = upload.parseRequest(requestContext);
            FileItem binary = items.get(0);
            FileItem json = items.get(1);

            assertEquals("bin", binary.getFieldName());
            assertEquals("application/octet-stream", binary.getContentType());
            Assertions.assertArrayEquals(binData, binary.get());

            assertEquals("json", json.getFieldName());
            assertEquals("text/plain;charset=UTF-8", json.getContentType());
            JSONAssert.assertEquals(expectedJsonRequest, json.getString(), false);


        } catch (InterruptedException | FileUploadException | JSONException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testLogin() {

        mockResponse(MockResponseModels.getLoginResponseJson());

        Assertions.assertTrue(assemblylineClient.getSession().isEmpty());

        StepVerifier.create(this.assemblylineClient.login())
                .expectNext(MockResponseModels.getLoginResponse())
                .expectComplete()
                .verify();

        assertEquals(session, assemblylineClient.getSession());
    }


    @Test
    void testLoginFailed() {

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(MockResponseModels.notLoggedInJson()));

        Assertions.assertTrue(assemblylineClient.getSession().isEmpty());

        StepVerifier.create(this.assemblylineClient.login())
                .expectError(WebClientResponseException.Unauthorized.class)
                .verify();

        Assertions.assertTrue(assemblylineClient.getSession().isEmpty());
    }

    @Test
    void testAuthBearerToken() throws InterruptedException {

        String session = "testerSession";
        String authToken = "authToken";
        // Make a copy of the client, with the auth token set.
        AssemblylineClient clientWithToken = assemblylineClient.withAuthBearerToken(authToken);

        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getIsSubmissionCompleteResponseJson())
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "session=" + session));

        StepVerifier.create(this.assemblylineClient.isSubmissionComplete("test"))
                .expectNext(true)
                .expectComplete()
                .verify();

        RecordedRequest actualRequestNoToken = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
        Assertions.assertNull(actualRequestNoToken.getHeader(HttpHeaders.AUTHORIZATION));

        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getIsSubmissionCompleteResponseJson())
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "session=" + session));

        StepVerifier.create(clientWithToken.isSubmissionComplete("test"))
                .expectNext(true)
                .expectComplete()
                .verify();

        RecordedRequest actualRequestToken = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("Bearer " + authToken, actualRequestToken.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void testSessionUpdate() {

        String session = "testerSession";
        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getIsSubmissionCompleteResponseJson())
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "session=" + session));

        StepVerifier.create(this.assemblylineClient.isSubmissionComplete("test"))
                .expectNext(true)
                .expectComplete()
                .verify();

        assertEquals(session, assemblylineClient.getSession());
    }


    @Test
    void testIsSubmissionComplete() {

        String session = "testerSession";
        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getIsSubmissionCompleteResponseJson())
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "session=" + session));

        verifyHttpGet(this.assemblylineClient.isSubmissionComplete("test"),
                "/api/v4/submission/is_completed/test/",
                MockResponseModels.getIsSubmissionCompleteResponse());
    }

    @Test
    void testIsSubmissionCompleteRetryWithLogin() {

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(MockResponseModels.notLoggedInJson()));

        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getLoginResponseJson())
                .addHeader("Content-Type", "application/json"));

        mockBackEnd.enqueue(new MockResponse().setBody(MockResponseModels.getIsSubmissionCompleteResponseJson())
                .addHeader("Content-Type", "application/json"));

        verifyHttpGet(this.assemblylineClient.isSubmissionComplete("test"),
                "/api/v4/submission/is_completed/test/",
                MockResponseModels.getIsSubmissionCompleteResponse());
    }

    @Test
    void testIsSubmissionCompleteRetryWithLoginFailed() {
        // The first 401 is caused by a lack of session token. This will cause the client to attempt a login.
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(MockResponseModels.notLoggedInJson()));

        // The first 401 then triggers a login attempt, which we mock as failing. We expect this failure to be propagated back to the user.
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(MockResponseModels.invalidApiKeyJson()));

        StepVerifier.create(this.assemblylineClient.isSubmissionComplete("test"))
                .expectErrorMatches(e -> e instanceof WebClientResponseException.Unauthorized
                    && e.getMessage().contains("Invalid apikey"))
                .verify();
    }

    @Test
    void testIsSubmissionCompleteServerError() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setBody(MockResponseModels.getInternalErrorJson()).setResponseCode(500)
                        .addHeader("Content-Type", "application/json"));
        StepVerifier.create(this.assemblylineClient.isSubmissionComplete("test"))
                .expectErrorMatches(e -> e instanceof WebClientResponseException.InternalServerError &&
                        e.getMessage().contains("Message from Assemblyline"))
                .verify();
    }

    @Test
    void testIsSubmissionJsonError() {
        String someException = "someException";
        mockBackEnd.enqueue(
                new MockResponse()
                        .setBody(someException).setResponseCode(500));
        StepVerifier.create(this.assemblylineClient.isSubmissionComplete("test"))
                .expectErrorMatches(e -> e instanceof WebClientResponseException.InternalServerError &&
                        e.getMessage().contains(someException))
                .verify();
    }

    @Test
    void testSubmitBadRequest() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setBody(MockResponseModels.getBadRequestJson()).setResponseCode(400)
                        .addHeader("Content-Type", "application/json"));
        // We don't really care about the request content here; we just want *something* to trigger the mocked response.
        StepVerifier.create(this.assemblylineClient.submitBinary(RequestModels.getBinarySubmitObject()))
                .expectErrorMatches(e -> e instanceof WebClientResponseException.BadRequest &&
                        e.getMessage().contains("You cannot start a scan with higher classification then you're allowed to see"))
                .verify();
    }

    @Test
    void testGetFileInfo() {
        mockResponse(MockResponseModels.getFileInfoJson());

        verifyHttpGet(this.assemblylineClient.getFileInfo("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7"),
                "/api/v4/file/info/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/",
                MockResponseModels.getFileInfo());
    }

    @Test
    void testGetFileResults() {
        mockResponse(MockResponseModels.getFileResultsJson());

        verifyHttpGet(this.assemblylineClient.getFileResults("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7"),
                "/api/v4/file/result/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/",
                MockResponseModels.getFileResults());
    }

    @Test
    void testGetFileResultsForService() {
        mockResponse(MockResponseModels.getFileResultForServiceJson());

        verifyHttpGet(this.assemblylineClient.getFileResultForService("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7", "Characterize"),
                "/api/v4/file/result/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/Characterize/",
                MockResponseModels.getFileResultForService());
    }

    @Test
    void testGetResult() {
        mockResponse(MockResponseModels.getResultBlockJson());

        verifyHttpGet(this.assemblylineClient.getResult("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable5.cDyOMFE1phHM"),
                "/api/v4/result/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable5.cDyOMFE1phHM/",
                MockResponseModels.getResultBlock());
    }

    @Test
    void testGetSubmissionFileResults() {
        mockResponse(MockResponseModels.getSubmissionFileResultsJson());

        verifyHttpGet(this.assemblylineClient.getSubmissionFileResults("3p9RPMzkoYJ1p4vfdZj6B0", "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7"),
                "/api/v4/submission/3p9RPMzkoYJ1p4vfdZj6B0/file/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/",
                MockResponseModels.getSubmissionFileResults());
    }

    @Test
    void testIngestSha256File() {
        mockResponse(MockResponseModels.getIngestResponseJson());

        verifyHttpPostJson(this.assemblylineClient.ingestUrlOrSha256(RequestModels.getSha256IngestObject()),
                MockResponseModels.getIngestResponse(),
                "/api/v4/ingest/",
                RequestModels.getSha256IngestJson());
    }

    @Test
    void testIngestUrlFile() {

        mockResponse(MockResponseModels.getIngestResponseJson());

        verifyHttpPostJson(this.assemblylineClient.ingestUrlOrSha256(RequestModels.getUrlIngestObject()),
                MockResponseModels.getIngestResponse(),
                "/api/v4/ingest/",
                RequestModels.getUrlIngestJson());
    }

    @Test
    void testIngestBinaryFile() {

        mockResponse(MockResponseModels.getIngestResponseJson());

        this.verifyHttpPostJsonMultiformData(this.assemblylineClient.ingestBinary(RequestModels.getBinaryIngestObject()),
                MockResponseModels.getIngestResponse(), "/api/v4/ingest/",
                RequestModels.getBinaryIngestBaseJson(),
                RequestModels.getBinaryData());
    }

    @Test
    void testGetSubmissionTree() {
        mockResponse(MockResponseModels.getSubmissionTreeJson());

        verifyHttpGet(this.assemblylineClient.getSubmissionTree("3p9RPMzkoYJ1p4vfdZj6B0"),
                "/api/v4/submission/tree/3p9RPMzkoYJ1p4vfdZj6B0/",
                MockResponseModels.getSubmissionTree());
    }

    @Test
    void testGetSubmission() {
        mockResponse(MockResponseModels.getSubmissionJson());

        verifyHttpGet(this.assemblylineClient.getSubmission("3p9RPMzkoYJ1p4vfdZj6B0"),
                "/api/v4/submission/3p9RPMzkoYJ1p4vfdZj6B0/",
                MockResponseModels.getSubmission());
    }

    @Test
    void testGetSubmissionFull() {
        mockResponse(MockResponseModels.getSubmissionFullJson());

        verifyHttpGet(this.assemblylineClient.getSubmissionFull("3p9RPMzkoYJ1p4vfdZj6B0"),
                "/api/v4/submission/full/3p9RPMzkoYJ1p4vfdZj6B0/",
                MockResponseModels.getSubmissionFull());
    }

    @Test
    void testLargeResponse_largeClientBuffer() {
        // Increase the maximum buffer size to allow client to handle large responses.
        assemblylineClientProperties.setMaxInMemorySize(DataSize.ofMegabytes(2));
        assemblylineClient = new AssemblylineClient(assemblylineClientProperties, httpClient, defaultMapper,
                new AssemblylineAuthenticationTestImpl());

        // The large response is over 1MB.
        mockResponse(MockResponseModels.getSubmissionFullLargeJson());

        verifyHttpGet(this.assemblylineClient.getSubmissionFull("3p9RPMzkoYJ1p4vfdZj6B0"),
                "/api/v4/submission/full/3p9RPMzkoYJ1p4vfdZj6B0/",
                MockResponseModels.getSubmissionFullLarge());
    }

    @Test
    void testSubmit() {
        mockResponse(MockResponseModels.getSubmissionJson());

        this.verifyHttpPostJsonMultiformData(this.assemblylineClient.submitBinary(RequestModels.getBinarySubmitObject()),
                MockResponseModels.getSubmission(), "/api/v4/submit/",
                RequestModels.getBinarySubmitMetadataJson(),
                RequestModels.getBinaryData());

    }

    @Test
    void testSubmitUrl() {
        mockResponse(MockResponseModels.getSubmissionJson());

        verifyHttpPostJson(this.assemblylineClient.submitUrlOrSha256(RequestModels.getUrlSubmitObject()),
                MockResponseModels.getSubmission(),
                "/api/v4/submit/",
                RequestModels.getUrlSubmitJson());
    }

    @Test
    void testSubmitSha256() {
        mockResponse(MockResponseModels.getSubmissionJson());

        verifyHttpPostJson(this.assemblylineClient.submitUrlOrSha256(RequestModels.getSha256SubmitObject()),
                MockResponseModels.getSubmission(),
                "/api/v4/submit/",
                RequestModels.getSha256SubmitJson());
    }

    @Test
    void testDownloadFileNoParams() throws IOException {
        String session = "testerSession";
        mockBackEnd.enqueue(new MockResponse()
                .setBody(MockResponseModels.getDownloadFileBuffer())
                .addHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .addHeader("Set-Cookie", "session=" + session));

        try (InputStream fileStream = this.assemblylineClient.downloadFile("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            fileStream.transferTo(outputStream);

            Assertions.assertArrayEquals(MockResponseModels.getDownloadFileBytes(), outputStream.toByteArray());
        }

        try {
            RecordedRequest actualRequest = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
            assertEquals("/api/v4/file/download/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/?encoding=cart", actualRequest.getPath());
        } catch (InterruptedException ie) {
            Assertions.fail(ie);
        }
    }

    @Test
    void testDownloadFileWithParams() throws IOException {
        String session = "testerSession";
        mockBackEnd.enqueue(new MockResponse()
                .setBody(MockResponseModels.getDownloadFileBuffer())
                .addHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .addHeader("Set-Cookie", "session=" + session));

        String sha256 = "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7";
        String name = "al_test.txt";
        String sid = "3p9RPMzkoYJ1p4vfdZj6B0";

        DownloadFileParams params = DownloadFileParams.builder()
                .encoding(DownloadFileParams.Encoding.RAW)
                .name(name)
                .sid(sid)
                .build();

        try (InputStream fileStream = this.assemblylineClient.downloadFile(sha256, params);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            fileStream.transferTo(outputStream);

            /* Note: Even though this request specifies "encoding=raw", we are mocking and expecting a CaRTed file.
            This is because we aren't really checking the specific data that is returned by Assemblyline, we are
            checking that the client sends the correct parameters and correctly constructs an InputStream from the HTTP
            response body. */
            Assertions.assertArrayEquals(MockResponseModels.getDownloadFileBytes(), outputStream.toByteArray());
        }

        try {
            RecordedRequest actualRequest = mockBackEnd.takeRequest(5, TimeUnit.SECONDS);
            String expectedPath = "/api/v4/file/download/" + sha256 + "/"
                    + "?encoding=raw"
                    + "&name=" + name
                    + "&sid=" + sid;
            assertEquals(expectedPath, actualRequest.getPath());
        } catch (InterruptedException ie) {
            Assertions.fail(ie);
        }
    }

    @Test
    void testGetHashSearchDataSources() {
        mockResponse(MockResponseModels.getHashSearchDataSourcesJson());

        verifyHttpGet(this.assemblylineClient.getHashSearchDataSources(),
                "/api/v4/hash_search/list_data_sources/",
                MockResponseModels.getHashSearchDataSources());
    }

    @Test
    void testHashSearchNoOptionalParams() {
        mockResponse(MockResponseModels.getHashSearchJson());

        verifyHttpGet(this.assemblylineClient.hashSearch("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7"),
                "/api/v4/hash_search/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/",
                MockResponseModels.getHashSearch());
    }

    @Test
    void testHashSearchWithOptionalParams() {
        mockResponse(MockResponseModels.getHashSearchJson());

        /* As with some other tests, we don't need the mock/expected response to match the parameters that are
        submitted; using those parameters is the job of *real* Assemblyline. We just need to make sure that the
        parameters get sent properly and that our client handles the response. */
        verifyHttpGet(this.assemblylineClient.hashSearch("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7", List.of("a", "b", "c"), 15),
                // %7C is a URL-encoded "|" (pipe)
                "/api/v4/hash_search/334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7/?db=a%7Cb%7Cc&max_timeout=15",
                MockResponseModels.getHashSearch());
    }

    @Test
    void testIngestGetMessageList() {
        mockResponse(MockResponseModels.getIngestMessageListJson());

        verifyHttpGet(this.assemblylineClient.getIngestMessageList("test_java_client"),
                "/api/v4/ingest/get_message_list/test_java_client/", MockResponseModels.getIngestMessageList());
    }

    @Data
    static class MetadataObjectTest {
        private String field1;
        private String field2;

        MetadataObjectTest() {
            this.field1 = "test1";
            this.field2 = "test2";
        }
    }


}
