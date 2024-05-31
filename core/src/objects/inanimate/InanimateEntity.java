package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameScreen;

public abstract class InanimateEntity {
    private final int id;
    protected float x, y, velX, velY, speed;
    protected float width, height;
    
    protected Body body;

    public InanimateEntity(float width, float height, Body body, GameScreen gameScreen, int id) {
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = width;
        this.height = height;
        this.body = body;
        this.id = id;
        this.speed = 0;
    }

    public abstract void update(float delta);

    public abstract void update();

    public abstract void render(SpriteBatch batch);

    public Body getBody() {
        return body;
    }
}
