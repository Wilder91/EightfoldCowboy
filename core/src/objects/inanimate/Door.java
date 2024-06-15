package objects.inanimate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import objects.GameAssets;
import objects.animals.object_helper.DoorManager;

import static helper.Constants.PPM;

public class Door extends InanimateEntity {
    private final int doorId;
    private GameAssets gameAssets;
    private GameScreen gameScreen;
    private boolean isContacted;
    private BitmapFont font;
    private float messageTimer;
    private int messageState;

    public Door(float width, float height, Body body, GameScreen gameScreen, int doorId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, doorId, gameAssets);
        this.doorId = doorId;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.isContacted = false;
        this.messageTimer = 0;
        this.messageState = 0;
        this.font = new BitmapFont();

        // Initialize the DoorManager and add this door
        DoorManager.addDoor(this);
    }

    @Override
    public void update(float delta) {
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;

        if (isContacted) {
            handleInteraction(delta, x, y);
        } else {
            gameScreen.hideInfoBox();
        }
    }

    private void handleInteraction(float delta, float x, float y) {
        messageTimer += delta;
        System.out.println(messageTimer);
        if (messageState == 0) {
            gameScreen.showInfoBox("Press E to Open Doors");
            if (messageTimer >= 1.5f) {
                messageTimer = 0;
                isContacted = false;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                messageState = 1;
                messageTimer = 0;
//                gameScreen.enterPauseScreen();
            }
        } else if (messageState == 1) {
            gameScreen.showInfoBox("Locked");
            if (messageTimer >= 1.5f) {
                messageState = 0;
                isContacted = false;
            }
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        // Render the door sprite here if you have one
    }

    public void playerContact() {
        isContacted = true;
        messageTimer = 0;  // Reset timer on contact
    }

    public void playerLeave() {
        isContacted = false;
        messageState = 0;  // Reset message state on leave
    }

    public int getId() {
        return doorId;
    }
}
