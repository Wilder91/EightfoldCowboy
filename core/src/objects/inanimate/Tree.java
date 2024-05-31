package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameScreen;

import static helper.Constants.FRAME_DURATION;

public class Tree extends InanimateEntity {
    private final int treeType;
    private Animation<TextureRegion> firstTreeAnimation;
    private Animation<TextureRegion> secondTreeAnimation;
    private float stateTime;
    private Animation<TextureRegion>[] treeAnimations;


    public Tree(float width, float height, Body body, GameScreen gameScreen, int treeType, int id) {
        super(width, height, body, gameScreen, id);
        this.stateTime = 0f;
        this.treeType = treeType;
        treeAnimations = new Animation[5];
        initAnimations();

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void update() {

    }

    private void initAnimations() {
        // Load frames for the first tree animation
        Array<TextureRegion> firstTreeFrames = new Array<>();
        TextureAtlas firstTreeAtlas = new TextureAtlas("plants/trees/atlases/treeone.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = firstTreeAtlas.findRegion("tree-one-" + i);
            if (region == null) {
                System.out.println("Region tree-one-" + i + " not found!");
            } else {
                firstTreeFrames.add(region);
            }
        }
        firstTreeAnimation = new Animation<>(FRAME_DURATION, firstTreeFrames, Animation.PlayMode.LOOP);

        // Load frames for the second tree animation
        Array<TextureRegion> secondTreeFrames = new Array<>();
        TextureAtlas secondTreeAtlas = new TextureAtlas("plants/trees/atlases/treetwo.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = secondTreeAtlas.findRegion("tree-two-" + i);
            if (region == null) {
                System.out.println("Region tree-two-" + i + " not found!");
            } else {
                secondTreeFrames.add(region);
            }
        }
        secondTreeAnimation = new Animation<>(FRAME_DURATION, secondTreeFrames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Select the appropriate animation based on the tree type
        Animation<TextureRegion> currentAnimation = treeAnimations[treeType];

        // Get the current frame of the animation
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // Draw the current frame at the tree's position
        batch.draw(currentFrame,
                body.getPosition().x - width / 2,
                body.getPosition().y - height / 2,
                width,
                height);
    }
}
