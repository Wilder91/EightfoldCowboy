package helper.tiledmap.factories;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.ContactType;
import helper.BodyUserData;
import objects.inanimate.Fence;


import static helper.Constants.PPM;

public class FenceFactory {
    private final ScreenInterface screenInterface;
    private final GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private static int fenceCounter = 0;

    public FenceFactory(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
    }

    // Update the fence creation code in your FenceFactory:

    public void createFence(PolygonMapObject polygonMapObject, String fenceName) {
        int fenceId = ++fenceCounter;

        // Get polygon bounds
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle bounds = polygon.getBoundingRectangle();

        // Calculate center position for the body
        float centerX = bounds.x + bounds.width / 2;
        float centerY = bounds.y + bounds.height / 2;

        // Create the physics body at the center position
        Body fenceBody = createBodyFromPolygon(polygonMapObject, fenceId, fenceName);

        // Get the texture for the fence
        TextureRegion texture = gameAssets.getAtlas("atlases/eightfold/fences.atlas").findRegion(fenceName);
        if (texture == null) {
            System.err.println("Fence texture not found for type: " + fenceName);
            return;
        }



        // Create the fence object with texture and body
        Fence fence = new Fence(// Center Y
                fenceBody,
                fenceId,
                fenceName,
                screenInterface,
                gameAssets,
                gameContactListener
        );

        // Add the fence to the screen
        screenInterface.addFence(fence);
    }

    private Body createBodyFromPolygon(PolygonMapObject polygonMapObject, int fenceId, String fenceType) {
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle bounds = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (bounds.x + bounds.width / 2) / PPM,
                (bounds.y + bounds.height / 2) / PPM
        );

        Body body = screenInterface.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        shape.set(worldVertices);
        Fixture fixture = body.createFixture(shape, 0.0f);

        fixture.setUserData(new BodyUserData(fenceId, ContactType.FENCE, body));

        Filter filter = new Filter();
        filter.categoryBits = ContactType.FENCE.getCategoryBits();
        filter.maskBits = ContactType.FENCE.getMaskBits();
        fixture.setFilterData(filter);

        shape.dispose();
        return body;
    }
}