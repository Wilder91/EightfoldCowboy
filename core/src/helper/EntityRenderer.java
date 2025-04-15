package helper;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import objects.GameEntity;

public class EntityRenderer {
    protected GameEntity entity;
    protected Sprite mainSprite;

    public EntityRenderer(GameEntity entity) {
        this.entity = entity;
        // The mainSprite might be null initially and set later
    }

    public void setMainSprite(Sprite sprite) {
        this.mainSprite = sprite;
    }

    public void update(float delta) {
        // Basic update logic
        // Override in specialized renderers if needed
    }

    public void render(SpriteBatch batch) {
        if (mainSprite == null) {
            System.out.println("WARNING: mainSprite is null for entity at " + entity.getX() + ", " + entity.getY());
            return; // Skip rendering if no sprite is set
        }
        // First position the sprite
        mainSprite.setPosition(
                entity.getX() - mainSprite.getWidth() / 2,
                entity.getY() - mainSprite.getHeight() / 2
        );

        // Then draw it at the new position
        mainSprite.draw(batch);
    }
}
