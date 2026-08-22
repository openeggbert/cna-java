import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.GraphicsDeviceManager;

public final class ProbeGame extends Game {
    private final GraphicsDeviceManager graphics;

    public ProbeGame() {
        graphics = new GraphicsDeviceManager(this);
        getContent().setRootDirectory("Content");
        setIsMouseVisible(true);
    }

    @Override
    protected void Update(GameTime gameTime) {
        if (gameTime.getElapsedGameTime().isNegative()) {
            Exit();
        }
    }

    @Override
    protected void Draw(GameTime gameTime) {
        getGraphicsDevice().Clear(Color.CornflowerBlue);
    }
}
