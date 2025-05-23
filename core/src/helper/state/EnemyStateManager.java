package helper.state;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import objects.GameEntity;
import objects.enemies.ThicketSaint;

/**
 * A state manager for enemies that extends GameEntity
 */
public class EnemyStateManager extends EntityStateManager<GameEntity, GameEntity.State> {

    private float attackTimer = 0;
    private float attackDuration = 5f; // Default value, can be overridden
    private float pursuitTimer = 0;
    private float maxPursuitTime = 10f; // Default pursuit time limit

    private final StateHandler<GameEntity, GameEntity.State> idleStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        @Override
        public void update(GameEntity enemy, float delta) {
            if (enemy.getState() == GameEntity.State.DYING) {
                return; // Don't process any other state if already dying
            }
            Vector2 velocity = enemy.getBody().getLinearVelocity();

            // Check for state transitions
            if (Math.abs(velocity.x) > 0.1f || Math.abs(velocity.y) > 0.1f) {
                changeState(enemy, GameEntity.State.RUNNING);
                return;
            }

            // Check if we're dealing with a ThicketSaint
            if (enemy instanceof objects.enemies.ThicketSaint) {
                objects.enemies.ThicketSaint thicketSaint = (objects.enemies.ThicketSaint) enemy;

                // Update idle animation
                thicketSaint.getSimpleIdleHelper().setDirection(thicketSaint.getLastDirection());
                thicketSaint.getSimpleIdleHelper().setFacingRight(thicketSaint.isFacingRight());
                thicketSaint.getSimpleIdleHelper().update(delta);

                Sprite sprite = thicketSaint.getSimpleIdleHelper().getSprite();
                if (sprite != null) {
                    // Handle sprite flipping explicitly
                   // sprite.setFlip(!thicketSaint.isFacingRight(), false);
                    thicketSaint.setSprite(sprite);
                }
            }
            // For other entity types that have a standard idle helper
            else if (enemy.getIdleHelper() != null) {
                enemy.getIdleHelper().setDirection(enemy.getLastDirection());
                enemy.getIdleHelper().setFacingRight(enemy.isFacingRight());
                enemy.getIdleHelper().update(delta);

                Sprite sprite = enemy.getIdleHelper().getSprite();
                if (sprite != null) {
                   // sprite.setFlip(!enemy.isFacingRight(), false);
                    enemy.setSprite(sprite);
                }
            }
        }

        @Override
        public void enter(GameEntity enemy) {
            // Init idle state
        }

        @Override
        public void exit(GameEntity enemy) {
            // Clean up
        }
    };

    private final StateHandler<GameEntity, GameEntity.State> runningStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        @Override
        public void update(GameEntity enemy, float delta) {
            if (enemy.getState() == GameEntity.State.DYING) {
                return; // Don't process any other state if already dying
            }
            Vector2 velocity = enemy.getBody().getLinearVelocity();

            // Check for state transitions
            if (Math.abs(velocity.x) <= 0.1f && Math.abs(velocity.y) <= 0.1f) {
                changeState(enemy, GameEntity.State.IDLE);
                return;
            }

            // Update facing direction
            updateFacingDirection(enemy, velocity);

            // Check if we're dealing with a ThicketSaint
            if (enemy instanceof objects.enemies.ThicketSaint) {
                objects.enemies.ThicketSaint thicketSaint = (objects.enemies.ThicketSaint) enemy;

                // Update walking animation
                thicketSaint.getMovementHelper().setAction("walk");
                thicketSaint.getMovementHelper().setFacingRight(thicketSaint.isFacingRight());
                thicketSaint.getMovementHelper().updateAnimation(velocity, delta);
                Sprite sprite = thicketSaint.getMovementHelper().getSprite();
                thicketSaint.setSprite(sprite);
                // Get sprite and set flipping based on direction
                //Sprite sprite = thicketSaint.getMovementHelper().getSprite();

                if (sprite != null) {
                    sprite.setFlip(thicketSaint.isFacingRight(), false);
                    thicketSaint.setSprite(sprite);
                }

            }
            // For other entity types that have a standard walking helper
            else if (enemy.getWalkingHelper() != null) {
                enemy.getMovementHelper().setFacingRight(enemy.isFacingRight());
                enemy.getMovementHelper().setAction("walk");
                enemy.getMovementHelper().updateAnimation(velocity, delta);

//                enemy.getWalkingHelper().setFacingRight(enemy.isFacingRight());
//                enemy.getWalkingHelper().updateAnimation(velocity, delta);

                Sprite sprite = enemy.getMovementHelper().getSprite();
                if (sprite != null) {
                    //sprite.setFlip(!enemy.isFacingRight(), false);
                    enemy.setSprite(sprite);
                }
            }
        }

        @Override
        public void enter(GameEntity enemy) {
            // Init running state
        }

        @Override
        public void exit(GameEntity enemy) {
            // Clean up
        }
    };

    private final StateHandler<GameEntity, GameEntity.State> pursuingStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        @Override
        public void update(GameEntity enemy, float delta) {
            if (enemy.getState() == GameEntity.State.DYING) {
                return; // Don't process any other state if already dying
            }

            Vector2 velocity = enemy.getBody().getLinearVelocity();
            //System.out.println("Entity entering PURSUING state");
            // Update pursuit timer
            pursuitTimer += delta;

            // Check for state transitions
            if (Math.abs(velocity.x) <= 0.1f && Math.abs(velocity.y) <= 0.1f) {
                changeState(enemy, GameEntity.State.IDLE);
                return;
            }

            // Check if pursuit time exceeded
            if (pursuitTimer >= maxPursuitTime) {
                changeState(enemy, GameEntity.State.RUNNING);
                return;
            }

            // Update facing direction
            updateFacingDirection(enemy, velocity);

            // Check if we're dealing with a ThicketSaint
            if (enemy instanceof objects.enemies.ThicketSaint) {
                objects.enemies.ThicketSaint thicketSaint = (objects.enemies.ThicketSaint) enemy;
                // Use combat walking animation for pursuit
                if (thicketSaint.getMovementHelper() != null) {

                    thicketSaint.getMovementHelper().setFacingRight(thicketSaint.isFacingRight());
                    thicketSaint.getMovementHelper().setAction("combatwalk");
                    thicketSaint.getMovementHelper().updateAnimation(velocity, delta);
//                    thicketSaint.getCombatWalkingHelper().setFacingRight(thicketSaint.isFacingRight());
//                    thicketSaint.getCombatWalkingHelper().updateAnimation(velocity, delta);

                    Sprite sprite = thicketSaint.getMovementHelper().getSprite();
                    if (sprite != null) {
//                        System.out.println("Before flip - isFacingRight: " + thicketSaint.isFacingRight());
//                        System.out.println("Before flip - sprite is flipped: " + sprite.isFlipX());

                        // Try both ways to see which works
                        //sprite.setFlip(!thicketSaint.isFacingRight(), false);
                        sprite.setFlip(thicketSaint.isFacingRight(), false);

                        //System.out.println("After flip - sprite is flipped: " + sprite.isFlipX());
                        thicketSaint.setSprite(sprite);
                    }
                }

            }
            // For other entity types that have a combat walking helper
            else if (enemy.getCombatWalkingHelper() != null) {
                enemy.getCombatWalkingHelper().setFacingRight(enemy.isFacingRight());
                enemy.getCombatWalkingHelper().updateAnimation(velocity, delta);

                Sprite sprite = enemy.getCombatWalkingHelper().getSprite();
                if (sprite != null) {
                    sprite.setFlip(!enemy.isFacingRight(), false);
                    enemy.setSprite(sprite);
                }
            }

        }

        @Override
        public void enter(GameEntity enemy) {
            // Init pursuing state
            pursuitTimer = 0;
        }

        @Override
        public void exit(GameEntity enemy) {
            // Clean up
            pursuitTimer = 0;
        }
    };

    private final StateHandler<GameEntity, GameEntity.State> dyingStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        @Override
        public void update(GameEntity enemy, float delta) {
            // Stop movement
            //enemy.hideHealthBar();

            enemy.getBody().setLinearVelocity(0, 0);


            // Use death animation
            if (enemy instanceof objects.enemies.ThicketSaint) {
                objects.enemies.ThicketSaint thicketSaint = (objects.enemies.ThicketSaint) enemy;

                // Update death animation
                boolean animationStillPlaying = thicketSaint.getDeathHelper().update(delta);

                // Set the sprite
                Sprite deathSprite = thicketSaint.getDeathHelper().getSprite();
                if (deathSprite != null) {
                    thicketSaint.setSprite(deathSprite);
                }

                // Handle completion
                if (!animationStillPlaying) {
                    //System.out.println("Death animation complete for: " + thicketSaint.getEntityName());
                    // Optional: fade out or mark for removal

                }

            }

            // Add similar handling for other entity types
        }

        @Override
        public void enter(GameEntity enemy) {

            enemy.getBody().setLinearVelocity(0, 0);
           //System.out.println("Entity entering DYING state");
        }


        @Override
        public void exit(GameEntity enemy) {
            // Clean up
        }
    };

    private final StateHandler<GameEntity, GameEntity.State> woundedStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        private boolean animationStarted = false;

        @Override
        public void update(GameEntity enemy, float delta) {
            enemy.getBody().setLinearVelocity(0, 0);

            // Only set the action once when entering this state
            if (!animationStarted) {
                enemy.getMovementHelper().setAction("hit");
                animationStarted = true;
            }

            // Update the animation
            enemy.getMovementHelper().updateAnimation(enemy.getBody().getLinearVelocity(), delta);
            Sprite sprite = enemy.getMovementHelper().getSprite();
            enemy.setSprite(sprite);

            // Check if animation is complete
            Animation<TextureRegion> currentAnimation = enemy.getMovementHelper().getCurrentAnimation();
            if (currentAnimation != null &&
                    enemy.getMovementHelper().getStateTime() >= currentAnimation.getAnimationDuration()) {
                // Animation is complete, change to next state
                changeState(enemy, GameEntity.State.IDLE); // or whatever state should come next
            }
        }

        @Override
        public void enter(GameEntity enemy) {
            enemy.getBody().setLinearVelocity(0, 0);
            animationStarted = false; // Reset the flag when entering state
        }

        @Override
        public void exit(GameEntity enemy) {
            // Clean up
            animationStarted = false; // Reset for next time
        }

    };

    private final StateHandler<GameEntity, GameEntity.State> attackingStateHandler = new StateHandler<GameEntity, GameEntity.State>() {
        @Override
        public void update(GameEntity enemy, float delta) {
            // Update attack timer
            attackTimer += delta;

            // Check if we're dealing with a ThicketSaint
            if (enemy instanceof ThicketSaint) {
                ThicketSaint thicketSaint = (ThicketSaint) enemy;

                // Get enemy position and direction
                Body body = thicketSaint.getBody();
                Vector2 position = new Vector2(body.getPosition().x * 100, body.getPosition().y * 100); // Assuming PPM = 100
                Vector2 direction = new Vector2(thicketSaint.isFacingRight() ? 1 : -1, 0);

                // Update combat helper
                thicketSaint.getMeleeHelper().setFacingRight(thicketSaint.isFacingRight());
                thicketSaint.getMeleeHelper().update(delta, position, direction, thicketSaint.isFacingRight(), thicketSaint.getLastDirection());

                // Set sprite from combat helper
                Sprite sprite = thicketSaint.getMeleeHelper().getSprite();
                if (sprite != null) {
                    sprite.setFlip(!thicketSaint.isFacingRight(), false);
                    thicketSaint.setSprite(sprite);
                }

                // Check if attack is complete
                if (!thicketSaint.getMeleeHelper().isAttacking() || attackTimer >= attackDuration) {
                    // After attack, go back to pursuing if target is still valid
                    if (thicketSaint.hasValidTarget()) {

                        changeState(enemy, GameEntity.State.PURSUING);
                    } else {
                        changeState(enemy, GameEntity.State.IDLE);
                    }
                    attackTimer = 0;
                }
                if (thicketSaint.getHp() < 1){
                    thicketSaint.dispose();
                }
            }
            // For other entity types that have a standard melee helper
            else if (enemy.getMeleeHelper() != null) {
                // Get enemy position and direction
                Body body = enemy.getBody();
                Vector2 position = new Vector2(body.getPosition().x * 100, body.getPosition().y * 100); // Assuming PPM = 100
                Vector2 direction = new Vector2(enemy.isFacingRight() ? 1 : -1, 0);

                // Update combat helper
                enemy.getMeleeHelper().setFacingRight(enemy.isFacingRight());
                enemy.getMeleeHelper().update(delta, position, direction, enemy.isFacingRight(), enemy.getLastDirection());

                // Set sprite from combat helper
                Sprite sprite = enemy.getMeleeHelper().getSprite();
                if (sprite != null) {
                   // sprite.setFlip(!enemy.isFacingRight(), false);
                    enemy.setSprite(sprite);
                }

                // Check if attack is complete
                if (!enemy.getMeleeHelper().isAttacking() || attackTimer >= attackDuration) {
                    // After attack, go back to pursuing if target is still valid
                    if (enemy.hasValidTarget()) {
                        changeState(enemy, GameEntity.State.PURSUING);
                    } else {
                        changeState(enemy, GameEntity.State.PURSUING);
                    }
                    attackTimer = 0;
                }
            }
        }

        @Override
        public void enter(GameEntity enemy) {
            // Init attacking state
            attackTimer = 0;

            Vector2 position = new Vector2(enemy.getX(), enemy.getY());
            enemy.getMeleeHelper().startAttack(enemy.getLastDirection(), position);
        }

        @Override
        public void exit(GameEntity enemy) {
            // Clean up
        }
    };

    /**
     * Updates the facing direction based on movement velocity
     */
    private void updateFacingDirection(GameEntity enemy, Vector2 velocity) {
        float vx = velocity.x;
        float vy = velocity.y;

        try {
            // Check if we're dealing with a ThicketSaint
            if (enemy instanceof objects.enemies.ThicketSaint) {
                objects.enemies.ThicketSaint thicketSaint = (objects.enemies.ThicketSaint) enemy;

                // Update facing direction and lastDirection based on movement
                if (Math.abs(vy) > Math.abs(vx)) {
                    // Vertical movement is dominant
                    if (vy > 0.1f) {
                        thicketSaint.setLastDirection("idleUp");
                    } else if (vy < -0.1f) {
                        thicketSaint.setLastDirection("idleDown");
                    }
                } else {
                    // Horizontal movement is dominant
                    if (vx > 0.1f) {
                        thicketSaint.setLastDirection("idleSide");
                        thicketSaint.setFacingRight(true);
                    } else if (vx < -0.1f) {
                        thicketSaint.setLastDirection("idleSide");
                        thicketSaint.setFacingRight(false);
                    }
                }
            }
            // Generic entity handling
            else {
                // Update facing direction and lastDirection based on movement
                if (Math.abs(vy) > Math.abs(vx)) {
                    // Vertical movement is dominant
                    if (vy > 0.1f) {
                        enemy.setLastDirection("idleUp");
                    } else if (vy < -0.1f) {
                        enemy.setLastDirection("idleDown");
                    }
                } else {
                    // Horizontal movement is dominant
                    if (vx > 0.1f) {
                        enemy.setLastDirection("idleSide");
                        enemy.setFacingRight(true);
                    } else if (vx < -0.1f) {
                        enemy.setLastDirection("idleSide");
                        enemy.setFacingRight(false);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating facing direction: " + e.getMessage());
        }
    }

    /**
     * Sets the attack duration for the enemy
     * @param duration The duration in seconds
     */
    public void setAttackDuration(float duration) {
        this.attackDuration = duration;
    }


    @Override
    protected GameEntity.State getCurrentState(GameEntity entity) {
        return entity.getState();
    }

    @Override
    protected void setCurrentState(GameEntity entity, GameEntity.State newState) {
        entity.setState(newState);
    }

    @Override
    protected StateHandler<GameEntity, GameEntity.State> getHandlerForState(GameEntity.State state) {
        switch (state) {
            case IDLE: return idleStateHandler;
            case RUNNING: return runningStateHandler;
            case PURSUING: return pursuingStateHandler;
            case ATTACKING: return attackingStateHandler;
            case DYING: return dyingStateHandler;
            case WOUNDED: return woundedStateHandler;
            default: return null;
        }
    }
}