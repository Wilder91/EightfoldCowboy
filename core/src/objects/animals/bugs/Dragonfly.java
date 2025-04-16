package objects.animals.bugs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import objects.StationaryObjectAnimator;

import java.util.Map;

import static helper.Constants.PPM;

public class Dragonfly extends Bug {
    private int id;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private String dragonflyName;
    private StationaryObjectAnimator animator;


    public Dragonfly(float width, float height, float x, float y, Body body, int dragonflyId, String dragonflyName, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, x, y, body, screenInterface, gameAssets);
        this.id = dragonflyId;
        this.dragonflyName = dragonflyName;
        this.stateTime = 0f;
        this.animator = new StationaryObjectAnimator(this, "bugs", dragonflyName, gameAssets);

    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
        animator.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}