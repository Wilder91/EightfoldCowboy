package text.textbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class TextBox {
    protected Stage stage;
    protected Skin skin;
    protected Table table;
    protected Label textLabel;
    protected Image image;

    public TextBox(Skin skin, String imagePath) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());

        // Use the commodore-64 font and label style from the skin
        this.textLabel = new Label("", skin, "default");
        this.textLabel.setWrap(true); // Enable word wrap

        // Load the image
        this.image = new Image(new Texture(Gdx.files.internal(imagePath)));

        // Create the table and set the background
        this.table = new Table(skin);
        table.setFillParent(false);

        // Set the table's background color
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(207 / 255f, 185 / 255f, 151 / 255f, 1f); // RGBA: color with 100% opacity
        pixmap.fill();
        TextureRegionDrawable solidColorDrawable = new TextureRegionDrawable(new Texture(pixmap));
        table.setBackground(solidColorDrawable);

        // Add elements to the table
        table.add(image).expandX();
        table.add(textLabel).expandX().fillX(); // Make sure label fills its cell

        // Position the table at the bottom center of the screen
        table.center();
        table.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 4);
        table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2, 0);

        stage.addActor(table);
        table.setVisible(false);

        // Dispose the pixmap after creating the texture
        pixmap.dispose();
    }

    public void showTextBox(String text) {
        textLabel.setText(text);
        table.setVisible(true);
    }

    public void hideTextBox() {
        table.setVisible(false);
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public abstract void setFontColor(float r, float g, float b, float a);
}
