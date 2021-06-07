package ca.gc.cyber.assemblyline.java.client.model.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
public class IngestSubmissionResponse {

    /**
     * If file had extended scan or it was skipped
     */
    ExtendedScan extendedScan;
    /**
     * Ingestion ID
     */
    String ingestId;
    /**
     * If Ingestion was a failure
     */
    String failure;
    /**
     * Time of ingestion
     */
    Instant ingestTime;
    /**
     * Number of times file ingestion was retried
     */
    int retries;
    /**
     * Scan Key
     */
    String scanKey;
    /**
     * Assemblyline File score
     */
    int score;
    /**
     * Submission
     */
    IngestSubmission submission;

    @Value
    @EqualsAndHashCode(callSuper = true)
    @Jacksonized
    @SuperBuilder
    public static class IngestSubmission extends SubmissionBase {
        /**
         * Notification Queue information
         */
        Notification notification;
        /**
         * Time of submission
         */
        Instant time;

        @Value
        @Builder
        public static class Notification {
            /**
             * Notification Queue name
             */
            String queue;
            /**
             * Notification Queue threshold
             */
            int threshold;
        }
    }

    /**
     * ref: assemblyline-base/assemblyline/odm/models/alert.py
     */
    public enum ExtendedScan {
        @JsonProperty("submitted")
        SUBMITTED,
        @JsonProperty("skipped")
        SKIPPED,
        @JsonProperty("incomplete")
        INCOMPLETE,
        @JsonProperty("completed")
        COMPLETED;
    }

}