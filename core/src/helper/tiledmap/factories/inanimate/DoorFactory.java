package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import objects.inanimate.Door;

import static helper.Constants.PPM;
import static helper.ContactType.DOOR;

public class DoorFactory {
    private static int doorCounter = 0;
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    public DoorFactory(GameScreen gameScreen, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
    }

    public void createDoor(PolygonMapObject polygonMapObject) {
        int doorId = ++doorCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Adjust position calculation
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body doorBody = gameScreen.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        // Adjust size calculation
        shape.setAsBox(
                boundingRectangle.width / 2 / PPM,
                boundingRectangle.height / 2 / PPM
        );

        Fixture doorFixture = doorBody.createFixture(shape, 0.0f);
        doorFixture.setUserData(new BodyUserData(doorId, DOOR, doorBody));
        shape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = DOOR.getCategoryBits();
        filter.maskBits = DOOR.getMaskBits();
        doorFixture.setFilterData(filter);

        Door door = new Door(
                boundingRectangle.width,
                boundingRectangle.height,
                doorBody,
                gameScreen,
                doorId,
                gameAssets,
                gameContactListener
        );

        gameScreen.addDoor(door);
    }
}
