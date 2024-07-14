package objects.animals.bison;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.ConversationManager;
import conversations.firstLevel.FirstLevelConversations;
import helper.movement.BisonMovementHelper;
import helper.movement.SpriteMovementHelper;
import com.mygdx.eightfold.GameAssets;
import objects.animals.object_helper.BisonManager;
import com.mygdx.eightfold.player.GameEntity;
import helper.movement.Facing;

import java.util.Random;

import static helper.Constants.PPM;

public class Bison extends GameEntity {
    private SpriteMovementHelper movementHelper;
    private Facing facingDirection;
    private int id;
    private Sprite sprite;
    private GameAssets gameAssets;
    private float movementThreshold;
    private Random random;
    private boolean isPaused;
    public boolean isContacted = false;
    public boolean talkingBison;
    private BitmapFont font;
    private GameScreen gameScreen;
    private ScreenInterface screenInterface;
    private float restingTime;
    private float pauseDuration;
    private float contactTimer;
    private boolean inConversation = false;

    private FirstLevelConversations conversations;
    private ConversationManager conversationManager;
    private Sound contactSound;

    public Bison(float width, float height, float x, float y, Body body, Facing initialDirection,  ScreenInterface screenInterface, int bisonId, GameAssets gameAssets, Boolean talkingBison) {
        super(width, height, body, screenInterface, gameAssets);
        this.random = new Random();
        this.id = bisonId;
        this.talkingBison = talkingBison;
        this.facingDirection = Facing.LEFT;
        this.body = body;
        this.gameAssets = gameAssets;
        this.contactSound = gameAssets.getSound("sounds/bison-sound.mp3");
        this.conversations = new FirstLevelConversations(screenInterface, this, screenInterface.getPlayer(), "commodore64/skin/uiskin.json", "animals/bison/bison-single.png");
        this.conversationManager = new ConversationManager(1, this, screenInterface.getPlayer(), screenInterface);
        this.movementThreshold = 1f;
        this.restingTime = 0f;
        this.pauseDuration = 3f; // 3 seconds pause duration
        this.isPaused = true;
        this.contactTimer = 1;
        this.screenInterface = screenInterface;
        int[] eightfoldFrameCounts = {5, 7, 8, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 5};
        boolean startFlipped = talkingBison; // Set startFlipped to true if talkingBison is true, otherwise false
        boolean randomFlip = !talkingBison;
        this.movementHelper = new BisonMovementHelper(gameAssets, "bison", eightfoldFrameCounts, randomFlip, startFlipped);
        this.font = new BitmapFont();
        movementHelper.loadAnimations();// Load animations
        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(movementHelper.getCurrentAnimation().getKeyFrame(0));
        sprite.setAlpha(.3f);
        BisonManager.addBison(this);
        //this.bisonConversations = new FirstLevelBisonConversations(screenInterface, this, "commodore64/skin/uiskin.json", "animals/bison/bison-single.png");
    }

    @Override
    public void update(float delta) {
        // Update the state time
        // Update sprite position
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Set the origin of the sprite to its center
        sprite.setOriginCenter();

        // Use the helper to update the animation based on body velocity
        movementHelper.updateAnimation(body.getLinearVelocity(), delta);
        if (isContacted) {
            contactTimer += delta;

        }


        // The sprite's flip state should be managed within the helper
        sprite = movementHelper.getSprite();


    }


    public void setInConversation(boolean inConversation) {
        this.inConversation = inConversation;
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);

        if (isContacted) {
            if (!talkingBison) {
                font.setColor(Color.WHITE);
                font.draw(batch, "mooo", body.getPosition().x * PPM, body.getPosition().y * PPM + 70);
                if (contactTimer >= 1.5) {
                    contactTimer = 0;
                    //isContacted = false;
                }
            } else if (talkingBison) {
                if(!inConversation) {
                    screenInterface.showInfoBox("Press E to begin Conversation");

                    if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                        inConversation = true;
                        screenInterface.hideInfoBox();

                        //bisonConversations.startBisonConversations(this);
                        conversationManager.startConversation();
                    }
                    if (contactTimer >= 1.5) {
                        contactTimer = 0;
                        //screenInterface.hideInfoBox();
                        //isContacted = false;
                        //isContacted = false;

                    }
                }

            }
        }
        if(inConversation) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                conversations.showNextBisonLine();

            }
        }

    }

    public void playContactSound(){
        contactSound.play(.05f);
    }


    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void endPlayerContact(){
        isContacted = false;
    }

    public void playerContact(Body body, Vector2 linearVelocity) {

        body.setLinearDamping(1.5f);
        body.setLinearVelocity(linearVelocity);
        System.out.println(talkingBison);

    }

}