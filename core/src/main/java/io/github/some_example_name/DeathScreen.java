package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DeathScreen implements Screen {

    private Main main;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shape;
    Sprite sprite;
    Texture background;
    boolean dir = true;


    private int selected = 0; // 0 = Play, 1 = Exit
    private final String[] options = {"Play", "Exit"};

    public DeathScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shape = new ShapeRenderer();
        sprite = new Sprite(new Texture("block.png"));
        sprite.setScale(4);
        sprite.setPosition(1300, 300);
        background = new Texture("backas.jpg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.getData().setScale(5);
        font.draw(batch, "YOU DIED", 700, 400);

        font.getData().setScale(2f);
        for (int i = 0; i < options.length; i++) {
            font.draw(batch, options[i], 150, 200 - i * 60);
        }
        batch.end();

        // Draw selection rectangle
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(1, 1, 1, 1);
        shape.rect(140, 168 - selected * 60, 120, 40);
        shape.end();
        move();

        // Input
        if (Gdx.input.isKeyJustPressed(Keys.UP)) selected = (selected + options.length - 1) % options.length;
        if (Gdx.input.isKeyJustPressed(Keys.DOWN)) selected = (selected + 1) % options.length;
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            if (selected == 0) main.setScreen(new GameScreen(main)); // switch to game
            else Gdx.app.exit();
            dispose();
        }
    }

    private void move() {
        if(sprite.getY() <  200) {
            dir = true;
        }
        if(sprite.getY() > 700) {
            dir = false;
        }
        if(dir)
            sprite.translateY(3f);
        else
            sprite.translateY(-3f);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shape.dispose();
    }
}
