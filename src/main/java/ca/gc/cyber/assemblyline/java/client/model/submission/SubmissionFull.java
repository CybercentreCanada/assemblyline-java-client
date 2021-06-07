package ca.gc.cyber.assemblyline.java.client.model.submission;

import ca.gc.cyber.assemblyline.java.client.model.Error;
import ca.gc.cyber.assemblyline.java.client.model.FileInfo;
import ca.gc.cyber.assemblyline.java.client.model.ResultBlock;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Maps the response from /submission/full/{sid}/
 * <p>
 * This class is the same as {@link Submission} except for the following differences:
 * <ul>
 *     <li>The list of error keys is replaced with a map from error key to error.</li>
 *     <li>There is a missingErrorKeys field that contains error keys which could not be retrieved from the data store.</li>
 *     <li>The list of result keys is replaced with a map from result key to result.</li>
 *     <li>There is a missingResultKeys field that contains result keys which could not be retrieved from the data store.</li>
 *     <li>There is a map from SHA256 to FileInfo</li>
 *     <li>There is a missingFileKeys field that contains SHA256es which could not be retrieved from the data store.</li>
 *     <li>There is a fileTree field which describes the hierarchy of the files.</li>
 * </ul>
 */
@Value
@EqualsAndHashCode(callSuper = true)
@Jacksonized
@SuperBuilder
public class SubmissionFull extends SubmissionBase {
    /**
     * Archiving timestamp
     */
    Instant archiveTs;
    /**
     * Classification of the submission
     */
    String classification;
    /**
     * Total number of errors in the submission
     */
    int errorCount;
    /**
     * Map of error key to error
     */
    Map<String, Error> errors;
    /**
     * Expiry timestamp
     */
    Instant expiryTs;
    /**
     * Total number of files in the submission
     */
    int fileCount;
    /**
     * Map of SHA256 to FileInfo
     */
    Map<String, FileInfo> fileInfos;
    /**
     * Map of SHA256-of-root-node to root node.
     */
    Map<String, SubmissionTree.TreeNode> fileTree;
    /**
     * Maximum score of all the files in the scan
     */
    int maxScore;
    /**
     * Error keys that could not be found in the data store
     */
    List<String> missingErrorKeys;
    /**
     * File keys that could not be found in the data store
     */
    List<String> missingFileKeys;
    /**
     * Result keys that could not be found in the data store
     */
    List<String> missingResultKeys;
    /**
     * Map of result key (SHA256.ServiceName.ServiceVersion.Config) to service result.
     */
    Map<String, ResultBlock> results;
    /**
     * Status of the submission
     */
    String state;
    /**
     * Timing block
     */
    Submission.Times times;
    /**
     * Verdict voting
     */
    Submission.Verdict verdict;
}
