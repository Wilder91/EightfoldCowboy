package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyUserData;
import objects.GameAssets;
import objects.inanimate.Tree;

import static helper.Constants.PPM;
import static helper.ContactType.TREE;

public class TreeFactory {
    private GameScreen gameScreen;
    private static int treeCounter = 0;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;

    public TreeFactory(GameScreen gameScreen, GameAssets gameAssets, GameContactListener gameContactListener){
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.gameContactListener = gameContactListener;
    }

    public void createTree(PolygonMapObject polygonMapObject, int treeType) {
        int treeId = ++treeCounter;

        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                (boundingRectangle.y + boundingRectangle.height / 2) / PPM
        );

        Body treeBody = gameScreen.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        shape.set(worldVertices);
        shape.setAsBox(boundingRectangle.width / 2/  PPM, boundingRectangle.height /2 / PPM);
        Fixture treeFixture = treeBody.createFixture(shape, 0.0f);
        treeFixture.setUserData(new BodyUserData(treeId, TREE, treeBody));


        Filter filter = new Filter();
        filter.categoryBits = TREE.getCategoryBits();
        filter.maskBits = TREE.getMaskBits();
        treeFixture.setFilterData(filter);

        shape.dispose();

        Tree tree = new Tree(
                boundingRectangle.width,
                boundingRectangle.height,
                treeBody,
                gameScreen,
                treeType,
                treeId,
                gameAssets,
                gameContactListener
        );

        gameScreen.addTree(tree);
    }
}
