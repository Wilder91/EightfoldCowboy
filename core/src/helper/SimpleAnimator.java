package helper;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import objects.GameEntity;

public class SimpleAnimator extends EntityAnimator {
    private SimpleSpriteWalkingHelper walkingHelper;
    private SimpleIdleHelper idleHelper;
    public SimpleAnimator(GameEntity entity, SimpleSpriteWalkingHelper walkingHelper, SimpleIdleHelper idleHelper) {
        super(entity);
        this.walkingHelper = walkingHelper;
        this.idleHelper = idleHelper;
    }
    public Sprite updateAnimation(float delta) {
        Vector2 velocity = entity.getBody().getLinearVelocity();
        Vector2 absVelocity = new Vector2(Math.abs(velocity.x), Math.abs(velocity.y));

        // Update facing direction
        if (velocity.x < -0.1f) {
            isFacingRight = true;
        } else if (velocity.x > 0.1f) {
            isFacingRight = false;
        }

        // Choose between walking and idle animations based on movement
        if (absVelocity.x > 0.01 || absVelocity.y > 0.01) {
            walkingHelper.updateAnimation(velocity, delta);
            sprite = walkingHelper.getSprite();
        } else {
            idleHelper.setFacingRight(isFacingRight);
            idleHelper.update(delta);
            sprite = idleHelper.getSprite();
        }

        // Apply flip AFTER getting the new sprite
        sprite.setFlip(!isFacingRight, false);
        return sprite;
    }
}
