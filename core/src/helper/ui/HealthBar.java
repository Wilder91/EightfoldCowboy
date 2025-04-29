package helper.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/**
 * A flexible health bar that can be rendered above game entities
 */
public class HealthBar {
    private float width;
    private float height;
    private float maxHealth;
    private float currentHealth;
    private Color backgroundColor;
    private Color foregroundColor;
    private Color borderColor;
    private float padding = 2;
    private boolean showBorder = true;
    private float offsetY = 10;
    private ShapeRenderer shapeRenderer;
    private boolean visible = true;

    /**
     * Creates a new health bar
     */
    public HealthBar(float width, float height, float maxHealth, float currentHealth, boolean isEnemy) {
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.shapeRenderer = new ShapeRenderer();
        this.borderColor = new Color(0, 0, 0, 1);

        if (isEnemy) {
            this.backgroundColor = new Color(0.7f, 0, 0, 1);
            this.foregroundColor = new Color(1, 0.2f, 0.2f, 1);
        } else {
            this.backgroundColor = new Color(0, 0.5f, 0, 1);
            this.foregroundColor = new Color(0.2f, 1, 0.2f, 1);
        }

        // Debug print
        System.out.println("HealthBar created with dimensions: " + width + "x" + height +
                ", health: " + currentHealth + "/" + maxHealth);
    }

    /**
     * Updates the current health value
     */
    public void updateHealth(float health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
        // Debug print
       // System.out.println("HealthBar updated: " + currentHealth + "/" + maxHealth);
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Renders the health bar above an entity
     */
    public void render(Matrix4 projectionMatrix, float x, float y) {
        if (!visible) {
            return;
        }

        // We've removed the check for currentHealth = maxHealth so it always shows
        // We'll also force a minimum percentage to ensure visibility
        float healthPercent = Math.max(0.05f, currentHealth / maxHealth);
        float healthBarX = x - width / 2;
        float healthBarY = y + offsetY;

//        System.out.println("Drawing health bar at: " + healthBarX + ", " + healthBarY +
//                ", percent: " + healthPercent);

        shapeRenderer.setProjectionMatrix(projectionMatrix);

        // Draw the border
        if (showBorder) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(borderColor);
            shapeRenderer.rect(healthBarX - padding, healthBarY - padding,
                    width + padding * 2, height + padding * 2);
            shapeRenderer.end();
        }

        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(healthBarX, healthBarY, width, height);
        shapeRenderer.end();

        // Draw foreground
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(foregroundColor);
        shapeRenderer.rect(healthBarX, healthBarY, width * healthPercent, height);
        shapeRenderer.end();
    }

    public void dispose() {
        if (shapeRenderer != null) {
            // Set flag to prevent rendering after disposal
            visible = false;

            // Only dispose if we own this ShapeRenderer
            // Alternatively, you could remove this method entirely and handle disposal elsewhere
            shapeRenderer.dispose();
            shapeRenderer = null;  // Set to null to prevent double-disposal
        }
    }
}