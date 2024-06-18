package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import objects.animals.bison.Bison;
import helper.movement.Facing;

public class BisonFactory {
    private final Boolean talkingBison;
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    private static int bisonCounter = -1;

    public BisonFactory(GameScreen gameScreen, GameAssets gameAssets, Boolean talkingBison) {
        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
        this.talkingBison = talkingBison;
    }

    public void createBison(PolygonMapObject polygonMapObject) {
        int bisonId = ++bisonCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                boundingRectangle.width,
                boundingRectangle.height,
                false,
                gameScreen.getWorld(),
                ContactType.BISON,
                bisonId
        );

        Bison bison = new Bison(
                boundingRectangle.width,
                boundingRectangle.height,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                body,
                Facing.DOWN_LEFT,
                gameScreen,
                bisonId,
                gameAssets,
                talkingBison
        );

        gameScreen.addBison(bison);
    }
}