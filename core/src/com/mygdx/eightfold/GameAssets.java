package com.mygdx.eightfold;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class GameAssets {
    public AssetManager assetManager;

    public GameAssets() {
        assetManager = new AssetManager();
    }

    public void loadAssets() {
        // Load the texture atlas
        assetManager.load("player/atlas/player-horizontal.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/trees.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/bison-movement.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/character-running.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/Character-running.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/character-movement.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/Character-idle.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/Jim-movement.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/Martha-movement.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/NPC-movement.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/pond.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/bugs.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/bushes.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/farm_animal.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/wild-animal.atlas", TextureAtlas.class);
        assetManager.load("atlases/eightfold/fences.atlas", TextureAtlas.class);
        assetManager.load("Character_Horizontal_Run/Character_Horizontal_Run_3.png", Texture.class);
        assetManager.load("buildings/Barns/Barn-1.png", Texture.class);
        assetManager.load("buildings/Shops/Shop-1.png", Texture.class);
        assetManager.load("ethereal.mp3", Music.class);
        assetManager.load("lost & found.mp3", Music.class);
        assetManager.load("sounds/bison-sound.mp3", Sound.class);
        assetManager.load("animals/birds/bird.png", Texture.class);
        assetManager.load("player/player-single.png", Texture.class);
        assetManager.load("animals/bison/bison-single.png", Texture.class);
        assetManager.load("player/player-stationary.png", Texture.class);
        assetManager.load("plants/bushes/Bush_1.png", Texture.class);
        assetManager.load("plants/bushes/Bush_2.png", Texture.class);
        assetManager.load("plants/bushes/Bush_3.png", Texture.class);
        assetManager.load("plants/bushes/Bush_4.png", Texture.class);
        assetManager.load("plants/bushes/Bush_5.png", Texture.class);
        assetManager.load("atlases/eightfold/rocks.atlas", TextureAtlas.class);
        assetManager.load("buildings/Barn_Top.png", Texture.class);
        assetManager.load("buildings/Barn_Bottom.png", Texture.class);
        assetManager.load("buildings/Shop_Top.png", Texture.class);
        assetManager.load("buildings/Shop_Bottom.png", Texture.class);
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

    public Music getMusic(String musicPath) {
        if (!assetManager.isLoaded(musicPath, Music.class)) {
            throw new IllegalArgumentException("Music asset not loaded: " + musicPath);
        }
        return assetManager.get(musicPath, Music.class);
    }
    public Sound getSound(String soundPath) {
        if (!assetManager.isLoaded(soundPath, Sound.class)) {
            throw new IllegalArgumentException("Music asset not loaded: " + soundPath);
        }
        return assetManager.get(soundPath, Sound.class);
    }

    public TextureRegion getRockTopTexture(int rockType) {
        TextureAtlas atlas = getAtlas("atlases/eightfold/rocks.atlas");
        String regionName = getRockRegionName(rockType, true); // true for top
        TextureRegion region = atlas.findRegion(regionName);
        if (region == null) {
            throw new IllegalArgumentException("Rock top texture region not found: " + regionName);
        }
        return region;
    }

    public TextureRegion getRockBottomTexture(int rockType) {
        TextureAtlas atlas = getAtlas("atlases/eightfold/rocks.atlas");
        String regionName = getRockRegionName(rockType, false); // false for bottom
        TextureRegion region = atlas.findRegion(regionName);
        if (region == null) {
            throw new IllegalArgumentException("Rock bottom texture region not found: " + regionName);
        }
        return region;
    }

    // Private helper method for mapping rock types
    private String getRockRegionName(int rockType, boolean isTop) {
        String suffix = isTop ? "_Top" : "_Bottom";
        switch (rockType) {
            case 0:
                return "Small_Rock_1" + suffix;
            case 4:
                return "Small_Rock_2" + suffix;
            case 3:
                return "Medium_Rock_1" + suffix;
            case 2:
                return "Medium_Rock_2" + suffix;
            case 1:
                return "Large_Rock" + suffix;
            case 5:
                return "Cliff_1" + suffix;
            case 6:
                return "Cliff_2" + suffix;
            default:
                throw new IllegalArgumentException("Invalid rock type: " + rockType);
        }
    }

}
