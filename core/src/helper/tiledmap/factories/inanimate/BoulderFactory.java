package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.eightfold.GameScreen;
import objects.GameAssets;
import objects.inanimate.Boulder;

import static helper.Constants.PPM;

public class BoulderFactory {
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    private static int boulderCounter = 0;

    public BoulderFactory(GameScreen gameScreen, GameAssets gameAssets){
        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
    }
    public void createBoulder(PolygonMapObject polygonMapObject) {
        int boulderId = ++boulderCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body boulderBody = gameScreen.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                boundingRectangle.width / 2 / PPM,
                boundingRectangle.height / 2 / PPM
        );

        boulderBody.createFixture(shape, 0.0f);
        shape.dispose();

        Boulder boulder = new Boulder(
                boundingRectangle.width * 2,
                boundingRectangle.height * 2,
                boulderBody,
                gameScreen,
                boulderId,
                gameAssets
        );
        gameScreen.addBoulder(boulder);
    }

}
