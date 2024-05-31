package objects.inanimate;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameScreen;

import static helper.Constants.PPM;

public class Boulder extends InanimateEntity {

    private final int boulderId;
    private Sprite sprite;
    private boolean isFacingRight;
    public Boulder(float width, float height, Body body, GameScreen gameScreen, int boulderId) {
        super(width, height, body, gameScreen, boulderId);
        this.speed = 10f;
        this.sprite = new Sprite(new Texture("boulder.png"));
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
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        sprite.setPosition(x - width / 2 , y - height /2);


    }


}
