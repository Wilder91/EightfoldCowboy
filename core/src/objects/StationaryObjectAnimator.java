package objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import helper.EntityAnimator;

public class StationaryObjectAnimator extends EntityAnimator {
    private Animation<TextureRegion> animation;
    private String objectName;
    private float stateTime = 0f;
    private GameEntity entity;

    public StationaryObjectAnimator(GameEntity stationaryObject, String atlasName, String objectName, GameAssets gameAssets) {
        super(stationaryObject);
        this.entity = stationaryObject;

        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/bugs.atlas");
        Array<TextureRegion> frames = new Array<>();
        this.objectName = objectName;

        for (int i = 1; i <= 4; i++) {
            TextureRegion region = atlas.findRegion(objectName, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + objectName + " " + i);
            }
        }

        this.animation = new Animation<>(.2f, frames, Animation.PlayMode.LOOP);
    }


    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        return animation.getKeyFrame(stateTime, true);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        // Assuming your GameEntity has position and dimension properties
        float x = entity.getX();
        float y = entity.getY();
        float width = entity.getWidth();
        float height = entity.getHeight();

        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }
}