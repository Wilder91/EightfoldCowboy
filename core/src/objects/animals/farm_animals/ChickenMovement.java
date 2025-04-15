package objects.animals.farm_animals;

import com.badlogic.gdx.math.Vector2;
import helper.EntityMovement;
import objects.GameEntity;

public class ChickenMovement extends EntityMovement {
    private float movementTimer = 0;
    private float movementDuration = 1.2f; // Duration of movement in one direction
    private float restTimer = (float) (Math.random() * 3f); // Random initial rest time between 0 and 3
    private float restDuration = 1.5f; // Duration of rest between movements
    private boolean isMoving = false;
    private Vector2 currentDirection = new Vector2(0, 0);
    private float chickenSpeed = 0.8f;
    private Vector2 originalPosition = new Vector2();
    private int moveCount = 0;
    private boolean returningToOrigin = false;

    public ChickenMovement(GameEntity entity) {
        super(entity);

    }
    @Override
    public void update(float delta){
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

    public void setRestDuration(float duration) {
        this.restDuration = duration;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setChickenSpeed(float speed){
        this.chickenSpeed = speed;
    }

    public boolean isReturningToOrigin(){
        return returningToOrigin;
    }

    public void setMovementDuration(float duration){
        this.movementDuration = duration;
    }
}
