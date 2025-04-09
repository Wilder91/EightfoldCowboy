package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import java.util.Comparator;

public abstract class GameEntity {
    protected float x, y, velX, velY, speed;
    protected float width, height;
    protected Body body;
    private int entityId;
    // Depth value used for rendering order (defaults to y position)
    private float depth;
    // Flag to indicate if depth should automatically update with y position
    private boolean autoUpdateDepth = true;

    public GameEntity(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.depth = y; // Initialize depth to match y position
        this.entityId = 0; // Default ID
        this.width = width;
        this.height = height;
        this.body = body;
        this.velX = 0;
        this.velY = 0;
        this.speed = 0;
    }

    // Comparator for Y-based depth sorting
    public static final Comparator<GameEntity> Y_COMPARATOR =
            (entity1, entity2) -> Float.compare(entity2.depth, entity1.depth);


    public float getDepth() {
        return depth;
    }

    /**
     * Set a custom depth value for this entity.
     * This will override the automatic depth updating based on y position.
     *
     * @param depth The custom depth value to use
     */
    public void setDepth(float depth) {
        this.depth = depth;
        this.autoUpdateDepth = false; // Disable auto-updating
    }

    /**
     * Set depth to match the current y position of the entity.
     * This also re-enables automatic depth updating.
     */
    public void resetDepthToY() {
        this.depth = this.y;
        this.autoUpdateDepth = true;
    }

    /**
     * Apply an offset to the depth value relative to the y position.
     * This can be used to fine-tune rendering order without completely
     * overriding the y-based depth sorting.
     *
     * @param offset The amount to offset depth from y position
     */
    public void setDepthOffset(float offset) {
        this.depth = this.y + offset;
        this.autoUpdateDepth = false;
    }

    /**
     * Controls whether depth automatically updates with the y position
     *
     * @param autoUpdate True to automatically update depth when y changes
     */
    public void setAutoUpdateDepth(boolean autoUpdate) {
        this.autoUpdateDepth = autoUpdate;
    }

    /**
     * Updates the entity's position from its Box2D body and
     * updates depth if auto-updating is enabled
     */
    protected void updatePositionFromBody() {
        if (body != null) {
            this.x = body.getPosition().x;
            this.y = body.getPosition().y;

            // Update depth automatically if enabled
            if (autoUpdateDepth) {
                this.depth = this.y;
            }
        }
    }

    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch);

    public Body getBody() {
        return body;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public int getId() {
        return entityId;
    }

    public void setId(int id) {
        this.entityId = id;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Get the width of this entity
     * @return width value
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get the height of this entity
     * @return height value
     */
    public float getHeight() {
        return height;
    }
}