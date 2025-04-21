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
import objects.StationaryObjectAnimator;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Bug extends GameEntity {
    protected int id;
    protected int type;
    protected Animation<TextureRegion> animation;
    private StationaryObjectAnimator animator;
    protected float stateTime;
    private String bugName;
    private GameAssets gameAssets;

    public Bug(float width, float height, float x, float y, Body body, String bugName, ScreenInterface screenInterface, GameAssets gameAssets, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        this.stateTime = 0f;
        this.bugName = bugName;
        this.gameAssets = gameAssets;

        // Use the StationaryObjectAnimator instead of creating our own animation
        this.animator = new StationaryObjectAnimator(this, "bugs", bugName, gameAssets);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update the animator
        animator.update(delta);
    }

    public GameAssets getGameAssets() {
        return this.gameAssets;
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void playerContact(){
        // Your contact handling code here
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }




}