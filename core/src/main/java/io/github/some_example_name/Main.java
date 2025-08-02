package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Character ch;
    public static OrthographicCamera camera;
    private Texture background;
    public static List<Entity> enemies;
    public static List<Entity> terrains;
    public static List<Entity> projectiles;
    ShapeRenderer shapeRenderer;
    BitmapFont font;

    private static final float CAMERA_ZOOM = 0.75f;
    private static final float CAMERA_SPEED = 0.1f;
    public static Random random;
    private boolean gridActive;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // uses default font

        ch = new Character(300f);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        terrains = new ArrayList<>();

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

        draw();
        if(ch.markAsDeleted) return;
        ch.update();
        updateEntityList(enemies);
        updateEntityList(projectiles);
        updateEntityList(terrains);

        garbageRemoveList(enemies);
        garbageRemoveList(projectiles);
        garbageRemoveList(terrains);

        for (Entity entity : enemies) {
            if(entity.updateHurtBox().overlaps(ch.updateHurtBox())){
                ch.gotHit();
            }
        }

        for (Entity projectile : projectiles) {
            if(projectile.updateHurtBox().overlaps(ch.updateHurtBox())){
                projectile.markAsDeleted = true;
                ch.gotHit();
            }
        }

        for(Entity terrain: terrains) {
            for (Entity projectile : projectiles) {
                if (projectile.updateHurtBox().overlaps(terrain.updateHurtBox())) {
                    projectile.markAsDeleted = true;
                    terrain.gotHit(new Vector2(),0);
                }
            }
        }

        ifCircleAttacksExecuteThis(enemies,(Entity entity)-> entity.gotHit(new Vector2(ch.centerX(),ch.centerY()),30f));
        ifCircleAttacksExecuteThis(terrains, (Entity entity)-> entity.gotHit(new Vector2(),0f));
        ifCircleAttacksExecuteThis(projectiles ,(Entity entity)-> {
            entity.gotHit(new Vector2(),0f);
            ch.dashToMouse.cooldown.finish();
            ch.circleAttack.cooldown.finish();
            }
        );


        updateCamera();

        handleInput();

        drawGridLines();
        drawHUD();
    }

    private void drawHUD() {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (ch.circleAttack.cooldown.isValid())
            shapeRenderer.rect(30, 30, 50, 50);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(400, 100, 11.2f * ch.health, 50);


        shapeRenderer.end();
    }

    private void drawGridLines() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float modX =  ((int) (ch.sprite.getX() / 64)) * 64;
        float modY =  ((int) (ch.sprite.getY() / 64)) * 64;

        if(gridActive) {
            shapeRenderer.setColor(Color.WHITE);
            for (int i = 0; i < 20; i++) {
                shapeRenderer.line(modX + (10-i)*64, modY - 20*64,modX + (10-i)*64 , modY+20*64);
            }
            for (int i = 0; i < 10; i++) {
                shapeRenderer.line(modX - (20)*64, modY + (5-i)*64,modX+20*64 , modY + (5-i)*64);
            }
        }
        shapeRenderer.end();
    }

    private void updateCamera() {
        camera.position.lerp(
            new Vector3(
                ch.sprite.getX() + ch.sprite.getWidth()/2,
                ch.sprite.getY() + ch.sprite.getHeight()/2,
                0
            ),
            CAMERA_SPEED
        );
        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);

        camera.update();
    }

    private static void updateEntityList(List<Entity> list) {
        for (Entity entity : list) {
            entity.update();
        }
    }

    private void ifCircleAttacksExecuteThis(List<Entity> list, Consumer<Entity> consumer) {
        for (Entity entity : list) {
            if(ch.circleAttack.durationTimer.isFlagged() && Intersector.overlaps(ch.circleAttack.circle, entity.updateHurtBox()))
                consumer.accept(entity);
        }
    }

    private static void garbageRemoveList(List<Entity> enemies) {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Entity entity = enemies.get(i);
            if (entity.markAsDeleted)
                enemies.remove(entity);
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawBackground();

        drawEntityList(enemies);
        drawEntityList(terrains);
        drawEntityList(projectiles);

        if(!ch.markAsDeleted) {
            ch.sprite.draw(batch);

            if (ch.circleAttack.durationTimer.isFlagged()) {
                if (ch.circleAttack.clockwise)
                    batch.draw(ch.circleAttack.animationClockwise.getKeyFrame(ch.circleAttack.animationTimer.currentTimer), ch.centerX() - 128, ch.centerY() - 128,
                        256, 256);
                else {
                    batch.draw(ch.circleAttack.animationCounterClockwise.getKeyFrame(ch.circleAttack.animationTimer.currentTimer), ch.centerX() - 128, ch.centerY() - 128,
                        256, 256);
                }
            }

            batch.end();

            batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            batch.begin();
            font.draw(batch, String.valueOf(ch.wood), 30, 120);
        }
        batch.end();
    }

    private void drawEntityList(List<Entity> enemies) {
        for (Entity entity : enemies) {
            entity.sprite.draw(batch);
        }
    }

    private void drawBackground() {
        float bgX = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
        float bgY = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
        float bgWidth = camera.viewportWidth * camera.zoom;
        float bgHeight = camera.viewportHeight * camera.zoom;

        float u = bgX / background.getWidth();
        float v = bgY / background.getHeight();
        float u2 = u + bgWidth / background.getWidth();
        float v2 = v + bgHeight / background.getHeight();

        batch.draw(background, bgX, bgY, bgWidth, bgHeight, u, v, u2, v2);

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            float radius = 1000;
            int numEnemies = 6;
            Vector2 center = new Vector2(ch.centerX(), ch.centerY());

            for (int i = 0; i < numEnemies; i++) {
                float angle = (float)(2 * Math.PI * i / numEnemies);
                float x = center.x + radius * (float)Math.cos(angle);
                float y = center.y + radius * (float)Math.sin(angle);

                terrains.add(new Greeno(new Vector2(x, y),2f, ch));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            gridActive = !gridActive;
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if(gridActive && ch.wood >=10) {
                ch.wood -= 10;
                Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(screenCoords);

                float snappedX = ((int) (screenCoords.x / 64)) * 64;
                float snappedY = ((int) (screenCoords.y / 64)) * 64;

                terrains.add(new Block(new Vector2(snappedX, snappedY), 10f, ch));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) terrains.add(new Greeno(new Vector2(), 0f,ch));
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
