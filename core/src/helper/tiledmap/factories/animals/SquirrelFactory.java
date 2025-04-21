package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import objects.animals.squirrel.Squirrel;

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
        this.customSpeed = 2.2f; // Default speed - making squirrels faster than chickens
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
                boundingRectangle.x + boundingRectangle.width,
                boundingRectangle.y + boundingRectangle.height,
                boundingRectangle.width * 0.2f,  // Make hitbox smaller than chicken for faster movement
                boundingRectangle.height * 0.2f, // and better gameplay feel
                isStatic,
                screenInterface.getWorld(),
                ContactType.SQUIRREL,
                squirrelId
        );

        // Create the squirrel game entity
        Squirrel squirrel = new Squirrel(
                boundingRectangle.width,
                boundingRectangle.height,
                body,
                screenInterface,
                gameAssets,
                100f
        );

        // Set custom properties
        //squirrel.setSquirrelSpeed(customSpeed);

        // Squirrels move more quickly and erratically than chickens


        // Add the squirrel to the screen
        screenInterface.addSquirrel(squirrel);
    }
}