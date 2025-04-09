

package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Building extends GameEntity {
    private boolean isCollidable;
    private int buildingId;
    private String textureName;
    private int stateTime;
    private Sprite buildingSprite; // Add a sprite field specific to Building

    public Building(float width, float height, float x, float y, Body body, boolean isCollidable,
                    ScreenInterface screenInterface, int buildingId, GameAssets gameAssets,
                    String textureName) {
        super(width, height, body, screenInterface, gameAssets);

        this.isCollidable = isCollidable;
        this.buildingId = buildingId;
        this.textureName = textureName;

        // Load the sprite
        String path = "buildings/" + textureName + "s/" + textureName + "-1.png";
        //System.out.println("Trying to load texture from: " + path);
        try {
            this.buildingSprite = new Sprite(gameAssets.getTexture(path));
            if (buildingSprite.getTexture() == null) {
                System.err.println("Texture loaded but is null: " + path);
            } else {
                //System.out.println("Texture successfully loaded: " + path);
                // Print texture dimensions for debugging
               // System.out.println("Texture dimensions: " + buildingSprite.getTexture().getWidth() + "x" + buildingSprite.getTexture().getHeight());
            }
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + path);
            e.printStackTrace();
        }
    }

    @Override
    public void update(float delta) {
        // Update position based on physics body
        this.x = this.getBody().getPosition().x * PPM;
        this.y = this.getBody().getPosition().y * PPM;
        stateTime += delta;
        //resetDepthToY();
        setDepthOffset(-29f);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (buildingSprite != null && buildingSprite.getTexture() != null) {
            // Calculate screen coordinates
            float x = body.getPosition().x * PPM - width * PPM / 2;
            float y = body.getPosition().y * PPM - height * PPM / 2;

            // Scale dimensions by PPM to convert from world units to pixels
            float renderWidth = width * PPM;
            float renderHeight = height * PPM;


            // Draw using batch directly
            batch.draw(
                    buildingSprite.getTexture(),
                    x,
                    y,
                    renderWidth,
                    renderHeight
            );
        } else {
            System.out.println("Cannot render building - sprite is null or texture is null");
        }
    }

    public int getBuildingId() {
        return buildingId;
    }
}