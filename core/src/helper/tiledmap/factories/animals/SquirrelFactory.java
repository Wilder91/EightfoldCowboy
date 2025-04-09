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
import objects.animals.Squirrel;

public class SquirrelFactory {
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private static int squirrelCounter = -1;
    private boolean isStatic;
    private float customSpeed;

    /**
     * Creates a factory for squirrels
     * @param screenInterface The game screen interface
     * @param gameAssets The game assets
     * @param isStatic Whether the squirrels should be static (not moving)
     */
    public SquirrelFactory(ScreenInterface screenInterface, GameAssets gameAssets, boolean isStatic) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.isStatic = isStatic;
        this.customSpeed = 1.2f; // Default speed - making squirrels faster than chickens
    }

    /**
     * Sets a custom movement speed for squirrels created by this factory
     * @param speed The movement speed
     */
    public void setCustomSpeed(float speed) {
        this.customSpeed = speed;
    }

    /**
     * Creates a squirrel from a polygon map object
     * @param polygonMapObject The polygon map object defining the squirrel's position
     */
    public void createSquirrel(PolygonMapObject polygonMapObject) {
        int squirrelId = ++squirrelCounter;

        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        // Create the squirrel's physics body
        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                boundingRectangle.width * 0.5f,  // Make hitbox smaller than chicken for faster movement
                boundingRectangle.height * 0.5f, // and better gameplay feel
                isStatic,
                screenInterface.getWorld(),
                ContactType.SQUIRREL,  // Assuming you have an ANIMAL contact type
                squirrelId
        );

        // Create the squirrel game entity
        Squirrel squirrel = new Squirrel(
                boundingRectangle.width,
                boundingRectangle.height,
                body,
                screenInterface,
                gameAssets
        );

        // Set custom properties
        squirrel.setSquirrelSpeed(customSpeed);

        // Squirrels move more quickly and erratically than chickens
        float randomMovementDuration = 0.3f + (float)(Math.random() * 0.7f); // 0.3 to 1.0 seconds
        float randomRestDuration = 0.5f + (float)(Math.random() * 1.5f); // 0.5 to 2.0 seconds
        squirrel.setMovementDuration(randomMovementDuration);
        squirrel.setRestDuration(randomRestDuration);

        // Add the squirrel to the screen
        screenInterface.addSquirrel(squirrel);
    }
}