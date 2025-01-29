package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

        // Position the rock at the center of its bounding rectangle
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        // Create the physics body for the rock
        Body rockBody = screenInterface.getWorld().createBody(bodyDef);
        TextureRegion topTexture = gameAssets.getRockTopTexture(rockType);
        TextureRegion bottomTexture = gameAssets.getRockBottomTexture(rockType);
        // Define the shape of the rock (using a polygon based on its vertices)
        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        // Convert vertices to world coordinates
        for (int i = 0; i < vertices.length / 2; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
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
                1,
                topTexture,
                bottomTexture,
                gameAssets,
                gameContactListener
        );

        // Add the rock to the default rock list in the ScreenInterface
        screenInterface.addRock(rock);
    }
}
