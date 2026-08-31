package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The three light value types, against the live runtime.
 *
 * <p>VERIFIED_PURE: a light is a value and needs no device. What is worth checking is that the
 * defaults are CNA's rather than this projection's, that the immutability actually holds against
 * XNA's mutable {@link Vector3}, and that each field reaches the native structure at its own
 * offset -- which the gizmo tests in {@code DebugDrawTests} then confirm from the other side, by
 * making CNA draw geometry whose shape depends on the values that arrived.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class LightTests {

    @Test
    void theDefaultsAreCnasOwn() {
        DirectionalLight directional = DirectionalLight.createDefault();
        // CNA's header says the default direction points straight down and the default colour is
        // white; asked of CNA rather than written down, but the header's own words are what make
        // these assertions meaningful rather than tautological.
        assertEquals(new Vector3(0f, -1f, 0f), directional.getDirection(),
                "the default points straight down");
        assertEquals(new Vector3(1f, 1f, 1f), directional.getColor(), "the default is white");
        assertTrue(directional.getIntensity() > 0f);
        assertFalse(directional.getCastsShadows(), "a light does not ask for a shadow by default");

        PointLight point = PointLight.createDefault();
        assertEquals(new Vector3(1f, 1f, 1f), point.getColor());
        assertTrue(point.getRange() > 0f, "a range of zero would light nothing");

        SpotLight spot = SpotLight.createDefault();
        assertEquals(new Vector3(0f, -1f, 0f), spot.getDirection());
        assertTrue(spot.getRange() > 0f);
        assertTrue(spot.getInnerAngle() >= 0f);
        assertTrue(spot.getOuterAngle() > spot.getInnerAngle(),
                "the cone must open outwards: " + spot.getInnerAngle()
                + " then " + spot.getOuterAngle());
    }

    @Test
    void aLightIsAValueAndCannotBeChangedThroughItsVectors() {
        Vector3 direction = new Vector3(1f, 0f, 0f);
        DirectionalLight light = new DirectionalLight(direction, new Vector3(1f, 0f, 0f), 2.0f,
                true);

        // XNA's Vector3 is mutable, so a light that stored the caller's instance would change
        // under it. This is the check that it copied.
        direction.X = -99f;
        assertEquals(new Vector3(1f, 0f, 0f), light.getDirection(),
                "the light copied the vector it was given");

        // And the other direction: a getter that handed the field back would let a caller edit
        // a value type in place.
        Vector3 read = light.getDirection();
        assertNotSame(read, light.getDirection());
        read.Y = 42f;
        assertEquals(new Vector3(1f, 0f, 0f), light.getDirection(),
                "the light copied the vector it handed back");
    }

    @Test
    void withReturnsANewLightAndLeavesThisOneAlone() {
        DirectionalLight light = new DirectionalLight(new Vector3(0f, -1f, 0f),
                new Vector3(1f, 1f, 1f), 1.0f, false);
        DirectionalLight brighter = light.withIntensity(4.0f);
        assertEquals(1.0f, light.getIntensity(), "the original is untouched");
        assertEquals(4.0f, brighter.getIntensity());
        assertNotEquals(light, brighter);
        assertEquals(light, light.withIntensity(1.0f), "an unchanged copy equals the original");
        assertEquals(light.hashCode(), light.withIntensity(1.0f).hashCode());

        SpotLight spot = new SpotLight(new Vector3(1f, 2f, 3f), new Vector3(0f, -1f, 0f),
                new Vector3(1f, 1f, 1f), 1.0f, 10.0f, 0.2f, 0.4f, false);
        SpotLight wider = spot.withCone(0.3f, 0.9f);
        assertEquals(0.2f, spot.getInnerAngle());
        assertEquals(0.3f, wider.getInnerAngle());
        assertEquals(0.9f, wider.getOuterAngle());
        assertEquals(spot.getPosition(), wider.getPosition(), "everything else is carried over");

        assertThrows(NullPointerException.class,
                () -> new PointLight(null, new Vector3(), 1f, 1f, false));
        assertThrows(NullPointerException.class,
                () -> new PointLight(new Vector3(), null, 1f, 1f, false));
    }
}
