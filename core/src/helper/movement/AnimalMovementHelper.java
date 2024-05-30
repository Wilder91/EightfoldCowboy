package helper.movement;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class AnimalMovementHelper {
    public static boolean checkLinearVelocity(Body body, Sprite sprite, boolean isFacingRight) {
        float velocityThreshold = 0.5f; // Adjust this threshold as needed

        // Check if the body's velocity exceeds the threshold
        if (Math.abs(body.getLinearVelocity().x) > velocityThreshold) {
            // Determine the direction based on the velocity
            boolean newFacingRight = (body.getLinearVelocity().x > 0);

            // Flip the sprite if the direction has changed
            if (newFacingRight != isFacingRight) {
                sprite.flip(true, false); // Flip horizontally
                return newFacingRight; // Return the new direction
            }
        }

        // Return the current direction if no flipping occurred
        return isFacingRight;
    }
}

