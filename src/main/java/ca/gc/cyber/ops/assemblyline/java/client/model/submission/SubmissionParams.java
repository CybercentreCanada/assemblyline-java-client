package ca.gc.cyber.ops.assemblyline.java.client.model.submission;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SubmissionParams {
    /**
     * Original classification of the submission
     */
    String classification;
    /**
     * Should a deep scan be performed?
     */
    boolean deepScan;
    /**
     * Description of the submission
     */
    String description;
    /**
     * Should this submission generate an alert
     */
    boolean generateAlert;
    /**
     * List of groups related to this scan
     */
    List<String> groups;
    /**
     * Ignore the service caching or not
     */
    boolean ignoreCache;
    /**
     * Should Assemblyline ignore dynamic recursion prevention
     */
    boolean ignoreDynamicRecursionPrevention;
    /**
     * Should Assemblyline ignore filtering services
     */
    boolean ignoreFiltering;
    /**
     * Ignore file size limits
     */
    boolean ignoreSize;
    /**
     * Initialization for auxiliary 'temporary_data'
     */
    String initialData;
    /**
     * Is the file submitted known to be malicious?
     */
    boolean malicious;
    /**
     * Max number of extracted files
     */
    int maxExtracted;
    /**
     * Max number of supplementary files
     */
    int maxSupplementary;
    /**
     * Exempt from being dropped by ingester
     */
    boolean neverDrop;
    /**
     * Priority of the scan
     */
    int priority;
    /**
     * Should the submission do extra profiling
     */
    boolean profile;
    /**
     * Parent submission ID
     */
    String psid;
    /**
     * Does this item count against quota
     */
    boolean quotaItem;
    /**
     * Service-specific parameters
     */
    Map<String, Map<String, String>> serviceSpec;
    /**
     * Service selection block
     */
    ServiceSelection services;
    /**
     * User who submitted the file
     */
    String submitter;
    /**
     * Time to live for this submission in days
     */
    int ttl;
    /**
     * Type of submission
     */
    String type;

    @Value
    @Builder
    public static class ServiceSelection {
        /**
         * List of excluded services
         */
        List<String> excluded;
        /**
         * Add to service selection re-submitting
         */
        List<String> resubmit;
        /**
         * List of runtime excluded services
         */
        List<String> runtimeExcluded;
        /**
         * List of selected services
         */
        List<String> selected;
        /**
         * List of rescan services
         */
        List<String> rescan;
    }
}