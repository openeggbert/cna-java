package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/** Constructs strict cross-package facades without widening their XNA constructor contracts. */
public final class FacadeFactory {

    private static final Constructor<GraphicsDevice> GRAPHICS_DEVICE = graphicsDeviceConstructor();

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
}
