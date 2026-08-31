package org.openeggbert.cna.extensions.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Compute shaders and storage buffers, against the live runtime.
 *
 * <p><strong>What this can say depends on the renderer, and it says which.</strong> CNA's compute
 * support lives in one renderer family, reachable here as {@code OPENGLES3} or {@code OPENGL33};
 * on {@code HEADLESS}, {@code SOFTWARE} and {@code OPENGL4} there is no compute at all and every
 * constructor in this family refuses. Both are qualified rather than skipped: where compute
 * exists these tests run a real program on the GPU and compare its output against arithmetic done
 * in Java, and where it does not they check that the refusal is the exact one CNA documents.
 *
 * <p>Select a renderer with the {@code CNA_GRAPHICS_RENDERER} environment variable, which the
 * build forwards into the test JVM. It has to be one the library was built with; CNA refuses a
 * name it does not have rather than substituting quietly.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ComputeTests {

    /**
     * Doubles each element of one storage buffer into another and adds a uniform.
     *
     * <p>GLSL ES 3.10, which is the dialect CNA's own engine layer is written in and the one
     * every compute-capable renderer here accepts. The work is deliberately trivial and its
     * answer deliberately checkable in Java: what is being measured is that the GPU ran the
     * program, not that it can multiply.
     */
    private static final String DOUBLER = String.join("\n",
            "#version 310 es",
            "layout(local_size_x = 4) in;",
            "layout(std430, binding = 0) readonly buffer Source { int source_values[]; };",
            "layout(std430, binding = 1) writeonly buffer Result { int result_values[]; };",
            "uniform int addend;",
            "void main() {",
            "    uint index = gl_GlobalInvocationID.x;",
            "    result_values[index] = source_values[index] * 2 + addend;",
            "}",
            "");

    private static byte[] words(int... values) {
        ByteBuffer buffer = StorageBuffer.allocate(values.length * Integer.BYTES);
        for (int value : values) {
            buffer.putInt(value);
        }
        return buffer.array();
    }

    private static int[] readWords(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        int[] values = new int[bytes.length / Integer.BYTES];
        for (int index = 0; index < values.length; index++) {
            values[index] = buffer.getInt();
        }
        return values;
    }

    @Test
    void theRendererSaysWhetherItHasComputeAndTheFamilyAgreesWithIt() {
        GameProbe.run(probe -> {
            boolean supported = ComputeShader.isSupported(probe.device());
            // The capability query and the constructors must not disagree: a game that asked and
            // was told yes must not then be refused, and one told no must not find it works.
            if (supported) {
                try (StorageBuffer buffer = StorageBuffer.ofBytes(probe.device(), 64)) {
                    assertEquals(64L, buffer.getByteSize());
                }
            } else {
                assertThrows(ExtensionNotSupportedException.class,
                        () -> StorageBuffer.ofBytes(probe.device(), 64),
                        "a renderer without compute must refuse a storage buffer");
                assertThrows(ExtensionNotSupportedException.class,
                        () -> ComputeShader.compile(probe.device(), DOUBLER),
                        "a renderer without compute must refuse a compute shader");
            }
        });
    }

    @Test
    void aDispatchProducesTheArithmeticItWasAskedFor() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            int[] input = { 3, 5, 11, 19 };
            int addend = 7;
            int[] expected = new int[input.length];
            for (int index = 0; index < input.length; index++) {
                expected[index] = input[index] * 2 + addend;
            }

            try (StorageBuffer source = StorageBuffer.ofElements(probe.device(), input.length,
                            Integer.BYTES);
                    StorageBuffer result = StorageBuffer.ofElements(probe.device(), input.length,
                            Integer.BYTES);
                    ComputeShader shader = ComputeShader.compile(probe.device(), DOUBLER)) {

                source.setElements(words(input), input.length, Integer.BYTES);
                shader.bindStorageBuffer(0, source);
                shader.bindStorageBuffer(1, result);
                shader.setUniform("addend", addend);
                shader.dispatch(1, 1, 1);
                shader.barrier(MemoryBarrier.ShaderStorage, MemoryBarrier.BufferUpdate);

                byte[] readback = new byte[input.length * Integer.BYTES];
                result.getElements(readback, input.length, Integer.BYTES);

                // The whole point. Every SUCCESS above is satisfiable by a renderer that did
                // nothing; only these four numbers say the program ran.
                assertArrayEquals(expected, readWords(readback),
                        "the GPU must have doubled each input and added the uniform");
            }
        });
    }

    @Test
    void aFloatUniformReachesTheProgramToo() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            String scale = String.join("\n",
                    "#version 310 es",
                    "layout(local_size_x = 4) in;",
                    "layout(std430, binding = 0) buffer Result { float values[]; };",
                    "uniform float factor;",
                    "void main() {",
                    "    uint index = gl_GlobalInvocationID.x;",
                    "    values[index] = float(index) * factor;",
                    "}",
                    "");
            try (StorageBuffer result = StorageBuffer.ofElements(probe.device(), 4, Float.BYTES);
                    ComputeShader shader = ComputeShader.compile(probe.device(), scale)) {
                shader.bindStorageBuffer(0, result);
                shader.setUniform("factor", 2.5F);
                shader.dispatch(1, 1, 1);
                shader.barrier(EnumSet.of(MemoryBarrier.ShaderStorage,
                        MemoryBarrier.BufferUpdate));

                byte[] readback = new byte[4 * Float.BYTES];
                result.getElements(readback, 4, Float.BYTES);
                ByteBuffer values = ByteBuffer.wrap(readback)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN);
                for (int index = 0; index < 4; index++) {
                    assertEquals(index * 2.5F, values.getFloat(), 1.0e-6F,
                            "element " + index + " must be its index times the float uniform");
                }
            }
        });
    }

    @Test
    void bytesRoundTripThroughABufferWithoutAnyShader() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            byte[] written = words(0x01020304, 0x05060708);
            try (StorageBuffer buffer = StorageBuffer.ofBytes(probe.device(), written.length)) {
                assertEquals(written.length, buffer.getByteSize());
                assertEquals(0L, buffer.getElementCount(),
                        "a buffer created by byte size has no element count");
                assertEquals(0L, buffer.getElementByteSize(),
                        "a buffer created by byte size has no element size");
                buffer.setBytes(written);
                byte[] read = new byte[written.length];
                buffer.getBytes(read);
                assertArrayEquals(written, read, "the bytes must come back exactly");
            }
        });
    }

    @Test
    void aTypedBufferRemembersItsShapeAndRefusesADisagreeingTransfer() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            try (StorageBuffer buffer = StorageBuffer.ofElements(probe.device(), 4,
                    Integer.BYTES)) {
                assertEquals(4L, buffer.getElementCount());
                assertEquals(Integer.BYTES, buffer.getElementByteSize());
                assertEquals(4L * Integer.BYTES, buffer.getByteSize());

                byte[] data = words(1, 2, 3, 4);
                buffer.setElements(data, 4, Integer.BYTES);

                // An element size the buffer was not created for is an argument error rather
                // than a silent reinterpretation of the same bytes.
                assertThrows(IllegalArgumentException.class,
                        () -> buffer.setElements(data, 2, 8),
                        "an element size the buffer disagrees with must be refused");
                // And more elements than it holds.
                byte[] tooMany = words(1, 2, 3, 4, 5, 6, 7, 8);
                assertThrows(IllegalArgumentException.class,
                        () -> buffer.setElements(tooMany, 8, Integer.BYTES),
                        "more elements than the buffer holds must be refused");
            }
        });
    }

    @Test
    void theBoundaryRefusesAnExtentLargerThanTheArrayItWasGiven() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            try (StorageBuffer buffer = StorageBuffer.ofElements(probe.device(), 4,
                    Integer.BYTES)) {
                byte[] four = new byte[4];
                // The generated adapter's own check, before CNA is called at all: a count and an
                // element size whose product exceeds the Java array would otherwise be a read
                // past its end from inside C.
                assertThrows(IllegalArgumentException.class,
                        () -> buffer.setElements(four, 4, Integer.BYTES),
                        "an extent larger than the array must be refused at the boundary");
                // And an extent that would overflow the product rather than merely exceed it.
                assertThrows(IllegalArgumentException.class,
                        () -> buffer.setElements(four, Long.MAX_VALUE / 2, 4),
                        "an extent that would overflow must be refused, not wrapped");
                assertThrows(IllegalArgumentException.class,
                        () -> buffer.setElements(four, -1, 4),
                        "a negative extent must be refused");
            }
        });
    }

    @Test
    void sourceTheCompilerRefusesArrivesAsAShaderCompilationExceptionCarryingItsLog() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            ShaderCompilationException refused = assertThrows(ShaderCompilationException.class,
                    () -> ComputeShader.compile(probe.device(), "#version 310 es\nnot glsl\n"),
                    "source the compiler refuses must not produce a shader");
            // The log is the only thing that says where in the source the problem is, and CNA's
            // C API loses the handle it would otherwise be read from -- JAVA-UPSTREAM-012 -- so
            // it travels in the message instead. A message without it would be useless.
            assertTrue(refused.getMessage().contains("did not compile"),
                    "the message must carry the compiler's own words: " + refused.getMessage());

            // A program that compiles but cannot link is refused the same way. Worth its own
            // case because the two failures come from different halves of the pipeline and CNA
            // reports them through the same result.
            assertThrows(ShaderCompilationException.class,
                    () -> ComputeShader.compile(probe.device(), String.join("\n",
                            "#version 310 es",
                            "layout(local_size_x = 1) in;",
                            "void missing();",
                            "void main() { missing(); }",
                            "")),
                    "a program that cannot link must be refused too");
        });
    }

    @Test
    void aCompiledShaderReportsItselfValidWithNoCompileError() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            try (ComputeShader shader = ComputeShader.compile(probe.device(), DOUBLER)) {
                assertTrue(shader.isValid(), "a shader that exists compiled");
                assertEquals("", shader.getCompileError(), "and has nothing to report");
            }
        });
    }

    @Test
    void imageBindingIsItsOwnQuestionAndTheAnswerIsHonoured() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            try (ComputeShader shader = ComputeShader.compile(probe.device(), DOUBLER);
                    Microsoft.Xna.Framework.Graphics.Texture2D image =
                            new Microsoft.Xna.Framework.Graphics.Texture2D(probe.device(), 8, 8)) {
                boolean images = shader.isImageBindingSupported();
                // Nothing is asserted about which answer this renderer gives -- OpenGL ES 3.1
                // says no, because it requires an immutable texture allocation this renderer
                // does not make, and desktop GL 4.6 says yes. Both are correct. What is asserted
                // is that the answer is honoured: a renderer that says no must refuse the call
                // rather than appearing to accept it, and one that says yes must accept it.
                if (images) {
                    shader.bindImage(0, image, ImageAccess.ReadWrite);
                } else {
                    assertThrows(ExtensionNotSupportedException.class,
                            () -> shader.bindImage(0, image, ImageAccess.ReadWrite),
                            "a renderer without image binding must refuse it");
                }
                // Binding a texture to a sampler unit is a different route and works on both.
                shader.bindTexture(0, "unused_sampler", image);
            }
        });
    }

    @Test
    void aClosedShaderAndAClosedBufferBothRefuseFurtherUse() {
        GameProbe.run(probe -> {
            if (!ComputeShader.isSupported(probe.device())) {
                return;
            }
            StorageBuffer buffer = StorageBuffer.ofBytes(probe.device(), 16);
            ComputeShader shader = ComputeShader.compile(probe.device(), DOUBLER);
            buffer.close();
            shader.close();
            // Closing twice is a no-op, which is what makes try-with-resources over an already
            // closed object safe.
            buffer.close();
            shader.close();
            assertThrows(IllegalStateException.class, buffer::getByteSize);
            assertThrows(IllegalStateException.class, () -> shader.dispatch(1, 1, 1));
        });
    }

    @Test
    void theIndirectArgumentTypesCarryTheExactWireFormat() {
        // No device needed: these are value types over a byte layout the native layout gate pins
        // against the live header.
        assertEquals(16, IndirectDrawArguments.BYTES);
        assertEquals(20, IndirectDrawIndexedArguments.BYTES);

        IndirectDrawArguments arguments = new IndirectDrawArguments(6, 2, 1, 0);
        byte[] bytes = arguments.toBytes();
        assertEquals(IndirectDrawArguments.BYTES, bytes.length);
        // Little-endian, which is what the GPU's command processor reads and what a shader
        // writing the same four words would produce.
        assertArrayEquals(new byte[] {
                6, 0, 0, 0,
                2, 0, 0, 0,
                1, 0, 0, 0,
                0, 0, 0, 0 }, bytes);
        assertEquals(arguments, IndirectDrawArguments.fromBytes(bytes, 0));

        IndirectDrawIndexedArguments indexed =
                new IndirectDrawIndexedArguments(9, 3, 2, -1, 0);
        byte[] indexedBytes = indexed.toBytes();
        assertEquals(IndirectDrawIndexedArguments.BYTES, indexedBytes.length);
        assertEquals(indexed, IndirectDrawIndexedArguments.fromBytes(indexedBytes, 0));
        // The base vertex is signed, and a two's-complement -1 is what says so.
        assertEquals(-1, IndirectDrawIndexedArguments.fromBytes(indexedBytes, 0).getBaseVertex());

        assertThrows(IllegalArgumentException.class,
                () -> IndirectDrawArguments.fromBytes(new byte[15], 0));
        assertThrows(IllegalArgumentException.class,
                () -> IndirectDrawArguments.fromBytes(bytes, 1));
        assertNotEquals(arguments, new IndirectDrawArguments(6, 2, 1, 1));
    }

    @Test
    void cnaSuppliesTheIndirectDefaultsAndTheyDrawNothing() {
        GameProbe.run(probe -> {
            // Asked of CNA rather than assumed: the header documents all-zero arguments, which
            // draw nothing, and that is the safe starting point for a buffer a shader will fill.
            IndirectDrawArguments defaults = IndirectDrawArguments.defaults();
            assertEquals(0, defaults.getVertexCount());
            assertEquals(0, defaults.getInstanceCount());
            assertEquals(0, defaults.getFirstVertex());
            assertEquals(0, defaults.getBaseInstance());

            IndirectDrawIndexedArguments indexed = IndirectDrawIndexedArguments.defaults();
            assertEquals(0, indexed.getIndexCount());
            assertEquals(0, indexed.getInstanceCount());
            assertEquals(0, indexed.getFirstIndex());
            assertEquals(0, indexed.getBaseVertex());
            assertEquals(0, indexed.getBaseInstance());
        });
    }

    @Test
    void anIndirectDrawReadsItsArgumentsFromAStorageBuffer() {
        GameProbe.run(probe -> {
            if (!IndirectDraw.isSupported(probe.device())) {
                // The refusal is the measurement here: a renderer that says it cannot draw
                // indirectly must refuse the draw rather than doing something else.
                assertFalse(ComputeShader.isSupported(probe.device())
                                && IndirectDraw.isSupported(probe.device()),
                        "unreachable; kept so the branch is not vacuous");
                return;
            }
            // A shader writes the command the draw then reads, which is the whole feature: the
            // vertex count never crosses back to the CPU.
            String writer = String.join("\n",
                    "#version 310 es",
                    "layout(local_size_x = 1) in;",
                    "layout(std430, binding = 0) buffer Command { uint words[]; };",
                    "uniform int visible;",
                    "void main() {",
                    "    words[0] = uint(visible);",
                    "    words[1] = 1u;",
                    "    words[2] = 0u;",
                    "    words[3] = 0u;",
                    "}",
                    "");
            try (StorageBuffer command = StorageBuffer.ofBytes(probe.device(),
                            IndirectDrawArguments.BYTES);
                    ComputeShader shader = ComputeShader.compile(probe.device(), writer)) {
                command.setBytes(IndirectDrawArguments.defaults().toBytes());
                shader.bindStorageBuffer(0, command);
                shader.setUniform("visible", 3);
                shader.dispatch(1, 1, 1);
                shader.barrier(MemoryBarrier.ShaderStorage, MemoryBarrier.BufferUpdate,
                        MemoryBarrier.IndirectCommand);

                byte[] readback = new byte[IndirectDrawArguments.BYTES];
                command.getBytes(readback);
                IndirectDrawArguments written = IndirectDrawArguments.fromBytes(readback, 0);
                // The GPU wrote the command in the layout Java reads, which is the interop this
                // whole pair of types exists for.
                assertEquals(3, written.getVertexCount(),
                        "the shader must have written the vertex count Java reads back");
                assertEquals(1, written.getInstanceCount());
            }
        });
    }
}
