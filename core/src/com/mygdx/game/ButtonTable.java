package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.function.Consumer;

// Defines functionality for making a Button table and adding buttons to it
public class ButtonTable {
    private static final float FONT_SCALE = 0.75f;
    private static final float PAD_COEFF = 0.08f;
    private float totalWidth, totalHeight;
    private Table table;
    private Array<TextButton> buttons;
    private Skin skin;


    // TODO: change controlwidth/height name
    public ButtonTable(float totalWidth, float totalHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        buttons = new Array<>();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.getAtlas().getTextures().iterator().next().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skin.getFont("default-font").getData().setScale(FONT_SCALE);
        skin.getFont("default-font").setUseIntegerPositions(false);
    }

    // pre: buttons not empty
    public Table makeTable() {
        if(buttons.isEmpty()) throw new IllegalStateException("No buttons added to table.");
        GlyphLayout glyphLayout = new GlyphLayout(skin.getFont("default-font"),
                buttons.get(0).getText());
        float height = glyphLayout.height;
        float maxWidth = 0;

        for(int i = 1; i < buttons.size; i++) {
            glyphLayout.setText(skin.getFont("default-font"), buttons.get(i).getText());
            if(glyphLayout.width > maxWidth) {
                maxWidth = glyphLayout.width;
            }
        }

        // table-related values
        final float textPad = PAD_COEFF * maxWidth;
        final float buttonPad = PAD_COEFF * (maxWidth + textPad);
        final float totalPad = 2 * (textPad + buttonPad);
        float buttonWidth = maxWidth + totalPad;
        float buttonHeight = height + totalPad;
        int numCols = (int) Math.floor(totalWidth / buttonWidth);
        int numRows = (int) Math.ceil((float) buttons.size / numCols);
        int maxNumRows = (int) Math.floor(totalHeight / buttonHeight);
        if(numRows > maxNumRows) {
            throw new IllegalStateException("Too many buttons! Decrease the font scale.");
        }

        table = new Table();
        table.setFillParent(true);
        buttonWidth -= 2 * buttonPad;
        buttonHeight -= 2 * buttonPad;
        table.defaults()
                .width(buttonWidth)
                .height(buttonHeight)
                .pad(buttonPad);

        for(int i = 0; i < buttons.size; i++) {
            if(i % numCols == 0) table.row();
            table.add(buttons.get(i));
        }

        return table;
    }

    public void addTextButton(String text, Consumer<TextButton> leftClickFn) {
        TextButton button = new TextButton(text, skin);
        addListener(button, Input.Buttons.LEFT, leftClickFn);
        buttons.add(button);
    }

    public void addTextButton(String text, Consumer<TextButton> leftClickFn,
                              Consumer<TextButton> rightClickFn) {
        TextButton button = new TextButton(text, skin);
        addListener(button, Input.Buttons.LEFT, leftClickFn);
        addListener(button, Input.Buttons.RIGHT, rightClickFn);
        buttons.add(button);
    }

    private void addListener(TextButton button, int clickType, Consumer<TextButton> clickFn) {
        button.addListener(new ClickListener(clickType) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickFn.accept(button);
            }
        });
    }

}
