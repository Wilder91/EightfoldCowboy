package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import objects.GameAssets;

import java.util.HashMap;
import java.util.Map;

import static helper.Constants.FRAME_DURATION;

public class AnimalMovementHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float movementThreshold;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;

    public AnimalMovementHelper(GameAssets gameAssets, String animalType) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.stateTime = 0f;
        this.movementThreshold = 1f;
        this.animations = new HashMap<>();
        loadAnimations();
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));

    }

    public void loadAnimations() {

        System.out.println(animalType + "_Down_Rest" + "     animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas");
        animations.put("stationary", createAnimation(animalType + "_Down_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingUp", createAnimation(animalType + "_Up_Walk", 7, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDown", createAnimation(animalType + "_Down_Walk", 8, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalUp", createAnimation(animalType + "_DiagUP_Walk", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalDown", createAnimation(animalType + "_DiagDOWN_Walk", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingHorizontal", createAnimation(animalType + "_Horizontal_Walk", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningUp", createAnimation(animalType + "_Up_Run", 7, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDiagonalUp", createAnimation(animalType + "_DiagUP_Run", 7, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDown", createAnimation(animalType + "_Down_Run", 7, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDiagonalDown", createAnimation(animalType + "_DiagDOWN_Run", 7, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningHorizontal", createAnimation(animalType + "_Horizontal_Run", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryUp", createAnimation(animalType + "_Up_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryDown", createAnimation(animalType + "_Down_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryHorizontal", createAnimation(animalType + "_Horizontal_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryUpDiagonal", createAnimation(animalType + "_DiagUP_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryDownDiagonal", createAnimation(animalType + "_DiagDOWN_Rest", 5, "animals/" + animalType + "/walking/atlases/eightfold/" + animalType + "-movement.atlas"));
        currentAnimation = animations.get("stationaryHorizontal"); // Default to stationary animation
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region " + regionNamePrefix + "_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;

        boolean isMoving = Math.abs(vx) > movementThreshold || Math.abs(vy) > movementThreshold;
        boolean isRunning = Math.abs(vx) > movementThreshold * 2 || Math.abs(vy) > movementThreshold * 2;

        if (isMoving) {
            if (isRunning) {
                setRunningAnimation(vx, vy);
            } else {
                setWalkingAnimation(vx, vy);
            }
        } else {
            setRestingAnimation();
        }

        stateTime += delta;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(frame);
        sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        sprite.setOriginCenter(); // Ensure the origin is centered
    }

    private void setRestingAnimation() {
        // Determine the appropriate resting animation based on the facing direction
        // (You can customize this logic further if needed)
        currentAnimation = animations.get("stationaryHorizontal");
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    private void setWalkingAnimation(float vx, float vy) {
        System.out.println("walking!");
        if (vy > movementThreshold) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
            } else {
                currentAnimation = animations.get("walkingUp");
            }
        } else if (vy < -movementThreshold) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
            } else {
                currentAnimation = animations.get("walkingDown");
            }
        } else if (vx > movementThreshold) {
            currentAnimation = animations.get("walkingHorizontal");
        } else if (vx < -movementThreshold) {
            currentAnimation = animations.get("walkingHorizontal");
        }
    }

    private void setRunningAnimation(float vx, float vy) {
        System.out.println("running!");
        if (vy > movementThreshold * 2) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalUp");
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalUp");
            } else {
                currentAnimation = animations.get("runningUp");
            }
        } else if (vy < -movementThreshold * 2) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalDown");
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalDown");
            } else {
                currentAnimation = animations.get("runningDown");
            }
        } else if (vx > movementThreshold * 2) {
            currentAnimation = animations.get("runningHorizontal");
        } else if (vx < -movementThreshold * 2) {
            currentAnimation = animations.get("runningHorizontal");
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

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
