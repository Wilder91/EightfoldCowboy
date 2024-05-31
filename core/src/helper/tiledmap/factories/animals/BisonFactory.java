package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameScreen;
import helper.BodyHelperService;
import helper.ContactType;
import objects.animals.bison.Bison;

public class BisonFactory {
    private GameScreen gameScreen;
    private static int bisonCounter = -1;

    public BisonFactory(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
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
                boundingRectangle.width * 7 / 4,
                boundingRectangle.height * 7 / 4,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                body,
                true,
                gameScreen,
                bisonId
        );

        gameScreen.addBison(bison);
    }
}