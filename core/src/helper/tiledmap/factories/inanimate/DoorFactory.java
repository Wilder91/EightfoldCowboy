package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import objects.inanimate.Door;

import static helper.Constants.PPM;
import static helper.ContactType.DOOR;

public class DoorFactory {
    private static int doorCounter = 0;
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    public DoorFactory(ScreenInterface screen, GameAssets gameAssets) {
        this.screenInterface = screen;
        this.gameAssets = gameAssets;

        this.screenInterface = screenInterface;
    }

    public void createDoor(PolygonMapObject polygonMapObject, String polygonName) {
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

        Body doorBody = screenInterface.getWorld().createBody(bodyDef);

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
        //println("door factory: " + polygonName);
        Door door = new Door(
                boundingRectangle.width,
                boundingRectangle.height,
                doorBody,
                screenInterface,
                doorId,
                gameAssets,
                gameContactListener,
                polygonName

        );

        screenInterface.addDoor(door);


    }
}
