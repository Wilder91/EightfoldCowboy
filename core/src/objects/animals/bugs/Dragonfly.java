package objects.animals.bugs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import java.util.Map;

import static helper.Constants.PPM;

public class Dragonfly extends Bug {
    private int id;
    private int bugType;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private String dragonflyName;



    public Dragonfly(float width, float height, float x, float y, Body body, int dragonflyId, String dragonflyName, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, x, y, body, screenInterface, gameAssets);
        this.id = dragonflyId;
        this.dragonflyName = dragonflyName;
        this.stateTime = 0f;

        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/bugs.atlas");
        Array<TextureRegion> frames = new Array<>();


       // System.out.println("animation prefix: " + animationPrefix);

        for (int i = 1; i <= 9; i++) {
            TextureRegion region = atlas.findRegion(dragonflyName, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + dragonflyName + " " + i);
            }
        }

        animation = new Animation<>(.2f, frames, Animation.PlayMode.LOOP);
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

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}