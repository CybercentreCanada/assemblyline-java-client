package ca.gc.cyber.ops.assemblyline.java.client.clients;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IAssemblylineClient {
    String BASE_URL = "api/v4/";
    String LOGIN_URL = BASE_URL + "auth/login/";
    String FILE_DOWNLOAD_URL = BASE_URL + "file/download/{sha256}/";
    String FILE_INFO_URL = BASE_URL + "file/info/{sha256}/";
    String FILE_RESULTS_URL = BASE_URL + "file/result/{sha256}/";
    String FILE_RESULT_FOR_SERVICE_URL = FILE_RESULTS_URL + "{serviceName}/";
    String HASH_SEARCH = BASE_URL + "hash_search/{hash}/";
    String HASH_SEARCH_LIST_DATA_SOURCES = BASE_URL + "hash_search/list_data_sources/";
    String INGEST_URL = BASE_URL + "ingest/";
    String INGEST_GET_MESSAGE_LIST_URL = BASE_URL + "ingest/get_message_list/{notificationQueue}/";
    String RESULT_URL = BASE_URL + "result/{path:cache_key}/";
    String SUBMISSION_COMPLETE_URL = BASE_URL + "submission/is_completed/{sid}/";
    String SUBMISSION_URL = BASE_URL + "submission/{sid}/";
    String SUBMISSION_FILE_RESULTS_URL = SUBMISSION_URL + "file/{sha256}/";
    String SUBMISSION_FULL_URL = BASE_URL + "submission/full/{sid}/";
    String SUBMISSION_TREE_URL = BASE_URL + "submission/tree/{sid}/";
    String SUBMIT_URL = BASE_URL + "submit/";

    AssemblylineClient withAuthBearerToken(String authBearerToken);

    Mono<LoginResponse> login();

    Mono<Boolean> isSubmissionComplete(String sid);

    Mono<FileInfo> getFileInfo(String sha256);

    Mono<FileResults> getFileResults(String sha256);

    Mono<FileResultForService> getFileResultForService(String sha256, String serviceName);

    Mono<ResultBlock> getResult(String cacheKey);

    Mono<SubmissionFileResults> getSubmissionFileResults(String sid, String sha256);

    Mono<SubmissionTree> getSubmissionTree(String sid);

    Mono<Submission> getSubmission(String sid);

    Mono<SubmissionFull> getSubmissionFull(String sid);

    Mono<IngestResponse> ingestUrlOrSha256(NonBinaryIngest ingest);

    Mono<IngestResponse> ingestBinary(BinaryFile<IngestBase> binaryIngest);

    Flux<IngestSubmissionResponse> getIngestMessageList(String notification);

    Mono<Submission> submitUrlOrSha256(NonBinarySubmit submit);

    Mono<Submission> submitBinary(BinaryFile<SubmitMetadata> binaryIngest);

    InputStream downloadFile(String sha256);

    InputStream downloadFile(String sha256, DownloadFileParams params);

    Mono<List<String>> getHashSearchDataSources();

    Mono<Map<String, HashSearchResult>> hashSearch(String fileHash, List<String> dataSources, Integer maxTimeout);

    Mono<Map<String, HashSearchResult>> hashSearch(String fileHash);

    String getSession();
}
