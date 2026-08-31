package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The named shader-effect cache, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME. The factory needs a
 * device, and every claim here is about the cache -- what it compiles, when it compiles it again,
 * and what it refuses while a borrowed effect is out. Nothing here claims a shader <em>works</em>:
 * this renderer accepts source it will never run, which the C probe measured and which is stated
 * rather than asserted around.
 *
 * <p>The compile count is what makes the caching testable at all. Without it "acquire returned an
 * effect" would be the only observable, and a factory with no cache would pass that.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ShaderEffectFactoryTests {

    private static final String VERTEX =
            "attribute vec4 a_position;\nvoid main() { gl_Position = a_position; }\n";
    private static final String FRAGMENT =
            "void main() { gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0); }\n";

    @Test
    void aNameIsCompiledOnceAndFoundAfterwards() {
        GameProbe.run(probe -> {
            try (ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device())) {
                assertEquals(0L, factory.getCompileCount(), "a fresh factory has compiled none");
                assertFalse(factory.contains("tint"));

                Effect first = factory.acquire("tint", VERTEX, FRAGMENT);
                assertNotNull(first);
                assertTrue(factory.contains("tint"), "the name is in the cache now");
                assertEquals(1L, factory.getCompileCount());

                // The whole point: a second acquire of the same name does not compile again.
                // A factory that forwarded straight to the compiler would answer two here.
                Effect second = factory.acquire("tint", VERTEX, FRAGMENT);
                assertEquals(1L, factory.getCompileCount(), "the cache was hit");
                assertNotSame(first, second, "each acquire is its own view to dispose");

                // And the key is the name rather than the source: identical source under a new
                // name is a second compile.
                Effect other = factory.acquire("other", VERTEX, FRAGMENT);
                assertEquals(2L, factory.getCompileCount(),
                        "the name is the key, not the source");

                // Which cuts the other way too, and is the surprising half: new source under an
                // existing name is ignored, so a game editing a shader must change the name or
                // clear the cache.
                Effect stale = factory.acquire("tint", "attribute vec4 p;\nvoid main() { "
                        + "gl_Position = p * 2.0; }\n", FRAGMENT);
                assertEquals(2L, factory.getCompileCount(),
                        "new source under a cached name is not compiled");

                first.Dispose();
                second.Dispose();
                other.Dispose();
                stale.Dispose();
            }
        });
    }

    @Test
    void theFactoryRefusesToLetGoWhileAViewIsOutAndRecoversWhenItIsBack() {
        GameProbe.run(probe -> {
            ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device());
            Effect effect = factory.acquire("held", VERTEX, FRAGMENT);

            // The borrow discipline, and the reason it matters: clearing the cache under a live
            // effect would leave a Java object naming a destroyed native one.
            assertThrows(IllegalStateException.class, factory::clear);
            assertThrows(IllegalStateException.class, factory::close);

            // A refused close must leave a usable factory, not a closed-and-leaked one. This is
            // the assertion that distinguishes the two: the factory still answers.
            assertTrue(factory.contains("held"), "a refused close left the factory usable");
            assertEquals(1L, factory.getCompileCount());

            effect.Dispose();
            factory.clear();
            assertFalse(factory.contains("held"), "clearing emptied the cache");
            // Clearing empties the cache and leaves the count alone, which is what makes it a
            // measure of work done rather than of what is held.
            assertEquals(1L, factory.getCompileCount(), "clearing does not reset the count");

            factory.close();
            factory.close();
            assertThrows(IllegalStateException.class, () -> factory.contains("held"));
        });
    }

    @Test
    void disposingAViewDoesNotDestroyTheCachedEffect() {
        GameProbe.run(probe -> {
            try (ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device())) {
                Effect first = factory.acquire("shared", VERTEX, FRAGMENT);
                Effect second = factory.acquire("shared", VERTEX, FRAGMENT);
                first.Dispose();

                // If disposing a view had destroyed the cached effect, the other view would now
                // name a dead object and the cache would have lost the entry. Both hold.
                assertTrue(factory.contains("shared"));
                assertNotNull(second.getCurrentTechnique(),
                        "the other view is still a live effect");
                assertEquals(1L, factory.getCompileCount(),
                        "and the cache did not have to compile it again");

                // Acquiring after a disposal still hits the cache rather than recompiling.
                Effect third = factory.acquire("shared", VERTEX, FRAGMENT);
                assertEquals(1L, factory.getCompileCount());

                second.Dispose();
                third.Dispose();
            }
        });
    }

    @Test
    void manyAcquiresAndDisposalsLeaveTheFactoryClosable() {
        GameProbe.run(probe -> {
            ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device());
            // The borrow count has to come back to zero exactly. One missed decrement in two
            // hundred acquires and the close below is refused.
            for (int attempt = 0; attempt < 200; attempt++) {
                Effect effect = factory.acquire("loop", VERTEX, FRAGMENT);
                effect.Dispose();
            }
            assertEquals(1L, factory.getCompileCount(), "two hundred acquires, one compile");
            factory.close();
        });
    }

    @Test
    void anEmptyNameIsRefusedAndNullsNeverReachCna() {
        GameProbe.run(probe -> {
            try (ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device())) {
                // CNA requires a stable non-empty key, and refusing it here is better than a
                // cache with an entry nothing can ask for.
                assertThrows(IllegalArgumentException.class,
                        () -> factory.acquire("", VERTEX, FRAGMENT));
                assertThrows(NullPointerException.class,
                        () -> factory.acquire(null, VERTEX, FRAGMENT));
                assertThrows(NullPointerException.class,
                        () -> factory.acquire("name", null, FRAGMENT));
                assertThrows(NullPointerException.class,
                        () -> factory.acquire("name", VERTEX, null));
                assertThrows(NullPointerException.class, () -> factory.contains(null));
                assertThrows(NullPointerException.class, () -> ShaderEffectFactory.create(null));
                assertEquals(0L, factory.getCompileCount(),
                        "no refused acquire compiled anything");
            }
        });
    }

    @Test
    void thisRendererAcceptsSourceItCouldNeverRun() {
        GameProbe.run(probe -> {
            try (ShaderEffectFactory factory = ShaderEffectFactory.create(probe.device())) {
                // Stated rather than asserted around: the headless renderer has no compiler, so
                // it takes any text at all. A game must not read "acquire succeeded" as "this
                // shader compiles" -- on a real renderer this line would throw.
                Effect nonsense = factory.acquire("nonsense", "this is not a shader", FRAGMENT);
                assertNotNull(nonsense);
                assertEquals(1L, factory.getCompileCount());
                nonsense.Dispose();
            }
        });
    }
}
