package org.openeggbert.cna.extensions.graphics;

import java.util.Set;

/**
 * One kind of memory access a compute barrier orders against later commands.
 *
 * <p>A CNA extension. A dispatch returns long before the GPU has run it, and a later command that
 * reads what the shader wrote may otherwise read the previous contents. A barrier says which kind
 * of later read has to wait.
 *
 * <p>These are bits rather than an ordered list, so {@link ComputeShader#barrier} takes a set of
 * them: ordering storage-buffer reads and index-array reads is one barrier with two bits, not two
 * barriers.
 */
public enum MemoryBarrier {

    /** Orders vertex-attribute array reads. */
    VertexAttribArray(0),

    /** Orders element (index) array reads. */
    ElementArray(1),

    /** Orders uniform reads. */
    Uniform(2),

    /** Orders texture fetches. */
    TextureFetch(3),

    /** Orders shader image accesses. */
    ShaderImageAccess(4),

    /** Orders shader storage-buffer accesses -- what a dispatch that filled a
     * {@link StorageBuffer} needs before the CPU or another shader reads it. */
    ShaderStorage(5),

    /** Orders buffer updates. */
    BufferUpdate(6),

    /** Orders framebuffer accesses. */
    Framebuffer(7),

    /** Orders indirect-command reads -- what a compute shader that wrote an
     * {@link IndirectDrawArguments} into a buffer needs before the draw reads it. */
    IndirectCommand(8);

    private final int bit;

    MemoryBarrier(int bit) {
        this.bit = bit;
    }

    int mask() {
        return 1 << bit;
    }

    /** Folds a set of bits into the mask CNA takes. */
    static int maskOf(Set<MemoryBarrier> bits) {
        int mask = 0;
        for (MemoryBarrier bit : bits) {
            mask |= bit.mask();
        }
        return mask;
    }
}
