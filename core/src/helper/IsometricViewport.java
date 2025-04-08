package helper;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A specialized viewport for isometric games.
 * This viewport handles the correct projection and scaling for isometric views.
 */
public class IsometricViewport extends Viewport {

    private float worldWidth;
    private float worldHeight;

    /**
     * Creates a new IsometricViewport with the specified world dimensions
     * @param worldWidth The width of the world
     * @param worldHeight The height of the world
     * @param camera The camera to use (should be an IsometricCamera for best results)
     */
    public IsometricViewport(float worldWidth, float worldHeight, Camera camera) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        setCamera(camera); // Use the setter method instead of direct assignment
    }

    /**
     * Updates the viewport based on screen dimensions
     */
    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        // Calculate the viewport size while maintaining aspect ratio
        float aspectRatio = (float)screenWidth / (float)screenHeight;
        float viewportHeight = worldHeight;
        float viewportWidth = viewportHeight * aspectRatio;

        // If width is smaller than world width, adjust height
        if (viewportWidth < worldWidth) {
            viewportWidth = worldWidth;
            viewportHeight = viewportWidth / aspectRatio;
        }

        // Set the camera viewport dimensions
        getCamera().viewportWidth = viewportWidth;
        getCamera().viewportHeight = viewportHeight;

        // Apply viewport to screen
        setScreenBounds(0, 0, screenWidth, screenHeight);
        setWorldSize(viewportWidth, viewportHeight);

        // Center camera if requested
        if (centerCamera) {
            getCamera().position.set(worldWidth / 2, worldHeight / 2, 0);
        }

        // Update camera
        getCamera().update();

        // Apply new viewport
        apply();
    }

    /**
     * Converts a screen point to world coordinates
     * @param screenCoords The screen coordinates
     * @return Vector2 containing the world coordinates
     */
    @Override
    public Vector2 unproject(Vector2 screenCoords) {
        // Convert to normalized device coordinates
        float x = (screenCoords.x - getScreenX()) / getScreenWidth();
        float y = (screenCoords.y - getScreenY()) / getScreenHeight();

        // Convert NDC to world coordinates
        x = x * getWorldWidth();
        y = y * getWorldHeight();

        // Account for isometric projection
        // This is a simplified conversion for a classic 2:1 isometric projection
        // You might need to adjust this based on your specific isometric setup
        float worldX = (x + y) / 2;
        float worldY = (y - x) / 2;

        return new Vector2(worldX, worldY);
    }

    /**
     * Converts world coordinates to screen coordinates
     * @param worldCoords The world coordinates
     * @return Vector2 containing the screen coordinates
     */
    @Override
    public Vector2 project(Vector2 worldCoords) {
        // Convert from isometric world to normalized device coordinates
        // This is a simplified conversion for a classic 2:1 isometric projection
        float x = worldCoords.x - worldCoords.y;
        float y = (worldCoords.x + worldCoords.y) / 2;

        // Convert to screen coordinates
        x = x / getWorldWidth() * getScreenWidth() + getScreenX();
        y = y / getWorldHeight() * getScreenHeight() + getScreenY();

        return new Vector2(x, y);
    }
}