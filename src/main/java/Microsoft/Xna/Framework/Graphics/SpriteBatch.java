package Microsoft.Xna.Framework.Graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Rectangle;
import Microsoft.Xna.Framework.Vector2;
import org.openeggbert.cna.internal.NativeBindings;

import java.util.Objects;

/** XNA SpriteBatch facade backed by CNA's batched quad commands. */
@SuppressWarnings("this-escape")
public class SpriteBatch extends GraphicsResource {

    private boolean begun;

    public SpriteBatch(GraphicsDevice graphicsDevice) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        NativeBindings.createSpriteBatch(this, graphicsDevice);
    }

    public final void Begin() {
        begin(SpriteSortMode.Deferred);
    }

    public final void End() {
        ensureNotDisposed();
        if (!begun) {
            throw new IllegalStateException("SpriteBatch.End requires a matching Begin");
        }
        try {
            NativeBindings.endSpriteBatch(this);
        } finally {
            begun = false;
        }
    }

    public final void Draw(Texture2D texture, Vector2 position, Color color) {
        drawScaled(texture, position, null, color, 0.0f, new Vector2(),
                new Vector2(1.0f), SpriteEffects.None, 0.0f);
    }

    public final void Draw(
            Texture2D texture,
            Vector2 position,
            Rectangle sourceRectangle,
            Color color) {
        drawScaled(texture, position, sourceRectangle, color, 0.0f, new Vector2(),
                new Vector2(1.0f), SpriteEffects.None, 0.0f);
    }

    public final void Draw(
            Texture2D texture,
            Vector2 position,
            Rectangle sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            float scale,
            SpriteEffects effects,
            float layerDepth) {
        drawScaled(texture, position, sourceRectangle, color, rotation, origin,
                new Vector2(scale), effects, layerDepth);
    }

    public final void Draw(
            Texture2D texture,
            Vector2 position,
            Rectangle sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects effects,
            float layerDepth) {
        drawScaled(texture, position, sourceRectangle, color, rotation, origin, scale, effects, layerDepth);
    }

    public final void Draw(Texture2D texture, Rectangle destinationRectangle, Color color) {
        drawRectangle(texture, destinationRectangle, null, color, 0.0f, new Vector2(),
                SpriteEffects.None, 0.0f);
    }

    public final void Draw(
            Texture2D texture,
            Rectangle destinationRectangle,
            Rectangle sourceRectangle,
            Color color) {
        drawRectangle(texture, destinationRectangle, sourceRectangle, color, 0.0f, new Vector2(),
                SpriteEffects.None, 0.0f);
    }

    public final void Draw(
            Texture2D texture,
            Rectangle destinationRectangle,
            Rectangle sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            SpriteEffects effects,
            float layerDepth) {
        drawRectangle(texture, destinationRectangle, sourceRectangle, color, rotation, origin,
                effects, layerDepth);
    }

    @Override
    protected void Dispose(boolean disposing) {
        if (disposing && !getIsDisposed()) {
            begun = false;
            NativeBindings.closeGraphicsResource(this);
        }
        super.Dispose(disposing);
    }

    private void begin(SpriteSortMode sortMode) {
        ensureNotDisposed();
        if (begun) {
            throw new IllegalStateException("SpriteBatch.Begin cannot be nested");
        }
        NativeBindings.beginSpriteBatch(this, sortMode);
        begun = true;
    }

    private void drawRectangle(
            Texture2D texture,
            Rectangle destinationRectangle,
            Rectangle sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            SpriteEffects effects,
            float layerDepth) {
        ensureBegun();
        NativeBindings.drawSpriteRectangle(this, texture, destinationRectangle, sourceRectangle,
                color, rotation, origin, effects, layerDepth);
    }

    private void drawScaled(
            Texture2D texture,
            Vector2 position,
            Rectangle sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects effects,
            float layerDepth) {
        ensureBegun();
        NativeBindings.drawSpriteScaled(this, texture, position, sourceRectangle, color, rotation,
                origin, scale, effects, layerDepth);
    }

    private void ensureBegun() {
        ensureNotDisposed();
        if (!begun) {
            throw new IllegalStateException("SpriteBatch.Draw requires a matching Begin");
        }
    }
}
