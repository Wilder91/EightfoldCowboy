package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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



    public void createRock(PolygonMapObject polygonMapObject,  String textureName) {
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



        // Create the rock entity
        Rock rock = new Rock(
                rockBody,
                screenInterface,
                rockId,
                textureName,
                gameAssets,
                gameContactListener
        );

        // Add the rock to the default rock list in the ScreenInterface
        screenInterface.addRock(rock);
    }
}
