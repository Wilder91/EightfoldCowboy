package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.BodyUserData;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import objects.animals.farm_animals.Chicken;

import static helper.Constants.PPM;
import static helper.ContactType.*;

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



    public void createChicken(PolygonMapObject polygonMapObject, String chickenName) {
        int chickenId = ++chickenCounter;

        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        float bodyWidth = boundingRectangle.width * 0.2f;
        float bodyHeight = boundingRectangle.height * 0.2f;

        // Create the chicken's physics body at the center of the bounding rectangle
        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                bodyWidth,
                bodyHeight,
                isStatic,
                screenInterface.getWorld(),
                ContactType.CHICKEN,
                chickenId
        );

        // Create the chicken game entity with position offset
        Chicken chicken = new Chicken(
                boundingRectangle.width,
                boundingRectangle.height,
                body,
                screenInterface,
                gameAssets,
                chickenName
        );
        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        shape.set(worldVertices);
        shape.setAsBox(boundingRectangle.width / 8/  PPM, boundingRectangle.height / 6 / PPM);

        // If the Chicken class handles its own rendering offset, make sure it has the correct values
        // You might need to add this method to your Chicken class
        Fixture chickenFixture = body.createFixture(shape, 0.0f);

        chickenFixture.setUserData(new BodyUserData(chickenId, CHICKEN, body));

        Filter filter = new Filter();

        filter.categoryBits = CHICKEN.getCategoryBits();
        filter.maskBits = CHICKEN.getMaskBits();
        chickenFixture.setFilterData(filter);

        // Rest of your code remains the same
        chicken.setChickenSpeed(customSpeed);
        float randomMovementDuration = .5f + (float)(Math.random() * 1f);
        float randomRestDuration = 1.0f + (float)(Math.random() * 2.0f);
        chicken.setMovementDuration(randomMovementDuration);
        chicken.setRestDuration(randomRestDuration);
        screenInterface.addChicken(chicken);
    }
}