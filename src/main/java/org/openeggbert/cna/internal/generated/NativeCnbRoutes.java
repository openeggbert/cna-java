package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeCnbRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeCnbRoutes {

    private NativeCnbRoutes() {
    }

    /**
     * cna_cnb_asset_type_id_from_name (cnb.h).
     */
    public static native int cnbAssetTypeIdFromName(byte[] name, int[] outAssetTypeId);

    /**
     * cna_cnb_audio_frame_bytes (cnb.h).
     */
    public static native int cnbAudioFrameBytes(int format, int channels, int[] outByteCount);

    /**
     * cna_cnb_checked_add (cnb.h).
     */
    public static native int cnbCheckedAdd(long a, long b, long[] outSum);

    /**
     * cna_cnb_checked_multiply (cnb.h).
     */
    public static native int cnbCheckedMultiply(long a, long b, long[] outProduct);

    /**
     * cna_cnb_chunk_entry_is_mandatory (cnb.h).
     *
     * <p>entryIntegral carries CNA_CnbChunkEntry in this order:
     * <ol start="0">
     *   <li>{@code offset} (uint64_t)</li>
     *   <li>{@code stored_size} (uint64_t)</li>
     *   <li>{@code uncompressed_size} (uint64_t)</li>
     *   <li>{@code type} (CNA_CnbChunkId)</li>
     *   <li>{@code flags} (uint32_t)</li>
     *   <li>{@code checksum} (uint32_t)</li>
     *   <li>{@code compression} (CNA_CnbCompression)</li>
     *   <li>{@code alignment} (uint32_t)</li>
     *   <li>{@code reserved} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbChunkEntryIsMandatory(long[] entryIntegral, boolean[] outMandatory);

    /**
     * cna_cnb_copy_asset_type_name (cnb.h).
     */
    public static native int cnbCopyAssetTypeName(int assetTypeId, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_audio_format_name (cnb.h).
     */
    public static native int cnbCopyAudioFormatName(int format, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_chunk_id_string (cnb.h).
     */
    public static native int cnbCopyChunkIdString(int id, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_compressed (cnb.h).
     */
    public static native int cnbCopyCompressed(byte[] raw, int codec, int level, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_compression_name (cnb.h).
     */
    public static native int cnbCopyCompressionName(int codec, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_decompressed (cnb.h).
     */
    public static native int cnbCopyDecompressed(byte[] stored, int codec, long uncompressedSize, long maxUncompressedSize, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_format_magic (cnb.h).
     */
    public static native int cnbCopyFormatMagic(byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_logical_name_problem (cnb.h).
     */
    public static native int cnbCopyLogicalNameProblem(byte[] logicalName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_copy_texture_format_name (cnb.h).
     */
    public static native int cnbCopyTextureFormatName(int format, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_crc32c (cnb.h).
     */
    public static native int cnbCrc32c(byte[] data, int[] outChecksum);

    /**
     * cna_cnb_crc32c_continue (cnb.h).
     */
    public static native int cnbCrc32cContinue(int previous, byte[] data, int[] outChecksum);

    /**
     * cna_cnb_crc32c_portable (cnb.h).
     */
    public static native int cnbCrc32cPortable(byte[] data, int[] outChecksum);

    /**
     * cna_cnb_crc32c_uses_hardware (cnb.h).
     */
    public static native int cnbCrc32cUsesHardware(boolean[] outUsesHardware);

    /**
     * cna_cnb_decode_song_duration_milliseconds (cnb.h).
     */
    public static native int cnbDecodeSongDurationMilliseconds(long document, int[] outDurationMilliseconds);

    /**
     * cna_cnb_decode_song_name (cnb.h).
     */
    public static native int cnbDecodeSongName(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_decode_song_name_size (cnb.h).
     */
    public static native int cnbDecodeSongNameSize(long document, long[] outByteCount);

    /**
     * cna_cnb_decode_song_stream_reference (cnb.h).
     */
    public static native int cnbDecodeSongStreamReference(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_decode_song_stream_reference_size (cnb.h).
     */
    public static native int cnbDecodeSongStreamReferenceSize(long document, long[] outByteCount);

    /**
     * cna_cnb_decode_sound_effect (cnb.h).
     */
    public static native int cnbDecodeSoundEffect(long document, long[] outSound);

    /**
     * cna_cnb_decode_sprite_font (cnb.h).
     */
    public static native int cnbDecodeSpriteFont(long document, long[] outFont);

    /**
     * cna_cnb_decode_texture2d (cnb.h).
     */
    public static native int cnbDecodeTexture2d(long document, long[] outTexture);

    /**
     * cna_cnb_decode_video (cnb.h).
     *
     * <p>outInfoIntegral carries CNA_CnbVideoInfo in this order:
     * <ol start="0">
     *   <li>{@code duration_milliseconds} (uint32_t)</li>
     *   <li>{@code width} (uint32_t)</li>
     *   <li>{@code height} (uint32_t)</li>
     *   <li>{@code soundtrack_type} (CNA_VideoSoundtrackType)</li>
     *   <li>{@code reserved} (uint32_t)</li>
     * </ol>
     *
     * <p>outInfoFloating carries CNA_CnbVideoInfo in this order:
     * <ol start="0">
     *   <li>{@code frames_per_second} (float)</li>
     * </ol>
     */
    public static native int cnbDecodeVideo(long document, long[] outInfoIntegral, float[] outInfoFloating);

    /**
     * cna_cnb_decode_video_stream_reference (cnb.h).
     */
    public static native int cnbDecodeVideoStreamReference(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_decode_video_stream_reference_size (cnb.h).
     */
    public static native int cnbDecodeVideoStreamReferenceSize(long document, long[] outByteCount);

    /**
     * cna_cnb_document_copy_chunk_data (cnb.h).
     */
    public static native int cnbDocumentCopyChunkData(long document, long index, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_document_copy_external_reference_name (cnb.h).
     */
    public static native int cnbDocumentCopyExternalReferenceName(long document, long index, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_document_copy_metadata_asset_type_name (cnb.h).
     */
    public static native int cnbDocumentCopyMetadataAssetTypeName(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_document_copy_metadata_content_name (cnb.h).
     */
    public static native int cnbDocumentCopyMetadataContentName(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_document_copy_origin (cnb.h).
     */
    public static native int cnbDocumentCopyOrigin(long document, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_document_destroy (cnb.h).
     */
    public static native int cnbDocumentDestroy(long document);

    /**
     * cna_cnb_document_find_all (cnb.h).
     */
    public static native int cnbDocumentFindAll(long document, int type, long[] destination, long[] outCount);

    /**
     * cna_cnb_document_find_single (cnb.h).
     */
    public static native int cnbDocumentFindSingle(long document, int type, boolean[] outFound, long[] outIndex);

    /**
     * cna_cnb_document_get_asset_schema_version (cnb.h).
     */
    public static native int cnbDocumentGetAssetSchemaVersion(long document, int[] outSchemaVersion);

    /**
     * cna_cnb_document_get_asset_type_id (cnb.h).
     */
    public static native int cnbDocumentGetAssetTypeId(long document, int[] outAssetTypeId);

    /**
     * cna_cnb_document_get_chunk (cnb.h).
     *
     * <p>outEntryIntegral carries CNA_CnbChunkEntry in this order:
     * <ol start="0">
     *   <li>{@code offset} (uint64_t)</li>
     *   <li>{@code stored_size} (uint64_t)</li>
     *   <li>{@code uncompressed_size} (uint64_t)</li>
     *   <li>{@code type} (CNA_CnbChunkId)</li>
     *   <li>{@code flags} (uint32_t)</li>
     *   <li>{@code checksum} (uint32_t)</li>
     *   <li>{@code compression} (CNA_CnbCompression)</li>
     *   <li>{@code alignment} (uint32_t)</li>
     *   <li>{@code reserved} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentGetChunk(long document, long index, long[] outEntryIntegral);

    /**
     * cna_cnb_document_get_chunk_count (cnb.h).
     */
    public static native int cnbDocumentGetChunkCount(long document, long[] outCount);

    /**
     * cna_cnb_document_get_container_major (cnb.h).
     */
    public static native int cnbDocumentGetContainerMajor(long document, int[] outMajor);

    /**
     * cna_cnb_document_get_container_minor (cnb.h).
     */
    public static native int cnbDocumentGetContainerMinor(long document, int[] outMinor);

    /**
     * cna_cnb_document_get_external_reference (cnb.h).
     *
     * <p>outReferenceIntegral carries CNA_CnbExternalReference in this order:
     * <ol start="0">
     *   <li>{@code flags} (uint32_t)</li>
     *   <li>{@code expected_asset_type_id} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentGetExternalReference(long document, long index, byte[] whatForDiagnostics, long[] outReferenceIntegral);

    /**
     * cna_cnb_document_get_external_reference_count (cnb.h).
     */
    public static native int cnbDocumentGetExternalReferenceCount(long document, long[] outCount);

    /**
     * cna_cnb_document_get_external_reference_name_size (cnb.h).
     */
    public static native int cnbDocumentGetExternalReferenceNameSize(long document, long index, long[] outByteCount);

    /**
     * cna_cnb_document_get_limits (cnb.h).
     *
     * <p>outLimitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentGetLimits(long document, long[] outLimitsIntegral);

    /**
     * cna_cnb_document_get_metadata (cnb.h).
     *
     * <p>outMetadataBytes carries CNA_CnbMetadata in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outMetadataIntegral carries CNA_CnbMetadata in this order:
     * <ol start="0">
     *   <li>{@code present} (CNA_Bool)</li>
     *   <li>{@code flags} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentGetMetadata(long document, byte[] outMetadataBytes, long[] outMetadataIntegral);

    /**
     * cna_cnb_document_get_metadata_asset_type_name_size (cnb.h).
     */
    public static native int cnbDocumentGetMetadataAssetTypeNameSize(long document, long[] outByteCount);

    /**
     * cna_cnb_document_get_metadata_content_name_size (cnb.h).
     */
    public static native int cnbDocumentGetMetadataContentNameSize(long document, long[] outByteCount);

    /**
     * cna_cnb_document_get_origin_size (cnb.h).
     */
    public static native int cnbDocumentGetOriginSize(long document, long[] outByteCount);

    /**
     * cna_cnb_document_open_chunk (cnb.h).
     */
    public static native int cnbDocumentOpenChunk(long document, long index, long[] outReader);

    /**
     * cna_cnb_document_parse (cnb.h).
     *
     * <p>limitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentParse(byte[] bytes, byte[] origin, long[] limitsIntegral, long[] outDocument);

    /**
     * cna_cnb_document_parse_file (cnb.h).
     *
     * <p>limitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbDocumentParseFile(byte[] path, long[] limitsIntegral, long[] outDocument);

    /**
     * cna_cnb_document_read_embedded_texture2d (cnb.h).
     */
    public static native int cnbDocumentReadEmbeddedTexture2d(long document, byte[] label, long[] outTexture);

    /**
     * cna_cnb_document_require_asset (cnb.h).
     */
    public static native int cnbDocumentRequireAsset(long document, int expectedAssetTypeId, int maxSchemaVersion);

    /**
     * cna_cnb_document_require_mandatory_chunks_understood (cnb.h).
     */
    public static native int cnbDocumentRequireMandatoryChunksUnderstood(long document, int[] knownTypes);

    /**
     * cna_cnb_document_require_single (cnb.h).
     */
    public static native int cnbDocumentRequireSingle(long document, int type, long[] outIndex);

    /**
     * cna_cnb_encode_song (cnb.h).
     */
    public static native int cnbEncodeSong(byte[] streamReference, byte[] name, int durationMilliseconds, byte[] contentName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_encode_sound_effect (cnb.h).
     */
    public static native int cnbEncodeSoundEffect(long sound, byte[] contentName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_encode_sprite_font (cnb.h).
     */
    public static native int cnbEncodeSpriteFont(long font, byte[] contentName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_encode_texture2d (cnb.h).
     */
    public static native int cnbEncodeTexture2d(long texture, byte[] contentName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_encode_video (cnb.h).
     *
     * <p>infoIntegral carries CNA_CnbVideoInfo in this order:
     * <ol start="0">
     *   <li>{@code duration_milliseconds} (uint32_t)</li>
     *   <li>{@code width} (uint32_t)</li>
     *   <li>{@code height} (uint32_t)</li>
     *   <li>{@code soundtrack_type} (CNA_VideoSoundtrackType)</li>
     *   <li>{@code reserved} (uint32_t)</li>
     * </ol>
     *
     * <p>infoFloating carries CNA_CnbVideoInfo in this order:
     * <ol start="0">
     *   <li>{@code frames_per_second} (float)</li>
     * </ol>
     */
    public static native int cnbEncodeVideo(byte[] streamReference, long[] infoIntegral, float[] infoFloating, byte[] contentName, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_get_asset_type_name_size (cnb.h).
     */
    public static native int cnbGetAssetTypeNameSize(int assetTypeId, long[] outByteCount);

    /**
     * cna_cnb_get_audio_format_name_size (cnb.h).
     */
    public static native int cnbGetAudioFormatNameSize(int format, long[] outByteCount);

    /**
     * cna_cnb_get_chunk_id_string_size (cnb.h).
     */
    public static native int cnbGetChunkIdStringSize(int id, long[] outByteCount);

    /**
     * cna_cnb_get_compressed_byte_count (cnb.h).
     */
    public static native int cnbGetCompressedByteCount(byte[] raw, int codec, int level, long[] outByteCount);

    /**
     * cna_cnb_get_compression_name_size (cnb.h).
     */
    public static native int cnbGetCompressionNameSize(int codec, long[] outByteCount);

    /**
     * cna_cnb_get_logical_name_problem_size (cnb.h).
     */
    public static native int cnbGetLogicalNameProblemSize(byte[] logicalName, long[] outByteCount);

    /**
     * cna_cnb_get_texture_format_name_size (cnb.h).
     */
    public static native int cnbGetTextureFormatNameSize(int format, long[] outByteCount);

    /**
     * cna_cnb_get_texture_format_unit_bytes (cnb.h).
     */
    public static native int cnbGetTextureFormatUnitBytes(int format, int[] outUnitBytes);

    /**
     * cna_cnb_get_texture_level_byte_size (cnb.h).
     */
    public static native int cnbGetTextureLevelByteSize(int format, int width, int height, int depth, long[] outByteSize);

    /**
     * cna_cnb_has_magic (cnb.h).
     */
    public static native int cnbHasMagic(byte[] bytes, boolean[] outHasMagic);

    /**
     * cna_cnb_is_block_compressed_texture_format (cnb.h).
     */
    public static native int cnbIsBlockCompressedTextureFormat(int format, boolean[] outBlockCompressed);

    /**
     * cna_cnb_is_compression_supported (cnb.h).
     */
    public static native int cnbIsCompressionSupported(int codec, boolean[] outSupported);

    /**
     * cna_cnb_is_custom_asset_type_id (cnb.h).
     */
    public static native int cnbIsCustomAssetTypeId(int assetTypeId, boolean[] outCustom);

    /**
     * cna_cnb_is_known_texture_format (cnb.h).
     */
    public static native int cnbIsKnownTextureFormat(int value, boolean[] outKnown);

    /**
     * cna_cnb_is_well_formed_chunk_id (cnb.h).
     */
    public static native int cnbIsWellFormedChunkId(int id, boolean[] outWellFormed);

    /**
     * cna_cnb_is_well_formed_utf8 (cnb.h).
     */
    public static native int cnbIsWellFormedUtf8(byte[] text, boolean[] outWellFormed);

    /**
     * cna_cnb_make_chunk_id (cnb.h).
     */
    public static native int cnbMakeChunkId(byte a, byte b, byte c, byte d, int[] outId);

    /**
     * cna_cnb_read_limits_init (cnb.h).
     *
     * <p>outLimitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbReadLimitsInit(long[] outLimitsIntegral);

    /**
     * cna_cnb_reader_copy_context (cnb.h).
     */
    public static native int cnbReaderCopyContext(long reader, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_reader_copy_string (cnb.h).
     */
    public static native int cnbReaderCopyString(long reader, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_reader_create (cnb.h).
     *
     * <p>limitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbReaderCreate(byte[] data, byte[] context, long[] limitsIntegral, long[] outReader);

    /**
     * cna_cnb_reader_destroy (cnb.h).
     */
    public static native int cnbReaderDestroy(long reader);

    /**
     * cna_cnb_reader_get_context_size (cnb.h).
     */
    public static native int cnbReaderGetContextSize(long reader, long[] outByteCount);

    /**
     * cna_cnb_reader_get_position (cnb.h).
     */
    public static native int cnbReaderGetPosition(long reader, long[] outPosition);

    /**
     * cna_cnb_reader_get_remaining (cnb.h).
     */
    public static native int cnbReaderGetRemaining(long reader, long[] outRemaining);

    /**
     * cna_cnb_reader_get_size (cnb.h).
     */
    public static native int cnbReaderGetSize(long reader, long[] outSize);

    /**
     * cna_cnb_reader_read_bytes (cnb.h).
     */
    public static native int cnbReaderReadBytes(long reader, long byteCount, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_reader_read_count (cnb.h).
     */
    public static native int cnbReaderReadCount(long reader, long elementSize, byte[] whatIsBeingCounted, int[] outCount);

    /**
     * cna_cnb_reader_read_f32 (cnb.h).
     */
    public static native int cnbReaderReadF32(long reader, float[] outValue);

    /**
     * cna_cnb_reader_read_f64 (cnb.h).
     */
    public static native int cnbReaderReadF64(long reader, double[] outValue);

    /**
     * cna_cnb_reader_read_i32 (cnb.h).
     */
    public static native int cnbReaderReadI32(long reader, int[] outValue);

    /**
     * cna_cnb_reader_read_string (cnb.h).
     */
    public static native int cnbReaderReadString(long reader, long[] outByteCount);

    /**
     * cna_cnb_reader_read_u16 (cnb.h).
     */
    public static native int cnbReaderReadU16(long reader, int[] outValue);

    /**
     * cna_cnb_reader_read_u32 (cnb.h).
     */
    public static native int cnbReaderReadU32(long reader, int[] outValue);

    /**
     * cna_cnb_reader_read_u64 (cnb.h).
     */
    public static native int cnbReaderReadU64(long reader, long[] outValue);

    /**
     * cna_cnb_reader_read_u8 (cnb.h).
     */
    public static native int cnbReaderReadU8(long reader, byte[] outValue);

    /**
     * cna_cnb_reader_require_exhausted (cnb.h).
     */
    public static native int cnbReaderRequireExhausted(long reader);

    /**
     * cna_cnb_reader_skip (cnb.h).
     */
    public static native int cnbReaderSkip(long reader, long byteCount);

    /**
     * cna_cnb_sound_effect_data_copy_samples (cnb.h).
     */
    public static native int cnbSoundEffectDataCopySamples(long sound, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_sound_effect_data_create (cnb.h).
     *
     * <p>infoIntegral carries CNA_CnbSoundEffectInfo in this order:
     * <ol start="0">
     *   <li>{@code format} (CNA_CnbAudioFormat)</li>
     *   <li>{@code sample_rate} (uint32_t)</li>
     *   <li>{@code channels} (uint32_t)</li>
     *   <li>{@code frame_count} (uint32_t)</li>
     *   <li>{@code loop_start} (uint32_t)</li>
     *   <li>{@code loop_length} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbSoundEffectDataCreate(long[] infoIntegral, byte[] samples, long[] outSound);

    /**
     * cna_cnb_sound_effect_data_destroy (cnb.h).
     */
    public static native int cnbSoundEffectDataDestroy(long sound);

    /**
     * cna_cnb_sound_effect_data_get_info (cnb.h).
     *
     * <p>outInfoIntegral carries CNA_CnbSoundEffectInfo in this order:
     * <ol start="0">
     *   <li>{@code format} (CNA_CnbAudioFormat)</li>
     *   <li>{@code sample_rate} (uint32_t)</li>
     *   <li>{@code channels} (uint32_t)</li>
     *   <li>{@code frame_count} (uint32_t)</li>
     *   <li>{@code loop_start} (uint32_t)</li>
     *   <li>{@code loop_length} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbSoundEffectDataGetInfo(long sound, long[] outInfoIntegral);

    /**
     * cna_cnb_sprite_font_data_add_glyph (cnb.h).
     *
     * <p>glyphIntegral carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code glyph_bounds.x} (int32_t)</li>
     *   <li>{@code glyph_bounds.y} (int32_t)</li>
     *   <li>{@code glyph_bounds.width} (int32_t)</li>
     *   <li>{@code glyph_bounds.height} (int32_t)</li>
     *   <li>{@code cropping.x} (int32_t)</li>
     *   <li>{@code cropping.y} (int32_t)</li>
     *   <li>{@code cropping.width} (int32_t)</li>
     *   <li>{@code cropping.height} (int32_t)</li>
     *   <li>{@code character} (CNA_Char16)</li>
     *   <li>{@code reserved} (uint16_t)</li>
     * </ol>
     *
     * <p>glyphFloating carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code kerning.x} (float)</li>
     *   <li>{@code kerning.y} (float)</li>
     *   <li>{@code kerning.z} (float)</li>
     * </ol>
     */
    public static native int cnbSpriteFontDataAddGlyph(long font, long[] glyphIntegral, float[] glyphFloating, long[] outIndex);

    /**
     * cna_cnb_sprite_font_data_copy_atlas (cnb.h).
     */
    public static native int cnbSpriteFontDataCopyAtlas(long font, long[] outAtlas);

    /**
     * cna_cnb_sprite_font_data_create (cnb.h).
     */
    public static native int cnbSpriteFontDataCreate(long[] outFont);

    /**
     * cna_cnb_sprite_font_data_destroy (cnb.h).
     */
    public static native int cnbSpriteFontDataDestroy(long font);

    /**
     * cna_cnb_sprite_font_data_get_glyph (cnb.h).
     *
     * <p>outGlyphIntegral carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code glyph_bounds.x} (int32_t)</li>
     *   <li>{@code glyph_bounds.y} (int32_t)</li>
     *   <li>{@code glyph_bounds.width} (int32_t)</li>
     *   <li>{@code glyph_bounds.height} (int32_t)</li>
     *   <li>{@code cropping.x} (int32_t)</li>
     *   <li>{@code cropping.y} (int32_t)</li>
     *   <li>{@code cropping.width} (int32_t)</li>
     *   <li>{@code cropping.height} (int32_t)</li>
     *   <li>{@code character} (CNA_Char16)</li>
     *   <li>{@code reserved} (uint16_t)</li>
     * </ol>
     *
     * <p>outGlyphFloating carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code kerning.x} (float)</li>
     *   <li>{@code kerning.y} (float)</li>
     *   <li>{@code kerning.z} (float)</li>
     * </ol>
     */
    public static native int cnbSpriteFontDataGetGlyph(long font, long index, long[] outGlyphIntegral, float[] outGlyphFloating);

    /**
     * cna_cnb_sprite_font_data_get_info (cnb.h).
     *
     * <p>outInfoBytes carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     * </ol>
     *
     * <p>outInfoIntegral carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code glyph_count} (uint64_t)</li>
     *   <li>{@code line_spacing} (int32_t)</li>
     *   <li>{@code default_character} (CNA_Char16)</li>
     *   <li>{@code has_default_character} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outInfoFloating carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code spacing} (float)</li>
     * </ol>
     */
    public static native int cnbSpriteFontDataGetInfo(long font, byte[] outInfoBytes, long[] outInfoIntegral, float[] outInfoFloating);

    /**
     * cna_cnb_sprite_font_data_set_atlas (cnb.h).
     */
    public static native int cnbSpriteFontDataSetAtlas(long font, long atlas);

    /**
     * cna_cnb_sprite_font_data_set_glyph (cnb.h).
     *
     * <p>glyphIntegral carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code glyph_bounds.x} (int32_t)</li>
     *   <li>{@code glyph_bounds.y} (int32_t)</li>
     *   <li>{@code glyph_bounds.width} (int32_t)</li>
     *   <li>{@code glyph_bounds.height} (int32_t)</li>
     *   <li>{@code cropping.x} (int32_t)</li>
     *   <li>{@code cropping.y} (int32_t)</li>
     *   <li>{@code cropping.width} (int32_t)</li>
     *   <li>{@code cropping.height} (int32_t)</li>
     *   <li>{@code character} (CNA_Char16)</li>
     *   <li>{@code reserved} (uint16_t)</li>
     * </ol>
     *
     * <p>glyphFloating carries CNA_SpriteFontGlyph in this order:
     * <ol start="0">
     *   <li>{@code kerning.x} (float)</li>
     *   <li>{@code kerning.y} (float)</li>
     *   <li>{@code kerning.z} (float)</li>
     * </ol>
     */
    public static native int cnbSpriteFontDataSetGlyph(long font, long index, long[] glyphIntegral, float[] glyphFloating);

    /**
     * cna_cnb_sprite_font_data_set_info (cnb.h).
     *
     * <p>infoBytes carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     * </ol>
     *
     * <p>infoIntegral carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code glyph_count} (uint64_t)</li>
     *   <li>{@code line_spacing} (int32_t)</li>
     *   <li>{@code default_character} (CNA_Char16)</li>
     *   <li>{@code has_default_character} (CNA_Bool)</li>
     * </ol>
     *
     * <p>infoFloating carries CNA_CnbSpriteFontInfo in this order:
     * <ol start="0">
     *   <li>{@code spacing} (float)</li>
     * </ol>
     */
    public static native int cnbSpriteFontDataSetInfo(long font, byte[] infoBytes, long[] infoIntegral, float[] infoFloating);

    /**
     * cna_cnb_texture_data_add_representation (cnb.h).
     */
    public static native int cnbTextureDataAddRepresentation(long texture, int format, long[] outIndex);

    /**
     * cna_cnb_texture_data_copy_level (cnb.h).
     */
    public static native int cnbTextureDataCopyLevel(long texture, long representation, long level, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_texture_data_create (cnb.h).
     */
    public static native int cnbTextureDataCreate(int width, int height, int depth, int faceCount, int mipCount, long[] outTexture);

    /**
     * cna_cnb_texture_data_create_rgba8 (cnb.h).
     */
    public static native int cnbTextureDataCreateRgba8(int width, int height, byte[] rgba, long[] outTexture);

    /**
     * cna_cnb_texture_data_destroy (cnb.h).
     */
    public static native int cnbTextureDataDestroy(long texture);

    /**
     * cna_cnb_texture_data_get_info (cnb.h).
     *
     * <p>outInfoIntegral carries CNA_CnbTextureInfo in this order:
     * <ol start="0">
     *   <li>{@code width} (uint32_t)</li>
     *   <li>{@code height} (uint32_t)</li>
     *   <li>{@code depth} (uint32_t)</li>
     *   <li>{@code face_count} (uint32_t)</li>
     *   <li>{@code mip_count} (uint32_t)</li>
     *   <li>{@code representation_count} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbTextureDataGetInfo(long texture, long[] outInfoIntegral);

    /**
     * cna_cnb_texture_data_get_level_count (cnb.h).
     */
    public static native int cnbTextureDataGetLevelCount(long texture, long representation, long[] outCount);

    /**
     * cna_cnb_texture_data_get_level_dimensions (cnb.h).
     */
    public static native int cnbTextureDataGetLevelDimensions(long texture, int level, int[] outWidth, int[] outHeight, int[] outDepth);

    /**
     * cna_cnb_texture_data_get_representation_count (cnb.h).
     */
    public static native int cnbTextureDataGetRepresentationCount(long texture, long[] outCount);

    /**
     * cna_cnb_texture_data_get_representation_format (cnb.h).
     */
    public static native int cnbTextureDataGetRepresentationFormat(long texture, long representation, int[] outFormat);

    /**
     * cna_cnb_texture_data_set_level (cnb.h).
     */
    public static native int cnbTextureDataSetLevel(long texture, long representation, long level, byte[] bytes);

    /**
     * cna_cnb_texture_format_from_surface_format (cnb.h).
     */
    public static native int cnbTextureFormatFromSurfaceFormat(int surfaceFormat, int[] outFormat);

    /**
     * cna_cnb_texture_format_to_surface_format (cnb.h).
     */
    public static native int cnbTextureFormatToSurfaceFormat(int format, int[] outSurfaceFormat);

    /**
     * cna_cnb_writer_add_chunk (cnb.h).
     */
    public static native int cnbWriterAddChunk(long writer, int type, byte[] data, int flags, int alignment);

    /**
     * cna_cnb_writer_append_embedded_texture2d (cnb.h).
     */
    public static native int cnbWriterAppendEmbeddedTexture2d(long writer, long texture, byte[] label);

    /**
     * cna_cnb_writer_build (cnb.h).
     */
    public static native int cnbWriterBuild(long writer, byte[] destination, long[] outByteCount);

    /**
     * cna_cnb_writer_create (cnb.h).
     */
    public static native int cnbWriterCreate(int assetTypeId, int assetSchemaVersion, long[] outWriter);

    /**
     * cna_cnb_writer_destroy (cnb.h).
     */
    public static native int cnbWriterDestroy(long writer);

    /**
     * cna_cnb_writer_get_limits (cnb.h).
     *
     * <p>outLimitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbWriterGetLimits(long writer, long[] outLimitsIntegral);

    /**
     * cna_cnb_writer_get_schema_chunk_count (cnb.h).
     */
    public static native int cnbWriterGetSchemaChunkCount(long writer, long[] outCount);

    /**
     * cna_cnb_writer_set_compression (cnb.h).
     */
    public static native int cnbWriterSetCompression(long writer, int codec, int level);

    /**
     * cna_cnb_writer_set_limits (cnb.h).
     *
     * <p>limitsIntegral carries CNA_CnbReadLimits in this order:
     * <ol start="0">
     *   <li>{@code max_file_size} (uint64_t)</li>
     *   <li>{@code max_chunk_size} (uint64_t)</li>
     *   <li>{@code max_total_uncompressed_size} (uint64_t)</li>
     *   <li>{@code max_chunk_count} (uint32_t)</li>
     *   <li>{@code max_string_bytes} (uint32_t)</li>
     *   <li>{@code max_array_element_count} (uint32_t)</li>
     *   <li>{@code max_chunk_alignment} (uint32_t)</li>
     * </ol>
     */
    public static native int cnbWriterSetLimits(long writer, long[] limitsIntegral);

    /**
     * cna_cnb_writer_set_metadata (cnb.h).
     */
    public static native int cnbWriterSetMetadata(long writer, byte[] assetTypeName, byte[] contentName);

    /**
     * cna_cnb_writer_write_to_file (cnb.h).
     */
    public static native int cnbWriterWriteToFile(long writer, byte[] path);
}
