package objects.animals.farm_animals;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import helper.EntityAnimator;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteRunningHelper;

public class ChickenAnimator extends EntityAnimator {
    private SimpleSpriteRunningHelper chickenWalkingHelper;
    private SimpleIdleHelper chickenIdleHelper;
    public ChickenAnimator(Chicken chicken, SimpleSpriteRunningHelper chickenWalkingHelper, SimpleIdleHelper chickenIdleHelper) {
        super(chicken);
        this.chickenWalkingHelper = chickenWalkingHelper;
        this.chickenIdleHelper = chickenIdleHelper;
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
            chickenWalkingHelper.updateAnimation(velocity, delta);
            sprite = chickenWalkingHelper.getSprite();
        } else {
            chickenIdleHelper.setFacingRight(isFacingRight);
            chickenIdleHelper.update(delta);
            sprite = chickenIdleHelper.getSprite();
        }

        // Apply flip AFTER getting the new sprite
        sprite.setFlip(!isFacingRight, false);
        return sprite;
    }
}
