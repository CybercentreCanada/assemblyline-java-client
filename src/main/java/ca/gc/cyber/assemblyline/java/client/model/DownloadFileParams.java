package ca.gc.cyber.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

/**
 * This class holds parameters to be sent to /api/v4/file/download/{SHA256}.
 */
@Value
@Builder
public class DownloadFileParams {
    /**
     * Desired encoding of the downloaded file. If CART is not chosen, the name and sid parameters will not have any affect on the result.
     * <p>
     * Default: CART.
     */
    @Builder.Default
    Encoding encoding = Encoding.CART;
    /**
     * File name to be included in a CaRTed file's metadata section. If the chosen encoding is not Encoding.CART, this
     * field will have no effect on the resulting download.
     * <p>
     * Default: not set.
     */
    String name;
    /**
     * If this field is set, Assemblyline will attempt to include some metadata from the specified submission in the
     * resulting CaRT file's metadata section. If the chosen encoding is not Encoding.CART, this field will have no
     * effect on the resulting download.
     * <p>
     * Default: not set
     */
    String sid;

    public enum Encoding {
        CART,
        RAW;
    }
}
