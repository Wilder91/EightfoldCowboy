package helper.state;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.eightfold.player.Player;

public class PlayerMovementStateManager extends EntityStateManager<Player, Player.State> {

    private final StateHandler<Player, Player.State> idleStateHandler = new StateHandler<Player, Player.State>() {
        @Override
        public void update(Player player, float delta) {
            Vector2 velocity = player.getBody().getLinearVelocity();
            // Check for state transitions
            if (Math.abs(velocity.x) > 0.1f || Math.abs(velocity.y) > 0.1f) {
                changeState(player, Player.State.RUNNING);
                return;
            }

            // Update idle animation - don't call updateDirectionVariables here!
            // This preserves the last direction from the running state
            player.getMovementHelper().setAction("idle");

            // Pass zero velocity to ensure we're properly in idle mode
            player.getMovementHelper().updateAnimation(new Vector2(0, 0), delta);

            // Make sure sprite is updated correctly
            player.setSprite(player.getMovementHelper().getSprite());

            // Handle sprite flipping
            if (!player.isFacingRight()) {
                player.getSprite().setFlip(true, false);
            } else if (player.isFacingRight()) {
                player.getSprite().setFlip(false, false);
            }
        }

        @Override public void enter(Player player) { /* Init idle state */ }
        @Override public void exit(Player player) { /* Clean up */ }
    };

    private final StateHandler<Player, Player.State> runningStateHandler = new StateHandler<Player, Player.State>() {
        @Override
        public void update(Player player, float delta) {
            Vector2 velocity = player.getBody().getLinearVelocity();
            // Check for state transitions
            if (Math.abs(velocity.x) <= 0.1f && Math.abs(velocity.y) <= 0.1f) {
                changeState(player, Player.State.IDLE);
                return;
            }
            // Update direction variables
            updateDirectionVariables(player, velocity);
            // Update running animation
            player.getMovementHelper().setAction("walk");
            player.getMovementHelper().updateAnimation(velocity, delta);
            player.setSprite(player.getMovementHelper().getSprite());
            // Handle sprite flipping
            if (!player.isFacingRight()) {
                player.getSprite().setFlip(true, false);
            } else if (player.isFacingRight()) {
                player.getSprite().setFlip(false, false);
            }
        }

        @Override public void enter(Player player) { /* Init running state */ }
        @Override public void exit(Player player) { /* Clean up */ }
    };

    private final StateHandler<Player, Player.State> attackingStateHandler = new StateHandler<Player, Player.State>() {
        @Override
        public void update(Player player, float delta) {
            // Check if attack animation is complete
            if (!player.getMeleeHelper().isAttacking()) {
                changeState(player, Player.State.IDLE);
            }
        }

        @Override
        public void enter(Player player) {
            Vector2 playerPosition = new Vector2(player.getX(), player.getY());
            player.getMeleeHelper().startAttack(player.getLastDirection(), playerPosition);
        }

        @Override public void exit(Player player) {
            /* Clean up */ }
    };

    // Helper method for direction updates (specific to Player)
    private void updateDirectionVariables(Player player, Vector2 velocity) {
        // Update isFacingRight
        if (velocity.x < -0.1f) {
            player.setFacingRight(false);
        } else if (velocity.x > 0.1f) {
            player.setFacingRight(true);
        }

        // Only update lastDirection when actually moving
        if (Math.abs(velocity.x) > 0.1f || Math.abs(velocity.y) > 0.1f) {
            if (velocity.y > 0.1f) {
                if (Math.abs(velocity.x) > 0.1f) {
                    player.setLastDirection("idleDiagonalUp");
                } else {
                    player.setLastDirection("idleUp");
                }
            } else if (velocity.y < -0.1f) {
                if (Math.abs(velocity.x) > 0.1f) {
                    player.setLastDirection("idleDiagonalDown");
                } else {
                    player.setLastDirection("idleDown");
                }
            } else if (Math.abs(velocity.x) > 0.1f) {
                player.setLastDirection("idleSide");
            }
        }
        // Don't change lastDirection when velocity is near zero
    }

    @Override
    protected Player.State getCurrentState(Player entity) {
        return entity.getCurrentPlayerState();
    }

    @Override
    protected void setCurrentState(Player entity, Player.State newState) {
        entity.setCurrentState(newState);
    }

    @Override
    protected StateHandler<Player, Player.State> getHandlerForState(Player.State state) {
        switch (state) {
            case IDLE: return idleStateHandler;
            case RUNNING: return runningStateHandler;
            case ATTACKING: return attackingStateHandler;
            default: return null;
        }
    }
}