package org.openeggbert.cna.extensions.content;

import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code .cnb} model family, against the live runtime.
 *
 * <p>The whole graph goes out through CNA's encoder and back in through its decoder, and what is
 * compared is the graph, not a result code: the bones with their transforms, the parts with their
 * bytes, the meshes with the parts they name, the skeleton's three matrix sets, and the lights.
 *
 * <p>This path was probed in C before any of it was bound. JAVA-UPSTREAM-004 found CNA's
 * <em>content manager</em> model loader segfaulting during teardown for any asset with a mesh
 * part, and the {@code .cnb} family is a different code path -- but that was measured rather than
 * assumed, with a build-encode-decode-destroy cycle in C, before a line of this existed.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class CnbModelTests {

    /** A triangle: three vertices of three floats, drawn by three sixteen-bit indices. */
    private static final float[] VERTICES = {0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f};
    private static final short[] INDICES = {0, 1, 2};

    private static byte[] vertexBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(VERTICES.length * 4)
                .order(ByteOrder.LITTLE_ENDIAN);
        for (float value : VERTICES) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }

    private static byte[] indexBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(INDICES.length * 2)
                .order(ByteOrder.LITTLE_ENDIAN);
        for (short value : INDICES) {
            buffer.putShort(value);
        }
        return buffer.array();
    }

    private static CnbModelPart trianglePart() {
        return new CnbModelPart(12, 3, 3, 2, 4, 1, CnbEffectKind.Pbr, true, false);
    }

    private static Matrix scaled(float value) {
        Matrix matrix = Matrix.getIdentity();
        matrix.M11 = value;
        matrix.M22 = value;
        matrix.M33 = value;
        return matrix;
    }

    private static CnbModelData buildModel() {
        CnbModelData model = CnbModelData.create();
        try {
            model.setFlags(true, true);
            assertEquals(0, model.addBone("root", -1, scaled(1.0f)));
            assertEquals(1, model.addBone("child", 0, scaled(2.0f)));
            assertEquals(0, model.addPart(trianglePart(), "triangle", ""));
            model.setPartVertexBytes(0, vertexBytes());
            model.setPartIndexBytes(0, indexBytes());
            assertEquals(0, model.addMesh("body", 1, 0));
            model.setSkeleton(new CnbSkeleton(
                    List.of(-1, 0),
                    List.of(scaled(1.0f), scaled(2.0f)),
                    List.of(scaled(3.0f), scaled(4.0f)),
                    List.of(scaled(5.0f), scaled(6.0f))));
            model.addLight(new CnbModelLight(
                    new Vector3(0f, -1f, 0f), new Vector3(0.25f, 0.5f, 0.75f)));
            return model;
        } catch (RuntimeException failure) {
            model.close();
            throw failure;
        }
    }

    private static void expectReferenceModel(CnbModelData model) {
        CnbModelInfo info = model.getInfo();
        assertEquals(2, info.BoneCount());
        assertEquals(1, info.PartCount());
        assertEquals(1, info.MeshCount());
        assertEquals(1, info.LightCount());
        assertTrue(info.HasSkeleton());
        assertTrue(info.AppliesGltfLightingPolicy());
        assertTrue(info.HasBoneHierarchy());

        List<CnbModelBone> bones = model.getBones();
        assertEquals(List.of("root", "child"), bones.stream().map(CnbModelBone::Name).toList());
        assertEquals(-1, bones.get(0).Parent());
        assertEquals(0, bones.get(1).Parent());
        // Each bone was given a different scale, so a swap or a transposed matrix shows.
        assertEquals(1.0f, bones.get(0).Transform().M11);
        assertEquals(2.0f, bones.get(1).Transform().M22);

        assertEquals(trianglePart(), model.getPart(0));
        assertEquals("triangle", model.getPartName(0));
        assertEquals("", model.getPartExternalEffect(0));
        assertArrayEquals(vertexBytes(), model.readPartVertexBytes(0));
        assertArrayEquals(indexBytes(), model.readPartIndexBytes(0));

        CnbModelMesh mesh = model.getMesh(0);
        assertEquals("body", mesh.Name());
        assertEquals(1, mesh.ParentBone());
        assertEquals(List.of(0), mesh.PartIndices());

        CnbSkeleton skeleton = model.getSkeleton();
        assertEquals(2, skeleton.getJointCount());
        assertEquals(List.of(-1, 0), skeleton.Hierarchy());
        // Three matrix sets with six distinct scales: a set read from the wrong slot cannot
        // pass, which is the point of not using identity anywhere.
        assertEquals(1.0f, skeleton.BindPose().get(0).M11);
        assertEquals(2.0f, skeleton.BindPose().get(1).M11);
        assertEquals(3.0f, skeleton.InverseBindPose().get(0).M11);
        assertEquals(4.0f, skeleton.InverseBindPose().get(1).M11);
        assertEquals(5.0f, skeleton.RootPrefix().get(0).M11);
        assertEquals(6.0f, skeleton.RootPrefix().get(1).M11);

        CnbModelLight light = model.getLight(0);
        assertEquals(new Vector3(0f, -1f, 0f), light.Direction());
        assertEquals(new Vector3(0.25f, 0.5f, 0.75f), light.DiffuseColor());
    }

    @Test
    void theWholeGraphSurvivesTheContainer() {
        byte[] file;
        try (CnbModelData model = buildModel()) {
            expectReferenceModel(model);
            file = Cnb.encodeModel(model, "models/probe");
        }
        try (CnbDocument document = CnbDocument.parse(
                     file, "probe.cnb", CnbReadLimits.standard())) {
            assertEquals(CnbAssetType.MODEL, document.getAssetType());
            try (CnbModelData decoded = document.decodeModel()) {
                expectReferenceModel(decoded);
            }
        }
    }

    @Test
    void aSkeletonIsSeparateFromTheBones() {
        try (CnbModelData model = CnbModelData.create()) {
            model.addBone("root", -1, Matrix.getIdentity());
            // Bones without a skeleton is an ordinary shape: the bones are the asset's node
            // hierarchy, the skeleton is what a skinned part is weighted against.
            assertEquals(1, model.getInfo().BoneCount());
            assertFalse(model.getInfo().HasSkeleton());
            assertNull(model.getSkeleton());

            model.setSkeleton(new CnbSkeleton(List.of(-1), List.of(scaled(2.0f)),
                    List.of(scaled(3.0f)), null));
            assertTrue(model.getInfo().HasSkeleton());
            // A source that carried no prefix is not the same as one that carried an identity,
            // so an absent prefix comes back absent.
            assertNull(model.getSkeleton().RootPrefix());

            model.clearSkeleton();
            assertFalse(model.getInfo().HasSkeleton());
            assertEquals(1, model.getInfo().BoneCount(), "clearing a skeleton keeps the bones");
        }

        // The record refuses a skeleton whose parts describe different joints, because the
        // native call would otherwise be given arrays that disagree.
        assertThrows(IllegalArgumentException.class, () -> new CnbSkeleton(
                List.of(-1, 0), List.of(Matrix.getIdentity()),
                List.of(Matrix.getIdentity(), Matrix.getIdentity()), null));
    }

    @Test
    void aDescriptionThatDoesNotMatchItsBytesIsRefusedByTheEncoder() {
        try (CnbModelData model = CnbModelData.create()) {
            model.addBone("root", -1, Matrix.getIdentity());
            model.addPart(trianglePart(), "triangle", "");
            // Three vertices at a stride of twelve is thirty-six bytes; this part is given
            // twenty-four. The handle takes it -- it stores a description and a payload -- and
            // the encoder is where the two have to agree, because that is the point at which a
            // file someone else would read gets written.
            model.setPartVertexBytes(0, new byte[24]);
            model.setPartIndexBytes(0, indexBytes());
            model.addMesh("body", 0, 0);
            assertThrows(CnbFormatException.class,
                    () -> Cnb.encodeModel(model, "models/short"));
        }
    }

    @Test
    void anExternalEffectNamesItsOwnAsset() {
        try (CnbModelData model = CnbModelData.create()) {
            CnbModelPart part = new CnbModelPart(12, 3, 3, 2, 4, 1,
                    CnbEffectKind.External, false, true);
            model.addPart(part, "custom", "effects/water");
            assertEquals(CnbEffectKind.External, model.getPart(0).EffectKind());
            assertEquals("effects/water", model.getPartExternalEffect(0));
            assertTrue(model.getPart(0).Unlit());

            // Replacing the description leaves the bytes and the names alone.
            model.setPartVertexBytes(0, vertexBytes());
            model.setPart(0, trianglePart());
            assertEquals(trianglePart(), model.getPart(0));
            assertEquals("custom", model.getPartName(0));
            assertArrayEquals(vertexBytes(), model.readPartVertexBytes(0));
        }
    }

    @Test
    void aClosedModelRefusesEveryOperation() {
        CnbModelData model = CnbModelData.create();
        model.close();
        model.close();
        assertThrows(IllegalStateException.class, model::getInfo);
        assertThrows(IllegalStateException.class,
                () -> model.addBone("late", -1, Matrix.getIdentity()));
    }
}
