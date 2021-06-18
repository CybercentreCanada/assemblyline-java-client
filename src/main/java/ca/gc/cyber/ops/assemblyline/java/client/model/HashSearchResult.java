package ca.gc.cyber.ops.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class HashSearchResult {
    /**
     * Error message returned by the data source.
     */
    String error;
    /**
     * List of items found in the data source.
     */
    List<Map<String, Object>> items;
}
