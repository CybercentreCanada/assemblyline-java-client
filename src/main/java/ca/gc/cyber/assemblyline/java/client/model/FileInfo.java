package ca.gc.cyber.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class FileInfo {
    /**
     * Archiving timestamp
     */
    Instant archiveTs;
    /**
     * Dotted ASCII representation of the first 64 bytes of the file
     */
    String ascii;
    /**
     * Classification of the file
     */
    String classification;
    /**
     * Entropy of the file
     */
    double entropy;
    /**
     * Expiry timestamp
     */
    Instant expiryTs;
    /**
     * Hex dump of the first 64 bytes of the file
     */
    String hex;
    /**
     * Output from libmagic related to the file
     */
    String magic;
    /**
     * MD5 of the top-level file
     */
    String md5;
    /**
     * Mime type of the file as identified by libmagic
     */
    String mime;
    /**
     * Attributes about when the file was seen
     */
    Seen seen;
    /**
     * SHA1 hash of the file
     */
    String sha1;
    /**
     * SHA256 hash of the file
     */
    String sha256;
    /**
     * Size of the file
     */
    long size;
    /**
     * SSDEEP hash of the file
     */
    String ssdeep;
    /**
     * Type of file as identified by Assemblyline
     */
    String type;

    @Value
    @Builder
    public static class Seen {
        /**
         * Number of times that Assemblyline has seen the file
         */
        long count;
        /**
         * Date of the first time that Assemblyline saw the file
         */
        Instant first;
        /**
         * Date of the last time that Assemblyline saw the file
         */
        Instant last;
    }
}
