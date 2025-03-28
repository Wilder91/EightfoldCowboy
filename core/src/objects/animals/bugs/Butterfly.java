package objects.animals.bugs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Butterfly extends Bug {
    private int id;
    private int butterflyType;
    private Animation<TextureRegion> animation;
    private float stateTime;

    private static final String[] BUTTERFLY_NAMES = {
            "Butterfly_Small_White",
            "Butterfly_Yellow",
            "Butterfly_Blue",
            "Butterfly_Orange"
    };

    public Butterfly(float width, float height, float x, float y, Body body, int butterflyId, int butterflyType, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, x, y, body, screenInterface, gameAssets);
        this.id = butterflyId;
        this.butterflyType = butterflyType;
        this.stateTime = 0f;

        // Load animation from shared bugs atlas
        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/bugs.atlas");
        Array<TextureRegion> frames = new Array<>();

        String animationPrefix = BUTTERFLY_NAMES[butterflyType];

        for (int i = 1; i <= 4; i++) {
            TextureRegion region = atlas.findRegion(animationPrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + animationPrefix + " " + i);
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
    public void playerContact(){

    }
    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}