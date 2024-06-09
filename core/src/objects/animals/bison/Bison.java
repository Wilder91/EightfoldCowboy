package objects.animals.bison;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.screens.GameScreen;
import objects.GameAssets;
import objects.animals.object_helper.BisonManager;
import objects.player.GameEntity;
import helper.movement.Facing;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Bison extends GameEntity {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float randomStateTime;
    private Facing facingDirection;
    private int id;
    private Sprite sprite;
    private GameAssets gameAssets;
    private float movementThreshold;
    private boolean isResting;
    private Random random;
   ;
    private boolean isPaused;
    private float restingTime;
    private float pauseDuration;

    public Bison(float width, float height, float x, float y, Body body, Facing initialDirection, GameScreen gameScreen, int bisonId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
        this.random = new Random();
        this.stateTime = 0f;
        this.randomStateTime = random.nextFloat();
        this.id = bisonId;
        this.facingDirection = initialDirection;
        this.body = body;
        this.gameAssets = gameAssets;
        this.movementThreshold = 1f;
        this.restingTime = 0f;
        this.pauseDuration = 3f; // 3 seconds pause duration
        this.isPaused = true;


        // Load animations
        loadAnimations();

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time
        randomStateTime += delta / 2;
        // Update sprite position
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Set the origin of the sprite to its center
        sprite.setOriginCenter();

        if (!isPaused) {
            // Determine the current animation frame and update sprite size if needed
            updateAnimation(body.getLinearVelocity());
        } else {
            // If paused, increment resting time
            restingTime += delta;
            // Check if pause duration has passed
            if (restingTime >= pauseDuration) {
                // Reset resting time
                restingTime = 0f;
                // Resume animation
                isPaused = false;
            } else {
                System.out.println("resting time: " + restingTime);
                // If still in pause duration, display the first frame of the animation
                TextureRegion frame = currentAnimation.getKeyFrame(0);
                sprite.setRegion(frame);
                sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
            }
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    private void loadAnimations() {
        animations = new HashMap<>();
        animations.put("stationary", createAnimation("Bison_Down_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("walkingUp", createAnimation("Bison_Up_Walk", 7, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("walkingDown", createAnimation("Bison_Down_Walk", 8, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("walkingDiagonalUp", createAnimation("Bison_DiagUP_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
        animations.put("walkingDiagonalDown", createAnimation("Bison_DiagDOWN_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
        animations.put("walkingHorizontal", createAnimation("Bison_Horizontal_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-horizontal.atlas"));
        animations.put("runningUp", createAnimation("Bison_Up_Run", 7, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("runningDiagonalUp", createAnimation("Bison_DiagUP_Run", 7, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
        animations.put("runningDown", createAnimation("Bison_Down_Run", 7, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("runningDiagonalDown", createAnimation("Bison_DiagDOWN_Run", 7, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
        animations.put("runningHorizontal", createAnimation("Bison_Horizontal_Run", 5, "animals/bison/walking/atlases/eightfold/bison-horizontal.atlas"));
        animations.put("stationaryUp", createAnimation("Bison_Up_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("stationaryDown", createAnimation("Bison_Down_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas"));
        animations.put("stationaryHorizontal", createAnimation("Bison_Horizontal_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-horizontal.atlas"));
        animations.put("stationaryUpDiagonal", createAnimation("Bison_DiagUP_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
        animations.put("stationaryDownDiagonal", createAnimation("Bison_DiagDOWN_Rest", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas"));
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

    private void updateAnimation(Vector2 linearVelocity) {
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

        TextureRegion frame;
        if (isResting) {

            // Limit random state time to animation duration


            frame = currentAnimation.getKeyFrame(randomStateTime, true);
        } else {
            frame = currentAnimation.getKeyFrame(stateTime, true);
        }

        sprite.setRegion(frame);
        sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        sprite.setOriginCenter(); // Ensure the origin is centered
        updateSpriteFlip();
    }

    public void playerContact(Body body, int bisonId, Vector2 linearVelocity) {
        body.setLinearDamping(1.5f);
        body.setLinearVelocity(linearVelocity); // Adjust the linear velocity
    }

    private void setRestingAnimation() {
        isResting = true;
        switch (facingDirection) {
            case UP:
                currentAnimation = animations.get("stationaryUp");
                break;
            case DOWN:
                currentAnimation = animations.get("stationaryDown");
                break;
            case LEFT:
                currentAnimation = animations.get("stationaryHorizontal");
                sprite.setFlip(true, false); // Flip for left direction
                break;
            case RIGHT:
                currentAnimation = animations.get("stationaryHorizontal");
                sprite.setFlip(false, false); // No flip for right direction
                break;
            case UP_RIGHT:
                currentAnimation = animations.get("stationaryUpDiagonal");
                sprite.setFlip(false, false); // No flip for right direction
                break;
            case UP_LEFT:
                currentAnimation = animations.get("stationaryUpDiagonal");
                sprite.setFlip(true, false); // Flip for left direction
                break;
            case DOWN_RIGHT:
                currentAnimation = animations.get("stationaryDownDiagonal");
                sprite.setFlip(false, false); // No flip for right direction
                break;
            case DOWN_LEFT:
                currentAnimation = animations.get("stationaryDownDiagonal");
                sprite.setFlip(true, false); // Flip for left direction
                break;
            default:
                currentAnimation = animations.get("stationary");
                break;
        }
        sprite.setOriginCenter(); // Set the origin to the center of the sprite
    }

    private void setWalkingAnimation(float vx, float vy) {
        isResting = false;
        if (vy > movementThreshold) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                facingDirection = Facing.UP_RIGHT;
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                facingDirection = Facing.UP_LEFT;
            } else {
                currentAnimation = animations.get("walkingUp");
                facingDirection = Facing.UP;
            }
        } else if (vy < -movementThreshold) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
                facingDirection = Facing.DOWN_RIGHT;
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
                facingDirection = Facing.DOWN_LEFT;
            } else {
                currentAnimation = animations.get("walkingDown");
                facingDirection = Facing.DOWN;
            }
        } else if (vx > movementThreshold) {
            currentAnimation = animations.get("walkingHorizontal");
            facingDirection = Facing.RIGHT;
        } else if (vx < -movementThreshold) {
            currentAnimation = animations.get("walkingHorizontal");
            facingDirection = Facing.LEFT;
        }
    }

    private void setRunningAnimation(float vx, float vy) {
        isResting = false;
        if (vy > movementThreshold * 2) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                facingDirection = Facing.UP_RIGHT;
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                facingDirection = Facing.UP_LEFT;
            } else {
                currentAnimation = animations.get("runningUp");
                facingDirection = Facing.UP;
            }
        } else if (vy < -movementThreshold * 2) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                facingDirection = Facing.DOWN_RIGHT;
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                facingDirection = Facing.DOWN_LEFT;
            } else {
                currentAnimation = animations.get("runningDown");
                facingDirection = Facing.DOWN;
            }
        } else if (vx > movementThreshold * 2) {
            currentAnimation = animations.get("runningHorizontal");
            facingDirection = Facing.RIGHT;
        } else if (vx < -movementThreshold * 2) {
            currentAnimation = animations.get("runningHorizontal");
            facingDirection = Facing.LEFT;
        }
    }

    private void updateSpriteFlip() {
        boolean flipX = (facingDirection == Facing.LEFT || facingDirection == Facing.UP_LEFT || facingDirection == Facing.DOWN_LEFT);
        if (sprite.isFlipX() != flipX) {
            sprite.flip(true, false);
        }
    }
}
