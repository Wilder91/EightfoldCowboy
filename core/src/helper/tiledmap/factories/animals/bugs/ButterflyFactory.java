package helper.tiledmap.factories.animals.bugs;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import objects.animals.bugs.Butterfly;

public class ButterflyFactory {
    private final ScreenInterface screenInterface;
    private final GameAssets gameAssets;
    private static int butterflyCounter = 0;

    public ButterflyFactory(ScreenInterface screenInterface, GameAssets gameAssets) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
    }

    public void createButterfly(PolygonMapObject polygonMapObject, int butterflyType) {
        int butterflyId = ++butterflyCounter;

        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle bounds = polygon.getBoundingRectangle();

        float centerX = bounds.x + bounds.width / 2;
        float centerY = bounds.y + bounds.height / 2;

        Body body = BodyHelperService.createBody(
                centerX,
                centerY,
                bounds.width,
                bounds.height,
                false, // butterflies are dynamic
                screenInterface.getWorld(),
                ContactType.BISON,
                butterflyId
        );

        Butterfly butterfly = new Butterfly(
                bounds.width,
                bounds.height,
                centerX,
                centerY,
                body,
                butterflyId,
                butterflyType,
                screenInterface,
                gameAssets
        );

        screenInterface.addButterfly(butterfly);
    }
}