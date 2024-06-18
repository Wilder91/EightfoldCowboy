package text.textbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class TextBox {
    protected Stage stage;
    protected Skin skin;
    protected Label textLabel;
    protected Image image;
    protected Dialog dialog;
    private BitmapFont originalFont;

    public TextBox(Skin skin, String imagePath) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());

        // Create a LabelStyle with a font from the skin
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        originalFont = skin.getFont("commodore-64");
        labelStyle.font = originalFont; // Use the exact font name from your skin file

        // Use the LabelStyle to create the Label
        this.textLabel = new Label("", labelStyle);
        this.textLabel.setWrap(true); // Enable word wrap
        this.textLabel.setAlignment(Align.left); // Align text to the left

        // Load the image
        this.image = new Image(new Texture(Gdx.files.internal(imagePath)));

        // Create the dialog
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = createSolidColorDrawable(207 / 255f, 185 / 255f, 151 / 255f, 0.3f); // Set dialog background color
        windowStyle.titleFont = skin.getFont("commodore-64"); // Set the font for the dialog title if needed

        this.dialog = new Dialog("", windowStyle);
        dialog.setMovable(false); // Make dialog non-movable

        // Create a stack to overlay the image on the left side of the text box
        Stack stack = new Stack();

        // Create a table for the image with a border
        Table imageContainer = new Table();
        imageContainer.setBackground(createBackgroundWithBorder(100f / 255f, 130f / 255f, 104f / 255f, .8f, 162f / 255f, 188f / 255f, 104f / 255f, .8f));
        imageContainer.add(image).size(50, 50).pad(5);

        // Create a table for the text and image
        Table textImageTable = new Table();
        textImageTable.add(imageContainer).left().padRight(10); // Add image container with border to the left with padding to the right
        textImageTable.add(textLabel).expand().fill().left(); // Add text label next to the image

        // Add the textImageTable to the stack
        stack.add(textImageTable);

        // Create a background table to add border and background color
        Table backgroundTable = new Table();
        backgroundTable.setBackground(createBackgroundWithBorder(100F / 255f, 137F / 255f, 109F / 255f, .8f, 162f / 255f, 188f / 255f, 104f / 255f, 0.8f));
        backgroundTable.add(stack).expand().fill().pad(0);

        // Add the background table to the dialog
        dialog.getContentTable().add(backgroundTable).expand().fill();

        // Position the dialog at the bottom center of the screen
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.addActor(dialog);
        dialog.setVisible(false);
    }

    private TextureRegionDrawable createSolidColorDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        return drawable;
    }

    private TextureRegionDrawable createBackgroundWithBorder(float borderR, float borderG, float borderB, float borderA, float bgR, float bgG, float bgB, float bgA) {
        int borderWidth = 3;
        int width = 50;  // Arbitrary size; can be adjusted
        int height = 50; // Arbitrary size; can be adjusted

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Fill with border color
        pixmap.setColor(borderR, borderG, borderB, borderA);
        pixmap.fill();

        // Fill the inside with background color
        pixmap.setColor(bgR, bgG, bgB, bgA);
        pixmap.fillRectangle(borderWidth, borderWidth, width - 2 * borderWidth, height - 2 * borderWidth);

        TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        return drawable;
    }

    public void showTextBox(String text) {
        textLabel.setText(text);
        dialog.setVisible(true);
    }

    public void hideTextBox() {
        dialog.setVisible(false);
    }

    private BitmapFont scaleFont(BitmapFont originalFont, float scale) {
        BitmapFont scaledFont = new BitmapFont(originalFont.getData().fontFile, originalFont.getRegion(), false);
        scaledFont.getData().setScale(scale);
        return scaledFont;
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public void resize(int width, int height) {
        dialog.setSize(width / 2, height / 4);
        dialog.setPosition((width - dialog.getWidth()) / 2, 0);

        // Update image size based on dialog size
        image.setSize(50, 50); // Maintain the image size
        stage.getViewport().update(width, height, true);
//        image.setScale(2,2);
        float scale = height / 720f; // Assuming 720 is the reference height
        textLabel.setStyle(new Label.LabelStyle(scaleFont(originalFont, scale), textLabel.getStyle().fontColor));
    }

    public abstract void setFontColor(float r, float g, float b, float a);
}
