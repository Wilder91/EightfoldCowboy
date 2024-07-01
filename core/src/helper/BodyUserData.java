package helper;

import com.badlogic.gdx.physics.box2d.Body;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;


public class BodyUserData {
    private int id;
    private ContactType type;
    private Bison bison;
    private Body body;
    private Bird bird;



    public BodyUserData(int id, ContactType type, Body body) {
        this.id = id;
        this.type = type;
        this.body = body;
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

    @Override
    public String toString() {
        return "BodyUserData{id=" + id + ", type=" + type + ", body=" + body + '}';
    }
}
