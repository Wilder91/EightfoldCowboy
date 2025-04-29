package helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import objects.GameEntity;

public class SensorHelper {
    private FixtureDef sensorFixtureDef;
    private CircleShape sensorShape;
    private Fixture sensorFixture;
    private int sensorId = 0;

    public void setupAttackSensor(float radius, GameEntity entity) {
        sensorId += 1;
        int id = sensorId;
        this.sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.isSensor = true;

        sensorShape = new CircleShape();
        sensorShape.setRadius(radius);
        sensorShape.setPosition(new Vector2(0.0f, 0)); // position relative to body center
        sensorFixtureDef.shape = sensorShape;

        // Add the fixture to your body
        this.sensorFixture = entity.getBody().createFixture(sensorFixtureDef);
        sensorFixture.setUserData(entity);

        // Dispose the shape when done
        sensorShape.dispose();
    }
}
