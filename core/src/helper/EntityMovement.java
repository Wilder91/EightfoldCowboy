package helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import objects.GameEntity;

public class EntityMovement {
    protected GameEntity entity;
    protected Body body;
    protected boolean isMoving = false;
    protected Vector2 originalPosition = new Vector2();

    public EntityMovement(GameEntity entity) {
        this.entity = entity;
        this.body = entity.getBody();
        this.originalPosition.set(body.getPosition());
    }

    /**
     * Updates movement based on entity state
     * Override in specific implementations
     */
    public void update(float delta) {
        // Basic implementation - override in subclasses
    }

    /**
     * Stops all movement
     */
    public void stopMovement() {
        body.setLinearVelocity(0, 0);
        isMoving = false;
    }

    /**
     * Returns to original position
     */
    public void returnToOrigin() {
        body.setTransform(originalPosition.x, originalPosition.y, body.getAngle());
        body.setLinearVelocity(0, 0);
    }

    /**
     * Checks if entity is currently moving
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Gets the current velocity
     */
    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }
}