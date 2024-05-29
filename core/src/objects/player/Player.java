package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.waldergames.eightfoldtwo.GameScreen;

import static helper.Constants.PPM;

public class Player extends GameEntity {

    private Sprite sprite;
    private boolean isFacingRight;
    public Player(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        this.speed = 10f;
        this.sprite = new Sprite(new Texture("kath.gif"));
        this.sprite.setSize(width, height);

        this.isFacingRight = false;
        this.body = body;

    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();


    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        sprite.setPosition(x - width / 2 , y - height /2);


    }



    private void checkUserInput() {
        velX = 0;
        velY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            velX = 1;
            if(isFacingRight == false) {
                isFacingRight = true;
                sprite.flip(true, false);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            velX = -1;
            if(isFacingRight == true) {
                isFacingRight = false;
                sprite.flip(true, false);
            }


        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            velY = 1;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            velY = -1;

        }
        body.setLinearVelocity(velX * speed, velY * speed);


    }

    public void setBody(Body body) {
        this.body = body;
    }
}
