package objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameAssets {
    public AssetManager assetManager;

    public GameAssets() {
        assetManager = new AssetManager();
    }

    public void loadAssets() {
        // Load the texture atlas
        assetManager.load("plants/trees/atlases/oak-trees.atlas", TextureAtlas.class);
        assetManager.load("animals/birds/bird.png", Texture.class);
        assetManager.load("boulder.png", Texture.class);
        assetManager.load("animals/bison/grazing/atlas/bison-grazing.atlas", TextureAtlas.class);
        assetManager.load("player/atlas/player-horizontal.atlas", TextureAtlas.class);
        assetManager.load("plants/trees/oak-trees.atlas", TextureAtlas.class);
        assetManager.load("animals/bison/walking/atlases/horizontal.atlas", TextureAtlas.class);
        assetManager.load("animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas", TextureAtlas.class);
        assetManager.load("animals/bison/walking/atlases/eightfold/bison-diagonal.atlas", TextureAtlas.class);
        assetManager.load("animals/bison/walking/atlases/eightfold/bison-horizontal.atlas", TextureAtlas.class);
        assetManager.load("Character_Horizontal_Run/Character_Horizontal_Run_3.png", Texture.class);
        // Load individual textures for bison grazing
        for (int i = 0; i <= 39; i++) {
            String filename = "animals/bison/grazing/Bison_Grazing_" + i + ".png";
            assetManager.load(filename, Texture.class);
        }

        // Add other assets to load here
    }

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public TextureAtlas getAtlas(String atlasPath) {
        if (!assetManager.isLoaded(atlasPath, TextureAtlas.class)) {
            throw new IllegalArgumentException("Asset not loaded: " + atlasPath);
        }
        return assetManager.get(atlasPath, TextureAtlas.class);
    }

    public Texture getTexture(String texturePath) {
        return assetManager.get(texturePath, Texture.class);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
