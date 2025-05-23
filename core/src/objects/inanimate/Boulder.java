package objects.inanimate;



import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;


import static helper.Constants.PPM;

public class Boulder extends InanimateEntity{
    private GameAssets gameAssets;
    private final int boulderId;
    private Sprite sprite;
    private boolean isFacingRight;
    public Boulder(float width, float height, Body body, ScreenInterface screenInterface, int boulderId, GameAssets gameAssets, GameContactListener gameContactListener, float hp) {
        super(width, height, body, screenInterface, boulderId, gameAssets, gameContactListener, hp);
        this.speed = 10f;
        this.gameAssets = gameAssets;
        gameAssets.loadAssets();
        gameAssets.finishLoading();

        Texture boulderTexture = gameAssets.getTexture("boulder.png");
        this.sprite = new Sprite(boulderTexture);

        this.sprite.setSize(width, height);
        this.boulderId = boulderId;
        this.isFacingRight = false;

    }


    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;




    }



    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        sprite.setPosition(x - width / 2 , y - height /2);


    }



}
