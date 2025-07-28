package ca.gc.cyber.ops.assemblyline.java.client.clients;

import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.AsyncBinaryFile;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.BinaryFile;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.IngestBase;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.Sha256Ingest;
import ca.gc.cyber.ops.assemblyline.java.client.model.ingest.UrlIngest;
import ca.gc.cyber.ops.assemblyline.java.client.model.submit.Sha256Submit;
import ca.gc.cyber.ops.assemblyline.java.client.model.submit.SubmitMetadata;
import ca.gc.cyber.ops.assemblyline.java.client.model.submit.UrlSubmit;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@UtilityClass
public class RequestModels {

    private String readTestJson(String jsonName) {
        String fullName = "/MockRequestModels/" + jsonName;
        try (InputStream inputStream = RequestModels.class.getResourceAsStream(fullName);
             ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Could not find resource " + fullName);
            }

            inputStream.transferTo(byteStream);
            return byteStream.toString(StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            /* fail() always throws an exception and never actually returns, but this needs to be a return statement to
            appease the compiler. */
            return Assertions.fail("Failed to read mock response JSON.", ioe);
        }
    }

    public Sha256Ingest getSha256IngestObject() {
        return Sha256Ingest.builder()
                .sha256("abc256")
                .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                .params(Map.of("param1", "value1"))
                .submissionProfile("static")
                .name("meta data")
                .generateAlert(true)
                .notificationQueue("notificationQueue")
                .notificationThreshold(100)
                .build();
    }

    public Sha256Submit getSha256SubmitObject() {
        return Sha256Submit.builder()
                .sha256("abc256")
                .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                .params(Map.of("param1", "value1"))
                .submissionProfile("static")
                .name("meta data")
                .build();
    }

    public String getSha256IngestJson() {
        return readTestJson("sha256_ingest.json");
    }

    public BinaryFile<IngestBase> getBinaryIngestObject() {
        return BinaryFile.<IngestBase>builder()
                .filename("fileName")
                .file(new byte[]{1, 2, 3})
                .metadata(IngestBase.builder()
                        .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                        .params(Map.of("param1", "value1"))
                        .submissionProfile("static")
                        .name("meta data")
                        .generateAlert(true)
                        .notificationQueue("notificationQueue")
                        .notificationThreshold(100)
                        .build())
                .build();
    }

    public AsyncBinaryFile<IngestBase> getAsyncBinaryIngestObject() {
        return AsyncBinaryFile.<IngestBase>builder()
                .filename("fileName")
                .file(Flux.just(ByteBuffer.wrap(new byte[]{1, 2, 3})))
                .metadata(IngestBase.builder()
                        .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                        .params(Map.of("param1", "value1"))
                        .submissionProfile("static")
                        .name("meta data")
                        .generateAlert(true)
                        .notificationQueue("notificationQueue")
                        .notificationThreshold(100)
                        .build())
                .build();
    }

    public byte[] getBinaryData(){
        return new byte[]{1, 2, 3};
    }

    public String getBinaryIngestBaseJson() {
        return readTestJson("binary_ingest.json");
    }

    public String getSha256SubmitJson() {
        return readTestJson("sha256_submit.json");
    }

    public BinaryFile<SubmitMetadata> getBinarySubmitObject() {
        return BinaryFile.<SubmitMetadata>builder()
                .filename("fileName")
                .file(getBinaryData())
                .metadata(SubmitMetadata.builder()
                        .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                        .params(Map.of("param1", "value1"))
                        .submissionProfile("static")
                        .name("meta data")
                        .build())
                .build();
    }

    public String getBinarySubmitMetadataJson() {
        return readTestJson("submit.json");
    }

    public UrlIngest getUrlIngestObject() {
        return UrlIngest.builder()
                .url("/test/url.com")
                .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                .params(Map.of("param1", "value1"))
                .submissionProfile("static")
                .name("meta data")
                .generateAlert(true)
                .notificationQueue("notificationQueue")
                .notificationThreshold(100)
                .build();
    }

    public String getUrlIngestJson() {
        return readTestJson("url_ingest.json");
    }

    public UrlSubmit getUrlSubmitObject() {
        return UrlSubmit.builder()
                .url("/test/url.com")
                .metadata(Map.of("key", "value", "key2", new AssemblylineClientTest.MetadataObjectTest()))
                .params(Map.of("param1", "value1"))
                .submissionProfile("static")
                .name("meta data")
                .build();
    }

    public String getUrlSubmitJson() {
        return readTestJson("url_submit.json");
    }
}
