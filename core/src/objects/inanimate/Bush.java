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

public class Bush extends InanimateEntity {
    // Define constants for bush types
    public static final int BUSH_1 = 0;
    public static final int BUSH_2 = 1;
    public static final int BUSH_3 = 2;
    public static final int BUSH_4 = 3;
    public static final int BUSH_5 = 4;


    private final int bushType;
    private float stateTime;
    private String atlasPath;
    private TextureRegion bushTexture;
    private GameAssets gameAssets;

    public Bush(Body body, ScreenInterface screenInterface, int bushType, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.bushType = bushType;
        this.gameAssets = gameAssets;
        this.atlasPath = "atlases/eightfold/bushes.atlas";
        initTexture();
    }

    private void initTexture() {
        // Get bush texture region name based on bush type
        String regionName = getBushRegionName(bushType);

        if (regionName.isEmpty()) {
            System.err.println("Unknown bushType: " + bushType);
            return;
        }

        // Get the texture from the atlas
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        if (atlas == null) {
            System.err.println("Bush atlas not found: " + atlasPath);
            return;
        }

        // Add debug output to check available regions


        // Find the region in the atlas
        bushTexture = atlas.findRegion(regionName);

        if (bushTexture == null) {
            System.err.println("Bush texture region not found: " + regionName + " for bush type: " + bushType);
            return;
        } else {
            //System.out.println("Successfully loaded texture for: " + regionName + " (type: " + bushType + ")");
        }

        // Set the width and height based on the texture size
        this.width = bushTexture.getRegionWidth();
        this.height = bushTexture.getRegionHeight();
        //System.out.println("Texture dimensions: " + width + "x" + height + " for bush type: " + bushType);

        // Update the body shape to match the texture size
        updateBodyShape();
    }

    /**
     * Returns the region name for a bush type to be used in texture lookups
     */
    private String getBushRegionName(int bushType) {
        switch (bushType) {
            case BUSH_1:
                return "Bush-1";
            case BUSH_2:
                return "Bush-2";
            case BUSH_3:
                return "Bush-3";
            case BUSH_4:
                return "Bush-4";
            case BUSH_5:
                return "Bush-5";
            default:
                return "";
        }
    }

    private void updateBodyShape() {
        // Assuming the body has been created, we update its shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 6 / PPM, height / 8 / PPM);

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
            System.err.println("Bush texture not loaded, cannot render. Bush type: " + bushType);
        }
    }
}