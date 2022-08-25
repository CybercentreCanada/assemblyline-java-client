package ca.gc.cyber.ops.assemblyline.java.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * Maps the response from /file/result/{sha256}
 */
@Value
@Builder
// FileResults needs custom deserialization for its "heuristics" field and that is easier to do via builder.
@JsonDeserialize(builder = FileResults.FileResultsBuilder.class)
public class FileResults {
    /**
     * Map of service name to alternate results.
     */
    Map<String, List<AlternateResult>> alternates;
    /**
     * Map from attack category to list of attacks
     */
    @JsonIgnore
    Map<String, List<Attack>> attackMatrix;
    /**
     * [sic]
     */
    List<ChildFile> childrens;
    /**
     * List of error keys
     */
    List<Error> errors;
    /**
     * File info
     */
    FileInfo fileInfo;
    /**
     * UI switch to disable features
     */
    boolean fileViewerOnly;
    /**
     * Map from heuristic type (info, suspicious, malicious) to list of heuristics
     */
    @JsonIgnore
    Map<String, List<Heuristic>> heuristics;
    /**
     * Metadata facets results
     */
    Map<String, Object> metadata;
    /**
     * This is a list of service result "keys". e.g .<sha256>.<service_name>.<service_version>.<...something>
     */
    List<String> parents;
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
    @JsonIgnore
    Map<String, List<Tag>> tags;

    /**
     * We need to define custom deserialization for some fields. The {@link Builder} annotation of FileResult will add
     * all of the "standard" builder methods.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class FileResultsBuilder {
        /**
         * The heuristic instances are stored as arrays in the JSON instead of objects, so we need special deserialization.
         *
         * @param map   Map of heuristics
         * @return The builder.
         */
        @JsonProperty("heuristics")
        // This method is used by Jackson for JSON deserialization.
        @SuppressWarnings("unused")
        public FileResultsBuilder heuristicsFromJson(Map<String, List<List<String>>> map) {
            heuristics = map.entrySet().stream()
                    .collect(toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .map(Heuristic::fromList)
                                    .collect(toUnmodifiableList())));

            return this;
        }

        /**
         * The attack instances are stored as arrays in the JSON instead of objects, so we need special deserialization.
         *
         * @param map   Map of attack instances
         * @return The builder.
         */
        @JsonProperty("attack_matrix")
        // This method is used by Jackson for JSON deserialization.
        @SuppressWarnings("unused")
        public FileResultsBuilder attackMatrixFromJson(Map<String, List<List<String>>> map) {
            attackMatrix = map.entrySet().stream()
                    .collect(toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .map(Attack::fromList)
                                    .collect(toUnmodifiableList())));

            return this;
        }

        /**
         * The tag instances are stored as arrays in the JSON instead of objects, so we need special deserialization.
         *
         * @param map   Map of tag instances
         * @return The builder.
         */
        @JsonProperty("tags")
        // This method is used by Jackson for JSON deserialization.
        @SuppressWarnings("unused")
        public FileResultsBuilder tagsFromJson(Map<String, List<List<String>>> map) {
            tags = map.entrySet().stream()
                    .collect(toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .map(Tag::fromList)
                                    .collect(toUnmodifiableList())));

            return this;
        }
    }

    @Value
    @Builder
    public static class AlternateResult {
        /**
         * Aggregate classification for the result
         */
        String classification;
        /**
         * Date at which the result got created
         */
        Instant created;
        /**
         * Do not pass to other stages after this run
         */
        boolean dropFile;
        /**
         * This is a  result "key". e.g .<sha256>.<service_name>.<service_version>.<config>.
         */
        String id;
        /**
         * The body of the response from the service
         */
        Response response;
        /**
         * The result body
         */
        Result result;

        /**
         * This class is a subset of {@link ResultBlock.Result}.
         */
        @Value
        @Builder
        public static class Response {
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
        }


        /**
         * This class is a subset of {@link ResultBlock.Result}.
         */
        @Value
        @Builder
        public static class Result {
            /**
             * Aggregate score for all heuristics
             */
            int score;
        }
    }

    @Value
    @Builder
    public static class Attack {
        /**
         * Attack matrix ID
         */
        String attackId;
        /**
         * Attack matrix pattern name
         */
        String attackPattern;
        /**
         * Heuristic type: info, suspicious, or malicious
         */
        String heuristicType;

        /**
         * These Attacks are stored as arrays in the JSON instead of objects.
         *
         * @param list 3-element list structured as [attackId, attackPattern, heuristicType]
         * @return An Attack instance
         */
        public static Attack fromList(List<String> list) {
            return new Attack(list.get(0), list.get(1), list.get(2));
        }
    }

    @Value
    @Builder
    public static class ChildFile {
        /**
         * Name of the file
         */
        String name;
        /**
         * SHA256 hash of the file
         */
        String sha256;
    }

    /**
     * This class is a subset of {@link ResultBlock.Result.Section.Heuristic}.
     */
    @Value
    @Builder
    public static class Heuristic {
        /**
         * ID of the heuristic triggered
         */
        String heurId;
        /**
         * Name of the heuristic
         */
        String name;

        /**
         * These heuristics are stored as arrays in the JSON instead of objects.
         *
         * @param list 2-element list structured as [heurId, name]
         * @return An Heuristic instance
         */
        public static Heuristic fromList(List<String> list) {
            return new Heuristic(list.get(0), list.get(1));
        }
    }

    @Value
    @Builder
    public static class Tag {
        String value;
        String heuristicType;

        /**
         * These tags are stored as arrays in the JSON instead of objects.
         *
         * @param list 2-element list structured as [tageValue, heuristicType]
         * @return An Heuristic instance
         */
        public static Tag fromList(List<String> list) {
            return new Tag(list.get(0), list.get(1));
        }
    }
}
