package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.SpriteFont;
import Microsoft.Xna.Framework.Graphics.Texture2D;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Constructs strict cross-package facades without widening their XNA constructor contracts. */
public final class FacadeFactory {

    private static final Constructor<GraphicsDevice> GRAPHICS_DEVICE = graphicsDeviceConstructor();
    private static final Constructor<Texture2D> TEXTURE_2D = texture2DConstructor();
    private static final Method TEXTURE_2D_INITIALIZE = texture2DInitializeMethod();
    private static final Constructor<SpriteFont> SPRITE_FONT = spriteFontConstructor();

    private FacadeFactory() {
    }

    public static GraphicsDevice createGraphicsDevice(Game game) {
        try {
            return GRAPHICS_DEVICE.newInstance(game);
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new IllegalStateException("Cannot construct the GraphicsDevice facade", exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new IllegalStateException("GraphicsDevice facade construction failed", cause);
        }
    }

    public static Texture2D createUninitializedTexture2D(GraphicsDevice graphicsDevice) {
        try {
            return TEXTURE_2D.newInstance(graphicsDevice);
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new IllegalStateException("Cannot construct the Texture2D facade", exception);
        } catch (InvocationTargetException exception) {
            throw facadeFailure("Texture2D facade construction failed", exception);
        }
    }

    public static void initializeTexture2D(Texture2D texture, int[] info) {
        try {
            TEXTURE_2D_INITIALIZE.invoke(texture, (Object)info);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot initialize the Texture2D facade", exception);
        } catch (InvocationTargetException exception) {
            throw facadeFailure("Texture2D facade initialization failed", exception);
        }
    }

    public static SpriteFont createSpriteFont() {
        try {
            return SPRITE_FONT.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new IllegalStateException("Cannot construct the SpriteFont facade", exception);
        } catch (InvocationTargetException exception) {
            throw facadeFailure("SpriteFont facade construction failed", exception);
        }
    }

    private static Constructor<GraphicsDevice> graphicsDeviceConstructor() {
        try {
            Constructor<GraphicsDevice> constructor =
                    GraphicsDevice.class.getDeclaredConstructor(Game.class);
            if (!constructor.trySetAccessible()) {
                throw new IllegalStateException(
                        "The runtime denied access to the hidden GraphicsDevice constructor");
            }
            return constructor;
        } catch (NoSuchMethodException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Constructor<Texture2D> texture2DConstructor() {
        try {
            Constructor<Texture2D> constructor =
                    Texture2D.class.getDeclaredConstructor(GraphicsDevice.class);
            if (!constructor.trySetAccessible()) {
                throw new IllegalStateException(
                        "The runtime denied access to the hidden Texture2D constructor");
            }
            return constructor;
        } catch (NoSuchMethodException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Method texture2DInitializeMethod() {
        try {
            Method method = Texture2D.class.getDeclaredMethod("initialize", int[].class);
            if (!method.trySetAccessible()) {
                throw new IllegalStateException(
                        "The runtime denied access to hidden Texture2D initialization");
            }
            return method;
        } catch (NoSuchMethodException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Constructor<SpriteFont> spriteFontConstructor() {
        try {
            Constructor<SpriteFont> constructor = SpriteFont.class.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                throw new IllegalStateException(
                        "The runtime denied access to the hidden SpriteFont constructor");
            }
            return constructor;
        } catch (NoSuchMethodException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static IllegalStateException facadeFailure(
            String message, InvocationTargetException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof RuntimeException runtime) {
            throw runtime;
        }
        return new IllegalStateException(message, cause);
    }
}
