package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;
import static helper.Constants.FRAME_DURATION;

public abstract class SpriteMovementHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float movementThreshold;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean randomFlip;
    // Variables to track the last movement direction
    private boolean wasMovingLeft = false;
    private boolean wasMovingRight = false;
    private boolean wasMovingUp = false;
    private boolean wasMovingDown = false;
    private boolean hasMoved = false;

    private boolean startFlipped;
    public SpriteMovementHelper(GameAssets gameAssets, String animalType, int[] frameCounts, boolean randomFlip ) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.stateTime = 0f;
        this.movementThreshold = 1f;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.startFlipped = random.nextBoolean();
        this.randomFlip = randomFlip;
        if(randomFlip){
            this.startFlipped = random.nextBoolean();
        }else{
            this.startFlipped = false;
        }
        loadAnimations();

            String[] restingAnimations = { "stationaryHorizontal", "stationaryUpDiagonal", "stationaryDownDiagonal"};
            Random random = new Random();
            int randomIndex = random.nextInt(restingAnimations.length);

            this.currentAnimation = animations.get(restingAnimations[randomIndex]);
            this.sprite = new Sprite(this.currentAnimation.getKeyFrame(0));

    }

    public static boolean checkLinearVelocity(Body body, Sprite sprite, boolean isFacingRight) {
        float velocityThreshold = 0.5f; // Adjust this threshold as needed
        // Check if the body's velocity exceeds the threshold
        if (Math.abs(body.getLinearVelocity().x) > velocityThreshold) {
            // Determine the direction based on the velocity
            boolean newFacingRight = (body.getLinearVelocity().x > 0);
            sprite.flip(true,false);
            // Flip the sprite if the direction has changed

        }
        // Return the current direction if no flipping occurred
        return isFacingRight;
    }

    public void loadAnimations() {
        // Populate the animations map with all available animations

        animations.put("walkingUp", createAnimation(animalType + "_Up_Walk", frameCounts[1], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDown", createAnimation(animalType + "_Down_Walk", frameCounts[2], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalUp", createAnimation(animalType + "_DiagUP_Walk", frameCounts[3], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalDown", createAnimation(animalType + "_DiagDOWN_Walk", frameCounts[4], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingHorizontal", createAnimation(animalType + "_Horizontal_Walk", frameCounts[5], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningUp", createAnimation(animalType + "_Up_Run", frameCounts[6], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDiagonalUp", createAnimation(animalType + "_DiagUP_Run", frameCounts[7], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDown", createAnimation(animalType + "_Down_Run", frameCounts[8], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningDiagonalDown", createAnimation(animalType + "_DiagDOWN_Run", frameCounts[9], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("runningHorizontal", createAnimation(animalType + "_Horizontal_Run", frameCounts[10], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryUp", createStationaryAnimation(animalType + "_Up_Rest", frameCounts[11], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryDown", createStationaryAnimation(animalType + "_Down_Rest", frameCounts[12], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryHorizontal", createAnimation(animalType + "_Horizontal_Rest", frameCounts[13], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryUpDiagonal", createAnimation(animalType + "_DiagUP_Rest", frameCounts[14], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("stationaryDownDiagonal", createAnimation(animalType + "_DiagDOWN_Rest", frameCounts[5], "atlases/eightfold/" + animalType + "-movement.atlas"));
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

    private Animation<TextureRegion> createStationaryAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        int maxValue = frameCount; // Example maximum value
        Random random = new Random();

        // Generate a random number between 1 and maxValue (inclusive)

        int randomNumber = random.nextInt(maxValue) + 1;
        for (int i = randomNumber; i <= frameCount; i++) {
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

        if(!hasMoved){

           if(isMoving){
               hasMoved = true;
               startFlipped = false;
           }
        }else if(hasMoved){
            if (isMoving) {
                if (isRunning) {
                    setRunningAnimation(vx, vy);
                } else {
                    setWalkingAnimation(vx, vy);
                }
            } else {
                setRestingAnimation();
            }

        }


        stateTime += delta;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(frame);
        sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        sprite.setOriginCenter();
//        System.out.println(isFlipped);
        if(startFlipped){
            sprite.flip(true,false);
        }
        if(!isFacingRight){
            sprite.flip(true,false);// Ensure the origin is centered
        }
    }

    private void setRestingAnimation() {
        if (wasMovingUp) {
            if(wasMovingRight){
                currentAnimation=animations.get("stationaryUpDiagonal");
            }else {
                currentAnimation = animations.get("stationaryUp");
            }
        } else if (wasMovingDown) {
            currentAnimation = animations.get("stationaryDown");
        } else if (wasMovingLeft) {
            currentAnimation = animations.get("stationaryHorizontal");
        } else {
            currentAnimation = animations.get("stationaryHorizontal");
        }
    }

    private void setWalkingAnimation(float vx, float vy) {
        if (vy > movementThreshold) {
            wasMovingUp = true;
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("walkingUp");
            }
        } else if (vy < -movementThreshold) {
            wasMovingUp = false;
            wasMovingDown = true;
            wasMovingLeft = false;
            wasMovingRight= false;
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("walkingDown");
            }
        } else if (vx > movementThreshold) {
            wasMovingUp = false;
            wasMovingDown = false;
            wasMovingLeft = true;
            currentAnimation = animations.get("walkingHorizontal");
            flipSprite(false);
        } else if (vx < -movementThreshold) {
            wasMovingUp = false;
            wasMovingDown = false;
            wasMovingRight = true;
            currentAnimation = animations.get("walkingHorizontal");
            flipSprite(true);
        }
    }

    private void setRunningAnimation(float vx, float vy) {
        if (vy > movementThreshold * 2) {
            wasMovingUp = true;

            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                wasMovingRight = true;
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("runningUp");
            }
        } else if (vy < -movementThreshold * 2) {
            wasMovingUp = false;
            wasMovingDown = true;
            wasMovingLeft = false;
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("runningDown");
            }
        } else if (vx > movementThreshold * 2) {
            wasMovingUp = false;
            wasMovingDown = false;
            wasMovingLeft = false;
            wasMovingRight = true;
            currentAnimation = animations.get("runningHorizontal");
            flipSprite(false);
        } else if (vx < -movementThreshold * 2) {
            wasMovingUp = false;
            wasMovingDown = false;
            wasMovingLeft = true;
            wasMovingRight = false;
            currentAnimation = animations.get("runningHorizontal");
            flipSprite(true);
        }
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    private void flipSprite(boolean shouldFaceLeft) {
        if (isFacingRight == shouldFaceLeft) {
            sprite.flip(true, false); // Flip horizontally
            isFacingRight = !isFacingRight; // Toggle the facing direction
            //System.out.println("Flipped sprite to " + (isFacingRight ? "right" : "left"));
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getStateTime() {
        return stateTime;
    }
}
