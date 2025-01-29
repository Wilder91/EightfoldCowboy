package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Rock extends InanimateEntity {
    // Define constants for rock types
    public static final int ROCK_SMALL = 0;
    public static final int ROCK_LARGE = 1;
    public static final int ROCK_MEDIUM_ONE = 2;
    public static final int ROCK_MEDIUM_TWO = 3;

    private final int rockType;
    private float stateTime;
    private String rockTextureAtlasPath;
    private TextureRegion rockTexture;
    private TextureRegion topTexture;
    private TextureRegion bottomTexture;

    private GameAssets gameAssets;

    public Rock(Body body, ScreenInterface screenInterface, int rockType, int id,
                TextureRegion topTexture, TextureRegion bottomTexture,
                GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.rockType = rockType;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.gameAssets = gameAssets;

        // Calculate the width and height based on texture regions
        this.width = bottomTexture.getRegionWidth();
        this.height = topTexture.getRegionHeight() + bottomTexture.getRegionHeight();

        // Update the body shape to match the combined height
        updateBodyShape();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {

    }


    private void updateBodyShape() {
        // Update the body shape to match the texture size
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 6 / PPM, height / 10 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void renderBottom(SpriteBatch batch) {
        if (bottomTexture != null) {
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;

            // Render the bottom texture
            batch.draw(bottomTexture, x, y, width, bottomTexture.getRegionHeight());
        }
    }
    public void renderTop(SpriteBatch batch) {
        if (topTexture != null) {
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2 + bottomTexture.getRegionHeight();

            // Render the top texture
            batch.draw(topTexture, x, y, width, topTexture.getRegionHeight());
        }
    }

}
