package objects.humans;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.screens.SaloonScreen;
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
    private ConversationManager conversationManager;
    private int conversationPhase;


    public NPC(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, int npcId) {
        super(width, height, body, screenInterface, gameAssets);

        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.id = npcId;
        this.screenInterface = screenInterface;
        this.conversationManager = new ConversationManager(1, this, screenInterface.getPlayer(), screenInterface);
        String characterName = getCharacterNameFromType(npcId);
        this.conversationPhase = 0;
        int[] frameCounts = getFrameCountsFromType(npcId);
        float stateTime = getStateTimeFromType(npcId);

        System.out.println("character name: " + npcId);
        NPCManager.addNPC(this);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "NPC", characterName, frameCounts, stateTime);
    }

    private int getStateTimeFromType(int npcType) {
        switch (npcType) {
            case 2:
                return 12;
            default:
                return 0;
        }

    }

    private int[] getFrameCountsFromType(int npcType) {
        switch (npcType) {
            case 1:
                return new int[]{23, 0, 0, 0, 0};
            case 2:
                return new int[]{24, 0, 0, 0, 0};
            default:
                return new int[]{0,0,0,0,0};
        }
    }

    private String getCharacterNameFromType(int npcType) {
        switch (npcType) {
            case 1:
                return "Jim";
            case 2:
                return "Martha";
            case 3:
                return "Miner";
            case 4:
                return "Cowboy";
            case 5:
                return "Blacksmith";
            default:
                return "NPC";
        }
    }


    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

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
        sprite.setPosition(x - width / 2, y - height / 2);

        if (isContacted && !inConversation) {
            //screenInterface.showInfoBox("Press E to begin Conversation");
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                inConversation = true;
                isContacted = false; // 👈 Prevent it from immediately retriggering
                screenInterface.hideInfoBox();
                conversationManager.startFirstLevelConversation();
            }
            }else if (inConversation) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                conversationManager.nextLine();
            }
        }

        sprite.draw(batch);
    }

    public Integer getId() {
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
