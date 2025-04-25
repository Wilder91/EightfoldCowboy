package helper;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import helper.movement.SpriteIdleHelper;
import helper.movement.SpriteWalkingHelper;
import objects.GameEntity;

import java.util.HashMap;

public class EntityAnimator {
    protected GameEntity entity;
    protected Sprite sprite;
    protected boolean isFacingRight = true;
    protected SpriteWalkingHelper walkingHelper;
    protected SpriteIdleHelper idleHelper;

    // Basic constructor
    public EntityAnimator(GameEntity entity) {
        this.entity = entity;
        this.sprite = new Sprite();
    }

    // Full constructor
    public EntityAnimator(GameEntity entity, SpriteWalkingHelper walkingHelper, SpriteIdleHelper idleHelper, GameAssets gameAssets) {
        this.entity = entity;
        this.walkingHelper = walkingHelper;
        this.idleHelper = idleHelper;
        this.sprite = new Sprite();

        // Initialize with idle sprite if available
        if (idleHelper != null) {
            Sprite idleSprite = idleHelper.getSprite();
            if (idleSprite != null) {
                this.sprite = idleSprite;
            }
        }
    }

    // Getters and setters
    public void setWalkingHelper(SpriteWalkingHelper walkingHelper) {
        this.walkingHelper = walkingHelper;
    }

    public void setIdleHelper(SpriteIdleHelper idleHelper) {
        this.idleHelper = idleHelper;
    }


    public Sprite updateAnimation(float delta) {
        try {
            // Get the entity's velocity
            Vector2 velocity = entity.getBody().getLinearVelocity();

            // Debug print to check velocities
            if (Math.abs(velocity.x) > 0.01f || Math.abs(velocity.y) > 0.01f) {
                System.out.println("Entity is moving: vx=" + velocity.x + ", vy=" + velocity.y);
            }

            // Update facing based on velocity
            if (velocity.x < -0.1f) {
                isFacingRight = false;
            } else if (velocity.x > 0.1f) {
                isFacingRight = true;
            }

            // Check if entity is moving - use a small threshold
            float movementThreshold = 0.1f; // Increased threshold
            boolean isMoving = Math.abs(velocity.x) > movementThreshold || Math.abs(velocity.y) > movementThreshold;

            // Apply appropriate animation
            if (isMoving && walkingHelper != null) {
                try {
                    System.out.println("Using walking animation");
                    walkingHelper.updateAnimation(velocity, delta);
                    Sprite walkingSprite = walkingHelper.getSprite();
                    if (walkingSprite != null) {

                        sprite = walkingSprite;
                    }
                } catch (Exception e) {
                    System.err.println("Error in walking animation: " + e.getMessage());
                    // Fall back to idle if walking fails
                    if (idleHelper != null) {
                        idleHelper.setFacingRight(isFacingRight);
                        idleHelper.update(delta);
                        sprite = idleHelper.getSprite();
                    }
                }
            } else {
                // Use idle animation
                if (idleHelper != null) {
                    idleHelper.setFacingRight(isFacingRight);
                    idleHelper.update(delta);
                    Sprite idleSprite = idleHelper.getSprite();
                    if (idleSprite != null) {
                        sprite = idleSprite;
                    }
                }
            }

            // Make sure the sprite is flipped correctly
            if (sprite != null) {
                sprite.setFlip(!isFacingRight, false);
            }

        } catch (Exception e) {
            System.err.println("Error in EntityAnimator.updateAnimation: " + e.getMessage());
            e.printStackTrace();
        }

        return sprite;
    }
}