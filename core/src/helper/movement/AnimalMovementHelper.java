package helper.movement;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import objects.animals.Bird;

public class AnimalMovementHelper {
    public static boolean checkLinearVelocity(Body body, Sprite sprite, boolean isFacingRight) {
        // Check if the body is moving left and was previously facing right
        if (body.getLinearVelocity().x < 0 && isFacingRight) {
            isFacingRight = false;
            sprite.flip(true, false); // Flip the sprite to face left
        }
        // Check if the body is moving right and was previously facing left
        else if (body.getLinearVelocity().x > 0 && !isFacingRight) {
            isFacingRight = true;
            sprite.flip(true, false); // Flip the sprite to face right

        }


        // Ensure the sprite is not unnecessarily flipped back
        // if already facing in the correct direction
        if (isFacingRight && sprite.isFlipX()) {
            sprite.flip(true, false); // Ensure it's facing right without unnecessary flipping
        } else if (!isFacingRight && !sprite.isFlipX()) {
            sprite.flip(true, false); // Ensure it's facing left without unnecessary flipping
        }

        return isFacingRight;
    }
}
