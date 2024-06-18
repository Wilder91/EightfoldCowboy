package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.eightfold.GameAssets;

public class BisonConversationScreen extends ScreenAdapter {
    private final OrthographicCamera camera;
    private final GameAssets gameAssets;
    private final GameScreen gameScreen;
    private Stage stage;
    private Skin skin;
    private Label dialogueLabel;
    private Image characterImage;
    private int convoCounter;
    private String[] dialogues;
    private Texture characterTexture;
    private int bisonId;

    public BisonConversationScreen(OrthographicCamera camera, GameAssets gameAssets, GameScreen gameScreen, int bisonId) {
        this.camera = camera;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.stage = new Stage(new ScreenViewport());
        this.bisonId = bisonId;
        this.convoCounter = 0;

        // Initialize dialogues based on bisonId
        if (bisonId == 0) {
            this.dialogues = new String[]{
                    "Hello Kath, welcome to the ranch!",
                    "The ranch is a big ole ranch full of...",
                    "bison and there's a saloon, I guess",
                    "I'm a talking bison, and I'm here to help"
            };
            this.characterTexture = new Texture(Gdx.files.internal("animals/bison/grazing/Bison_Grazing_0.png"));
        } else if (bisonId == 1) {
            this.dialogues = new String[]{
                    "My name is Mike",
                    "I'm a big bison, as you can see",
                    "Welcome to the world of talking bison!"
            };
            this.characterTexture = new Texture(Gdx.files.internal("animals/bison/grazing/Bison_Grazing_1.png"));
        } else {
            this.dialogues = new String[]{"Unknown bison"};
            this.characterTexture = new Texture(Gdx.files.internal("animals/bison/grazing/Bison_Grazing_0.png"));
        }

        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("vhs/skin/vhs-ui.json"));

        // Create UI elements
        this.dialogueLabel = new Label(dialogues[0], skin);

        // Load the character image
        this.characterImage = new Image(characterTexture);

        Table table = new Table();
        table.setFillParent(true);

        // Add the image and label to the table with specific positioning
        table.add(characterImage).center().pad(100); // Adjust the padding as needed
        table.row(); // Move to the next row
        table.add(dialogueLabel).expand().center().pad(10); // Adjust the padding as needed
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Transition back to the game screen
            ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
        }
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            convoCounter++;
            if (convoCounter < dialogues.length) {
                dialogueLabel.setText(dialogues[convoCounter]);
            } else {
                // End of conversation, return to game screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        characterTexture.dispose(); // Dispose of the textures to avoid memory leaks
    }
}
