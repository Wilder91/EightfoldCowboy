package helper.debugging;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import objects.GameEntity;

/**
 * Utility class for visualizing depth and position information for GameEntities
 */
public class DepthVisualizer {
    // Dot textures for visualizing positions
    private static Texture redDotTexture;    // For depth position
    private static Texture yellowDotTexture; // For Y position
    private static Texture blueDotTexture;   // For bottom position
    private static Texture greenDotTexture;  // For custom/entity-specific points

    private static final int DOT_SIZE = 6;   // Size of the indicator dots
    private static boolean initialized = false;

    // Debug flags
    private static boolean visualizationEnabled = true;

    /**
     * Initialize the dot textures if they haven't been created yet
     */
    private static void initializeTextures() {
        if (initialized) return;

        // Create red dot texture (for depth)
        Pixmap redPixmap = new Pixmap(DOT_SIZE, DOT_SIZE, Pixmap.Format.RGBA8888);
        redPixmap.setColor(Color.RED);
        redPixmap.fillCircle(DOT_SIZE/2, DOT_SIZE/2, DOT_SIZE/2);
        redDotTexture = new Texture(redPixmap);
        redPixmap.dispose();

        // Create yellow dot texture (for actual Y position)
        Pixmap yellowPixmap = new Pixmap(DOT_SIZE, DOT_SIZE, Pixmap.Format.RGBA8888);
        yellowPixmap.setColor(Color.YELLOW);
        yellowPixmap.fillCircle(DOT_SIZE/2, DOT_SIZE/2, DOT_SIZE/2);
        yellowDotTexture = new Texture(yellowPixmap);
        yellowPixmap.dispose();

        // Create blue dot texture (for bottom of entity)
        Pixmap bluePixmap = new Pixmap(DOT_SIZE, DOT_SIZE, Pixmap.Format.RGBA8888);
        bluePixmap.setColor(Color.BLUE);
        bluePixmap.fillCircle(DOT_SIZE/2, DOT_SIZE/2, DOT_SIZE/2);
        blueDotTexture = new Texture(bluePixmap);
        bluePixmap.dispose();

        // Create green dot texture (for custom use)
        Pixmap greenPixmap = new Pixmap(DOT_SIZE, DOT_SIZE, Pixmap.Format.RGBA8888);
        greenPixmap.setColor(Color.GREEN);
        greenPixmap.fillCircle(DOT_SIZE/2, DOT_SIZE/2, DOT_SIZE/2);
        greenDotTexture = new Texture(greenPixmap);
        greenPixmap.dispose();

        initialized = true;
    }

    /**
     * Renders visualization dots showing the entity's position and depth
     *
     * @param batch SpriteBatch to draw with
     * @param entity The GameEntity to visualize
     * @param showBottomDot Whether to show a dot at the bottom of the entity
     */
    public static void drawEntityDots(SpriteBatch batch, GameEntity entity, boolean showBottomDot) {
        if (!visualizationEnabled) return;

        // Initialize textures if needed
        initializeTextures();

        float x = entity.getX();
        float y = entity.getY();
        float depth = entity.getDepth();
        float width = entity.getWidth();
        float height = entity.getHeight();

        // Draw a yellow dot at the Y position (center)
        batch.draw(yellowDotTexture, x - DOT_SIZE/2, y - DOT_SIZE/2);

        // Draw a red dot at the depth position
        batch.draw(redDotTexture, x - DOT_SIZE/2, depth - DOT_SIZE/2);

        // Optionally draw a blue dot at the bottom of the entity
        if (showBottomDot) {
            float bottomY = y - height/2;
            batch.draw(blueDotTexture, x - DOT_SIZE/2, bottomY - DOT_SIZE/2);
        }
    }

    /**
     * Renders visualization dots showing the entity's position and depth,
     * with an additional custom point visualized with a green dot
     *
     * @param batch SpriteBatch to draw with
     * @param entity The GameEntity to visualize
     * @param customX X position for the green custom dot
     * @param customY Y position for the green custom dot
     * @param showBottomDot Whether to show a dot at the bottom of the entity
     */
    public static void drawEntityDotsWithCustomPoint(SpriteBatch batch, GameEntity entity,
                                                     float customX, float customY,
                                                     boolean showBottomDot) {
        // Draw the standard dots
        drawEntityDots(batch, entity, showBottomDot);

        if (!visualizationEnabled) return;

        // Draw the green custom point
        batch.draw(greenDotTexture, customX - DOT_SIZE/2, customY - DOT_SIZE/2);
    }

    /**
     * Enable or disable the visualization dots
     * @param enabled Whether visualization should be enabled
     */
    public static void setVisualizationEnabled(boolean enabled) {
        visualizationEnabled = enabled;
    }

    /**
     * Toggle the visualization dots on/off
     * @return The new state (true = enabled, false = disabled)
     */
    public static boolean toggleVisualization() {
        visualizationEnabled = !visualizationEnabled;
        return visualizationEnabled;
    }

    /**
     * Check if visualization is currently enabled
     * @return true if enabled, false if disabled
     */
    public static boolean isVisualizationEnabled() {
        return visualizationEnabled;
    }

    /**
     * Dispose resources to prevent memory leaks
     * Should be called when the game is closing
     */
    public static void dispose() {
        if (redDotTexture != null) {
            redDotTexture.dispose();
            redDotTexture = null;
        }
        if (yellowDotTexture != null) {
            yellowDotTexture.dispose();
            yellowDotTexture = null;
        }
        if (blueDotTexture != null) {
            blueDotTexture.dispose();
            blueDotTexture = null;
        }
        if (greenDotTexture != null) {
            greenDotTexture.dispose();
            greenDotTexture = null;
        }
        initialized = false;
    }
}
