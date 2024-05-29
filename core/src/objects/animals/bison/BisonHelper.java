package objects.animals.bison;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BisonHelper {
    private TextureAtlas textureAtlas;

    public Sprite parseSpriteSheet() {
        textureAtlas = new TextureAtlas("animals/bison/walking/atlases/horizontal.atlas");
        System.out.println("atlas: " + textureAtlas);// Adjusted file extension to .atlas
        Sprite sprite = textureAtlas.createSprite("horizontal");
        System.out.println("sprite: " + sprite);// Adjusted file extension to .atlas

        return sprite;
    }
}
