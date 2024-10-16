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
    public static final int ROCK_LARGE_TOP = 4;
    public static final int ROCK_LARGE_BOTTOM= 5;

    private final int rockType;
    private float stateTime;
    private String rockTextureAtlasPath;
    private TextureRegion rockTexture;
    private GameAssets gameAssets;

    public Rock(Body body, ScreenInterface screenInterface, int rockType, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.rockType = rockType;
        this.gameAssets = gameAssets;
        this.rockTextureAtlasPath = "atlases/eightfold/rocks.atlas";
        initTexture();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
    }

    private void initTexture() {
        String regionName = getRegionNameForType(rockType);
        loadTextureFromAtlas(rockTextureAtlasPath, regionName);
    }

    private String getRegionNameForType(int rockType) {
        switch (rockType) {
            case ROCK_SMALL:
                return "Small_Rock";
            case ROCK_LARGE:
                return "Large_Rock";  // This should point to the full large rock
            case ROCK_LARGE_TOP:
                return "Large_Rock_Top";  // Correct name for the top half
            case ROCK_LARGE_BOTTOM:
                return "Large_Rock_Bottom";  // Correct name for the bottom half
            case ROCK_MEDIUM_ONE:
                return "Medium_Rock";  // Medium rock with index 1
            case ROCK_MEDIUM_TWO:
                return "Medium_Rock";  // Medium rock with index 2
            default:
                throw new IllegalArgumentException("Invalid rock type: " + rockType);
        }
    }


    private void loadTextureFromAtlas(String atlasPath, String regionName) {
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        if (atlas == null) {
            System.err.println("TextureAtlas " + atlasPath + " not found!");
        } else {
            // Log the loading process for debugging
            System.out.println("Loading texture from atlas: " + atlasPath + ", region: " + regionName + ", rockType: " + rockType);


            if (rockType == ROCK_MEDIUM_ONE) {
                rockTexture = atlas.findRegion(regionName, 1);
            } else if (rockType == ROCK_MEDIUM_TWO) {
                rockTexture = atlas.findRegion(regionName, 2);
            } else {
                rockTexture = atlas.findRegion(regionName);  // For Large_Rock_Top and Large_Rock_Bottom
            }

            if (rockTexture == null) {
                System.err.println("Region " + regionName + " not found in atlas " + atlasPath + "!");
            } else {
                // Set the width and height based on the texture size
                this.width = rockTexture.getRegionWidth();
                this.height = rockTexture.getRegionHeight();

                // Update the body shape to match the texture size
                updateBodyShape();
            }
            System.out.println("Attempting to load region: " + regionName);
            if (rockTexture == null) {
                System.err.println("Failed to load texture region: " + regionName);
            }
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
    public void render(SpriteBatch batch) {
        if (rockTexture != null) {
            // Convert the Box2D position from meters to pixels for rendering
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;
            batch.draw(rockTexture, x, y, width, height);
        } else {
            System.out.println(rockType);
            System.err.println("Rock texture not loaded, cannot render.");
        }
    }


}
