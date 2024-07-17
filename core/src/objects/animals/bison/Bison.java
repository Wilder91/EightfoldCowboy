package objects.animals.bison;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.ConversationManager;
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
    private ConversationManager conversationManager;
    private Sound contactSound;

    public Bison(float width, float height, float x, float y, Body body, Facing initialDirection, ScreenInterface screenInterface, int bisonId, GameAssets gameAssets, Boolean talkingBison) {
        super(width, height, body, screenInterface, gameAssets);
        this.random = new Random();
        this.id = bisonId;
        this.talkingBison = talkingBison;
        this.facingDirection = Facing.LEFT;
        this.body = body;
        this.gameAssets = gameAssets;
        this.contactSound = gameAssets.getSound("sounds/bison-sound.mp3");
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
        movementHelper.loadAnimations(); // Load animations
        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(movementHelper.getCurrentAnimation().getKeyFrame(0));
        sprite.setAlpha(.3f);
        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        sprite.setOriginCenter();
        movementHelper.updateAnimation(body.getLinearVelocity(), delta);

        if (isContacted) {
            contactTimer += delta;
        }

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
                if (contactTimer >= 1.5) {
                    contactTimer = 0;
                }
            } else if (talkingBison) {
                if (!inConversation) {
                    screenInterface.showInfoBox("Press E to begin Conversation");
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        inConversation = true;
                        screenInterface.hideInfoBox();
                        conversationManager.startFirstLevelConversation();
                    }
                }
            }
        }

        if (inConversation) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                conversationManager.nextLine();
            }
        }
    }

    public void playContactSound() {
        contactSound.play(.05f);
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void endPlayerContact() {
        isContacted = false;
    }

    public void playerContact(Body body, Vector2 linearVelocity) {
        body.setLinearDamping(1.5f);
        body.setLinearVelocity(linearVelocity);
    }
}
