package objects.inanimate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.SaloonScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import objects.animals.object_helper.DoorManager;
import objects.inanimate.InanimateEntity;

import static helper.Constants.PPM;

public class Door extends InanimateEntity {
    private final int doorId;
    private GameAssets gameAssets;
    private ScreenInterface screen;
    private boolean isContacted;
    private boolean moveOn;
    private BitmapFont font;
    private float messageTimer;
    private int messageState;
    private String name;
    private SaloonScreen saloonScreen;

    public Door(float width, float height, Body body, ScreenInterface screen, int doorId, GameAssets gameAssets, GameContactListener gameContactListener, String doorName) {
        super(width, height, body, screen, doorId, gameAssets, gameContactListener);
        this.doorId = doorId;
        this.gameAssets = gameAssets;
        this.screen = screen;
        this.isContacted = false;
        this.messageTimer = 0;
        this.messageState = 0;
        this.moveOn = false;
        this.name = doorName != null ? doorName.trim() : null; // Trim the name to remove any leading/trailing spaces
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
            screen.hideInfoBox();
        }
    }

    private void handleInteraction(float delta, float x, float y) {
        messageTimer += delta;
        if (messageState == 0) {
            screen.showInfoBox("Press E to Open Doors");
            if (messageTimer >= 1.5f) {
                messageTimer = 0;
                isContacted = false;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                messageState = 1;
                messageTimer = 0;
                System.out.println("door name " + name);
                if (name != null) {
                    switch (name) {
                        case "enter_saloon":
                            if (screen instanceof GameScreen) {
                                GameScreen gameScreen = (GameScreen) screen;
                                System.out.println("Switching to Saloon...");
                                screen.setSaloonTime(!screen.isSaloonTime());
                            }
                            break;
                        case "leave_saloon":
                            System.out.println("leave!");
                            if (saloonScreen != null) {
                                System.out.println("Leaving Saloon...");
                                saloonScreen.setGameTime(!saloonScreen.isGameTime());
                            } else {
                                System.out.println("Saloon screen not initialized");
                            }
                            break;
                        default:
                            System.out.println("Unknown door name: " + name);
                            break;
                    }
                }
            }
        } else if (messageState == 1) {
            moveOn = true;
            screen.showInfoBox("Locked");

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

    public String getName() {
        return name;
    }

    public int getId() {
        return doorId;
    }
}
