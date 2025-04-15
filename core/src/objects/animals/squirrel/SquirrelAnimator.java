package objects.animals.squirrel;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import helper.EntityAnimator;
import helper.movement.HorizontalSpriteHelper;
import helper.movement.SimpleIdleHelper;

public class SquirrelAnimator extends EntityAnimator {
    private HorizontalSpriteHelper walkingHelper;
    private SimpleIdleHelper idleHelper;

    public SquirrelAnimator(Squirrel squirrel, HorizontalSpriteHelper walkingHelper, SimpleIdleHelper idleHelper) {
        super(squirrel);
        this.walkingHelper = walkingHelper;
        this.idleHelper = idleHelper;
    }

    private Squirrel getSquirrel() {
        return (Squirrel) entity;
    }

    @Override
    public Sprite updateAnimation(float delta) {
        Vector2 velocity = entity.getBody().getLinearVelocity();
        float absVelocityX = Math.abs(velocity.x);

        // Squirrels face opposite their movement direction
        if (velocity.x < -0.1f) {
            isFacingRight = true;
        } else if (velocity.x > 0.1f) {
            isFacingRight = false;
        }

        // Choose animation based on movement
        if (absVelocityX > 0.01) {
            walkingHelper.updateAnimation(velocity, delta);
            sprite = walkingHelper.getSprite();
        } else {
            idleHelper.setFacingRight(isFacingRight);
            idleHelper.update(delta);
            sprite = idleHelper.getSprite();
        }

        // Apply flip
        sprite.setFlip(isFacingRight, false);

        return sprite;
    }
}