package helper;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import objects.GameEntity;

public class EntityAnimator {
    protected GameEntity entity;
    protected Sprite sprite;
    protected boolean isFacingRight = true;

    public EntityAnimator(GameEntity entity) {
        this.entity = entity;
    }

    /**
     * Updates animation based on entity state
     * Override in specific implementations
     */
    public Sprite updateAnimation(float delta) {
        // Basic implementation - override in subclasses
        return sprite;
    }

    /**
     * Updates directionality based on velocity
     */
    protected void updateDirection(Vector2 velocity) {
        if (velocity.x < -0.1f) {
            isFacingRight = false;
        } else if (velocity.x > 0.1f) {
            isFacingRight = true;
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.isFacingRight = facingRight;
    }
}