package ca.gc.cyber.ops.assemblyline.java.client.clients;

import ca.gc.cyber.ops.assemblyline.java.client.model.*;
import ca.gc.cyber.ops.assemblyline.java.client.model.submission.*;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public final class MockResponseModels {

    private static final String TLP_WHITE = "TLP:WHITE";

    /**
     * Private constructor because this is a utility class..
     */
    private MockResponseModels() {
    }

    private static String readFileIntoString(String jsonName) {
        String fullName = "/MockResponseModels/" + jsonName;
        try (InputStream inputStream = MockResponseModels.class.getResourceAsStream(fullName);
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

    public static String getLoginResponseJson() {
        return readFileIntoString("login_response.json");
    }

    public static LoginResponse getLoginResponse() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getLoginResponse(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from AssemblyLine that will be input for a deserializer whose
        deserialization we are testing.*/
        return LoginResponse.builder()
                .privileges(List.of(LoginResponse.Privleges.R, LoginResponse.Privleges.W))
                .rolesLimit(List.of("submission_create", "submission_delete", "submission_manage"))
                .sessionDuration(300)
                .username("test")
                .build();
    }

    public static FileInfo getFileInfo() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getFileInfoJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return FileInfo.builder()
                .archiveTs(Instant.parse("2021-02-24T18:15:12.770369Z"))
                .ascii("Hello!")
                .classification(TLP_WHITE)
                .entropy(2.251629167387823)
                .expiryTs(null)
                .hex("48656c6c6f21")
                .magic("ASCII text, with no line terminators")
                .md5("952d2c56d0485958336747bcdd98590d")
                .mime("text/plain")
                .seen(FileInfo.Seen.builder()
                        .count(2)
                        .first(Instant.parse("2021-02-15T17:37:51.953004Z"))
                        .last(Instant.parse("2021-02-19T18:15:12.770385Z"))
                        .build())
                .sha1("69342c5c39e5ae5f0077aecc32c0f81811fb8193")
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7")
                .size(6)
                .ssdeep("3:at:at")
                .type("unknown")
                .build();
    }

    public static String getFileInfoJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getFileInfo(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("file_info.json");
    }

    public static FileResults getFileResults() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getFileResultsJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return FileResults.builder()
                .alternates(Map.of(
                        "Characterize", List.of(FileResults.AlternateResult.builder()
                                .classification(TLP_WHITE)
                                .created(ZonedDateTime
                                        .of(2021, 2, 15,
                                                17, 37, 52, 510000000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .dropFile(false)
                                .id("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable4.cDyOMFE1phHM")
                                .response(FileResults.AlternateResult.Response.builder()
                                        .serviceName("Characterize")
                                        .serviceVersion("v4.0.0.stable4")
                                        .build())
                                .result(FileResults.AlternateResult.Result.builder()
                                        .score(0)
                                        .build())
                                .build()),
                        "VirusTotalCache", List.of(FileResults.AlternateResult.builder()
                                .classification(TLP_WHITE)
                                .created(ZonedDateTime
                                        .of(2021, 2, 15,
                                                17, 37, 53, 816000000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .dropFile(false)
                                .id("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.VirusTotalCache.v4_0_1_stable6.c7uXpMuO5nLU")
                                .response(FileResults.AlternateResult.Response.builder()
                                        .serviceName("VirusTotalCache")
                                        .serviceToolVersion("DB Checked: 2021-02-15 14:12:49 UTC")
                                        .serviceVersion("v4.0.1.stable6")
                                        .build())
                                .result(FileResults.AlternateResult.Result.builder()
                                        .score(0)
                                        .build())
                                .build())
                ))
                .attackMatrixFromJson(Map.of())
                .childrens(List.of())
                .errors(List.of())
                .fileInfo(getFileInfo())
                .fileViewerOnly(true)
                .heuristics(Map.of())
                .metadata(Map.of(
                        "submitter", Map.of(
                                "username", 2
                        )
                ))
                .parents(List.of(
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36.Extract.v4_0_0_stable9.c2i2PqguInqd"
                ))
                .results(List.of(
                        getVirusTotalCacheResultBlockWithSectionHierarchy(),
                        getCharacterizeResultBlockWithSectionHierarchy()
                ))
                .signatures(List.of())
                .tags(Map.of())
                .build();
    }

    /**
     * @return The result block for VirusTotalCache service run against file al_test.txt (334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7)
     */
    private static ResultBlock getVirusTotalCacheResultBlock() {
        return getVirusTotalCacheResultBlockBuilder().build();
    }

    /**
     * The result block needs to have a "section_hierarchy" field in some cases but not others.
     *
     * @return The result block for VirusTotalCache service (including section_hierarchy) run against file al_test.txt (334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7)
     */
    private static ResultBlock getVirusTotalCacheResultBlockWithSectionHierarchy() {
        return getVirusTotalCacheResultBlockBuilder()
                .sectionHierarchy(List.of(ResultBlock.SectionHierarchyNode.builder()
                        .id(0)
                        .children(List.of())
                        .build()))
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getVirusTotalCacheResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 24,
                                18, 15, 14, 673617000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 19,
                                18, 15, 14, 673584000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 19,
                                                18, 15, 14, 672364000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 19,
                                                18, 15, 14, 459308000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceContext("DB Checked: 2021-02-19 17:13:55 UTC")
                        .serviceName("VirusTotalCache")
                        .serviceToolVersion("DB Checked: 2021-02-19 15:56:31 UTC")
                        .serviceVersion("4.0.1.stable7")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of(ResultBlock.Result.Section.builder()
                                .bodyFormat(ResultBlock.Result.Section.BodyFormat.KEY_VALUE)
                                .classification(TLP_WHITE)
                                .depth(0)
                                .tags(List.of())
                                .titleText("vt_test_file.txt")
                                .build()))
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    /**
     * @return The ResultBlock.Builder for Characterize service run against file al_test.txt (334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7)
     */
    private static ResultBlock getCharacterizeResultBlock() {
        return getCharacterizeResultBlockBuilder().build();
    }

    /**
     * The result block needs to have a "section_hierarchy" field in some cases but not others.
     *
     * @return The ResultBlock.Builder for Characterize service (including section_hierarchy) run against file al_test.txt (334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7)
     */
    private static ResultBlock getCharacterizeResultBlockWithSectionHierarchy() {
        return getCharacterizeResultBlockBuilder()
                .sectionHierarchy(List.of(ResultBlock.SectionHierarchyNode.builder()
                        .id(0)
                        .children(List.of())
                        .build()))
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getCharacterizeResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 24,
                                18, 15, 13, 377863000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 19,
                                18, 15, 13, 377831000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 19,
                                                18, 15, 13, 377078000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 19,
                                                18, 15, 13, 149844000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("Characterize")
                        .serviceVersion("4.0.0.stable5")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of(ResultBlock.Result.Section.builder()
                                .bodyFormat(ResultBlock.Result.Section.BodyFormat.GRAPH_DATA)
                                .classification(TLP_WHITE)
                                .depth(0)
                                .tags(List.of())
                                .titleText("File entropy: 2.252")
                                .build()))
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getMetaDefenderResultBlock() {
        return getMetaDefenderResultBlockBuilder().build();
    }

    private static ResultBlock getMetaDefenderResultBlockWithSectionHierarchy() {
        return getMetaDefenderResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getMetaDefenderResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 613748000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 613863000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 613767000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 613968000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 613941000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("MetaDefender")
                        .serviceVersion("4_0_0_stable5")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getTagCheckResultBlock() {
        return getTagCheckResultBlockBuilder().build();
    }

    private static ResultBlock getTagCheckResultBlockWithSectionHierarchy() {
        return getTagCheckResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getTagCheckResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614121000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 614169000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614128000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614253000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614228000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("TagCheck")
                        .serviceVersion("4_0_0_stable9")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getFrankenStringsResultBlock() {
        return getFrankenStringsResultBlockBuilder().build();
    }

    private static ResultBlock getFrankenStringsResultBlockWithSectionHierarchy() {
        return getFrankenStringsResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getFrankenStringsResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614364000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 614409000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614370000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614491000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614465000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("FrankenStrings")
                        .serviceVersion("4_0_0_stable3")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getYaraResultBlock() {
        return getYaraResultBlockBuilder().build();
    }

    private static ResultBlock getYaraResultBlockWithSectionHierarchy() {
        return getYaraResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getYaraResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614598000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 614641000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614604000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614721000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614697000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("YARA")
                        .serviceVersion("4_0_0_stable9")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getMetaPeekResultBlock() {
        return getMetaPeekResultBlockBuilder().build();
    }

    private static ResultBlock getMetaPeekResultBlockWithSectionHierarchy() {
        return getMetaPeekResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getMetaPeekResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614827000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 614870000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 614833000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614949000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 614924000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("MetaPeek")
                        .serviceVersion("4_0_0_stable4")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getCodeVectorResultBlock() {
        return getCodeVectorResultBlockBuilder().build();
    }

    private static ResultBlock getCodeVectorResultBlockWithSectionHierarchy() {
        return getCodeVectorResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getCodeVectorResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 615054000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 615096000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 615060000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 615177000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 615152000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("Codevector")
                        .serviceVersion("4_0_0_stable6")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    private static ResultBlock getConfigExtractorResultBlock() {
        return getConfigExtractorResultBlockBuilder().build();
    }

    private static ResultBlock getConfigExtractorResultBlockWithSectionHierarchy() {
        return getConfigExtractorResultBlockBuilder()
                .sectionHierarchy(List.of())
                .build();
    }

    private static ResultBlock.ResultBlockBuilder getConfigExtractorResultBlockBuilder() {
        return ResultBlock.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 615282000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .created(ZonedDateTime
                        .of(2021, 2, 23,
                                14, 39, 9, 615325000,
                                ZoneOffset.UTC)
                        .toInstant())
                .dropFile(false)
                .expiryTs(ZonedDateTime
                        .of(2021, 2, 28,
                                14, 39, 9, 615288000,
                                ZoneOffset.UTC)
                        .toInstant())
                .response(ResultBlock.Response.builder()
                        .extracted(List.of())
                        .milestones(ResultBlock.Response.Milestones.builder()
                                .serviceCompleted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 615404000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .serviceStarted(ZonedDateTime
                                        .of(2021, 2, 23,
                                                14, 39, 9, 615380000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                        .serviceName("ConfigExtractor")
                        .serviceVersion("4_0_1_stable15")
                        .supplementary(List.of())
                        .build())
                .result(ResultBlock.Result.builder()
                        .score(0)
                        .sections(List.of())
                        .build())
                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7");
    }

    public static String getFileResultsJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getFileResults(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("file_results.json");
    }

    public static FileResultForService getFileResultForService() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getFileResultForServiceJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return FileResultForService.builder()
                .fileInfo(getFileInfo())
                .results(List.of(getCharacterizeResultBlock()))
                .build();
    }

    public static String getFileResultForServiceJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getFileResultForService(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("file_result_service.json");
    }

    public static ResultBlock getResultBlock() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getResultBlockJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return getCharacterizeResultBlockWithSectionHierarchy();
    }

    public static String getResultBlockJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getResultBlock(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("result.json");
    }

    public static SubmissionFileResults getSubmissionFileResults() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getSubmissionFileResultsJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return SubmissionFileResults.builder()
                .attackMatrix(Map.of())
                .errors(List.of())
                .fileInfo(getFileInfo())
                .heuristics(Map.of())
                .metadata(Map.of(
                        "submitter", Map.of(
                                "username", 2
                        )
                ))
                .results(List.of(
                        getMetaDefenderResultBlockWithSectionHierarchy(),
                        getTagCheckResultBlockWithSectionHierarchy(),
                        getFrankenStringsResultBlockWithSectionHierarchy(),
                        getYaraResultBlockWithSectionHierarchy(),
                        getMetaPeekResultBlockWithSectionHierarchy(),
                        getCodeVectorResultBlockWithSectionHierarchy(),
                        getConfigExtractorResultBlockWithSectionHierarchy(),
                        getVirusTotalCacheResultBlockWithSectionHierarchy(),
                        getCharacterizeResultBlockWithSectionHierarchy()
                ))
                .signatures(List.of())
                .tags(Map.of())
                .build();
    }

    public static String getSubmissionFileResultsJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getSubmissionFileResults(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("submission_file.json");
    }

    public static SubmissionTree getSubmissionTree() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getSubmissionTreeJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return SubmissionTree.builder()
                .classification(TLP_WHITE)
                .filtered(false)
                .partial(false)
                .tree(Map.of(
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36", SubmissionTree.TreeNode.builder()
                                .children(Map.of("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7", SubmissionTree.TreeNode.builder()
                                        .children(Map.of())
                                        .name(List.of("al_test.txt"))
                                        .score(0)
                                        .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7")
                                        .size(6)
                                        .truncated(false)
                                        .type("unknown")
                                        .build()))
                                .name(List.of("al_test.zip"))
                                .score(0)
                                .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                                .size(162)
                                .truncated(false)
                                .type("archive/zip")
                                .build()))
                .build();
    }

    public static String getSubmissionTreeJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getSubmissionTree(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("submission_tree.json");
    }

    public static Submission getSubmission() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getSubmissionJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return Submission.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 24,
                                18, 15, 12, 257136000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .errorCount(0)
                .errors(List.of())
                .fileCount(2)
                .files(List.of(SubmissionBase.File.builder()
                        .name("al_test.zip")
                        .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                        .size(162)
                        .build()))
                .maxScore(0)
                .metadata(Map.of())
                .params(getSubmissionParams())
                .results(List.of(
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaPeek.v4_0_0_stable4.cDyOMFE1phHM.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.ConfigExtractor.v4_0_1_stable15.cDyOMFE1phHM.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.FrankenStrings.v4_0_0_stable3.cDyOMFE1phHM.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.TagCheck.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable5.cDyOMFE1phHM",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Codevector.v4_0_0_stable6.cDyOMFE1phHM.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.VirusTotalCache.v4_0_1_stable7.c1waIr4vQjB5",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.YARA.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaDefender.v4_0_0_stable5.cB4MCKLVFawL.e",
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36.Extract.v4_0_0_stable9.c2i2PqguInqd"
                ))
                .sid("3p9RPMzkoYJ1p4vfdZj6B0")
                .state(Submission.State.COMPLETED)
                .times(Submission.Times.builder()
                        .completed(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 15, 129742000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .submitted(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 12, 257298000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .build())
                .verdict(Submission.Verdict.builder()
                        .malicious(List.of())
                        .nonMalicious(List.of("username"))
                        .build())
                .build();
    }

    private static SubmissionParams getSubmissionParams() {
        return SubmissionParams.builder()
                .classification(TLP_WHITE)
                .deepScan(false)
                .description("Inspection of file: al_test.zip")
                .generateAlert(false)
                .groups(List.of("USERS"))
                .ignoreCache(false)
                .ignoreDynamicRecursionPrevention(false)
                .ignoreFiltering(false)
                .ignoreSize(false)
                .maxExtracted(500)
                .maxSupplementary(500)
                .neverDrop(false)
                .priority(1000)
                .profile(false)
                .quotaItem(true)
                .serviceSpec(Map.of())
                .services(SubmissionParams.ServiceSelection.builder()
                        .excluded(List.of())
                        .resubmit(List.of())
                        .runtimeExcluded(List.of())
                        .selected(List.of(
                                "Static Analysis",
                                "Extraction",
                                "Antivirus",
                                "Networking"
                        ))
                        .build())
                .submitter("username")
                .ttl(0)
                .type("USER")
                .build();
    }

    public static String getSubmissionJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getSubmission(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("submission.json");
    }

    public static SubmissionFull getSubmissionFull() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getSubmissionFullJson(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return SubmissionFull.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 24,
                                18, 15, 12, 257136000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .errorCount(0)
                .errors(Map.of())
                .fileCount(2)
                .fileInfos(Map.of(
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7", getFileInfo(),
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36", FileInfo.builder()
                                .archiveTs(ZonedDateTime
                                        .of(2021, 2, 24,
                                                18, 15, 12, 223811000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .ascii("PK.........ESRV.*.............al_test.txtHello!PK..?........ESRV")
                                .classification(TLP_WHITE)
                                .entropy(4.245672537577632)
                                .hex("504b03040a0300000000bc45535256cc2a9d06000000060000000b000000616c5f746573742e74787448656c6c6f21504b01023f030a0300000000bc45535256")
                                .magic("Zip archive data, at least v1.0 to extract")
                                .md5("012858aaf7c7e26dc010d30508df84ff")
                                .mime("application/zip")
                                .seen(FileInfo.Seen.builder()
                                        .count(1)
                                        .first(ZonedDateTime
                                                .of(2021, 2, 19,
                                                        18, 15, 12, 223832000,
                                                        ZoneOffset.UTC)
                                                .toInstant())
                                        .last(ZonedDateTime
                                                .of(2021, 2, 19,
                                                        18, 15, 12, 223832000,
                                                        ZoneOffset.UTC)
                                                .toInstant())
                                        .build())
                                .sha1("ca5c15bd3d88a7206ba4a1edfe1b42c47e1ff9b7")
                                .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                                .size(162)
                                .ssdeep("3:vh+5Fqllb8+QdwJKEkMvS/lpFqllbQBemQd5llmnTeagj39TeNt+lBlu/:5geW+QdwhgeCEmQdyiaAeP+lC/")
                                .type("archive/zip")
                                .build()
                ))
                .fileTree(getSubmissionTree().getTree())
                .files(List.of(SubmissionBase.File.builder()
                        .name("al_test.zip")
                        .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                        .size(162)
                        .build()))
                .maxScore(0)
                .metadata(Map.of())
                .missingErrorKeys(List.of())
                .missingFileKeys(List.of())
                .missingResultKeys(List.of())
                .params(getSubmissionParams())
                .results(Map.of(
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaPeek.v4_0_0_stable4.cDyOMFE1phHM.e",
                        getMetaPeekResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.ConfigExtractor.v4_0_1_stable15.cDyOMFE1phHM.e",
                        getConfigExtractorResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.FrankenStrings.v4_0_0_stable3.cDyOMFE1phHM.e",
                        getFrankenStringsResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.TagCheck.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        getTagCheckResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable5.cDyOMFE1phHM",
                        getCharacterizeResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Codevector.v4_0_0_stable6.cDyOMFE1phHM.e",
                        getCodeVectorResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.VirusTotalCache.v4_0_1_stable7.c1waIr4vQjB5",
                        getVirusTotalCacheResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.YARA.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        getYaraResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaDefender.v4_0_0_stable5.cB4MCKLVFawL.e",
                        getMetaDefenderResultBlock(),
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36.Extract.v4_0_0_stable9.c2i2PqguInqd",
                        ResultBlock.builder()
                                .archiveTs(ZonedDateTime.
                                        of(2021, 2, 24,
                                                18, 15, 12, 666435000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .classification(TLP_WHITE)
                                .created(ZonedDateTime.
                                        of(2021, 2, 19,
                                                18, 15, 12, 666411000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .dropFile(true)
                                .response(ResultBlock.Response.builder()
                                        .extracted(List.of(ResultBlock.Response.File.builder()
                                                .classification(TLP_WHITE)
                                                .description("zip")
                                                .name("al_test.txt")
                                                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7")
                                                .build()))
                                        .milestones(ResultBlock.Response.Milestones.builder()
                                                .serviceCompleted(ZonedDateTime.
                                                        of(2021, 2, 19,
                                                                18, 15, 12, 598722000,
                                                                ZoneOffset.UTC)
                                                        .toInstant())
                                                .serviceStarted(ZonedDateTime.
                                                        of(2021, 2, 19,
                                                                18, 15, 12, 589947000,
                                                                ZoneOffset.UTC)
                                                        .toInstant())
                                                .build())
                                        .serviceName("Extract")
                                        .serviceVersion("4.0.0.stable9")
                                        .supplementary(List.of())
                                        .build())
                                .result(ResultBlock.Result.builder()
                                        .score(0)
                                        .sections(List.of(ResultBlock.Result.Section.builder()
                                                .bodyFormat(ResultBlock.Result.Section.BodyFormat.TEXT)
                                                .classification(TLP_WHITE)
                                                .depth(0)
                                                .heuristic(ResultBlock.Result.Section.Heuristic.builder()
                                                        .attack(List.of())
                                                        .heurId("EXTRACT.1")
                                                        .name("Extracted from archive")
                                                        .score(0)
                                                        .signature(List.of())
                                                        .build())
                                                .tags(List.of())
                                                .titleText("Successfully extracted 1 files")
                                                .build()))
                                        .build())
                                .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                                .build()
                ))
                .sid("3p9RPMzkoYJ1p4vfdZj6B0")
                .state("completed")
                .times(Submission.Times.builder()
                        .completed(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 15, 129742000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .submitted(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 12, 257298000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .build())
                .verdict(Submission.Verdict.builder()
                        .malicious(List.of())
                        .nonMalicious(List.of("username"))
                        .build())
                .build();
    }

    public static String getSubmissionFullJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getSubmissionFull(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("submission_full.json");
    }

    public static SubmissionFull getSubmissionFullLarge() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getSubmissionFullLargeJson(). However, it should not literally return a deserialized copy of the JSON. The
        result of this method is supposed to be the expected result of deserializing the JSON here would result in
        tautological tests that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return SubmissionFull.builder()
                .archiveTs(ZonedDateTime
                        .of(2021, 2, 24,
                                18, 15, 12, 257136000,
                                ZoneOffset.UTC)
                        .toInstant())
                .classification(TLP_WHITE)
                .errorCount(0)
                .errors(Map.of())
                .fileCount(2)
                .fileInfos(Map.of(
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7", getFileInfo(),
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36", FileInfo.builder()
                                .archiveTs(ZonedDateTime
                                        .of(2021, 2, 24,
                                                18, 15, 12, 223811000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .ascii("PK.........ESRV.*.............al_test.txtHello!PK..?........ESRV")
                                .classification(TLP_WHITE)
                                .entropy(4.245672537577632)
                                .hex("504b03040a0300000000bc45535256cc2a9d06000000060000000b000000616c5f746573742e74787448656c6c6f21504b01023f030a0300000000bc45535256")
                                // This value is over 1MB, so it's a bit cleaner to just store it in a separate file.
                                .magic(readFileIntoString("large_json_value.txt").trim())
                                .md5("012858aaf7c7e26dc010d30508df84ff")
                                .mime("application/zip")
                                .seen(FileInfo.Seen.builder()
                                        .count(1)
                                        .first(ZonedDateTime
                                                .of(2021, 2, 19,
                                                        18, 15, 12, 223832000,
                                                        ZoneOffset.UTC)
                                                .toInstant())
                                        .last(ZonedDateTime
                                                .of(2021, 2, 19,
                                                        18, 15, 12, 223832000,
                                                        ZoneOffset.UTC)
                                                .toInstant())
                                        .build())
                                .sha1("ca5c15bd3d88a7206ba4a1edfe1b42c47e1ff9b7")
                                .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                                .size(162)
                                .ssdeep("3:vh+5Fqllb8+QdwJKEkMvS/lpFqllbQBemQd5llmnTeagj39TeNt+lBlu/:5geW+QdwhgeCEmQdyiaAeP+lC/")
                                .type("archive/zip")
                                .build()
                ))
                .fileTree(getSubmissionTree().getTree())
                .files(List.of(SubmissionBase.File.builder()
                        .name("al_test.zip")
                        .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                        .size(162)
                        .build()))
                .maxScore(0)
                .metadata(Map.of())
                .missingErrorKeys(List.of())
                .missingFileKeys(List.of())
                .missingResultKeys(List.of())
                .params(getSubmissionParams())
                .results(Map.of(
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaPeek.v4_0_0_stable4.cDyOMFE1phHM.e",
                        getMetaPeekResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.ConfigExtractor.v4_0_1_stable15.cDyOMFE1phHM.e",
                        getConfigExtractorResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.FrankenStrings.v4_0_0_stable3.cDyOMFE1phHM.e",
                        getFrankenStringsResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.TagCheck.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        getTagCheckResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Characterize.v4_0_0_stable5.cDyOMFE1phHM",
                        getCharacterizeResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.Codevector.v4_0_0_stable6.cDyOMFE1phHM.e",
                        getCodeVectorResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.VirusTotalCache.v4_0_1_stable7.c1waIr4vQjB5",
                        getVirusTotalCacheResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.YARA.v4_0_0_stable9.c5Mx9otMNWwr.e",
                        getYaraResultBlock(),
                        "334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7.MetaDefender.v4_0_0_stable5.cB4MCKLVFawL.e",
                        getMetaDefenderResultBlock(),
                        "49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36.Extract.v4_0_0_stable9.c2i2PqguInqd",
                        ResultBlock.builder()
                                .archiveTs(ZonedDateTime.
                                        of(2021, 2, 24,
                                                18, 15, 12, 666435000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .classification(TLP_WHITE)
                                .created(ZonedDateTime.
                                        of(2021, 2, 19,
                                                18, 15, 12, 666411000,
                                                ZoneOffset.UTC)
                                        .toInstant())
                                .dropFile(true)
                                .response(ResultBlock.Response.builder()
                                        .extracted(List.of(ResultBlock.Response.File.builder()
                                                .classification(TLP_WHITE)
                                                .description("zip")
                                                .name("al_test.txt")
                                                .sha256("334d016f755cd6dc58c53a86e183882f8ec14f52fb05345887c8a5edd42c87b7")
                                                .build()))
                                        .milestones(ResultBlock.Response.Milestones.builder()
                                                .serviceCompleted(ZonedDateTime.
                                                        of(2021, 2, 19,
                                                                18, 15, 12, 598722000,
                                                                ZoneOffset.UTC)
                                                        .toInstant())
                                                .serviceStarted(ZonedDateTime.
                                                        of(2021, 2, 19,
                                                                18, 15, 12, 589947000,
                                                                ZoneOffset.UTC)
                                                        .toInstant())
                                                .build())
                                        .serviceName("Extract")
                                        .serviceVersion("4.0.0.stable9")
                                        .supplementary(List.of())
                                        .build())
                                .result(ResultBlock.Result.builder()
                                        .score(0)
                                        .sections(List.of(ResultBlock.Result.Section.builder()
                                                .bodyFormat(ResultBlock.Result.Section.BodyFormat.TEXT)
                                                .classification(TLP_WHITE)
                                                .depth(0)
                                                .heuristic(ResultBlock.Result.Section.Heuristic.builder()
                                                        .attack(List.of())
                                                        .heurId("EXTRACT.1")
                                                        .name("Extracted from archive")
                                                        .score(0)
                                                        .signature(List.of())
                                                        .build())
                                                .tags(List.of())
                                                .titleText("Successfully extracted 1 files")
                                                .build()))
                                        .build())
                                .sha256("49a41506349514a98c6cbb040224a8c91ed40b2cd11af570ec672df6b1d7bd36")
                                .build()
                ))
                .sid("3p9RPMzkoYJ1p4vfdZj6B0")
                .state("completed")
                .times(Submission.Times.builder()
                        .completed(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 15, 129742000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .submitted(ZonedDateTime
                                .of(2021, 2, 19,
                                        18, 15, 12, 257298000,
                                        ZoneOffset.UTC)
                                .toInstant())
                        .build())
                .verdict(Submission.Verdict.builder()
                        .malicious(List.of())
                        .nonMalicious(List.of("username"))
                        .build())
                .build();
    }

    public static String getSubmissionFullLargeJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getSubmissionFullLarge(). However, it should not literally return a serialized copy of the object. The result
        of this method is supposed to represent real output from Assemblyline that will be input for a deserializer
        whose deserialization we are testing.*/
        return readFileIntoString("submission_full_large.json");
    }

    public static String getIngestResponseJson() {
        return readFileIntoString("ingest_response.json");
    }

    public static IngestResponse getIngestResponse() {
        return IngestResponse.builder().ingestId("sid").build();
    }

    public static String getIsSubmissionCompleteResponseJson() {
        return readFileIntoString("is_submission_complete.json");
    }
    public static boolean getIsSubmissionCompleteResponse() {
        return true;
    }

    public static IngestSubmissionResponse getIngestMessageList() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getIngestMessageList(). However, it should not literally return a deserialized copy of the JSON. The result of this
        method is supposed to be the expected result of deserializing the JSON here would result in tautological tests
        that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return IngestSubmissionResponse.builder()
                .extendedScan(IngestSubmissionResponse.ExtendedScan.SKIPPED)
                .failure("")
                .ingestId("4gI9ndesxMbQVRa1OWB5CJ")
                .ingestTime(ZonedDateTime.of(2021, 3,
                        4, 21, 23, 0, 358435000,
                        ZoneOffset.UTC)
                        .toInstant())
                .retries(0)
                .scanKey("f985350201a0ad7ffec89c171140d0c6v1")
                .score(0)
                .submission(
                        IngestSubmissionResponse.IngestSubmission
                                .builder()
                                .files(
                                        List.of(SubmissionBase.File.builder()
                                                .name("test")
                                                .sha256("9282b318f39312b66981e7cf46d9542c6c706a7a7fd1126b11598f2979bfe5bc")
                                                .size(19)
                                                .build()))
                                .metadata(Map.of(
                                        "ingest_id", "4gI9ndesxMbQVRa1OWB5CJ",
                                        "ts", "2021-03-04T21:23:00.354786Z",
                                        "type", "INGEST"
                                ))
                                .notification(
                                        IngestSubmissionResponse.IngestSubmission.Notification
                                                .builder()
                                                .queue("test_java_client")
                                                .build()
                                )
                                .params(getSubmissionParams())
                                .sid("1BUDjiaJ6aly2lRTSt6nhw")
                                .time(ZonedDateTime.of(2021, 3, 4,
                                        21, 23, 0, 354853000,
                                        ZoneOffset.UTC)
                                        .toInstant())
                                .build())
                .build();
    }

    public static String getIngestMessageListJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getIngestMessageList(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("ingest_get_message_list.json");
    }

    public static Buffer getDownloadFileBuffer() {
        try (InputStream inputStream = MockResponseModels.class.getResourceAsStream("/MockResponseModels/al_test.txt.cart");
             Source source = Okio.source(inputStream)) {

            Buffer buffer = new Buffer();
            buffer.writeAll(source);
            return buffer;
        } catch (IOException ioe) {
            /* fail() always throws an exception and never actually returns, but this needs to be a return statement to
            appease the compiler. */
            return Assertions.fail("Failed to load mock response data from disk.", ioe);
        }
    }

    public static byte[] getDownloadFileBytes() {
        /* Bytes of al_test.txt.cart. It isn't necessary to cast every hex literal to byte, but it was easier to just
        add the byte cast to every literal with a find-replace than to go through and only cast where necessary. */
        return new byte[]{
                (byte) 0x43, (byte) 0x41, (byte) 0x52, (byte) 0x54, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01,
                (byte) 0x04, (byte) 0x01, (byte) 0x05, (byte) 0x09, (byte) 0x02, (byte) 0x06, (byte) 0x03, (byte) 0x01, (byte) 0x04, (byte) 0x01, (byte) 0x05, (byte) 0x09, (byte) 0x02, (byte) 0x06, (byte) 0x68, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc2, (byte) 0xa4, (byte) 0xa8, (byte) 0x51, (byte) 0x5f, (byte) 0xc3, (byte) 0x12, (byte) 0xa4, (byte) 0x3d, (byte) 0x7c,
                (byte) 0x35, (byte) 0xc5, (byte) 0x75, (byte) 0x5a, (byte) 0x8f, (byte) 0xcb, (byte) 0x9d, (byte) 0xdc, (byte) 0x8b, (byte) 0x69, (byte) 0xde, (byte) 0x1c, (byte) 0x46, (byte) 0x40, (byte) 0x02, (byte) 0x33,
                (byte) 0x3a, (byte) 0xf3, (byte) 0xa2, (byte) 0xf4, (byte) 0x74, (byte) 0x2d, (byte) 0x71, (byte) 0x77, (byte) 0x57, (byte) 0xd6, (byte) 0x29, (byte) 0x76, (byte) 0x34, (byte) 0xbf, (byte) 0x1d, (byte) 0x41,
                (byte) 0x39, (byte) 0x2b, (byte) 0x8f, (byte) 0xd0, (byte) 0xa7, (byte) 0x83, (byte) 0x56, (byte) 0x45, (byte) 0x09, (byte) 0x10, (byte) 0xbc, (byte) 0x72, (byte) 0x24, (byte) 0xc0, (byte) 0xf5, (byte) 0x5a,
                (byte) 0x5b, (byte) 0x78, (byte) 0xf6, (byte) 0x43, (byte) 0xb7, (byte) 0x16, (byte) 0xda, (byte) 0x5a, (byte) 0x89, (byte) 0xdc, (byte) 0xee, (byte) 0xa6, (byte) 0x44, (byte) 0xa8, (byte) 0xf4, (byte) 0xbd,
                (byte) 0x8b, (byte) 0xe1, (byte) 0x5e, (byte) 0x52, (byte) 0x2d, (byte) 0xb2, (byte) 0x8d, (byte) 0x62, (byte) 0x66, (byte) 0x5e, (byte) 0x36, (byte) 0x80, (byte) 0x62, (byte) 0xeb, (byte) 0x98, (byte) 0x79,
                (byte) 0xc9, (byte) 0xbe, (byte) 0x8d, (byte) 0x7d, (byte) 0xf4, (byte) 0x84, (byte) 0x57, (byte) 0x15, (byte) 0x06, (byte) 0x11, (byte) 0x0e, (byte) 0x86, (byte) 0xf8, (byte) 0x17, (byte) 0xc1, (byte) 0x87,
                (byte) 0x38, (byte) 0x75, (byte) 0xf3, (byte) 0x79, (byte) 0xa8, (byte) 0x9a, (byte) 0x5f, (byte) 0x15, (byte) 0x51, (byte) 0x06, (byte) 0x03, (byte) 0x25, (byte) 0xc2, (byte) 0xa4, (byte) 0xa7, (byte) 0x58,
                (byte) 0x50, (byte) 0xd7, (byte) 0x15, (byte) 0xa5, (byte) 0x79, (byte) 0x2f, (byte) 0x74, (byte) 0x92, (byte) 0x23, (byte) 0x1f, (byte) 0xc2, (byte) 0xc8, (byte) 0xdb, (byte) 0xd3, (byte) 0x8b, (byte) 0x07,
                (byte) 0xb0, (byte) 0x75, (byte) 0x49, (byte) 0x25, (byte) 0x2e, (byte) 0x48, (byte) 0x0d, (byte) 0x83, (byte) 0xb6, (byte) 0xbc, (byte) 0x66, (byte) 0x77, (byte) 0x28, (byte) 0x2f, (byte) 0x0b, (byte) 0xc1,
                (byte) 0x2b, (byte) 0x67, (byte) 0x34, (byte) 0xba, (byte) 0x1e, (byte) 0x11, (byte) 0x3e, (byte) 0x78, (byte) 0xda, (byte) 0xd2, (byte) 0xf4, (byte) 0x8f, (byte) 0x5b, (byte) 0x13, (byte) 0x54, (byte) 0x16,
                (byte) 0xbc, (byte) 0x33, (byte) 0x3d, (byte) 0xda, (byte) 0xe5, (byte) 0x07, (byte) 0x09, (byte) 0x28, (byte) 0xec, (byte) 0x4f, (byte) 0xf0, (byte) 0x11, (byte) 0xdb, (byte) 0x5a, (byte) 0x85, (byte) 0xd6,
                (byte) 0xbf, (byte) 0xf5, (byte) 0x1f, (byte) 0xfe, (byte) 0xae, (byte) 0xe9, (byte) 0x8a, (byte) 0xe6, (byte) 0x0e, (byte) 0x55, (byte) 0x2d, (byte) 0xe0, (byte) 0x8d, (byte) 0x60, (byte) 0x62, (byte) 0x0b,
                (byte) 0x66, (byte) 0xdb, (byte) 0x39, (byte) 0xef, (byte) 0xc9, (byte) 0x22, (byte) 0x98, (byte) 0xed, (byte) 0xd0, (byte) 0x28, (byte) 0xa8, (byte) 0x81, (byte) 0x54, (byte) 0x10, (byte) 0x5c, (byte) 0x1e,
                (byte) 0x5d, (byte) 0x88, (byte) 0xe9, (byte) 0x48, (byte) 0xee, (byte) 0x52, (byte) 0x1c, (byte) 0xec, (byte) 0xf4, (byte) 0x2e, (byte) 0x6b, (byte) 0x79, (byte) 0x0a, (byte) 0x97, (byte) 0x0b, (byte) 0x73,
                (byte) 0xb5, (byte) 0xc0, (byte) 0x22, (byte) 0x72, (byte) 0x8a, (byte) 0x2f, (byte) 0x16, (byte) 0xa6, (byte) 0xe7, (byte) 0x12, (byte) 0x05, (byte) 0xcd, (byte) 0x84, (byte) 0xdf, (byte) 0x81, (byte) 0x8d,
                (byte) 0x03, (byte) 0xb2, (byte) 0x64, (byte) 0xf8, (byte) 0x3a, (byte) 0xea, (byte) 0x26, (byte) 0x66, (byte) 0xb2, (byte) 0xcc, (byte) 0xe0, (byte) 0x94, (byte) 0x68, (byte) 0x21, (byte) 0xdb, (byte) 0x8e,
                (byte) 0xe4, (byte) 0xcc, (byte) 0xa8, (byte) 0x91, (byte) 0x26, (byte) 0x8d, (byte) 0xe8, (byte) 0x08, (byte) 0x73, (byte) 0x59, (byte) 0x49, (byte) 0xda, (byte) 0x87, (byte) 0xb0, (byte) 0x3e, (byte) 0xfc,
                (byte) 0x61, (byte) 0x26, (byte) 0x5f, (byte) 0x78, (byte) 0xa7, (byte) 0x94, (byte) 0xa6, (byte) 0x91, (byte) 0x30, (byte) 0xb5, (byte) 0x66, (byte) 0xdf, (byte) 0x82, (byte) 0x47, (byte) 0x31, (byte) 0x58,
                (byte) 0xd1, (byte) 0x54, (byte) 0x52, (byte) 0x41, (byte) 0x43, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x9c, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xb5, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
    }

    public static List<String> getHashSearchDataSources() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getHashSearchDataSourcesJson(). However, it should not literally return a deserialized copy of the JSON. The
        result of this method is supposed to be the expected result of deserializing the JSON here would result in
        tautological tests that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return List.of(
                "al",
                "alert"
        );
    }

    public static String getHashSearchDataSourcesJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getHashSearchDataSources(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("hash_search_list_data_sources.json");
    }

    public static Map<String, HashSearchResult> getHashSearch() {
        /* The object returned by this method must be equivalent to a deserialized copy of the JSON returned by
        getHashSearchJson(). However, it should not literally return a deserialized copy of the JSON. The
        result of this method is supposed to be the expected result of deserializing the JSON here would result in
        tautological tests that amount to assertEquals(json.deserialize(), json.deserialize()).*/
        return Map.of(
                "al", HashSearchResult.builder()
                        .items(List.of())
                        .build(),
                "alert", HashSearchResult.builder()
                        .items(List.of())
                        .build()
        );
    }

    public static String getHashSearchJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned by
        getHashSearch(). However, it should not literally return a serialized copy of the object. The result of this
        method is supposed to represent real output from Assemblyline that will be input for a deserializer whose
        deserialization we are testing.*/
        return readFileIntoString("hash_search.json");
    }

    public static String getInternalErrorJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned when
        Assemblyline returns an internal error. However, it should not literally return a serialized copy of the object.
        The result of this method is supposed to represent real output from Assemblyline that will be input for a
        deserializer whose deserialization we are testing.*/
        return readFileIntoString("internal_error.json");
    }

    public static String getBadRequestJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned when
        Assemblyline returns an 400 error. However, it should not literally return a serialized copy of the object.
        The result of this method is supposed to represent real output from Assemblyline that will be input for a
        deserializer whose deserialization we are testing.*/
        return readFileIntoString("bad_request.json");
    }

    public static String notLoggedInJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned when
        Assemblyline returns an 401 error for a user that has not logged in yet. However, it should not literally
        return a serialized copy of the object. The result of this method is supposed to represent real output from
        Assemblyline that will be input for a deserializer whose deserialization we are testing.*/
        return readFileIntoString("not_logged_in.json");
    }

    public static String invalidApiKeyJson() {
        /* The object returned by this method must be equivalent to a serialized copy of the object returned when
        Assemblyline returns an 401 error for an invalid API key. However, it should not literally return a serialized
        copy of the object. The result of this method is supposed to represent real output from Assemblyline that will
        be input for a deserializer whose deserialization we are testing.*/
        return readFileIntoString("invalid_apikey.json");
    }

}
