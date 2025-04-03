package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.PPM;

public class Building {
    private float width;
    private float height;
    private float x;
    private float y;
    private Body body;
    private boolean isCollidable;
    private ScreenInterface screenInterface;
    private int buildingId;
    private GameAssets gameAssets;

    // Top and bottom textures
    private Sprite topSprite;
    private Sprite bottomSprite;

    // Y-coordinate where top and bottom parts split
    private float splitY;

    public Building(float width, float height, float x, float y, Body body, boolean isCollidable,
                    ScreenInterface screenInterface, int buildingId, GameAssets gameAssets,
                    String topTextureName, String bottomTextureName) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.body = body;
        this.isCollidable = isCollidable;
        this.screenInterface = screenInterface;
        this.buildingId = buildingId;
        this.gameAssets = gameAssets;

        // Initialize sprites
        this.topSprite = new Sprite(gameAssets.getTexture("buildings/" + topTextureName + ".png"));
        this.bottomSprite = new Sprite(gameAssets.getTexture("buildings/" + bottomTextureName + ".png"));

        // Set sprite positions and sizes
        setupSprites();

        // Calculate split Y position (middle of the building by default)
        this.splitY = y * PPM;
    }

    // Constructor for backward compatibility
    public Building(float width, float height, float x, float y, Body body, boolean isCollidable,
                    ScreenInterface screenInterface, int buildingId, GameAssets gameAssets) {
        this(width, height, x, y, body, isCollidable, screenInterface, buildingId, gameAssets,
                "buildings/barns/" + getBuildingNameFromId(buildingId) + "_Top",
                "buildings/barns/" + getBuildingNameFromId(buildingId) + "_Bottom");
    }

    private static String getBuildingNameFromId(int buildingId) {
        switch (buildingId) {
            case 0:
                return "Shop";
            case 1:
                return "Barn";
            // Add more cases for other building types
            default:
                return "Building_" + buildingId;
        }
    }

    private void setupSprites() {
        float splitOffset;

        // Customize split offset based on building type
        if (buildingId == 0) { // Shop
            splitOffset = bottomSprite.getHeight();
        } else if (buildingId == 1) { // Barn
            splitOffset = bottomSprite.getHeight() / 2;
        } else {
            // Default behavior
            splitOffset = bottomSprite.getHeight();
        }

        // Bottom sprite positioning
        bottomSprite.setPosition(
                (x - width/2) * PPM,
                (y - height/2) * PPM
        );

        // Top sprite positioning with custom offset
        topSprite.setPosition(
                (x - width/2) * PPM,
                (y - height/2) * PPM + splitOffset
        );

        // The split Y uses the same offset
        this.splitY = (y - height/2) * PPM + splitOffset;
    }

    // Method to draw the building with depth sorting
    public void draw(SpriteBatch batch) {
        // Only draw the sprites, don't handle the depth sorting here
        // That should be done in the main rendering code
        bottomSprite.draw(batch);
        // topSprite will be drawn later in the rendering order
    }

    public void drawTop(SpriteBatch batch) {
        topSprite.draw(batch);
    }

    // Getter methods for sprites
    public Sprite getTopSprite() {
        return topSprite;
    }

    public Sprite getBottomSprite() {
        return bottomSprite;
    }

    // Additional methods for getting collision data
    public float getSplitY() {
        return splitY;
    }

    // Getters and setters
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }

    public int getBuildingId() {
        return buildingId;
    }
}