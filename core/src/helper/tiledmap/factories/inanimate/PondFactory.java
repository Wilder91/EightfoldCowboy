package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import helper.ContactType;
import objects.inanimate.Bush;
import objects.inanimate.Pond;


import static helper.Constants.PPM;
import static helper.ContactType.TREE;



public class PondFactory extends InanimateEntityFactory {

    private static int pondCounter = 0;

    public PondFactory(ScreenInterface screen, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(screen, gameAssets, gameContactListener);
    }

    @Override
    public void createEntity(PolygonMapObject polygonMapObject, String polygonName) {

    }


    public void createPond(PolygonMapObject polygonMapObject, int pondType) {
        int pondId = ++pondCounter;

        // Get the polygon position
        Polygon polygon = polygonMapObject.getPolygon();
        float[] vertices = polygon.getTransformedVertices();

        // Calculate center position from polygon bounds
        Rectangle boundingRectangle = polygon.getBoundingRectangle();
        float centerX = (boundingRectangle.x + boundingRectangle.width / 2) / PPM;
        float centerY = (boundingRectangle.y + boundingRectangle.height / 2) / PPM;

        // Get texture size from atlas
        TextureRegion texture = gameAssets.getAtlas("atlases/eightfold/pond.atlas").findRegion("Pond" );
        if (texture == null) {
            System.err.println("Pond texture not found for type: " + pondType);
            return;
        }

        float textureWidth = texture.getRegionWidth();
        float textureHeight = texture.getRegionHeight();

        // Define Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(centerX, centerY);

        Body pondBody = screenInterface.getWorld().createBody(bodyDef);

        // Create Pond with texture size
        Pond pond = new Pond(
                textureWidth,
                textureHeight,
                pondBody,
                screenInterface,
                pondType,
                pondId,
                gameAssets,
                gameContactListener,
                100f
        );

        screenInterface.addPond(pond);
    }



}

