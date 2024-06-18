package text.textbox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BisonTextBox extends TextBox {

    public BisonTextBox(Skin skin, String imagePath) {
        super(skin, imagePath);
    }

    @Override
    public void setFontColor(float r, float g, float b, float a) {

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = new Color(r, g, b, a);
        textLabel.setStyle(labelStyle);
    }
}
