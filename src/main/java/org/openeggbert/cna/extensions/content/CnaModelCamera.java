package org.openeggbert.cna.extensions.content;

import Microsoft.Xna.Framework.Matrix;

/**
 * A camera authored into a model's scene.
 *
 * <p>A CNA extension with no XNA counterpart at all: XNA's {@code Model} carries geometry and
 * bones and nothing else, so a camera the artist placed was lost at import.
 *
 * @param Name the camera's name, empty when the asset gave it none
 * @param SceneNodeIndex the scene node the camera hangs from
 * @param Projection the camera's projection matrix
 * @param WorldTransform where the camera sits in the scene
 * @param IsPerspective whether the projection is perspective rather than orthographic
 * @param AspectRatio the aspect ratio, meaningful when {@code HasAuthoredAspectRatio}
 * @param FieldOfView the vertical field of view in radians, for a perspective camera
 * @param NearPlaneDistance the near clip distance
 * @param FarPlaneDistance the far clip distance, meaningless when {@code HasInfiniteFarPlane}
 * @param HasInfiniteFarPlane whether the camera's far plane is at infinity
 * @param HasAuthoredAspectRatio whether the asset stated an aspect ratio rather than leaving it
 *     to the viewport
 */
public record CnaModelCamera(
        String Name,
        long SceneNodeIndex,
        Matrix Projection,
        Matrix WorldTransform,
        boolean IsPerspective,
        float AspectRatio,
        float FieldOfView,
        float NearPlaneDistance,
        float FarPlaneDistance,
        boolean HasInfiniteFarPlane,
        boolean HasAuthoredAspectRatio) {
}
