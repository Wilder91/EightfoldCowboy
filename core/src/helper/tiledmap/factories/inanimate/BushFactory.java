package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import objects.inanimate.Bush;


import static helper.Constants.PPM;
import static helper.ContactType.TREE;


public class BushFactory extends InanimateEntityFactory {
    private static int bushCounter = 0;

    public BushFactory(ScreenInterface screen, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(screen, gameAssets, gameContactListener);
    }

    @Override
    public void createEntity(PolygonMapObject polygonMapObject, String polygonName) {

    }


    public void createBush(PolygonMapObject polygonMapObject, int bushType) {
        System.out.println("create bush 2");
        int bushId = ++bushCounter;
        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body bushBody = screenInterface.getWorld().createBody(bodyDef);




        Filter filter = new Filter();
        filter.categoryBits = TREE.getCategoryBits();
        filter.maskBits = TREE.getMaskBits();

        Bush bush = new Bush(
                bushBody,
                screenInterface,
                bushType,
                bushId,
                gameAssets,
                gameContactListener

        );

        screenInterface.addBush(bush);
    }
}
