package ca.gc.cyber.ops.assemblyline.java.client.model.ingest;

import ca.gc.cyber.ops.assemblyline.java.client.model.submit.SubmitBase;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

@SuperBuilder
@Value
public class AsyncBinaryFile<T extends SubmitBase> {

    /**
     * Metadata of file being submitted; in Http post, sent as Json String
     * For Ingest endpoint it is IngestBase
     * For Submit endpoint it is SubmitBase
     */
    T metadata;
    /**
     * Binary File Name
     */
    String filename;
    /**
     * Flux of Binary File Data
     */
    Flux<ByteBuffer> file;
}
