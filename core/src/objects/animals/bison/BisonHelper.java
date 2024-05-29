package objects.animals.bison;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BisonHelper {
    private TextureAtlas textureAtlas;

    public Sprite parseSpriteSheet() {
        textureAtlas = new TextureAtlas("animals/bison/walking/horizontal.png"); // Adjusted file extension to .atlas
        Sprite sprite = textureAtlas.createSprite("bison");
        return sprite;
    }
}
