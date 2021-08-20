package ca.gc.cyber.ops.assemblyline.java.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Error {
    /**
     * Archiving timestamp
     */
    Instant archiveTs;
    /**
     * Date at which the error was created
     */
    Instant created;
    /**
     * Expiry timestamp
     */
    Instant expiryTs;
    /**
     * Response from the service
     */
    Response response;
    /**
     * Hash of the file the error is related to
     */
    String sha256;
    /**
     * Type of error
     */
    Type type;

    @Value
    @Builder
    public static class Response {
        /**
         * Error message
         */
        String message;
        /**
         * Info about where the error was processed
         */
        String serviceDebugInfo;
        /**
         * Name of the service that had the error
         */
        String serviceName;
        /**
         * Tool version of the service
         */
        String serviceToolVersion;
        /**
         * Version of the service
         */
        String serviceVersion;
        /**
         * Status of the error
         */
        Status status;

        public enum Status {
            @JsonProperty("FAIL_NONRECOVERABLE")
            FAIL_NONRECOVERABLE,
            @JsonProperty("FAIL_RECOVERABLE")
            FAIL_RECOVERABLE;
        }
    }

    public enum Type {
        @JsonProperty("UNKNOWN")
        UNKNOWN,
        @JsonProperty("EXCEPTION")
        EXCEPTION,
        @JsonProperty("MAX DEPTH REACHED")
        MAX_DEPTH_REACHED,
        @JsonProperty("MAX FILES REACHED")
        MAX_FILES_REACHED,
        @JsonProperty("MAX RETRY REACHED")
        MAX_RETRY_REACHED,
        @JsonProperty("SERVICE BUSY")
        SERVICE_BUSY,
        @JsonProperty("SERVICE DOWN")
        SERVICE_DOWN,
        @JsonProperty("TASK PRE-EMPTED")
        TASK_PRE_EMPTED;
    }
}
