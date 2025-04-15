package objects.animals.farm_animals;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import helper.EntityRenderer;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteRunningHelper;

import static helper.Constants.PPM;

public class Chicken extends GameEntity {
    private SimpleSpriteRunningHelper chickenWalkingHelper;
    private SimpleIdleHelper chickenIdleHelper;
    private EntityRenderer chickenRenderer;
    private Sprite sprite;
    private float stateTime;
    private boolean isFacingRight = true;
    private float movementTimer = 0;
    private float movementDuration = 1.2f; // Duration of movement in one direction
    private float restTimer = (float) (Math.random() * 3f); // Random initial rest time between 0 and 3
    private float restDuration = 3f; // Duration of rest between movements
    private boolean isMoving = false;
    private Vector2 currentDirection = new Vector2(0, 0);
    private float chickenSpeed = 0.8f;

    // Track original position and movement count
    private Vector2 originalPosition = new Vector2();
    private int moveCount = 0;
    private boolean returningToOrigin = false;

    public Chicken(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, String chickenName) {
        super(width, height, body, screenInterface, gameAssets);
        int[] frameCounts = {4, 4, 4};  // [up, down, horizontal]
        int idleFrameCount = 25;
        this.chickenWalkingHelper = new SimpleSpriteRunningHelper(gameAssets, "farm_animal", "Chicken", frameCounts, false);
        this.chickenIdleHelper = new SimpleIdleHelper(gameAssets, "farm_animal", "Chicken", idleFrameCount, 0.4f);
        this.chickenRenderer = new EntityRenderer(this);
        // Initialize sprite
        this.sprite = chickenIdleHelper.getSprite();
        if (this.sprite == null) {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize chicken sprite from idle helper");
        }
        this.sprite.setSize(width, height);

        // Store the original position
        originalPosition.set(body.getPosition().x, body.getPosition().y);
    }

    public static void playerContact(Body body, int id) {
    }

    public static void chickenContact(Body body, int id) {
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        // Update position from the physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        // First update the movement
        updateMovement(delta);

        // Then update animation - this sets the current sprite
        updateAnimation(delta);

        // Finally set the updated sprite on the renderer
        chickenRenderer.setMainSprite(sprite);

        // Let the renderer do any additional updates it needs
        chickenRenderer.update(delta);
    }

    private void updateMovement(float delta) {
        if (isMoving) {
            // Chicken is currently moving
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
            // Chicken is resting
            restTimer += delta;
            if (restTimer >= restDuration) {
                // Start moving
                isMoving = true;
                movementTimer = 0;

                if (returningToOrigin) {
                    // Calculate direction vector to the original position
                    Vector2 currentPos = body.getPosition();
                    currentDirection.set(originalPosition.x - currentPos.x, originalPosition.y - currentPos.y);

                    // Check if we're already very close to the origin
                    if (currentDirection.len() < 0.1f) {
                        // If very close, just teleport to exact position
                        body.setTransform(originalPosition.x, originalPosition.y, body.getAngle());
                        body.setLinearVelocity(0, 0);
                        returningToOrigin = false;
                        moveCount = 0;
                        isMoving = false;
                        return;
                    }

                    // Normalize the direction vector
                    currentDirection.nor();
                } else {
                    // Choose a random direction
                    float angle = (float) (Math.random() * 2 * Math.PI);
                    currentDirection.set((float) Math.cos(angle), (float) Math.sin(angle));
                    currentDirection.nor(); // Normalize to get direction vector
                }

                // Apply the movement
                body.setLinearVelocity(currentDirection.x * chickenSpeed, currentDirection.y * chickenSpeed);
            }
        }
    }

    private void updateAnimation(float delta) {
        Vector2 velocity = body.getLinearVelocity();
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
    }

    @Override
    public void render(SpriteBatch batch) {
//        if (isFacingRight){
//            sprite.flip(true,false);
//        }
//        if (sprite != null) {
//            sprite.setPosition(x - width / 3, y - height / 2);
//
//            sprite.draw(batch);
//        } else {
//            System.err.println("Error: Cannot render chicken, sprite is null");
//        }
        chickenRenderer.render(batch);
    }

    /**
     * Sets the chicken's movement speed
     * @param speed Movement speed in units per second
     */
    public void setChickenSpeed(float speed) {
        this.chickenSpeed = speed;
    }

    /**
     * Sets how long the chicken moves in one direction before resting
     * @param duration Duration in seconds
     */
    public void setMovementDuration(float duration) {
        this.movementDuration = duration;
    }

    /**
     * Sets how long the chicken rests before moving again
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
     * Checks if the chicken is currently returning to origin
     * @return True if returning to origin, false otherwise
     */
    public boolean isReturningToOrigin() {
        return returningToOrigin;
    }

}