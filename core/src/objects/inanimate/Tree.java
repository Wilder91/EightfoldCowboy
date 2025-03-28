package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Tree extends InanimateEntity {
    public static final int LARGE_OAK = 0;
    public static final int MEDIUM_1 = 1;
    public static final int MEDIUM_2 = 2;
    public static final int SMALL = 3;
    public static final int JUVENILE = 4;
    public static final int SEEDLING = 5;
    public static final int ASPEN = 6;

    private final TextureRegion topTexture;
    private final TextureRegion bottomTexture;

    private final int treeType;
    private final GameAssets gameAssets;

    public Tree(Body body, ScreenInterface screenInterface, int treeType, int id,
                TextureRegion topTexture, TextureRegion bottomTexture,
                GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.treeType = treeType;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.gameAssets = gameAssets;

        // Use texture size for width/height
        this.width = bottomTexture.getRegionWidth();
        this.height = topTexture.getRegionHeight() + bottomTexture.getRegionHeight();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
    }

    @Override
    public void render(SpriteBatch batch) {
        renderBottom(batch);
        renderTop(batch);
    }

    public void renderBottom(SpriteBatch batch) {
        if (bottomTexture != null) {
            float drawX = body.getPosition().x * PPM - width / 2;
            float drawY = body.getPosition().y * PPM - height / 2;
            batch.draw(bottomTexture, drawX, drawY, width, bottomTexture.getRegionHeight());
        }
    }

    public void renderTop(SpriteBatch batch) {
        if (topTexture != null) {
            float drawX = body.getPosition().x * PPM - width / 2;
            float drawY = body.getPosition().y * PPM - height / 2 + bottomTexture.getRegionHeight();
            batch.draw(topTexture, drawX, drawY, width, topTexture.getRegionHeight());
        }
    }
}
