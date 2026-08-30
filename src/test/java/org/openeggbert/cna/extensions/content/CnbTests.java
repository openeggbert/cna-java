package org.openeggbert.cna.extensions.content;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.GraphicsDeviceManager;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code .cnb} container, against the live runtime.
 *
 * <p>Every fixture here is produced by <strong>CNA's own encoder and writer</strong>, and read
 * back by CNA's own parser. Hand-writing the bytes would only establish that a hand-written
 * writer and a hand-written reader share the same misunderstanding; encoding with the format's
 * own writer is what makes a successful read mean the file is a real one.
 *
 * <p>And the assertions are about payload, not about result codes: the pixels that come back are
 * compared to the pixels that went in, the metadata names are compared to the names that were
 * set, and the checksum is compared to the one CNA's portable path computes.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class CnbTests {

    /** Four pixels, each a different colour, so a channel swap or a stride error cannot hide. */
    private static final byte[] PIXELS = {
        (byte) 0xFF, 0x00, 0x00, (byte) 0xFF,
        0x00, (byte) 0xFF, 0x00, (byte) 0x80,
        0x00, 0x00, (byte) 0xFF, (byte) 0xFF,
        (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
    };

    @Test
    void theContainerAnswersForItself() {
        byte[] magic = CnbDocument.magic();
        assertEquals(4, magic.length);
        assertTrue(CnbDocument.hasMagic(magic));
        assertFalse(CnbDocument.hasMagic(new byte[] {'X', 'N', 'B', 'w'}),
                "an .xnb is not a .cnb, and the guard has to say so");
        assertFalse(CnbDocument.hasMagic(new byte[0]));

        CnbChunkId identifier = CnbChunkId.of("tEx2");
        assertEquals("tEx2", identifier.toString());
        assertTrue(identifier.isWellFormed());
        assertEquals("CMET", CnbChunkId.METADATA.toString());
        assertEquals("XREF", CnbChunkId.EXTERNAL_REFERENCES.toString());
        // Every byte must be printable ASCII, so a control byte is not an identifier.
        assertFalse(new CnbChunkId(0x00000001).isWellFormed());
        assertThrows(IllegalArgumentException.class, () -> CnbChunkId.of("too long"));

        assertFalse(CnbAssetType.TEXTURE_2D.isCustom());
        assertFalse(CnbAssetType.TEXTURE_2D.getName().isEmpty());
        assertTrue(new CnbAssetType(0x80000001).isCustom(),
                "the custom range belongs to a game's own schemas");
        // A custom identifier is minted by hashing the name, not looked up, so the same name
        // always mints the same identifier and it always lands in the custom range.
        CnbAssetType minted = CnbAssetType.custom("MyGame.Level");
        assertEquals(minted, CnbAssetType.custom("MyGame.Level"));
        assertTrue(minted.isCustom());
        assertNotEquals(minted, CnbAssetType.custom("MyGame.Other"));
        assertThrows(RuntimeException.class, () -> CnbAssetType.custom(""));

        assertTrue(CnbCompression.None.isSupported(), "storing uncompressed always works");
        assertEquals("none", CnbCompression.None.getName(),
                "the name is CNA's own spelling, not the Java constant's");
        for (CnbCompression codec : CnbCompression.values()) {
            assertFalse(codec.getName().isEmpty());
        }
    }

    @Test
    void checksumsAreCnasOwnAndAgreeWithThemselves() {
        // CRC-32C, not java.util.zip's CRC-32: a different polynomial and a different answer.
        // The hardware and portable paths must agree, or one of them is broken.
        assertEquals(CnbChecksum.portable(PIXELS), CnbChecksum.of(PIXELS));
        assertNotEquals(CnbChecksum.of(PIXELS), CnbChecksum.of(new byte[] {1, 2, 3, 4}));
        assertEquals(CnbChecksum.of(new byte[0]), CnbChecksum.of(new byte[0]));
        // Summing in two blocks must equal summing in one.
        long first = CnbChecksum.of(new byte[] {PIXELS[0], PIXELS[1]});
        byte[] rest = new byte[PIXELS.length - 2];
        System.arraycopy(PIXELS, 2, rest, 0, rest.length);
        assertEquals(CnbChecksum.of(PIXELS), CnbChecksum.continued(first, rest));
        assertEquals(CnbChecksum.usesHardware(), CnbChecksum.usesHardware());
    }

    @Test
    void limitsAndCheckedArithmeticRefuseWhatTheyMust() {
        CnbReadLimits limits = CnbReadLimits.standard();
        assertTrue(limits.MaxFileSize() > 0);
        assertTrue(limits.MaxChunkSize() > 0);
        assertTrue(limits.MaxChunkCount() > 0);

        assertEquals(3L, CnbFormat.checkedAdd(1L, 2L));
        assertEquals(6L, CnbFormat.checkedMultiply(2L, 3L));
        // A malformed file's offset plus size is exactly where an unchecked add wraps.
        assertThrows(RuntimeException.class, () -> CnbFormat.checkedAdd(-1L, 2L));
        assertThrows(RuntimeException.class, () -> CnbFormat.checkedMultiply(-1L, 4L));

        assertTrue(CnbFormat.isWellFormedUtf8("ok".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertTrue(CnbFormat.isWellFormedUtf8("日本".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertFalse(CnbFormat.isWellFormedUtf8(new byte[] {(byte) 0xC3}),
                "a truncated multi-byte sequence is not well-formed UTF-8");
        assertEquals("", CnbFormat.logicalNameProblem("textures/hero"));
        assertFalse(CnbFormat.logicalNameProblem("../escape").isEmpty(),
                "a name that escapes the content root has to be rejected with a reason");
    }

    @Test
    void textureFormatsMeasureThemselves() {
        assertEquals(4, CnbTextureFormat.Rgba8.getUnitBytes());
        assertFalse(CnbTextureFormat.Rgba8.isBlockCompressed());
        assertTrue(CnbTextureFormat.Bc1.isBlockCompressed());
        assertEquals(16L, CnbTextureFormat.Rgba8.getLevelByteSize(2, 2, 1),
                "four RGBA pixels are sixteen bytes");
        // A block-compressed level is measured in four-by-four blocks, so a two-by-two level is
        // still one whole block. Measuring it as width times height would be wrong.
        assertEquals(8L, CnbTextureFormat.Bc1.getLevelByteSize(2, 2, 1));
        assertEquals(SurfaceFormat.Color, CnbTextureFormat.Rgba8.toSurfaceFormat());
        assertEquals(CnbTextureFormat.Rgba8,
                CnbTextureFormat.fromSurfaceFormat(SurfaceFormat.Color));
        assertTrue(CnbTextureFormat.isKnown(CnbTextureFormat.Bc7.ordinal()));
        assertFalse(CnbTextureFormat.isKnown(9999));
        assertFalse(CnbTextureFormat.Bc7Srgb.getName().isEmpty());
    }

    @Test
    void aTextureRoundTripsThroughCnasOwnEncoderAndParser() throws Exception {
        byte[] file;
        try (CnbTextureData source = CnbTextureData.ofRgba8(2, 2, PIXELS)) {
            CnbTextureInfo info = source.getInfo();
            assertEquals(2, info.Width());
            assertEquals(2, info.Height());
            assertEquals(1, info.Depth());
            assertEquals(1, info.FaceCount());
            assertEquals(1, info.MipCount());
            assertEquals(1, info.RepresentationCount());
            assertEquals(CnbTextureFormat.Rgba8, source.getRepresentationFormat(0));
            assertArrayEquals(PIXELS, source.readLevel(0, 0),
                    "the pixels a texture was built from must read back unchanged");
            file = Cnb.encodeTexture2D(source, "textures/four-pixels");
        }

        assertTrue(CnbDocument.hasMagic(file), "CNA's encoder writes a real .cnb");

        try (CnbDocument document = CnbDocument.parse(
                file, "four-pixels.cnb", CnbReadLimits.standard())) {
            assertEquals("four-pixels.cnb", document.getOrigin());
            assertEquals(CnbAssetType.TEXTURE_2D.Id(), document.getAssetType().Id());
            assertEquals(1, document.getContainerMajor());
            document.requireAsset(CnbAssetType.TEXTURE_2D, document.getAssetSchemaVersion());
            // A texture loader handed something else must say so rather than misread it.
            assertThrows(RuntimeException.class,
                    () -> document.requireAsset(CnbAssetType.MODEL, 99));

            CnbMetadata metadata = document.getMetadata();
            assertTrue(metadata.Present());
            assertEquals("textures/four-pixels", metadata.ContentName(),
                    "the content name written must be the content name read");
            assertFalse(metadata.AssetTypeName().isEmpty());

            List<CnbChunk> chunks = document.getChunks();
            assertFalse(chunks.isEmpty());
            for (CnbChunk chunk : chunks) {
                assertTrue(chunk.Id().isWellFormed());
                assertTrue(chunk.StoredSize() > 0 || chunk.UncompressedSize() == 0);
                // Every chunk carries the CRC-32C of what is stored, and reading it back and
                // summing it has to produce the number the table of contents recorded.
                if (chunk.Compression() == CnbCompression.None) {
                    assertEquals(chunk.Checksum(),
                            CnbChecksum.of(document.readChunk(chunks.indexOf(chunk))),
                            "chunk " + chunk.Id() + " does not match its recorded checksum");
                }
            }
            int metadataIndex = document.requireSingle(CnbChunkId.METADATA);
            assertTrue(metadataIndex >= 0);
            assertEquals(List.of(metadataIndex), document.findAll(CnbChunkId.METADATA));
            assertEquals(-1, document.findSingle(CnbChunkId.of("zzzz")),
                    "a chunk the file has none of is absent, not an error");
            // A mandatory chunk the reader does not know must stop the load.
            assertThrows(RuntimeException.class, () ->
                    document.requireMandatoryChunksUnderstood(List.of(CnbChunkId.of("zzzz"))));

            try (CnbTextureData decoded = document.decodeTexture2D()) {
                assertEquals(2, decoded.getInfo().Width());
                assertEquals(2, decoded.getInfo().Height());
                assertArrayEquals(PIXELS, decoded.readLevel(0, 0),
                        "the pixels that came out are the pixels that went in");
            }
        }

        // The same bytes from disk, through the file parser rather than the memory one.
        Path directory = Files.createTempDirectory("cna-java-cnb");
        Path path = directory.resolve("four-pixels.cnb");
        Files.write(path, file);
        try (CnbDocument document = CnbDocument.parse(path, CnbReadLimits.standard());
             CnbTextureData decoded = document.decodeTexture2D()) {
            assertArrayEquals(PIXELS, decoded.readLevel(0, 0));
        } finally {
            Files.deleteIfExists(path);
            Files.deleteIfExists(directory);
        }
    }

    @Test
    void aWrittenFileCarriesTheChunksAndStringsItWasGiven() throws Exception {
        byte[] payload = "a game's own schema payload".getBytes(
                java.nio.charset.StandardCharsets.UTF_8);
        byte[] file;
        CnbAssetType level = CnbAssetType.custom("MyGame.Content.Level");
        try (CnbWriter writer = CnbWriter.of(level, 3)) {
            writer.setMetadata("MyGame.Content.Level", "levels/one");
            writer.addChunk(CnbChunkId.of("lvl1"), payload, true);
            assertEquals(1, writer.getChunkCount());
            // Alignment is a power of two from one through 4096, and anything else is the
            // caller's mistake rather than a file CNA will write.
            assertThrows(IllegalArgumentException.class,
                    () -> writer.addChunk(CnbChunkId.of("bad0"), payload, false, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> writer.addChunk(CnbChunkId.of("bad3"), payload, false, 3));
            assertThrows(IllegalArgumentException.class,
                    () -> writer.addChunk(CnbChunkId.of("bad9"), payload, false, 8192));
            writer.setCompression(CnbCompression.None, 0);
            assertEquals(CnbReadLimits.standard().MaxChunkCount(),
                    writer.getLimits().MaxChunkCount());
            file = writer.build();
        }

        try (CnbDocument document = CnbDocument.parse(
                file, "level.cnb", CnbReadLimits.standard())) {
            assertEquals(level, document.getAssetType());
            assertTrue(document.getAssetType().isCustom());
            assertEquals(3, document.getAssetSchemaVersion());
            assertEquals("MyGame.Content.Level", document.getMetadata().AssetTypeName());
            assertEquals("levels/one", document.getMetadata().ContentName());

            int index = document.requireSingle(CnbChunkId.of("lvl1"));
            assertTrue(document.getChunks().get(index).Mandatory(),
                    "a chunk written as mandatory must read back as mandatory");
            assertArrayEquals(payload, document.readChunk(index),
                    "the chunk bytes written must be the chunk bytes read");

            // The checked cursor over the same chunk sees the same bytes.
            try (CnbReader reader = document.openChunk(index)) {
                assertEquals(payload.length, reader.getSize());
                assertEquals(payload.length, reader.getRemaining());
                assertEquals(0, reader.getPosition());
                assertArrayEquals(payload, reader.readBytes(payload.length));
                assertEquals(0, reader.getRemaining());
                reader.requireExhausted();
                // Reading past the end is refused rather than returning zeros.
                assertThrows(RuntimeException.class, () -> reader.readUnsignedByte());
            }
        }
    }

    @Test
    void aCheckedReaderRefusesWhatAMalformedFileWouldClaim() {
        CnbReadLimits limits = CnbReadLimits.standard();
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8};
        try (CnbReader reader = CnbReader.of(data, "a probe", limits)) {
            assertEquals("a probe", reader.getContext());
            assertEquals(8, reader.getSize());
            assertEquals(1, reader.readUnsignedByte());
            assertEquals(0x0302, reader.readUnsignedShort(), "little-endian, as the format is");
            assertEquals(5, reader.getRemaining());
            reader.skip(1);
            assertEquals(4, reader.getRemaining());
            assertEquals(4, reader.getPosition());

            // Reading past the end is refused, and a refused read must not move the cursor:
            // a schema that catches the failure and carries on has to see the same bytes.
            assertThrows(RuntimeException.class, () -> reader.readBytes(64));
            assertEquals(4, reader.getRemaining(),
                    "a refused read leaves the cursor where it was");
            assertThrows(RuntimeException.class, reader::requireExhausted);

            assertArrayEquals(new byte[] {5, 6, 7, 8}, reader.readBytes(4));
            assertEquals(0, reader.getRemaining());
            reader.requireExhausted();
        }

        // A count whose elements cannot fit in what remains is refused before anything is
        // allocated for it, which is the reason to read counts this way rather than as an int.
        byte[] hugeCount = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F, 0, 0, 0, 0};
        try (CnbReader reader = CnbReader.of(hugeCount, "a hostile count", limits)) {
            assertThrows(RuntimeException.class, () -> reader.readCount(1024, "vertices"));
        }
        // The same count with no element size skips the fit check, because the elements are
        // variable-length and nothing can be concluded from the remaining bytes.
        try (CnbReader reader = CnbReader.of(hugeCount, "a variable-length count", limits)) {
            assertThrows(RuntimeException.class, () -> reader.readCount(0, "strings"),
                    "the count is still checked against the limits, which is what stops it");
        }
        assertThrows(NullPointerException.class, () -> CnbReader.of(null, "x", limits));
    }

    @Test
    void aDecodedTextureBecomesAnXnaTexture() {
        try (TextureProbe probe = new TextureProbe()) {
            probe.RunOneFrame();
            if (probe.failure != null) {
                if (probe.failure instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new IllegalStateException(probe.failure);
            }
            assertTrue(probe.ran, "the probe must have run");
        }
    }

    private static final class TextureProbe extends Game {

        private boolean ran;
        private Throwable failure;

        private TextureProbe() {
            new GraphicsDeviceManager(this);
        }

        @Override
        protected void Update(GameTime gameTime) {
            super.Update(gameTime);
            if (ran) {
                return;
            }
            ran = true;
            try {
                byte[] file;
                try (CnbTextureData source = CnbTextureData.ofRgba8(2, 2, PIXELS)) {
                    file = Cnb.encodeTexture2D(source, "textures/four-pixels");
                }
                try (CnbDocument document = CnbDocument.parse(
                             file, "four-pixels.cnb", CnbReadLimits.standard());
                     CnbTextureData decoded = document.decodeTexture2D()) {
                    Texture2D texture = decoded.toTexture2D(getGraphicsDevice());
                    try {
                        // The whole point of the slice: a .cnb file becomes a texture a game
                        // draws with, and the pixels survive the trip.
                        assertEquals(2, texture.getWidth());
                        assertEquals(2, texture.getHeight());
                        assertEquals(SurfaceFormat.Color, texture.getFormat());
                        Color[] pixels = new Color[4];
                        texture.GetData(pixels);
                        assertEquals(new Color(255, 0, 0, 255), pixels[0]);
                        assertEquals(new Color(0, 255, 0, 128), pixels[1]);
                        assertEquals(new Color(0, 0, 255, 255), pixels[2]);
                        assertEquals(new Color(0x12, 0x34, 0x56, 0x78), pixels[3]);
                    } finally {
                        texture.close();
                    }
                }
            } catch (Throwable exception) {
                failure = exception;
            }
        }
    }
}
