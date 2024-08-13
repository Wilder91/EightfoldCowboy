package objects.inanimate;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Bush extends InanimateEntity {
    // Define constants for bush types
    public static final int BUSH_1 = 0;
    public static final int BUSH_2 = 1;
    public static final int BUSH_3 = 2;
    public static final int BUSH_4 = 3;
    public static final int BUSH_5 = 4;
    public static final int BUSH_6 = 5;

    private final int bushType;
    private float stateTime;
    private String bushTexturePath;
    private TextureRegion bushTexture;
    private GameAssets gameAssets;

    public Bush(Body body, ScreenInterface screenInterface, int bushType, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.bushType = bushType;
        this.gameAssets = gameAssets;
        this.bushTexturePath = "plants/bushes/Bush_";
        initTexture();
    }

    private void initTexture() {
        Texture texture = gameAssets.getTexture(bushTexturePath + bushType + ".png");
        if (texture == null) {
            System.out.println("Texture " + bushTexturePath + bushType + ".png" + " not found!");
        } else {
            bushTexture = new TextureRegion(texture);

            // Set the width and height based on the texture size
            this.width = bushTexture.getRegionWidth();
            this.height = bushTexture.getRegionHeight();

            // Update the body shape to match the texture size
            updateBodyShape();
        }
    }

    private void updateBodyShape() {
        // Assuming the body has been created, we update its shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 4 / PPM, height / 4 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (bushTexture != null) {
            // Convert the Box2D position from meters to pixels for rendering
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;
            batch.draw(bushTexture, x, y, width, height);
        } else {
            System.err.println("Bush texture not loaded, cannot render.");
        }
    }
}
