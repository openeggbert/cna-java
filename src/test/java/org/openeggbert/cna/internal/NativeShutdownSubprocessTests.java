package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.Graphics.BasicEffect;
import Microsoft.Xna.Framework.Graphics.BufferUsage;
import Microsoft.Xna.Framework.Graphics.DynamicIndexBuffer;
import Microsoft.Xna.Framework.Graphics.DynamicVertexBuffer;
import Microsoft.Xna.Framework.Graphics.IndexElementSize;
import Microsoft.Xna.Framework.Graphics.VertexPositionColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Isolates process teardown with a live native ownership graph from the Gradle test worker. */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class NativeShutdownSubprocessTests {

    private static ShutdownGame retainedGame;

    @Test
    void jvmShutdownWithLiveEffectChildrenBuffersAndCallbacksExitsCleanly() throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        String jniLibrary = System.getProperty("cna.java.jniLibrary");
        if (jniLibrary != null && !jniLibrary.isBlank()) {
            command.add("-Dcna.java.jniLibrary=" + jniLibrary);
        }
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(NativeShutdownSubprocessTests.class.getName());
        command.add("child");

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new AssertionError("Native shutdown subprocess did not terminate within 30 seconds");
        }
        String output = new String(
                process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(0, process.exitValue(), output);
        assertTrue(output.contains("CNA_JAVA_SHUTDOWN_GRAPH_READY"), output);
    }

    public static void main(String[] arguments) throws IOException {
        if (arguments.length != 1 || !"child".equals(arguments[0])) {
            throw new IllegalArgumentException("Expected the child marker");
        }
        retainedGame = new ShutdownGame();
        retainedGame.RunOneFrame();
        if (!retainedGame.completed) {
            throw new IllegalStateException("Shutdown ownership graph was not initialized");
        }
        System.out.println("CNA_JAVA_SHUTDOWN_GRAPH_READY");
        // Deliberately retain the live graph. Natural JVM/process teardown is the behavior under test.
    }

    private static final class ShutdownGame extends Game {
        private boolean completed;
        private BasicEffect effect;
        private DynamicVertexBuffer vertices;
        private DynamicIndexBuffer indices;

        @Override
        protected void Update(GameTime gameTime) {
            effect = new BasicEffect(getGraphicsDevice());
            effect.getDirectionalLight0().setEnabled(true);
            effect.getCurrentTechnique().getPasses().get(0);

            vertices = new DynamicVertexBuffer(
                    getGraphicsDevice(), VertexPositionColor.class, 3, BufferUsage.WriteOnly);
            indices = new DynamicIndexBuffer(
                    getGraphicsDevice(), IndexElementSize.SixteenBits, 3, BufferUsage.WriteOnly);
            vertices.addContentLostListener((sender, args) -> { });
            indices.addContentLostListener((sender, args) -> { });
            getGraphicsDevice().SetVertexBuffer(vertices);
            getGraphicsDevice().setIndices(indices);
            completed = true;
        }
    }
}
