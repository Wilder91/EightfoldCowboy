package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import objects.StationaryObjectAnimator;
import objects.inanimate.Rock;

import static helper.Constants.PPM;
import static helper.ContactType.ROCK; // Assuming you have this contact type

public class RockFactory extends InanimateEntityFactory {
    private static int rockCounter = 0;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private String atlasName;
    private String textureName;

    public RockFactory(ScreenInterface screenInterface, GameAssets gameAssets, String atlasName, String textureName, GameContactListener gameContactListener) {
        super(screenInterface, gameAssets, gameContactListener);
        this.gameAssets = gameAssets;
        this.screenInterface = screenInterface;
        this.gameContactListener = gameContactListener;
        this.atlasName = atlasName;
        this.textureName = textureName;
    }

    public void createRock(PolygonMapObject polygonMapObject, String textureName) {
        int rockId = ++rockCounter;

        // Get the texture directly from the atlas
        String atlasPath = "atlases/eightfold/rocks.atlas";
        TextureRegion rockTexture = gameAssets.getAtlas(atlasPath).findRegion(textureName);

        if (rockTexture == null) {
            System.err.println("Missing texture for rock: " + textureName);
            return;
        }

        // Get texture dimensions
        float textureWidth = rockTexture.getRegionWidth();
        float textureHeight = rockTexture.getRegionHeight();

        // Create body
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body rockBody = screenInterface.getWorld().createBody(bodyDef);

        // Create physics shape proportional to texture size
        PolygonShape shape = new PolygonShape();

        // Scale factor based on size
        float scaleFactor = 0.25f;
        if (textureWidth < 50) { // Small rock
            scaleFactor = 0.1f;
        } else if (textureWidth > 100) { // Large rock
            scaleFactor = 0.3f;
        }

        shape.setAsBox(
                (textureWidth * scaleFactor) / .9f / PPM,
                (textureHeight * scaleFactor) / 4 / PPM
        );

        Fixture rockFixture = rockBody.createFixture(shape, 0.0f);
        rockFixture.setUserData(new BodyUserData(rockId, ROCK, rockBody, textureName));

        // Set up collision filtering
        Filter filter = new Filter();
        filter.categoryBits = ROCK.getCategoryBits();
        filter.maskBits = ROCK.getMaskBits();
        rockFixture.setFilterData(filter);

        shape.dispose();

        // Create the rock with properly sized body
        Rock rock = new Rock(
                rockBody,
                screenInterface,
                rockId,
                "rocks",
                textureName,
                gameAssets,
                gameContactListener,
                100f
        );

        screenInterface.addRock(rock);
    }

    @Override
    public void createEntity(PolygonMapObject polygonMapObject, String polygonName) {

    }
}