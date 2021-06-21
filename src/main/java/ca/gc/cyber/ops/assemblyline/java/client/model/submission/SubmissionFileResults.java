package ca.gc.cyber.ops.assemblyline.java.client.model.submission;

import ca.gc.cyber.ops.assemblyline.java.client.model.Error;
import ca.gc.cyber.ops.assemblyline.java.client.model.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

/**
 * Maps the response from /submission/{sid}/file/{sha256}
 */
@Value
@Builder
public class SubmissionFileResults {
    /**
     * Map from attack category to list of attacks
     */
    Map<String, List<FileResults.Attack>> attackMatrix;
    /**
     * List of error keys
     */
    List<Error> errors;
    /**
     * File info
     */
    FileInfo fileInfo;
    /**
     * Map from heuristic type (info, suspicious, malicious) to list of heuristics
     */
    Map<String, List<FileResults.Heuristic>> heuristics;
    /**
     * Metadata facets results
     */
    Map<String, Object> metadata;
    /**
     * List of results
     */
    List<ResultBlock> results;
    /**
     * Aggregation of signatures from all results
     */
    List<Signature> signatures;
    /**
     * Map of tag type to list of tags
     */
    Map<String, List<FileResults.Tag>> tags;
}
