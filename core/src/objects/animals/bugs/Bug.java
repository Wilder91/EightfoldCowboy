package objects.animals.bugs;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Bug extends GameEntity {
    protected int id;
    protected int type;
    protected Animation<TextureRegion> animation;
    protected float stateTime;

    public Bug(float width, float height, float x, float y, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        this.stateTime = 0f;


    }




    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;

    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    public void setBody(Body body) {
        this.body = body;
    }
}