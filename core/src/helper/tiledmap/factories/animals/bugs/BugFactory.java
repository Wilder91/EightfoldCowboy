package helper.tiledmap.factories.animals.bugs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.ContactType;
import helper.BodyUserData;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;

import static helper.Constants.PPM;

public class BugFactory {
    private final ScreenInterface screenInterface;
    private final GameAssets gameAssets;
    private static int bugCounter = 0;

    public static final int SMALL_WHITE_BUTTERFLY = 0;
    public static final int DRAGONFLY = 1;

    public BugFactory(ScreenInterface screenInterface, GameAssets gameAssets) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
    }

    public void createBug(PolygonMapObject polygonMapObject, int bugType) {
        switch (bugType) {
            case SMALL_WHITE_BUTTERFLY:
                createButterfly(polygonMapObject);
                break;
            case DRAGONFLY:
                createDragonfly(polygonMapObject);
                break;
            default:
                System.err.println("Unknown bug type: " + bugType);
        }
    }

    private void createButterfly(PolygonMapObject polygonMapObject) {
        int bugId = ++bugCounter;
        Body bugBody = createBodyFromPolygon(polygonMapObject, bugId, "butterfly");
        TextureRegion texture = gameAssets.getAtlas("atlases/eightfold/bugs.atlas").findRegion("Butterfly_Small_White" );
        if (texture == null) {
            System.err.println("Pond texture not found for type: " + bugId);
            return;
        }
        float textureWidth = texture.getRegionWidth();
        float textureHeight = texture.getRegionHeight();

        Rectangle bounds = polygonMapObject.getPolygon().getBoundingRectangle();
        Butterfly butterfly = new Butterfly(
                textureWidth,
                textureHeight,
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2,
                bugBody,
                bugId,
                SMALL_WHITE_BUTTERFLY,
                screenInterface,
                gameAssets
        );
        //System.out.println("Butterfly created! Type: " );
        screenInterface.addButterfly(butterfly);
    }

    private void createDragonfly(PolygonMapObject polygonMapObject) {
        int bugId = ++bugCounter;
        Body bugBody = createBodyFromPolygon(polygonMapObject, bugId, "dragonfly");

        Rectangle bounds = polygonMapObject.getPolygon().getBoundingRectangle();
        TextureRegion texture = gameAssets.getAtlas("atlases/eightfold/bugs.atlas").findRegion("Dragonfly" );
        if (texture == null) {
            System.err.println("Pond texture not found for type: " + bugId);
            return;
        }
        float textureWidth = texture.getRegionWidth();
        float textureHeight = texture.getRegionHeight();
        System.out.println(textureWidth);
        Dragonfly dragonfly = new Dragonfly(
                textureWidth,
                textureHeight,
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2,
                bugBody,
                bugId,
                DRAGONFLY,
                screenInterface,
                gameAssets
        );

        screenInterface.addDragonfly(dragonfly);
    }

    private Body createBodyFromPolygon(PolygonMapObject polygonMapObject, int bugId, String bugType ) {
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
        shape.setAsBox(bounds.width / 2 / PPM, bounds.height / 2 / PPM);
        Fixture fixture = body.createFixture(shape, 0.0f);

        fixture.setUserData(new BodyUserData(bugId, ContactType.BUTTERFLY, body));

        Filter filter = new Filter();
        filter.categoryBits = ContactType.BUG.getCategoryBits();
        filter.maskBits = ContactType.BUG.getMaskBits();
        fixture.setFilterData(filter);

        shape.dispose();
        return body;
    }
}
