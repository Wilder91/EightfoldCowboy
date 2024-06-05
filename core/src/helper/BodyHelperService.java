package helper;

import com.badlogic.gdx.physics.box2d.*;
import static helper.Constants.PPM;
import static helper.ContactType.TREE;

public class BodyHelperService {

    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world, ContactType type, int id) {
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
        fixture.setUserData(new BodyUserData(id, type, body));
        // Set user data for the fixture
        //System.out.println("Fixture User Data: " + fixture.getUserData());
        shape.dispose();

        body.setUserData( type + "," + id + ", " + body );
//        System.out.println("Body User Data: " + body.getUserData());
//        System.out.println("Fixture User Data: " + fixture.getUserData());
        //System.out.println("BODY HELPER BODY: " + type + " " + body);
        return body;
    }



}
