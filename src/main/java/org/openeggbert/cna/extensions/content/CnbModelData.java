package org.openeggbert.cna.extensions.content;

import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;

import org.openeggbert.cna.internal.generated.NativeCnbRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A model as {@code .cnb} carries it: a bone hierarchy, renderable parts, the meshes that group
 * them, an optional skinning skeleton and any baked lights.
 *
 * <p><strong>This is content, not a scene.</strong> Nothing here touches a graphics device, so a
 * build step or an inspector can read, rewrite and write a model with no window open. That is
 * also the boundary: there is no {@code toModel(GraphicsDevice)}, because XNA's {@code Model} has
 * no public constructor and CNA offers no route that builds one from this description. A game
 * loads a model through the content manager as it always did; this is for the tooling on the
 * other side of that file.
 *
 * <p>{@link CnaModel} is the other direction and a different thing: it wraps an XNA {@code Model}
 * a content manager already produced, to answer questions {@code Model} has nowhere to put.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class CnbModelData implements AutoCloseable {

    /** CNA's own selector for the skeleton's three matrix sets. */
    private static final int SKELETON_BIND_POSE = 0;
    private static final int SKELETON_INVERSE_BIND_POSE = 1;
    private static final int SKELETON_ROOT_PREFIX = 2;

    /** Sixteen floats to a matrix, which is what every skeleton matrix route counts in. */
    private static final int MATRIX_FLOATS = 16;

    private final long handle;
    private boolean closed;

    CnbModelData(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty model.
     *
     * @return the model data, which the caller closes
     */
    public static CnbModelData create() {
        CnbExtension.requireAvailable();
        long[] model = new long[1];
        CnbExtension.check("CnbModelData.create", NativeCnbRoutes.cnbModelCreate(model));
        return new CnbModelData(model[0]);
    }

    /** Returns what the model contains. */
    public CnbModelInfo getInfo() {
        long[] values = new long[9];
        CnbExtension.check("CnbModelData.getInfo",
                NativeCnbRoutes.cnbModelGetInfo(open(), values));
        return new CnbModelInfo((int) values[0], (int) values[1], (int) values[2],
                (int) values[3], (int) values[4], values[5] != 0, values[6] != 0,
                values[7] != 0);
    }

    /**
     * Sets the two facts about the model as a whole.
     *
     * @param appliesGltfLightingPolicy whether the source asset's glTF lighting rules apply
     * @param hasBoneHierarchy whether the bones form a hierarchy rather than a flat list
     */
    public void setFlags(boolean appliesGltfLightingPolicy, boolean hasBoneHierarchy) {
        CnbExtension.check("CnbModelData.setFlags", NativeCnbRoutes
                .cnbModelSetFlags(open(), appliesGltfLightingPolicy, hasBoneHierarchy));
    }

    /**
     * Appends one bone.
     *
     * @param name the bone's name, which may be empty
     * @param parent the parent bone's index, or -1 for a root
     * @param transform the bone's transform relative to its parent
     * @return the new bone's index
     */
    public int addBone(String name, int parent, Matrix transform) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(transform, "transform");
        long[] index = new long[1];
        CnbExtension.check("CnbModelData.addBone", NativeCnbRoutes.cnbModelAddBone(
                open(), CnbExtension.utf8(name), parent, floats(transform), index));
        return (int) index[0];
    }

    /**
     * Returns one bone.
     *
     * @param index the zero-based bone index
     * @return its name, parent and transform
     */
    public CnbModelBone getBone(int index) {
        long model = open();
        long[] integral = new long[2];
        float[] floating = new float[MATRIX_FLOATS];
        CnbExtension.check("CnbModelData.getBone",
                NativeCnbRoutes.cnbModelGetBone(model, index, integral, floating));
        return new CnbModelBone(
                CnbExtension.text("CnbModelData.getBone",
                        bytes -> NativeCnbRoutes.cnbModelGetBoneNameSize(model, index, bytes),
                        (destination, bytes) -> NativeCnbRoutes
                                .cnbModelCopyBoneName(model, index, destination, bytes)),
                (int) integral[0], matrix(floating, 0));
    }

    /** Returns every bone in index order. */
    public List<CnbModelBone> getBones() {
        return read(getInfo().BoneCount(), this::getBone);
    }

    /**
     * Appends one renderable part with empty geometry.
     *
     * @param part the part's numeric state
     * @param name the part's name, which may be empty
     * @param externalEffect the effect asset name, used only by
     *        {@link CnbEffectKind#External} and empty otherwise
     * @return the new part's index
     */
    public int addPart(CnbModelPart part, String name, String externalEffect) {
        Objects.requireNonNull(part, "part");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(externalEffect, "externalEffect");
        long[] index = new long[1];
        CnbExtension.check("CnbModelData.addPart", NativeCnbRoutes.cnbModelAddPart(
                open(), new byte[2], partLeaves(part), CnbExtension.utf8(name),
                CnbExtension.utf8(externalEffect), index));
        return (int) index[0];
    }

    /**
     * Returns one part's numeric state.
     *
     * @param index the zero-based part index
     * @return that part's description, without its bytes
     */
    public CnbModelPart getPart(int index) {
        long[] values = new long[9];
        CnbExtension.check("CnbModelData.getPart",
                NativeCnbRoutes.cnbModelGetPart(open(), index, new byte[2], values));
        return new CnbModelPart((int) values[0], (int) values[1], (int) values[2],
                (int) values[3], (int) values[4], (int) values[5],
                CnbEffectKind.fromValue(values[6]), values[7] != 0, values[8] != 0);
    }

    /**
     * Replaces one part's numeric state, leaving its bytes alone.
     *
     * @param index the zero-based part index
     * @param part the replacement description
     */
    public void setPart(int index, CnbModelPart part) {
        Objects.requireNonNull(part, "part");
        CnbExtension.check("CnbModelData.setPart", NativeCnbRoutes
                .cnbModelSetPart(open(), index, new byte[2], partLeaves(part)));
    }

    /**
     * Returns one part's name.
     *
     * @param index the zero-based part index
     * @return its name, empty when it has none
     */
    public String getPartName(int index) {
        long model = open();
        return CnbExtension.text("CnbModelData.getPartName",
                bytes -> NativeCnbRoutes.cnbModelGetPartNameSize(model, index, bytes),
                (destination, bytes) -> NativeCnbRoutes
                        .cnbModelCopyPartName(model, index, destination, bytes));
    }

    /**
     * Returns the effect asset one part names for itself.
     *
     * @param index the zero-based part index
     * @return the asset name, empty unless the part's kind is {@link CnbEffectKind#External}
     */
    public String getPartExternalEffect(int index) {
        long model = open();
        return CnbExtension.text("CnbModelData.getPartExternalEffect",
                bytes -> NativeCnbRoutes
                        .cnbModelGetPartExternalEffectSize(model, index, bytes),
                (destination, bytes) -> NativeCnbRoutes
                        .cnbModelCopyPartExternalEffect(model, index, destination, bytes));
    }

    /**
     * Replaces one part's vertex payload.
     *
     * @param index the zero-based part index
     * @param bytes the vertex bytes; CNA copies them
     */
    public void setPartVertexBytes(int index, byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        CnbExtension.check("CnbModelData.setPartVertexBytes",
                NativeCnbRoutes.cnbModelSetPartVertexBytes(open(), index, bytes));
    }

    /**
     * Returns one part's vertex payload.
     *
     * @param index the zero-based part index
     * @return a fresh copy of the bytes
     */
    public byte[] readPartVertexBytes(int index) {
        long model = open();
        return CnbExtension.bytes("CnbModelData.readPartVertexBytes",
                (destination, count) -> NativeCnbRoutes
                        .cnbModelCopyPartVertexBytes(model, index, destination, count));
    }

    /**
     * Replaces one part's index payload.
     *
     * @param index the zero-based part index
     * @param bytes the index bytes; CNA copies them
     */
    public void setPartIndexBytes(int index, byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        CnbExtension.check("CnbModelData.setPartIndexBytes",
                NativeCnbRoutes.cnbModelSetPartIndexBytes(open(), index, bytes));
    }

    /**
     * Returns one part's index payload.
     *
     * @param index the zero-based part index
     * @return a fresh copy of the bytes
     */
    public byte[] readPartIndexBytes(int index) {
        long model = open();
        return CnbExtension.bytes("CnbModelData.readPartIndexBytes",
                (destination, count) -> NativeCnbRoutes
                        .cnbModelCopyPartIndexBytes(model, index, destination, count));
    }

    /**
     * Appends one mesh over parts that already exist.
     *
     * @param name the mesh's name, which may be empty
     * @param parentBone the bone index the mesh follows, or -1 for none
     * @param partIndices the parts the mesh draws, in order
     * @return the new mesh's index
     */
    public int addMesh(String name, int parentBone, int... partIndices) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(partIndices, "partIndices");
        long[] index = new long[1];
        CnbExtension.check("CnbModelData.addMesh", NativeCnbRoutes.cnbModelAddMesh(
                open(), CnbExtension.utf8(name), parentBone, partIndices.clone(), index));
        return (int) index[0];
    }

    /**
     * Returns one mesh.
     *
     * @param index the zero-based mesh index
     * @return its name, parent bone and parts
     */
    public CnbModelMesh getMesh(int index) {
        long model = open();
        long[] values = new long[3];
        CnbExtension.check("CnbModelData.getMesh",
                NativeCnbRoutes.cnbModelGetMesh(model, index, values));
        int[] parts = new int[Math.toIntExact(values[2])];
        long[] count = new long[1];
        CnbExtension.check("CnbModelData.getMesh", NativeCnbRoutes
                .cnbModelCopyMeshPartIndices(model, index, parts, count));
        List<Integer> indices = new ArrayList<>(parts.length);
        for (int part = 0; part < count[0]; part++) {
            indices.add(parts[part]);
        }
        return new CnbModelMesh(
                CnbExtension.text("CnbModelData.getMesh",
                        bytes -> NativeCnbRoutes.cnbModelGetMeshNameSize(model, index, bytes),
                        (destination, bytes) -> NativeCnbRoutes
                                .cnbModelCopyMeshName(model, index, destination, bytes)),
                (int) values[0], indices);
    }

    /** Returns every mesh in index order. */
    public List<CnbModelMesh> getMeshes() {
        return read(getInfo().MeshCount(), this::getMesh);
    }

    /**
     * Gives the model a skinning skeleton, replacing any it had.
     *
     * @param skeleton the joints, their hierarchy and their bind poses
     */
    public void setSkeleton(CnbSkeleton skeleton) {
        Objects.requireNonNull(skeleton, "skeleton");
        int joints = skeleton.getJointCount();
        int[] hierarchy = new int[joints];
        for (int joint = 0; joint < joints; joint++) {
            hierarchy[joint] = skeleton.Hierarchy().get(joint);
        }
        CnbExtension.check("CnbModelData.setSkeleton", NativeCnbRoutes.cnbModelSetSkeleton(
                open(), hierarchy, floats(skeleton.BindPose()),
                floats(skeleton.InverseBindPose()),
                skeleton.RootPrefix() == null ? null : floats(skeleton.RootPrefix())));
    }

    /** Removes the model's skinning skeleton, leaving its bones and parts. */
    public void clearSkeleton() {
        CnbExtension.check("CnbModelData.clearSkeleton",
                NativeCnbRoutes.cnbModelClearSkeleton(open()));
    }

    /**
     * Returns the model's skinning skeleton.
     *
     * @return the skeleton, or null when the model has none
     */
    public CnbSkeleton getSkeleton() {
        if (!getInfo().HasSkeleton()) {
            return null;
        }
        long model = open();
        long[] values = new long[2];
        CnbExtension.check("CnbModelData.getSkeleton",
                NativeCnbRoutes.cnbModelGetSkeleton(model, new byte[7], values));
        int joints = Math.toIntExact(values[0]);
        boolean hasRootPrefix = values[1] != 0;
        int[] hierarchy = new int[joints];
        long[] count = new long[1];
        CnbExtension.check("CnbModelData.getSkeleton",
                NativeCnbRoutes.cnbModelCopySkeletonHierarchy(model, hierarchy, count));
        List<Integer> parents = new ArrayList<>(joints);
        for (int joint = 0; joint < joints; joint++) {
            parents.add(hierarchy[joint]);
        }
        return new CnbSkeleton(parents,
                matrices(model, SKELETON_BIND_POSE, joints),
                matrices(model, SKELETON_INVERSE_BIND_POSE, joints),
                hasRootPrefix ? matrices(model, SKELETON_ROOT_PREFIX, joints) : null);
    }

    /**
     * Appends one baked directional light.
     *
     * @param light its direction and diffuse colour
     * @return the new light's index
     */
    public int addLight(CnbModelLight light) {
        Objects.requireNonNull(light, "light");
        Vector3 direction = Objects.requireNonNull(light.Direction(), "light.Direction");
        Vector3 diffuse = Objects.requireNonNull(light.DiffuseColor(), "light.DiffuseColor");
        long[] index = new long[1];
        CnbExtension.check("CnbModelData.addLight", NativeCnbRoutes.cnbModelAddLight(
                open(),
                new float[] {direction.X, direction.Y, direction.Z,
                    diffuse.X, diffuse.Y, diffuse.Z},
                index));
        return (int) index[0];
    }

    /**
     * Returns one baked light.
     *
     * @param index the zero-based light index
     * @return its direction and diffuse colour
     */
    public CnbModelLight getLight(int index) {
        float[] values = new float[6];
        CnbExtension.check("CnbModelData.getLight",
                NativeCnbRoutes.cnbModelGetLight(open(), index, values));
        return new CnbModelLight(new Vector3(values[0], values[1], values[2]),
                new Vector3(values[3], values[4], values[5]));
    }

    long handle() {
        return open();
    }

    /** Releases the model data. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        CnbExtension.check("CnbModelData.close", NativeCnbRoutes.cnbModelDestroy(handle));
    }

    private List<Matrix> matrices(long model, int set, int joints) {
        float[] values = new float[Math.multiplyExact(joints, MATRIX_FLOATS)];
        long[] count = new long[1];
        CnbExtension.check("CnbModelData.getSkeleton", NativeCnbRoutes
                .cnbModelCopySkeletonMatrices(model, set, values, count));
        List<Matrix> result = new ArrayList<>(joints);
        for (int joint = 0; joint < joints; joint++) {
            result.add(matrix(values, joint * MATRIX_FLOATS));
        }
        return result;
    }

    private static <T> List<T> read(int count, java.util.function.IntFunction<T> reader) {
        List<T> values = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            values.add(reader.apply(index));
        }
        return List.copyOf(values);
    }

    private static long[] partLeaves(CnbModelPart part) {
        return new long[] {
            part.VertexStride(), part.VertexCount(), part.IndexCount(),
            part.IndexElementSize(), part.PrimitiveTopology(), part.PrimitiveCount(),
            Objects.requireNonNull(part.EffectKind(), "part.EffectKind").ordinal(),
            part.VertexColorEnabled() ? 1 : 0, part.Unlit() ? 1 : 0,
        };
    }

    private static float[] floats(List<Matrix> matrices) {
        float[] values = new float[Math.multiplyExact(matrices.size(), MATRIX_FLOATS)];
        for (int index = 0; index < matrices.size(); index++) {
            System.arraycopy(floats(matrices.get(index)), 0, values,
                    index * MATRIX_FLOATS, MATRIX_FLOATS);
        }
        return values;
    }

    private static float[] floats(Matrix matrix) {
        return new float[] {
            matrix.M11, matrix.M12, matrix.M13, matrix.M14,
            matrix.M21, matrix.M22, matrix.M23, matrix.M24,
            matrix.M31, matrix.M32, matrix.M33, matrix.M34,
            matrix.M41, matrix.M42, matrix.M43, matrix.M44,
        };
    }

    private static Matrix matrix(float[] values, int offset) {
        return new Matrix(
                values[offset], values[offset + 1], values[offset + 2], values[offset + 3],
                values[offset + 4], values[offset + 5], values[offset + 6], values[offset + 7],
                values[offset + 8], values[offset + 9], values[offset + 10], values[offset + 11],
                values[offset + 12], values[offset + 13], values[offset + 14],
                values[offset + 15]);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This CnbModelData is closed");
            }
        }
        return handle;
    }
}
