package helper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Body;
import objects.GameEntity;
import objects.animals.birds.Bird;



public class BodyUserData {
    private int id;
    private ContactType type;

    private Body body;
    private String name;
    private Bird bird;
    private GameEntity entity;





    public BodyUserData(int id, ContactType type, Body body, GameEntity entity) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.entity = entity;
    }

    public BodyUserData(int id, ContactType type, Body body, String name) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public ContactType getType() {
        return type;
    }



    public Body getBody() {
        return body;
    }

    public GameEntity getEntity() {
        return entity;
    }

    public void setEntity(GameEntity entity){
        this.entity = entity;

    }

    @Override
    public String toString() {
        return "BodyUserData{id=" + id + ", type=" + type + ", body=" + body + ", entity=" + entity + '}';
    }
}
