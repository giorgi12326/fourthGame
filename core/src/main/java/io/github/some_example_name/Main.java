package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Character ch;
    public static OrthographicCamera camera;
    private Texture background;
    private List<Blupy> enemies;

    // Camera control
    private static final float CAMERA_ZOOM = 0.75f;
    private static final float CAMERA_SPEED = 0.1f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ch = new Character();
        enemies = new ArrayList<>();

        enemies.add(new Blupy());

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = CAMERA_ZOOM;

        // Background setup
        background = new Texture("back.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Smooth camera follow
        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(
                ch.sprite.getX() + ch.sprite.getWidth()/2,
                ch.sprite.getY() + ch.sprite.getHeight()/2,
                0
            ),
            CAMERA_SPEED
        );
        camera.update();

        handleInput();
        ch.update();

        batch.setProjectionMatrix(camera.combined);
        draw();
    }

    private void draw() {
        batch.begin();
        drawBackground();
        ch.sprite.draw(batch);
        for(Blupy blupy : enemies) {
            blupy.sprite.draw(batch);
        }
        batch.end();
    }

    private void drawBackground() {
        float bgX = camera.position.x - camera.viewportWidth/2 * camera.zoom;
        float bgY = camera.position.y - camera.viewportHeight/2 * camera.zoom;
        float bgWidth = camera.viewportWidth * camera.zoom;
        float bgHeight = camera.viewportHeight * camera.zoom;

        float u = bgX / background.getWidth();
        float v = bgY / background.getHeight();
        float u2 = u + bgWidth / background.getWidth();
        float v2 = v + bgHeight / background.getHeight();

        batch.draw(background,bgX, bgY, bgWidth, bgHeight, u, v, u2, v2);
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) ch.dashToMouse.jumpToMouse();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) ch.moveUp(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) ch.moveDown(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) ch.moveLeft(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) ch.moveRight(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.G)) enemies.add(new Blupy());

        if (Gdx.input.isKeyPressed(Input.Keys.PLUS)) camera.zoom *= 0.99f;
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) camera.zoom *= 1.01f;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        ch.sprite.getTexture().dispose();
    }
}
