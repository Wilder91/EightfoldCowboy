package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import objects.animals.bison.Bison;
import helper.movement.Facing;

public class BisonFactory {
    private final Boolean talkingBison;
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private static int bisonCounter = -1;
    private Boolean isStatic;
    public BisonFactory(ScreenInterface screenInterface, GameAssets gameAssets, Boolean talkingBison) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.talkingBison = talkingBison;
    }

    public void createBison(PolygonMapObject polygonMapObject) {
        System.out.println("creating bison: " + bisonCounter);
        int bisonId = ++bisonCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();
        if(talkingBison){
            isStatic = true;
        }else {
            isStatic = false;
        }
        Body body = BodyHelperService.createBody(
                boundingRectangle.x + boundingRectangle.width /2 ,
                boundingRectangle.y + boundingRectangle.height / 2,
                boundingRectangle.width * 1.2f,
                boundingRectangle.height,
                isStatic,
                screenInterface.getWorld(),
                ContactType.BISON,
                bisonId
        );

        Bison bison = new Bison(
                boundingRectangle.width,
                boundingRectangle.height,
                boundingRectangle.x + boundingRectangle.width / 2,
                boundingRectangle.y + boundingRectangle.height / 2,
                body,
                Facing.LEFT ,
                screenInterface,
                bisonId,
                gameAssets,
                talkingBison
        );

        screenInterface.addBison(bison);
    }
}