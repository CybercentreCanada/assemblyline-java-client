package ca.gc.cyber.assemblyline.java.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class ResultBlock {
    /**
     * Archiving timestamp
     */
    Instant archiveTs;
    /**
     * Aggregate classification for the result
     */
    String classification;
    /**
     * Date at which the result object was created
     */
    Instant created;
    /**
     * Do not pass to other stages after this run
     */
    boolean dropFile;
    /**
     * Expiry timestamp
     */
    Instant expiryTs;
    /**
     * The body of the response from the service
     */
    Response response;
    /**
     * The result body
     */
    Result result;
    /**
     * Hierarchy of the sections in the result body
     */
    List<SectionHierarchyNode> sectionHierarchy;
    /**
     * SHA256 of the file that the result object relates to
     */
    String sha256;

    @Value
    @Builder
    public static class Response {
        /**
         * List of extracted files
         */
        List<File> extracted;
        /**
         * Milestones block
         */
        Milestones milestones;
        /**
         * Context about the service
         */
        String serviceContext;
        /**
         * Debug info about the service
         */
        String serviceDebugInfo;
        /**
         * Name of the service that scanned the file
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
         * List of supplementary files
         */
        List<File> supplementary;

        @Value
        @Builder
        public static class File {
            /**
             * Classification of the file
             */
            String classification;
            /**
             * Description of the file
             */
            String description;
            /**
             * Name of the file
             */
            String name;
            /**
             * SHA256 of the file
             */
            String sha256;
        }

        @Value
        @Builder
        public static class Milestones {
            /**
             * Date that the service finished scanning
             */
            Instant serviceCompleted;
            /**
             * Date that the service started scanning
             */
            Instant serviceStarted;
        }
    }


    @Value
    @Builder
    public static class Result {
        /**
         * Aggregate of the score for all heuristics
         */
        int score;
        /**
         * List of sections
         */
        List<Section> sections;

        /**
         * Unmapped fields:
         * <ul>
         *     <li>body</li>
         * </ul>
         */
        @Value
        @Builder
        public static class Section {
            /* From a Java type-correctness perspective, it's nice to make this an enum, but it could cause
            deserialization issues in the future. */
            /**
             * Type of body in this section
             */
            BodyFormat bodyFormat;
            /**
             * Classification of the section
             */
            String classification;
            /**
             * Depth of the section
             */
            int depth;
            /**
             * Heuristic used to score the result section
             */
            Heuristic heuristic;
            /**
             * List of tags associated with this section
             */
            List<Tag> tags;
            /**
             * Title of the section
             */
            String titleText;

            public enum BodyFormat {
                @JsonProperty("TEXT")
                TEXT,
                @JsonProperty("MEMORY_DUMP")
                MEMORY_DUMP,
                @JsonProperty("GRAPH_DATA")
                GRAPH_DATA,
                @JsonProperty("URL")
                URL,
                @JsonProperty("JSON")
                JSON,
                @JsonProperty("KEY_VALUE")
                KEY_VALUE,
                @JsonProperty("PROCESS_TREE")
                PROCESS_TREE,
                @JsonProperty("TABLE")
                TABLE;
            }

            @Value
            @Builder
            public static class Heuristic {
                /**
                 * List of attacks related to this heuristic
                 */
                List<FileResults.Attack> attack;
                /**
                 * ID of the heuristic that was triggered
                 */
                String heurId;
                /**
                 * Name of the heuristic
                 */
                String name;
                /**
                 * Computed heuristic's score
                 */
                int score;
                /**
                 * List of signatures that triggered the heuristic
                 */
                List<Signature> signature;
            }

            @Value
            @Builder
            public static class Tag {
                /**
                 * "Full" tag type, including parent types (e.g. file.file_data.created or av.virus_name)
                 */
                String type;
                /**
                 * Short tag type (e.g. created or virus_name)
                 */
                String shortType;
                /**
                 * Tag value
                 */
                //It's not immediately clear from the Python code if this value is restricted to a specific type.
                Object value;
            }
        }
    }

    @Value
    @Builder
    public static class SectionHierarchyNode {
        // id can be null.
        Integer id;
        List<SectionHierarchyNode> children;
    }
}
