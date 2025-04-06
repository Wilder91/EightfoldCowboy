package objects.animals.bird;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteRunningHelper;

import static helper.Constants.PPM;

public class Chicken extends GameEntity {
    private SimpleSpriteRunningHelper chickenWalkingHelper;
    private SimpleIdleHelper chickenIdleHelper;
    private Sprite sprite;
    private float stateTime;
    private boolean isFacingRight = true;
    private float movementTimer = 0;
    private float movementDuration = 2f; // Duration of movement in one direction
    private float restTimer = 0;
    private float restDuration = 1.5f; // Duration of rest between movements
    private boolean isMoving = false;
    private Vector2 currentDirection = new Vector2(0, 0);
    private float chickenSpeed = 0.8f;

    public Chicken(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        int[] frameCounts = {4, 4, 4};  // [up, down, horizontal]
        int idleFrameCount = 25;
        this.chickenWalkingHelper = new SimpleSpriteRunningHelper(gameAssets, "farm_animal", "Chicken", frameCounts, false);
        this.chickenIdleHelper = new SimpleIdleHelper(gameAssets, "farm_animal", "Chicken", idleFrameCount, 0.2f);

        // Initialize sprite
        this.sprite = chickenIdleHelper.getSprite();
        if (this.sprite == null) {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize chicken sprite from idle helper");
        }
        this.sprite.setSize(width, height);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        // Update position from the physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update chicken movement
        updateMovement(delta);

        // Update animation
        updateAnimation(delta);
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
            }
        } else {
            // Chicken is resting
            restTimer += delta;
            if (restTimer >= restDuration) {
                // Start moving in a random direction
                isMoving = true;
                movementTimer = 0;

                // Choose a random direction
                float angle = (float) (Math.random() * 2 * Math.PI);
                currentDirection.set((float) Math.cos(angle), (float) Math.sin(angle));
                currentDirection.nor(); // Normalize to get direction vector

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
            isFacingRight = false;
        } else if (velocity.x > 0.1f) {
            isFacingRight = true;
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
    }

    @Override
    public void render(SpriteBatch batch) {
        if (sprite != null) {
            sprite.setPosition(x - width / 2, y - height / 2);
            sprite.draw(batch);
        } else {
            System.err.println("Error: Cannot render chicken, sprite is null");
        }
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
}