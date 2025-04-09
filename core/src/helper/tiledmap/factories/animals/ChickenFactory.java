package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import helper.movement.Facing;
import com.mygdx.eightfold.GameAssets;
import objects.animals.bird.Chicken;

public class ChickenFactory {
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private static int chickenCounter = -1;
    private boolean isStatic;
    private float customSpeed;

    /**
     * Creates a factory for chickens
     * @param screenInterface The game screen interface
     * @param gameAssets The game assets
     * @param isStatic Whether the chickens should be static (not moving)
     */
    public ChickenFactory(ScreenInterface screenInterface, GameAssets gameAssets, boolean isStatic) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.isStatic = isStatic;
        this.customSpeed = 0.8f; // Default speed
    }

    /**
     * Sets a custom movement speed for chickens created by this factory
     * @param speed The movement speed
     */
    public void setCustomSpeed(float speed) {
        this.customSpeed = speed;
    }

    /**
     * Creates a chicken from a polygon map object
     * @param polygonMapObject The polygon map object defining the chicken's position
     */
    public void createChicken(PolygonMapObject polygonMapObject) {
        int chickenId = ++chickenCounter;

        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        // Create the chicken's physics body
        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                boundingRectangle.width * 0.6f,  // Make hitbox smaller than the visual
                boundingRectangle.height * 0.6f, // for better gameplay feel
                isStatic,
                screenInterface.getWorld(),
                ContactType.CHICKEN,  // Assuming you have an ANIMAL contact type
                chickenId
        );

        // Create the chicken game entity
        Chicken chicken = new Chicken(
                boundingRectangle.width,
                boundingRectangle.height,
                body,
                screenInterface,
                gameAssets
        );

        // Set custom properties if needed
        chicken.setChickenSpeed(customSpeed);

        // If you want to randomize movement durations to create variety
        float randomMovementDuration = .5f + (float)(Math.random() * 1f); // .5 to 1.5 seconds
        float randomRestDuration = 1.0f + (float)(Math.random() * 2.0f); // 1 to 3 seconds
        chicken.setMovementDuration(randomMovementDuration);
        chicken.setRestDuration(randomRestDuration);

        // Add the chicken to the screen
        screenInterface.addChicken(chicken);
    }
}