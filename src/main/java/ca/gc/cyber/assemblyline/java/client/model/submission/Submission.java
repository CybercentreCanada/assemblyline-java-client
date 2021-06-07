package ca.gc.cyber.assemblyline.java.client.model.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

/**
 * Maps the response from /submission/{sid}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@Jacksonized
@SuperBuilder
public class Submission extends SubmissionBase {
    /**
     * Archiving timestamp
     */
    Instant archiveTs;
    /**
     * Classification for the submission
     */
    String classification;
    /**
     * Total number of errors in the submission
     */
    int errorCount;
    /**
     * List of error keys
     */
    List<String> errors;
    /**
     * Expiry timestamp
     */
    Instant expiryTs;
    /**
     * Total number of files in the submission
     */
    int fileCount;
    /**
     * Maximum score of all the files in the scan
     */
    int maxScore;
    /**
     * List of result keys (SHA256.ServiceName.ServiceVersion.Config)
     */
    List<String> results;
    /**
     * Status of the submission
     */
    /* From a Java type-correctness perspective, it's nice to make this an enum, but it could cause
    deserialization issues in the future. */
    State state;
    /**
     * Timing block
     */
    Times times;
    /**
     * Verdict voting
     */
    Verdict verdict;

    /**
     * ref: assemblyline-base/assemblyline/odm/models/submission.py
     */
    public enum State {
        @JsonProperty("failed")
        FAILED,
        @JsonProperty("submitted")
        SUBMITTED,
        @JsonProperty("completed")
        COMPLETED
    }

    @Value
    @Builder
    public static class Times {
        /**
         * Date at which the submission finished scanning
         */
        Instant completed;
        /**
         * Date at which the submission started scanning
         */
        Instant submitted;
    }

    @Value
    @Builder
    public static class Verdict {
        /**
         * List of users who voted the submission as malicious.
         */
        List<String> malicious;
        /**
         * List of users who voted the submission as non-malicious.
         */
        List<String> nonMalicious;
    }
}
