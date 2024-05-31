package helper.tiledmap.factories.inanimate;


import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.eightfold.GameScreen;
import objects.inanimate.Building;

import static helper.Constants.PPM;

public class BuildingFactory {
    private static int buildingCounter = 0;
    private GameScreen gameScreen;
    public BuildingFactory(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    public void createBuilding(PolygonMapObject polygonMapObject) {
        int buildingId = ++buildingCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body buildingBody = gameScreen.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                boundingRectangle.width / 2 / PPM,
                boundingRectangle.height / 2 / PPM
        );

        buildingBody.createFixture(shape, 0.0f);
        shape.dispose();

        Building building = new Building(
                boundingRectangle.width / 2,
                boundingRectangle.height / 2,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                buildingBody,
                true,
                gameScreen,
                buildingId
        );

        gameScreen.addBuilding(building);
    }
}
