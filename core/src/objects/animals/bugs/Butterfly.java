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

public class Butterfly extends GameEntity {
    private int id;
    private int butterflyType;
    private Animation<TextureRegion> animation;
    private float stateTime;


    public Butterfly(float width, float height, float x, float y, Body body, int butterflyId, int butterflyType, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        this.id = butterflyId;
        this.butterflyType = butterflyType;
        this.stateTime = 0f;

        // Load animation from atlas
        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/butterfly.atlas");
        Array<TextureRegion> frames = new Array<>();

        for (int i = 1; i <= 4; i++) {
            TextureRegion region = atlas.findRegion("Butterfly_" + "Small_White", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: Butterfly_" +  "Small_White_" + i);
            }
        }

        animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
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

    public void endPlayerContact() {
        // Optional interaction logic for butterflies
    }

    public void playerContact(Body body, Vector2 linearVelocity) {
        body.setLinearDamping(0.5f);
        body.setLinearVelocity(linearVelocity);
    }
}
