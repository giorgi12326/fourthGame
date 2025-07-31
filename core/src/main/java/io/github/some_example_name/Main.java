package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Character ch;
    public static OrthographicCamera camera;
    private Texture background;
    public static List<Enemy> enemies;
    public static List<Projectile> projectiles;
    ShapeRenderer shapeRenderer;

    private static final float CAMERA_ZOOM = 0.75f;
    private static final float CAMERA_SPEED = 0.1f;
    public static Random random;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ch = new Character();
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();

        random = new Random();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = CAMERA_ZOOM;

        background = new Texture("back.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(
                ch.sprite.getX() + ch.sprite.getWidth()/2,
                ch.sprite.getY() + ch.sprite.getHeight()/2,
                0
            ),
            CAMERA_SPEED
        );
        camera.update();
        for (Enemy enemy : enemies) {
            enemy.update();
            if(enemy.updateHurtBox().overlaps(ch.updateHurtBox()))
                ch.gotHit();
        }

        for (Projectile projectile : projectiles) {
            projectile.update();
            if(projectile.updateHurtBox().overlaps(ch.updateHurtBox())){
                projectile.markAsDeleted = true;
                ch.gotHit();
            }
        }

        for(Projectile projectile : projectiles) {
            projectile.update();
        }

        for (int i = enemies.size()-1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if(enemy.markAsDeleted)
                enemies.remove(enemy);
        }
        for (int i = projectiles.size()-1; i >= 0; i--) {
            Projectile enemy = projectiles.get(i);
            if(enemy.markAsDeleted)
                projectiles.remove(enemy);
        }

        for (Enemy enemy : enemies) {
            if(ch.circleAttack.durationTimer.isFlagged() && Intersector.overlaps(ch.circleAttack.circle, enemy.updateHurtBox()))
                enemy.gotHit(new Vector2(ch.centerX(),ch.centerY()),30f);
        }
        for (Projectile projectile : projectiles) {
            if(ch.circleAttack.durationTimer.isFlagged() && Intersector.overlaps(ch.circleAttack.circle, projectile.updateHurtBox())) {

            }
        }

        handleInput();
        ch.update();

        batch.setProjectionMatrix(camera.combined);
        draw();

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if(ch.circleAttack.cooldown.isValid())
            shapeRenderer.rect(30,30,50,50);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(400,100,11.2f*ch.health,50);

        shapeRenderer.end();

    }

    private void draw() {
        batch.begin();
        drawBackground();
        ch.sprite.draw(batch);
        if(ch.circleAttack.durationTimer.isFlagged())
            batch.draw(ch.circleAttack.animation.getKeyFrame(ch.circleAttack.animationTimer.currentTimer), ch.centerX()-128, ch.centerY()-128,
                256,256);

        for(Enemy enemy : enemies) {
            enemy.sprite.draw(batch);
        }
        for(Projectile projectile : projectiles) {
            projectile.sprite.draw(batch);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            float radius = 1000;
            int numEnemies = 12;
            Vector2 center = new Vector2(ch.centerX(), ch.centerY());

            for (int i = 0; i < numEnemies; i++) {
                float angle = (float)(2 * Math.PI * i / numEnemies);
                float x = center.x + radius * (float)Math.cos(angle);
                float y = center.y + radius * (float)Math.sin(angle);

                enemies.add(new Blurpy(new Vector2(x, y),2f, ch));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            float radius = 1000;
            int numEnemies = 12;
            Vector2 center = new Vector2(ch.centerX(), ch.centerY());

            for (int i = 0; i < numEnemies; i++) {
                float angle = (float)(2 * Math.PI * i / numEnemies);
                float x = center.x + radius * (float)Math.cos(angle);
                float y = center.y + radius * (float)Math.sin(angle);

                if(i%2 == 0)
                    enemies.add(new Blurpy(new Vector2(x, y),2f, ch));
                else
                    enemies.add(new Booper(new Vector2(x, y),2f, ch));
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) ch.moveUp(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) ch.moveDown(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) ch.moveLeft(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) ch.moveRight(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) enemies.add(new Booper(new Vector2(),10f, ch));
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            ch.circleAttack.activate();
        }

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
