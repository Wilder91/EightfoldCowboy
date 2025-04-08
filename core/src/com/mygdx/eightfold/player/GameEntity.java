package com.mygdx.eightfold.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import java.util.Comparator;


public abstract class GameEntity {
    protected float x, y, velX, velY, speed;
    protected float width, height;
    protected Body body;
    private int entityId;
    private float depth;


    public GameEntity(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.depth = y;
        this.entityId = entityId;
        this.width = width;
        this.height = height;
        this.body = body;
        this.velX = 0;
        this.velY = 0;
        this.speed = 0;
    }

        // Comparator for Y-based depth sorting
        public static final Comparator<GameEntity> Y_COMPARATOR =
                (entity1, entity2) -> Float.compare(entity2.y, entity1.y);


    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch);

    public Body getBody() {
        return body;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return getId();
    }

    public void setId(int i) {
        entityId = i;
    }
}
