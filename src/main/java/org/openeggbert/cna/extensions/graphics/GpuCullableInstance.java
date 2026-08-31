package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * One instance a {@link GpuInstanceCuller} may or may not keep.
 *
 * <p>A CNA extension: a world transform and the bounds it occupies, in the space the culling
 * frustum is built in. The pair is what the compute shader tests, so it is what a game uploads.
 *
 * @param world the instance's world transform
 * @param bounds its bounds, in the same space the culling frustum is built in
 */
public record GpuCullableInstance(Matrix world, BoundingBox bounds) {

    /** Copies both values, because XNA's are mutable and a record's components are not. */
    public GpuCullableInstance {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(bounds, "bounds");
        world = new Matrix(world);
        bounds = new BoundingBox(new Vector3(bounds.Min), new Vector3(bounds.Max));
    }

    /**
     * Returns the instance CNA itself defaults to.
     *
     * @return the default instance
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static GpuCullableInstance createDefault() {
        GraphicsExtension.requireBackend();
        float[] leaves = new float[EngineValues.MATRIX_LEAVES + EngineValues.BOX_LEAVES];
        GraphicsExtension.check("GpuCullableInstance.createDefault",
                NativeEngineLayerRoutes.gpuCullableInstanceInit(leaves));
        return new GpuCullableInstance(EngineValues.matrix(leaves, 0),
                new BoundingBox(
                        new Vector3(leaves[16], leaves[17], leaves[18]),
                        new Vector3(leaves[19], leaves[20], leaves[21])));
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        float[] leaves = new float[EngineValues.MATRIX_LEAVES + EngineValues.BOX_LEAVES];
        System.arraycopy(EngineValues.floats(world, "world"), 0, leaves, 0,
                EngineValues.MATRIX_LEAVES);
        System.arraycopy(EngineValues.floats(bounds, "bounds"), 0, leaves,
                EngineValues.MATRIX_LEAVES, EngineValues.BOX_LEAVES);
        return leaves;
    }
}
