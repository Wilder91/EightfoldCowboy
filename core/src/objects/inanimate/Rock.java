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
    public static final int ROCK_MEDIUM_ONE = 1;
    public static final int ROCK_MEDIUM_TWO = 2;
    public static final int ROCK_LARGE = 3;
    public static final int ROCK_SMALL_TWO = 4;
    public static final int CLIFF_ONE = 5;
    private static final int CLIFF_TWO = 6;


    private TextureRegion texture;
    private float stateTime;
    private String textureName;


    private GameAssets gameAssets;

    public Rock(Body body, ScreenInterface screenInterface, int id, String textureName,
                GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;

        this.texture = texture;
        this.textureName = textureName;

        this.gameAssets = gameAssets;
        setDepth(y);

        initTexture();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
        setDepthOffset(-height/9f);
    }

    private void updateBodyShape() {
        // Update the body shape to match the texture size
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 3 / PPM, height / 8 / PPM);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    private void initTexture(){
        String regionName = textureName;
        //System.out.println("rock regionname: " + regionName);
        String atlasPath = "atlases/eightfold/rocks.atlas";
        if (regionName.isEmpty()) {
            System.err.println("Unknown rockType: " + textureName);
            return;
        }

        // Get the texture from the atlas
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        if (atlas == null) {
            System.err.println("Rock atlas not found: " + atlasPath);
            return;
        }

        // Add debug output to check available regions


        // Find the region in the atlas
        texture = atlas.findRegion(textureName);

        if (texture == null) {
            System.err.println("Rock texture region not found: " + regionName + " for rock type: " + textureName);
            return;
        } else {
            //System.out.println("Successfully loaded texture for: " + regionName + " (type: " + bushType + ")");
        }

        // Set the width and height based on the texture size
        this.width = texture.getRegionWidth();
        this.height = texture.getRegionHeight();
        //System.out.println("Texture dimensions: " + width + "x" + height + " for bush type: " + bushType);

        // Update the body shape to match the texture size
        updateBodyShape();

    }

    private String getRockRegionName(int rockType) {
            switch (rockType) {
                case ROCK_SMALL:
                    return "Small_Rock";
                case ROCK_SMALL_TWO:
                    return "Small_Rock_Two";
                case ROCK_MEDIUM_ONE:
                    return "Medium_Rock_One";
                case ROCK_MEDIUM_TWO:
                    return "Medium_Rock_Two";
                case ROCK_LARGE:
                    return "Large_Rock";
                case CLIFF_ONE:
                    return "Cliff_One";
                case CLIFF_TWO:
                    return "Cliff_Two";
                default:
                    return "";
            }

    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            // Convert the Box2D position from meters to pixels for rendering
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;
            batch.draw(texture, x, y, width, height);
        } else {
            System.err.println("Rock texture not loaded, cannot render. Rock type: " + textureName);
        }
    }




}
