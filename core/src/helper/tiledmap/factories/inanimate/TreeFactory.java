package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.eightfold.GameScreen;
import objects.inanimate.Tree;

import static helper.Constants.PPM;

public class TreeFactory {
    private GameScreen gameScreen;
    private static int treeCounter = 0;
    public TreeFactory(GameScreen gameScreen){
        this.gameScreen = gameScreen;
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

        treeBody.createFixture(shape, 0.0f);
        shape.dispose();

        Tree tree = new Tree(
                boundingRectangle.width * 2,
                boundingRectangle.height * 2,
                treeBody,
                gameScreen,
                treeType,
                treeId
        );

        gameScreen.addTree(tree);
    }
}
