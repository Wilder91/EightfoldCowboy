package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import com.mygdx.eightfold.GameAssets;
import objects.inanimate.Rock;


import static helper.Constants.PPM;
import static helper.ContactType.TREE;

public class RockFactory extends InanimateEntityFactory {
    private static int rockCounter = 0;

    public RockFactory(ScreenInterface screen, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(screen, gameAssets, gameContactListener);
    }

    @Override
    public void createEntity(PolygonMapObject polygonMapObject, String polygonName) {
        // Implement if needed for general entity creation
    }

    public void createRock(PolygonMapObject polygonMapObject, int rockType) {
        int rockId = ++rockCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Adjust the positioning based on the rock type (top or bottom)
        if (rockType == Rock.ROCK_LARGE_TOP) {
            bodyDef.position.set(
                    (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                    (boundingRectangle.y + boundingRectangle.height / 2 + boundingRectangle.height / 4) / PPM // Adjust for top half
            );
        } else if (rockType == Rock.ROCK_LARGE_BOTTOM) {
            bodyDef.position.set(
                    (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                    (boundingRectangle.y + boundingRectangle.height / 2 - boundingRectangle.height / 4) / PPM // Adjust for bottom half
            );
        } else {
            // For other rocks (not split rocks), center them normally
            bodyDef.position.set(
                    (boundingRectangle.x + boundingRectangle.width / 4) / PPM,
                    (boundingRectangle.y + boundingRectangle.height / 4) / PPM
            );
        }

        // Create the physics body for the rock
        Body rockBody = screenInterface.getWorld().createBody(bodyDef);

        // Define the shape of the rock (use a polygon or bounding box)
        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        // Adjust hitbox size based on rock type
        float hitboxScale = 1.0f; // Default scale for normal rocks

        if (rockType == Rock.ROCK_LARGE_TOP || rockType == Rock.ROCK_LARGE_BOTTOM) {
            hitboxScale = 0.1f; // Scale hitbox down to 70% for split rocks
        }

        // Apply the scaling to the vertices
        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            current.scl(hitboxScale); // Scale the hitbox size
            worldVertices[i] = current;
        }

        shape.set(worldVertices);

        // Create a fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f; // Bounciness of the rock

        rockBody.createFixture(fixtureDef);
        shape.dispose(); // Dispose of the shape after using it

        // Create the rock entity
        Rock rock = new Rock(
                rockBody,
                screenInterface,
                rockType,
                rockId,
                gameAssets,
                gameContactListener
        );

        // Add the rock to the appropriate list in the ScreenInterface
        if (rockType == Rock.ROCK_LARGE_TOP) {
            screenInterface.addUpperRock(rock);  // Add to top rock list
        } else if (rockType == Rock.ROCK_LARGE_BOTTOM) {
            screenInterface.addLowerRock(rock);  // Add to bottom rock list
        } else {
            screenInterface.addRock(rock);  // For non-split rocks, use the default rock list
        }
    }

}

