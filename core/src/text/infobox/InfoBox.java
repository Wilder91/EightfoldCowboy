package text.infobox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class InfoBox {
    private Stage stage;
    private Skin skin;
    private Table table;
    private Label textLabel;


    public InfoBox(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.textLabel = new Label("", skin);
        textLabel.setColor(Color.WHITE);

        // Create a solid color texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f); // RGBA: black with 80% opacity
        pixmap.fill();
        TextureRegionDrawable solidColorDrawable = new TextureRegionDrawable(new Texture(pixmap));

        // Create the table and set the background
        this.table = new Table();
        table.setFillParent(false);
        ///table.setBackground(solidColorDrawable);  // Set the background drawable
        table.add(textLabel).expandX().pad(10);



        // Position the table at the bottom of the screen
        table.bottom().left();
        table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 4);
        table.setPosition(0, 30);

        stage.addActor(table);
        table.setVisible(false);

        // Dispose the pixmap after creating the texture
        pixmap.dispose();
    }

    public void showInfoBox(String text) {
        textLabel.setText(text);
        textLabel.setColor(Color.WHITE);
        table.setVisible(true);
    }

    public void hideInfoBox() {
        //System.out.println("visible?");
        table.setVisible(false);
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }
}
