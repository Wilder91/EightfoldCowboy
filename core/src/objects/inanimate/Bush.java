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


    private float stateTime;
    private String atlasPath;
    private TextureRegion bushTexture;
    private GameAssets gameAssets;
    private float depth;
    private String bushName;

    public Bush(Body body, ScreenInterface screenInterface, String bushName, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.bushName = bushName;
        this.gameAssets = gameAssets;
        this.atlasPath = "atlases/eightfold/bushes.atlas";
        initTexture();
        //setDepth(y);
    }

    private void initTexture() {
        // Get bush texture region name based on bush type


        // Get the texture from the atlas
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        if (atlas == null) {
            System.err.println("Bush atlas not found: " + atlasPath);
            return;
        }

        // Add debug output to check available regions


        // Find the region in the atlas
        bushTexture = atlas.findRegion(bushName);

        if (bushTexture == null) {
            System.err.println("Bush texture region not found: " + bushName + " for bush type: " + bushName);
            return;
        } else {
            //System.out.println("Successfully loaded texture for: " + regionName + " (type: " + bushName + ")");
        }

        // Set the width and height based on the texture size
        this.width = bushTexture.getRegionWidth();
        this.height = bushTexture.getRegionHeight();
        //System.out.println("Texture dimensions: " + width + "x" + height + " for bush type: " + bushName);

        // Update the body shape to match the texture size
        updateBodyShape();
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
        resetDepthToY();
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
            System.err.println("Bush texture not loaded, cannot render. Bush type: " + bushName);
        }
    }
}