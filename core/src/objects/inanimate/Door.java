package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import objects.GameAssets;
import objects.animals.object_helper.DoorManager;

public class Door extends InanimateEntity {
    private final int doorId;
    private Sprite sprite;
    private GameAssets gameAssets;
    private boolean isFacingRight;
    private DoorManager doorManager;
    private GameScreen gameScreen;
    private boolean isContacted;
    private BitmapFont font;
    private int messageTimer;

    public Door(float width, float height, Body body, GameScreen gameScreen, int doorId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, doorId, gameAssets);
        this.doorId = doorId;
        this.gameAssets = gameAssets;
        this.doorManager = new DoorManager();
        this.gameScreen = gameScreen;
        this.isContacted = false;
        this.messageTimer = 0;
        this.font = new BitmapFont();

        gameAssets.loadAssets();
        gameAssets.finishLoading();

        // Example texture
//        Texture doorTexture = gameAssets.getTexture("door.png"); // Make sure the texture name matches your assets
//        this.sprite = new Sprite(doorTexture);
//        this.sprite.setSize(width, height);
        this.isFacingRight = false;

        doorManager.addDoor(this);
    }

    @Override
    public void update(float delta) {

        float x = body.getPosition().x ;
        float y = body.getPosition().y ;
        //sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        if (messageTimer >= 1.5) {

            messageTimer = 0;
            isContacted = false;

        }
//        if (isContacted) {
//            gameScreen.showDoorBox("DOOR", 0, 100);
//        } else {
//            gameScreen.hideDoorBox();
//        }
        if (isContacted) {
            messageTimer += delta;
            gameScreen.showTextBox("Press E to Open Doors", 0, y + 70);


        }else if(!isContacted){
            gameScreen.hideTextBox();
        }
    }



    @Override
    public void render(SpriteBatch batch) {
        //System.out.println(messageTimer);



    }

    public void playerContact() {
        System.out.println(isContacted);
        isContacted = true;

    }

    public void playerLeave() {
        isContacted = false;
    }

    public int getId() {
        return doorId;
    }
}
