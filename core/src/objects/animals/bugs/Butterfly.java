package objects.animals.bugs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import objects.StationaryObjectAnimator;

import static helper.Constants.PPM;

public class Butterfly extends Bug {
    private int id;
    private String butterflyName;
    private StationaryObjectAnimator animator;
    private GameAssets gameAssets;

    public Butterfly(float width, float height, float x, float y, Body body, String butterflyName, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, x, y, body, screenInterface, gameAssets);
        this.butterflyName = butterflyName;
        this.gameAssets = gameAssets;

        // Use the StationaryObjectAnimator instead of creating our own animation
        this.animator = new StationaryObjectAnimator(this, "bugs", butterflyName, gameAssets);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update the animator
        animator.update(delta);
    }

    public GameAssets getGameAssets() {
        return this.gameAssets;
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void playerContact(){
        // Your contact handling code here
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}