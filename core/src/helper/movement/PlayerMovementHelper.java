package helper.movement;

import objects.GameAssets;

public class PlayerMovementHelper extends SpriteMovementHelper{
    public PlayerMovementHelper(GameAssets gameAssets, String animalType, int[] frameCounts, boolean randomFlip) {
        super(gameAssets, animalType, frameCounts, randomFlip);
    }
}
