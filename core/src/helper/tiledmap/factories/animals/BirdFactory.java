package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyHelperService;
import helper.ContactType;
import objects.GameAssets;
import objects.animals.bird.Bird;

public class BirdFactory {
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    private static int birdCounter = 0;
    public BirdFactory(GameScreen gameScreen, GameAssets gameAssets){
        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
    }

    public void createBird(PolygonMapObject polygonMapObject) {
        int birdId = ++birdCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                boundingRectangle.width / 2,
                boundingRectangle.height / 2,
                false,
                gameScreen.getWorld(),
                ContactType.BIRD,
                birdId
        );

        Bird bird = new Bird(
                boundingRectangle.width / 2,
                boundingRectangle.height / 2,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                body,
                true,
                gameScreen,
                birdId,
                gameAssets
        );

        gameScreen.addBird(bird);
    }
}
