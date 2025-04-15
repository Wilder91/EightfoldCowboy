package objects.animals.squirrel;

import helper.EntityMovement;

public class SquirrelMovement extends EntityMovement {
    private float movementTimer = 0;
    private float movementDuration = 7f;
    private float restTimer = (float) (Math.random() * .5f);
    private float restDuration = 1.5f;
    private float squirrelSpeed = 2.2f;
    private int moveDirection = 1;
    private int moveCount = 0;
    private boolean returningToOrigin = false;


    public SquirrelMovement(Squirrel squirrel) {
        super(squirrel);
        float randomMovementDuration = 0.3f + (float)(Math.random() * 0.7f); // 0.3 to 1.0 seconds
        float randomRestDuration = 0.5f + (float)(Math.random() * 1.5f); // 0.5 to 2.0 seconds
       setMovementDuration(randomMovementDuration);
       setRestDuration(randomRestDuration);

    }

    @Override
    public void update(float delta) {
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

    // Configuration methods
    public void setSquirrelSpeed(float speed) {
        this.squirrelSpeed = speed;
    }

    public void setMovementDuration(float duration) {
        this.movementDuration = duration;
    }

    public void setRestDuration(float duration) {
        this.restDuration = duration;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isReturningToOrigin() {
        return returningToOrigin;
    }
}