package objects.humans;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SpriteIdleHelper;

import static helper.Constants.PPM;

public class NPC extends GameEntity {
    private Sprite sprite;
    private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean isFacingRight = true;
    private int[] frameCounts;
    private float stateTime;

    public NPC(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, int npcType) {
        super(width, height, body, screenInterface, gameAssets);
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);


        String characterName = getCharacterNameFromType(npcType);
        int[] frameCounts = getFrameCountsFromType(npcType);
        float stateTime = getStateTimeFromType(npcType);

        System.out.println("character name: " + npcType);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "NPC", characterName, frameCounts, stateTime);
    }

    private int getStateTimeFromType(int npcType) {
        switch (npcType) {
            case 2:
                return 12;
            default:
                return 0;
        }

    }

    private int[] getFrameCountsFromType(int npcType) {
        switch (npcType) {
            case 1:
                return new int[]{23, 0, 0, 0, 0};
            case 2:
                return new int[]{24, 0, 0, 0, 0};
            default:
                return new int[]{0,0,0,0,0};
        }
    }

    private String getCharacterNameFromType(int npcType) {
        switch (npcType) {
            case 1:
                return "Jim";
            case 2:
                return "Martha";
            case 3:
                return "Miner";
            case 4:
                return "Cowboy";
            case 5:
                return "Blacksmith";
            default:
                return "NPC";
        }
    }


    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        idleHelper.setDirection(lastDirection);
        idleHelper.setFacingRight(isFacingRight);
        idleHelper.update(delta);
        sprite = idleHelper.getSprite();
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.setPosition(x - width / 2, y - height / 2);
        sprite.draw(batch);
    }
}
