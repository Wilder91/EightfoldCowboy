package helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static helper.Constants.PPM;

public class IsometricBodyHelperService {

    private static float tileWidth = 64f;  // Default tile width - adjust as needed
    private static float tileHeight = 32f; // Default tile height - adjust as needed

    /**
     * Sets the tile dimensions used for isometric conversions
     * @param width The width of isometric tiles
     * @param height The height of isometric tiles
     */
    public static void setTileDimensions(float width, float height) {
        tileWidth = width;
        tileHeight = height;
    }

    /**
     * Converts isometric coordinates to Cartesian coordinates
     * @param isoX X coordinate in isometric space
     * @param isoY Y coordinate in isometric space
     * @return Vector2 containing Cartesian coordinates
     */
    public static Vector2 isoToCartesian(float isoX, float isoY) {
        // Convert from isometric to cartesian
        float cartX = (isoX / tileWidth + isoY / tileHeight) / 2;
        float cartY = (isoY / tileHeight - isoX / tileWidth) / 2;
        return new Vector2(cartX, cartY);
    }

    /**
     * Converts Cartesian coordinates to isometric coordinates
     * @param cartX X coordinate in Cartesian space
     * @param cartY Y coordinate in Cartesian space
     * @return Vector2 containing isometric coordinates
     */
    public static Vector2 cartesianToIso(float cartX, float cartY) {
        // Convert from cartesian to isometric
        float isoX = (cartX - cartY) * tileWidth;
        float isoY = (cartX + cartY) * tileHeight;
        return new Vector2(isoX, isoY);
    }

    /**
     * Creates a body in Box2D world, handling isometric to cartesian conversion
     * @param x Isometric X coordinate
     * @param y Isometric Y coordinate
     * @param width Width in pixels
     * @param height Height in pixels
     * @param isStatic Whether the body is static
     * @param world The Box2D world
     * @param bodyType The contact type for collision filtering
     * @param entityId The entity ID
     * @return The created Box2D body
     */
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world, ContactType bodyType, int entityId) {
        // Convert isometric position to cartesian for physics
        Vector2 cartesianPos = isoToCartesian(x, y);
        float cartX = cartesianPos.x / PPM;
        float cartY = cartesianPos.y / PPM;

        // Adjust dimensions for physics
        float halfWidth = width / (2f * PPM);
        float halfHeight = height / (2f * PPM);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(cartX, cartY);
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        // For isometric we might want to use a smaller collision shape
        // since visual sprites are larger than their collision areas
        shape.setAsBox(halfWidth * 0.7f, halfHeight * 0.7f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = isStatic ? 0f : 1f;
        fixtureDef.filter.categoryBits = bodyType.getCategoryBits();
        fixtureDef.filter.maskBits = bodyType.getMaskBits();
        fixtureDef.filter.groupIndex = (short) entityId;

        body.createFixture(fixtureDef);
        body.setUserData(entityId);
        shape.dispose();

        return body;
    }
}