package helper.tiledmap.factories.inanimate;


import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyUserData;
import objects.GameAssets;
import objects.inanimate.Building;

import static helper.Constants.PPM;
import static helper.ContactType.TREE;

public class BuildingFactory {
    private static int buildingCounter = 0;
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    public BuildingFactory(GameScreen gameScreen, GameAssets gameAssets){

        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
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
                boundingRectangle.width  / PPM,
                boundingRectangle.height  / PPM
        );

        Fixture buildingFixture = buildingBody.createFixture(shape, 0.0f);
        buildingFixture.setUserData(new BodyUserData(buildingId, TREE, buildingBody));
        shape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = TREE.getCategoryBits();
        filter.maskBits = TREE.getMaskBits();
        buildingFixture.setFilterData(filter);

        Building building = new Building(
                boundingRectangle.width /PPM ,
                boundingRectangle.height  / PPM ,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                buildingBody,
                true,
                gameScreen,
                buildingId,
                gameAssets
        );

        gameScreen.addBuilding(building);
    }
}
