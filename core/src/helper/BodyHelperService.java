package helper;

import com.badlogic.gdx.physics.box2d.*;
import static helper.Constants.PPM;

public class BodyHelperService {

    // Original method
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world, ContactType type, int id) {
        return createBody(x, y, width, height, isStatic, world, type, id, null);
    }

    // New overloaded method with entity reference
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world, ContactType type, int id, Object entityRef) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;

        Fixture fixture = body.createFixture(fixtureDef);

        // Set user data with entity reference if provided
        if (entityRef != null) {
            //fixture.setUserData(new BodyUserData(id, type, body, entityRef));
        } else {
            fixture.setUserData(new BodyUserData(id, type, body, "body"));
        }

        shape.dispose();

        body.setUserData(type + "," + id + ", " + body);
        return body;
    }
}