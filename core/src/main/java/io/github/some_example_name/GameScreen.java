package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

public class GameScreen implements Screen {
    public static boolean onKillResetAttack;
    public static boolean onKillResetDash;
    private SpriteBatch batch;
    private Character ch;
    public static OrthographicCamera camera;
    private Texture background;
    public static List<Entity> enemies;
    public static List<Entity> terrains;
    public static List<Entity> projectiles;
    public static List<Entity> friendlyProjectiles;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    Music music;

    private static final float CAMERA_ZOOM = 0.75f;
    private static final float CAMERA_SPEED = 0.1f;
    public static Random random;
    private boolean gridActive;
    public Main main;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont(); // uses default font

        ch = new Character(200f);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        friendlyProjectiles = new ArrayList<>();
        terrains = new ArrayList<>();

        random = new Random();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = CAMERA_ZOOM;

        background = new Texture("back.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        shapeRenderer = new ShapeRenderer();

        music = Gdx.audio.newMusic(Gdx.files.internal("kirby song.mp3"));
        music.setLooping(true);
//        music.play();

    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        draw();
        if(ch.markAsDeleted) {
            music.stop();
            main.setScreen(new DeathScreen(main));
        }
        ch.update();
        updateEntityList(enemies);
        updateEntityList(projectiles);
        updateEntityList(friendlyProjectiles);
        updateEntityList(terrains);

        garbageRemoveList(enemies);
        garbageRemoveList(friendlyProjectiles);
        garbageRemoveList(terrains);


        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Entity entity = projectiles.get(i);
            if (entity.markAsDeleted) {
                projectiles.remove(entity);
                entity.cleanUpAfterDeletion();
            }
        }

        for (Entity entity : enemies) {
            if(entity.updateHurtBox().overlaps(ch.updateHurtBox())){
                ch.gotHit();
            }
        }

        for (Entity projectile : projectiles) {
            if(projectile.updateHurtBox().overlaps(ch.updateHurtBox())){
                projectile.markAsDeleted = true;
                ch.gotHit();
                ch.slimes.add(new Timer(0.7f));
            }
        }

        for(Entity terrain: terrains) {
            for (Entity projectile : projectiles) {
                if (projectile.updateHurtBox().overlaps(terrain.updateHurtBox())) {
                    projectile.markAsDeleted = true;
                    terrain.gotHit(new Vector2(),0, 10);
                }
            }
        }

        for(Entity friendlyProjectile: friendlyProjectiles) {
            for (Entity enemy : enemies) {
                if (friendlyProjectile instanceof Explosion ex && Intersector.overlaps(ex.updateHurtBoxCircle(), enemy.updateHurtBox())) {
                    enemy.gotHit(new Vector2(),0, 10);
                }
                else if (friendlyProjectile instanceof Projectile && enemy.updateHurtBox().overlaps(friendlyProjectile.updateHurtBox())) {
                    enemy.gotHit(new Vector2(),0, 10f);
                    friendlyProjectile.markAsDeleted = true;
                }


            }
        }

        ifCircleAttacksExecuteThis(enemies,(Entity entity)-> entity.gotHit(new Vector2(ch.centerX(),ch.centerY()),30f, 10f));
        ifCircleAttacksExecuteThis(terrains, (Entity entity)-> entity.gotHit(new Vector2(), 0f,10f));
        ifCircleAttacksExecuteThis(projectiles ,(Entity entity)-> {
            entity.gotHit(new Vector2(),0f, 10f);
            if(GameScreen.onKillResetAttack)
                ch.circleAttack.cooldown.finish();
            if(GameScreen.onKillResetDash)
                ch.dashToMouse.cooldown.finish();
            }
        );
        ifCircleAttacksExecuteThis(friendlyProjectiles ,(Entity entity)->{
            entity.gotHit(new Vector2(ch.centerX(),ch.centerY()),30f, 10f);
            ch.dashToMouse.cooldown.finish();
            ch.circleAttack.cooldown.finish();
        });

        for(Entity terrain: terrains) {
            for (Entity projectile : projectiles) {
                if (projectile.updateHurtBox().overlaps(terrain.updateHurtBox())) {
                    projectile.markAsDeleted = true;
                    terrain.gotHit(new Vector2(),0, 10f);
                }
            }
        }
        for (int i = friendlyProjectiles.size()-1; i >= 0; i--) {
            Entity projectile = friendlyProjectiles.get(i);
            for (int j = enemies.size()-1; j >= 0; j--) {
                Entity enemy = enemies.get(j);
                if(projectile.updateHurtBox().overlaps(enemy.updateHurtBox())) {
                    if(projectile instanceof Shell shell && shell.isMoving) {
                        enemy.gotHit(new Vector2(ch.centerX(), ch.centerY()), 10f, 2f);
                        if(shell.moveSpeed > 17f) {
                            shell.markAsDeleted = true;
                            friendlyProjectiles.add(new Explosion(0,ch, new Vector2(shell.centerX(), shell.centerY())));
                        }
                    }
                }
            }
        }

        updateCamera();

        handleInput();

        drawGridLines();
        drawHUD();

//        drawDebugLines();
    }

    private void drawDebugLines() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        if(!friendlyProjectiles.isEmpty()){
            Entity entity = friendlyProjectiles.get(0);

            shapeRenderer.circle(entity.centerX() , entity.centerY(), 150);
        }
        shapeRenderer.end();
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

    private void garbageRemoveList(List<Entity> entities) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            if (entity.markAsDeleted) {
                entities.remove(entity);
            }
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawBackground();

        for (Entity entity : enemies) {
            if(entity instanceof Zimmer zimmer) {
                  batch.draw(zimmer.animation.getKeyFrame(zimmer.animationTimer.currentTimer), zimmer.centerX() - 128, zimmer.centerY() - 128,
                    256, 256);
            }
            entity.sprite.draw(batch);
        }
        for (Entity entity : friendlyProjectiles) {
            if(entity instanceof Explosion ex) {
                batch.draw(ex.animation.getKeyFrame(ex.animationTimer.currentTimer), ex.centerX() - 450, ex.centerY() - 180,
                    900, 360);
            }
            else
                entity.sprite.draw(batch);
        }

        drawEntityList(terrains);
        drawEntityList(projectiles);
        drawEntityList(friendlyProjectiles);

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
            font.draw(batch, String.valueOf(ch.health), 960, 170);
            font.draw(batch, "Shells " + ch.shells, 100, 120);

        }
        batch.end();
    }

    private void drawEntityList(List<Entity> entities) {
        for (Entity entity : entities) {
            if(entity instanceof Explosion) continue;

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

                enemies.add(new Blurpy(new Vector2(x, y),1f, ch));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            enemies.add(new Zimmer(new Vector2(),2f, ch));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            if(ch.shells > 0) {
                ch.shells--;
                float x = GameScreen.random.nextFloat(800);
                float y = GameScreen.random.nextFloat(800);
                x -= 400;
                y -= 400;

                friendlyProjectiles.add(new Shell(new Vector2(ch.centerX() + x, ch.centerY() + y), 2f, ch));
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
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            ch.circleAttack.activate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            main.setScreen(new MenuScreen(main));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET)) camera.zoom *= 0.99f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET)) camera.zoom *= 1.01f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) onKillResetAttack = true;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) onKillResetDash = true;

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) enemies.add(new Booper(new Vector2(),0,ch) );

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        ch.sprite.getTexture().dispose();
    }
}
