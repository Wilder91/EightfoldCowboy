package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Fence extends InanimateEntity {
    private final TextureRegion texture;
    private final Body body;
    private final int fenceId;
    private final String fenceName;
    private final ScreenInterface screen;
    private final GameAssets gameAssets;
    private Rectangle bounds;

    public Fence(Body body, int fenceId, String fenceName,
                 ScreenInterface screen, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(0, 0, body, screen, fenceId, gameAssets, gameContactListener);
        this.body = body;
        this.fenceId = fenceId;
        this.fenceName = fenceName;
        this.screen = screen;
        this.gameAssets = gameAssets;

        // Get the texture from the atlas
        this.texture = gameAssets.getAtlas("atlases/eightfold/fences.atlas").findRegion(fenceName);
        if (this.texture == null) {
            System.err.println("Fence texture not found for: " + fenceName);
        }

        // Calculate the bounds based on the body's fixtures
        calculateBounds();

        // Set initial position and depth
        this.x = body.getPosition().x * PPM;
        this.y = body.getPosition().y * PPM;

        // Important: Set the initial depth based on y-position
        setDepth(this.y);
    }

    private void calculateBounds() {
        // Get the physics body bounds
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        // Check all fixtures
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.getShape() instanceof PolygonShape) {
                PolygonShape polygonShape = (PolygonShape) fixture.getShape();
                for (int i = 0; i < polygonShape.getVertexCount(); i++) {
                    com.badlogic.gdx.math.Vector2 vertex = new com.badlogic.gdx.math.Vector2();
                    polygonShape.getVertex(i, vertex);

                    // Transform to world coordinates
                    vertex = body.getWorldPoint(vertex);

                    // Scale to pixels
                    vertex.x *= PPM;
                    vertex.y *= PPM;

                    // Update bounds
                    minX = Math.min(minX, vertex.x);
                    minY = Math.min(minY, vertex.y);
                    maxX = Math.max(maxX, vertex.x);
                    maxY = Math.max(maxY, vertex.y);
                }
            }
        }

        // Create bounds rectangle
        this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        //System.out.println("Fence bounds calculated: " + bounds.x + ", " + bounds.y + ", " +
                //bounds.width + "x" + bounds.height);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null && bounds != null) {
            // Draw the texture to match the exact bounds of the body
            batch.draw(texture,
                    bounds.x,     // Left edge
                    bounds.y,     // Bottom edge
                    bounds.width, // Width
                    bounds.height // Height
            );
        }
    }

    @Override
    public void update(float delta) {
        // Update position (even though it's static, update for consistency)
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Critical: Update the depth based on the Y position
        resetDepthToY();
    }

    // Make sure this method exists and works properly
    @Override
    public void resetDepthToY() {
        // The y-position determines render depth
        setDepth(y - 8);
    }

    public Body getBody() {
        return body;
    }

    public int getFenceId() {
        return fenceId;
    }

    public String getFenceName() {
        return fenceName;
    }
}