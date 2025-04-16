package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import com.mygdx.eightfold.GameAssets;
import objects.inanimate.Tree;

import static helper.Constants.PPM;
import static helper.ContactType.TREE;

public class TreeFactory extends InanimateEntityFactory{
    private ScreenInterface screenInterface;
    private static int treeCounter = 0;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private String atlasLink = "atlases/eightfold/trees.atlas";

    public TreeFactory(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener){
        super(screenInterface, gameAssets, gameContactListener);
        this.gameAssets = gameAssets;
        this.screenInterface = screenInterface;

        this.gameContactListener = gameContactListener;
    }

    public void createTree(PolygonMapObject polygonMapObject, String textureName) {
        int treeId = ++treeCounter;

        // Define the base name based on tree type
        String treeName = textureName;


        Polygon polygon = polygonMapObject.getPolygon();
        Rectangle boundingRectangle = polygon.getBoundingRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (boundingRectangle.x + boundingRectangle.getWidth() / 2) / PPM ,
                (boundingRectangle.y + boundingRectangle.getHeight() / 2 ) / PPM
        );

        Body treeBody = screenInterface.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        shape.set(worldVertices);
        shape.setAsBox(boundingRectangle.width / 4/  PPM, boundingRectangle.height / 2 / PPM);
        Fixture treeFixture = treeBody.createFixture(shape, 0.0f);
//        if(textureName.equals("aspen_stump")){
//            treeFixture.setUserData(new BodyUserData(treeId, ENEMY, treeBody));
//        } else {
            treeFixture.setUserData(new BodyUserData(treeId, TREE, treeBody));
 //       }


// Then set up the filter
        Filter filter = new Filter();
//        if(textureName.equals("aspen_stump")){  // Use .equals() for string comparison
//            filter.categoryBits = ENEMY.getCategoryBits();
//            filter.maskBits = ENEMY.getMaskBits();
//        } else {
            filter.categoryBits = TREE.getCategoryBits();
            filter.maskBits = TREE.getMaskBits();
        //}
        treeFixture.setFilterData(filter);

        shape.dispose();

        Tree tree = new Tree(
                treeBody,
                screenInterface,
                treeId,
                "trees",  // Pass the atlas path instead of the texture
                textureName, // Pass the texture name
                gameAssets,
                gameContactListener
        );

        screenInterface.addTree(tree);
    }


    @Override
    public void createEntity(PolygonMapObject polygonMapObject, String polygonName) {

    }
}