package helper.tiledmap.factories.animals;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import objects.humans.NPC;
import objects.humans.NPCManager;

import static helper.Constants.PPM;
import static helper.ContactType.NPC;

public class NpcFactory {
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private static int npcCounter = 0;
    private NPCManager npcManager;
    private int npcId;

    public NpcFactory(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener, int npcId) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
        this.npcId = npcId;

    }

    public void createNPC(RectangleMapObject rectangleMapObject, String name) {

        Rectangle rectangle = rectangleMapObject.getRectangle();
        float centerX = (rectangle.x + rectangle.width / 2) / PPM;
        float centerY = (rectangle.y + rectangle.height / 2) / PPM;
        float bodyWidth = rectangle.width / PPM;
        float bodyHeight = rectangle.height / PPM;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(centerX, centerY);
        this.npcManager = npcManager;
        Body npcBody = screenInterface.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bodyWidth / 3, bodyHeight / 3);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;

        npcBody.createFixture(fixtureDef).setUserData(new BodyUserData(npcId, NPC, npcBody));

        shape.dispose();

        NPC npc = new NPC(rectangle.width, rectangle.height, npcBody, screenInterface, gameAssets, npcId, name);
        //System.out.println("NAME: " + npcId + name);
        screenInterface.addNPC(npc);
    }
}