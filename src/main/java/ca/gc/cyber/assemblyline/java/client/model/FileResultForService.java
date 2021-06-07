package ca.gc.cyber.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FileResultForService {
    /**
     * File information
     */
    FileInfo fileInfo;
    /**
     * List of result blocks
     */
    List<ResultBlock> results;
}
