package helper.tiledmap.factories;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import helper.EntityManagers.ThicketSaintManager;
import objects.enemies.ThicketSaint;

import static helper.Constants.PPM;
import static helper.ContactType.ENEMY;

public class EntityFactory {
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private static int enemyCounter = 0;
    private Object entity;

    public EntityFactory(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
    }

    public void createEntity(RectangleMapObject rectangleMapObject, String entityType, String entityName) {
        Rectangle rectangle = rectangleMapObject.getRectangle();
        float centerX = (rectangle.x + rectangle.width / 2) / PPM;
        float centerY = (rectangle.y + rectangle.height / 2) / PPM;
        float bodyWidth = rectangle.width / PPM;
        float bodyHeight = rectangle.height / PPM;

        // Create a dynamic body with physics properties similar to player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;  // Prevent rotation when colliding
        bodyDef.position.set(centerX, centerY);
        bodyDef.linearDamping = 8.0f;  // Add damping to prevent sliding

        Body entityBody = screenInterface.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = .5f;     // Lower density makes it heavier
        fixtureDef.friction = 0.5f;     // Higher friction prevents sliding
        fixtureDef.restitution = 0.0f;  // No bounce

        // Use the enemyCounter to assign unique IDs
        int currentId = enemyCounter++;

        Fixture fixture = entityBody.createFixture(fixtureDef);
        shape.dispose();

        switch(entityType) {
            case "enemies":
                switch(entityName) {
                    case "saint_small":


                        ThicketSaint thicketSaint = new ThicketSaint(
                                rectangle.width,
                                rectangle.height,
                                entityBody,
                                screenInterface,
                                gameAssets,
                                entityType,
                                entityName,
                                25f
                        );
                        this.entity = thicketSaint;
                        screenInterface.addEntity(thicketSaint);


                        ThicketSaintManager.addEnemy(thicketSaint);
                        break;
                }
                fixture.setUserData(new BodyUserData(currentId, ENEMY, entityBody, entity));
        }

    }

}