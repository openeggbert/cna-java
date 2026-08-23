package Microsoft.Xna.Framework.Content;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Quaternion;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.openeggbert.cna.content.ContentTypeReaderRegistry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Per-asset reader table and deterministic custom-reader activator. */
public final class ContentTypeReaderManager {

    private final Map<Class<?>, ContentTypeReader> readersByTargetType = new LinkedHashMap<>();

    ContentTypeReaderManager() {
    }

    public ContentTypeReader GetTypeReader(Class<?> targetType) {
        return readersByTargetType.get(Objects.requireNonNull(targetType, "targetType"));
    }

    ContentTypeReader[] loadAssetReaders(ContentReader input) {
        int count = input.read7BitEncodedInt32();
        if (count < 0 || count > 4096) {
            throw input.failure("invalid type reader count " + count);
        }
        ContentTypeReader[] readers = new ContentTypeReader[count];
        int[] versions = new int[count];
        for (int index = 0; index < count; index++) {
            String serializedName = input.ReadString();
            readers[index] = createReader(serializedName, input.getAssetName());
            versions[index] = input.ReadInt32();
            if (versions[index] != readers[index].getTypeVersion()) {
                throw input.failure("reader version mismatch for '" + serializedName
                        + "': asset " + versions[index] + ", runtime "
                        + readers[index].getTypeVersion());
            }
            readersByTargetType.putIfAbsent(readers[index].getTargetType(), readers[index]);
        }
        input.setTypeReaderVersions(versions);
        for (ContentTypeReader reader : readers) {
            try {
                reader.initializeReader(this);
            } catch (RuntimeException exception) {
                throw new ContentLoadException(
                        "Failed to initialize content type reader '"
                                + reader.getClass().getName() + "'", exception);
            }
        }
        return readers;
    }

    private static ContentTypeReader createReader(String serializedName, String assetName) {
        String name = serializedName == null ? "" : serializedName.trim();
        if (name.isEmpty()) {
            throw new ContentLoadException(
                    "Content asset '" + assetName + "' declares an empty content reader name");
        }
        ContentTypeReader builtIn = builtIn(
                ContentTypeReaderRegistry.stripAssemblyQualification(name));
        if (builtIn != null) {
            return builtIn;
        }
        try {
            ContentTypeReader custom = ContentTypeReaderRegistry.create(name);
            if (custom != null) {
                return custom;
            }
        } catch (RuntimeException exception) {
            throw new ContentLoadException(
                    "Failed to activate content type reader '" + name
                            + "' while loading '" + assetName + "'", exception);
        }
        throw new ContentLoadException(
                "Unknown content type reader '" + name + "' while loading '" + assetName + "'");
    }

    private static ContentTypeReader builtIn(String name) {
        return switch (name) {
            case "Microsoft.Xna.Framework.Content.BooleanReader" -> new BooleanReader();
            case "Microsoft.Xna.Framework.Content.ByteReader" -> new ByteReader();
            case "Microsoft.Xna.Framework.Content.CharReader" -> new CharReader();
            case "Microsoft.Xna.Framework.Content.DoubleReader" -> new DoubleReader();
            case "Microsoft.Xna.Framework.Content.Int16Reader" -> new Int16Reader();
            case "Microsoft.Xna.Framework.Content.Int32Reader" -> new Int32Reader();
            case "Microsoft.Xna.Framework.Content.Int64Reader" -> new Int64Reader();
            case "Microsoft.Xna.Framework.Content.SByteReader" -> new SByteReader();
            case "Microsoft.Xna.Framework.Content.SingleReader" -> new SingleReader();
            case "Microsoft.Xna.Framework.Content.StringReader" -> new StringReader();
            case "Microsoft.Xna.Framework.Content.UInt16Reader" -> new UInt16Reader();
            case "Microsoft.Xna.Framework.Content.UInt32Reader" -> new UInt32Reader();
            case "Microsoft.Xna.Framework.Content.UInt64Reader" -> new UInt64Reader();
            case "Microsoft.Xna.Framework.Content.Vector2Reader" -> new Vector2Reader();
            case "Microsoft.Xna.Framework.Content.Vector3Reader" -> new Vector3Reader();
            case "Microsoft.Xna.Framework.Content.Vector4Reader" -> new Vector4Reader();
            case "Microsoft.Xna.Framework.Content.MatrixReader" -> new MatrixReader();
            case "Microsoft.Xna.Framework.Content.QuaternionReader" -> new QuaternionReader();
            case "Microsoft.Xna.Framework.Content.ColorReader" -> new ColorReader();
            default -> null;
        };
    }

    private static final class BooleanReader extends ContentTypeReaderOfT<Boolean> {
        @Override protected Boolean ReadTyped(ContentReader input, Boolean existing) { return input.ReadBoolean(); }
    }
    private static final class ByteReader extends ContentTypeReaderOfT<Integer> {
        @Override protected Integer ReadTyped(ContentReader input, Integer existing) { return input.ReadByte(); }
    }
    private static final class CharReader extends ContentTypeReaderOfT<Character> {
        @Override protected Character ReadTyped(ContentReader input, Character existing) { return input.ReadChar(); }
    }
    private static final class DoubleReader extends ContentTypeReaderOfT<Double> {
        @Override protected Double ReadTyped(ContentReader input, Double existing) { return input.ReadDouble(); }
    }
    private static final class Int16Reader extends ContentTypeReaderOfT<Short> {
        @Override protected Short ReadTyped(ContentReader input, Short existing) { return input.ReadInt16(); }
    }
    private static final class Int32Reader extends ContentTypeReaderOfT<Integer> {
        @Override protected Integer ReadTyped(ContentReader input, Integer existing) { return input.ReadInt32(); }
    }
    private static final class Int64Reader extends ContentTypeReaderOfT<Long> {
        @Override protected Long ReadTyped(ContentReader input, Long existing) { return input.ReadInt64(); }
    }
    private static final class SByteReader extends ContentTypeReaderOfT<Byte> {
        @Override protected Byte ReadTyped(ContentReader input, Byte existing) { return input.ReadSByte(); }
    }
    private static final class SingleReader extends ContentTypeReaderOfT<Float> {
        @Override protected Float ReadTyped(ContentReader input, Float existing) { return input.ReadSingle(); }
    }
    private static final class StringReader extends ContentTypeReaderOfT<String> {
        @Override protected String ReadTyped(ContentReader input, String existing) { return input.ReadString(); }
    }
    private static final class UInt16Reader extends ContentTypeReaderOfT<Integer> {
        @Override protected Integer ReadTyped(ContentReader input, Integer existing) { return input.ReadUInt16(); }
    }
    private static final class UInt32Reader extends ContentTypeReaderOfT<Long> {
        @Override protected Long ReadTyped(ContentReader input, Long existing) { return input.ReadUInt32(); }
    }
    private static final class UInt64Reader extends ContentTypeReaderOfT<Long> {
        @Override protected Long ReadTyped(ContentReader input, Long existing) { return input.ReadUInt64(); }
    }
    private static final class Vector2Reader extends ContentTypeReaderOfT<Vector2> {
        @Override protected Vector2 ReadTyped(ContentReader input, Vector2 existing) { return input.ReadVector2(); }
    }
    private static final class Vector3Reader extends ContentTypeReaderOfT<Vector3> {
        @Override protected Vector3 ReadTyped(ContentReader input, Vector3 existing) { return input.ReadVector3(); }
    }
    private static final class Vector4Reader extends ContentTypeReaderOfT<Vector4> {
        @Override protected Vector4 ReadTyped(ContentReader input, Vector4 existing) { return input.ReadVector4(); }
    }
    private static final class MatrixReader extends ContentTypeReaderOfT<Matrix> {
        @Override protected Matrix ReadTyped(ContentReader input, Matrix existing) { return input.ReadMatrix(); }
    }
    private static final class QuaternionReader extends ContentTypeReaderOfT<Quaternion> {
        @Override protected Quaternion ReadTyped(ContentReader input, Quaternion existing) { return input.ReadQuaternion(); }
    }
    private static final class ColorReader extends ContentTypeReaderOfT<Color> {
        @Override protected Color ReadTyped(ContentReader input, Color existing) { return input.ReadColor(); }
    }
}
