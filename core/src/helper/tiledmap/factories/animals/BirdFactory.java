package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.IsometricGameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import objects.animals.bird.Bird;

public class BirdFactory {
    private IsometricGameScreen gameScreen;
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private static int birdCounter = 0;
    public BirdFactory(ScreenInterface screenInterface, GameAssets gameAssets){
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
    }

    public void createBird(PolygonMapObject polygonMapObject) {
        int birdId = ++birdCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 4,
                boundingRectangle.y + boundingRectangle.height / 4,
                boundingRectangle.width ,
                boundingRectangle.height ,
                false,
                screenInterface.getWorld(),
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
                screenInterface,
                birdId,
                gameAssets
        );

        screenInterface.addBird(bird);
    }
}
