package ca.gc.cyber.assemblyline.java.client.model.submission;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SubmissionTree {
    /**
     * Maximum classification of files in the tree
     */
    String classification;
    /**
     * True if files were filtered from the tree because the user does not have permission to view them.
     */
    boolean filtered;
    /**
     * True if one of more files could not be retrieved while building the tree.
     */
    boolean partial;
    /**
     * Map of SHA256-of-root-node to root node.
     */
    Map<String, TreeNode> tree;

    @Value
    @Builder
    public static class TreeNode {
        /**
         * Map of SHA256-of-child-node to child node.
         */
        Map<String, TreeNode> children;
        /**
         * List of names of the file
         */
        List<String> name;
        /**
         * The aggregate score for the file
         */
        int score;
        /**
         * SHA256 hash of the file
         */
        String sha256;
        /**
         * Size of the file
         */
        long size;
        /**
         * True if this branch of the tree is truncated because it is repeated elsewhere in the tree
         */
        boolean truncated;
        /**
         * The type of the file as identified by Assemblyline
         */
        String type;
    }
}
