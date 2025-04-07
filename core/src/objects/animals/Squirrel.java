package objects.animals;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.HorizontalSpriteHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteRunningHelper;

import static helper.Constants.PPM;

public class Squirrel extends GameEntity {
    private HorizontalSpriteHelper squirrelWalkingHelper;
    private SimpleIdleHelper squirrelIdleHelper;
    private Sprite sprite;
    private float stateTime;
    private boolean isFacingRight = true;
    private float movementTimer = 0;
    private float movementDuration = 0.7f; // Duration of movement in one direction
    private float restTimer = (float) (Math.random() * 1.5f); // Random initial rest time between 0 and 1.5
    private float restDuration = 1.5f; // Duration of rest between movements
    private boolean isMoving = false;
    private float squirrelSpeed = 1.2f; // Faster than chicken
    private int moveDirection = 1; // 1 for right, -1 for left

    // Track original position and movement count
    private Vector2 originalPosition = new Vector2();
    private int moveCount = 0;
    private boolean returningToOrigin = false;

    public Squirrel(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        int[] frameCounts = {0, 0, 4};  // [up, down, horizontal]
        int idleFrameCount = 20;
        this.squirrelWalkingHelper = new HorizontalSpriteHelper(gameAssets, "wild-animal", "Squirrel", 4, false);
        this.squirrelIdleHelper = new SimpleIdleHelper(gameAssets, "wild-animal", "Squirrel", idleFrameCount, 0.3f);

        // Initialize sprite
        this.sprite = squirrelIdleHelper.getSprite();
        if (this.sprite == null) {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize squirrel sprite from idle helper");
        }
        this.sprite.setSize(width, height);

        // Store the original position
        originalPosition.set(body.getPosition().x, body.getPosition().y);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        // Update position from the physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update squirrel movement
        updateMovement(delta);

        // Update animation
        updateAnimation(delta);
    }

    private void updateMovement(float delta) {
        if (isMoving) {
            // Squirrel is currently moving
            movementTimer += delta;
            if (movementTimer >= movementDuration) {
                // Stop movement and start resting
                isMoving = false;
                restTimer = 0;
                body.setLinearVelocity(0, 0);

                // If not returning to origin, increment move count
                if (!returningToOrigin) {
                    moveCount++;

                    // If we've moved twice, set flag to return to origin on next movement
                    if (moveCount >= 2) {
                        returningToOrigin = true;
                    }
                } else {
                    // We've completed the return journey, reset
                    returningToOrigin = false;
                    moveCount = 0;
                }
            }
        } else {
            // Squirrel is resting
            restTimer += delta;
            if (restTimer >= restDuration) {
                // Start moving
                isMoving = true;
                movementTimer = 0;

                if (returningToOrigin) {
                    // Calculate horizontal direction to original position
                    float xDiff = originalPosition.x - body.getPosition().x;

                    // Check if we're already very close to the origin horizontally
                    if (Math.abs(xDiff) < 0.1f) {
                        // If very close, just teleport to exact position
                        body.setTransform(originalPosition.x, originalPosition.y, body.getAngle());
                        body.setLinearVelocity(0, 0);
                        returningToOrigin = false;
                        moveCount = 0;
                        isMoving = false;
                        return;
                    }

                    // Set direction based on where original position is
                    moveDirection = xDiff > 0 ? 1 : -1;
                } else {
                    // Randomly choose left or right
                    moveDirection = Math.random() > 0.5 ? 1 : -1;
                }

                // Apply horizontal movement only
                body.setLinearVelocity(moveDirection * squirrelSpeed, 0);
            }
        }
    }

    private void updateAnimation(float delta) {
        Vector2 velocity = body.getLinearVelocity();
        float absVelocityX = Math.abs(velocity.x);

        // Update facing direction
        if (velocity.x < -0.1f) {
            isFacingRight = false;
        } else if (velocity.x > 0.1f) {
            isFacingRight = true;
        }

        // Choose between walking and idle animations based on movement
        if (absVelocityX > 0.01) {
            squirrelWalkingHelper.updateAnimation(velocity, delta);
            sprite = squirrelWalkingHelper.getSprite();
        } else {
            squirrelIdleHelper.setFacingRight(isFacingRight);
            squirrelIdleHelper.update(delta);
            sprite = squirrelIdleHelper.getSprite();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isFacingRight){
            sprite.flip(true, false);
        }
        if (sprite != null) {
            sprite.setPosition(x - width / 2, y - height / 2);
            sprite.draw(batch);
        } else {
            System.err.println("Error: Cannot render squirrel, sprite is null");
        }
    }

    /**
     * Sets the squirrel's movement speed
     * @param speed Movement speed in units per second
     */
    public void setSquirrelSpeed(float speed) {
        this.squirrelSpeed = speed;
    }

    /**
     * Sets how long the squirrel moves in one direction before resting
     * @param duration Duration in seconds
     */
    public void setMovementDuration(float duration) {
        this.movementDuration = duration;
    }

    /**
     * Sets how long the squirrel rests before moving again
     * @param duration Duration in seconds
     */
    public void setRestDuration(float duration) {
        this.restDuration = duration;
    }

    /**
     * Gets the current movement count
     * @return Number of random movements since last return to origin
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Checks if the squirrel is currently returning to origin
     * @return True if returning to origin, false otherwise
     */
    public boolean isReturningToOrigin() {
        return returningToOrigin;
    }
}