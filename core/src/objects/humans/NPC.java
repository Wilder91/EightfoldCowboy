package objects.humans;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.ConversationManager;
import helper.movement.SpriteIdleHelper;

import static helper.Constants.PPM;

public class NPC extends GameEntity {
    private Sprite sprite;
    private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean isFacingRight = true;
    private int id;
    private int[] frameCounts;
    private float stateTime;
    private ScreenInterface screenInterface;
    private boolean isContacted = false;
    private boolean inConversation = false;
    private String name;
    private String characterName;
    private ConversationManager conversationManager;
    private int conversationPhase;


    public NPC(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, int npcId, String name) {
        super(width, height, body, screenInterface, gameAssets);

        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.id = npcId;
        this.name = name;
        this.screenInterface = screenInterface;
        this.conversationManager = new ConversationManager(1, this, screenInterface.getPlayer(), screenInterface);
        this.characterName = getCharacterNameFromName(name);

        // REMOVE this line - no longer needed
        // this.conversationPhase = 0;

        int[] frameCounts = getFrameCountsFromName(characterName);
        float stateTime = getStateTimeFromName(characterName);

        //System.out.println("character name: " + npcId);
        NPCManager.addNPC(this);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "NPC", characterName, frameCounts, stateTime);
    }

    private int getStateTimeFromName(String characterName) {
        switch (characterName) {
            case "Jim":
                return 12;
            default:
                return 0;
        }

    }

    private int[] getFrameCountsFromName(String characterName) {
        switch (characterName) {
            case "Jim":
                return new int[]{23, 0, 0, 0, 0};
            case "Martha":
                return new int[]{24, 0, 0, 0, 0};
            default:
                return new int[]{0,0,0,0,0};
        }
    }

    private String getCharacterNameFromName(String characterName) {
        switch (characterName) {
            case "Jim":
                return "Jim";
            case "Martha":
                return "Martha";
            default:
                return "NPC";
        }
    }


    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();
        idleHelper.setDirection(lastDirection);
        idleHelper.setFacingRight(isFacingRight);
        idleHelper.update(delta);
        sprite = idleHelper.getSprite();


    }

    public void setInConversation(boolean inConversation) {
        this.inConversation = inConversation;
    }

    @Override
    public void render(SpriteBatch batch) {
        // In NPC.render():
        if (inConversation) {
            // Check for key press only once
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                System.out.println("Key pressed - advancing conversation");
                conversationManager.nextLine();
            }
        } else if (isContacted) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                inConversation = true;
                isContacted = false;
                screenInterface.hideInfoBox();
                conversationManager.startFirstLevelConversation();
            }
        }

        sprite.draw(batch);
        sprite.setPosition(x - width / 2, y - height / 2);
    }

    public int getId() {
        return id;
    }

    public void playerContact(NPC npc) {
        //System.out.println("threaded the needle");
        isContacted = true;
    }

    public void endPlayerContact() {
    }

    public int getConversationPhase() {
        return conversationPhase;
    }

    public void advanceConversationPhase() {
        conversationPhase++;
    }
}
