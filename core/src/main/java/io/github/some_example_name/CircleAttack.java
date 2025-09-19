package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;


public class CircleAttack {
    Circle circle;
    public float radius = 100f;
    Cooldown cooldown = new Cooldown(1f);
    Timer durationTimer = new Timer(0.1f);
    Character ch;
    Animation<TextureRegion> animationClockwise;
    Animation<TextureRegion> animationCounterClockwise;
    Timer animationTimer = new Timer(0.1f);
    float rotation = 0f;
    boolean clockwise = true;
    Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("attack_swoosh.mp3"));

    public CircleAttack(Character ch){
        this.ch = ch;
        circle = new Circle();
        TextureRegion[][] tmp = TextureRegion.split(new Texture("circleAttack.png"), 32, 32);

        TextureRegion[] walkFrames = new TextureRegion[3];
        System.arraycopy(tmp[0], 0, walkFrames, 0, 3);

        animationClockwise = new Animation<>(0.03f, walkFrames);

        TextureRegion[] flippedFrames = new TextureRegion[3];
        for (int i = 0; i < walkFrames.length; i++) {
            flippedFrames[i] = new TextureRegion(walkFrames[i]);
            flippedFrames[i].flip(true, false);
        }

        animationCounterClockwise = new Animation<>(0.03f, flippedFrames);

    }

    public void update() {
        cooldown.update();
        handleHitbox();
    }

    public void activate(){
        if(cooldown.isValid() && !durationTimer.isFlagged()) {
            durationTimer.flag();
            playAttackSound();
            clockwise = !clockwise;
            cooldown.reset();
        }
    }

    private void playAttackSound() {
        float pitch = 0.9f + GameScreen.random.nextFloat() * 0.2f;
        attackSound.play(1.0f, pitch + 0.9f, 0f);
    }

    public void handleHitbox(){
        if(durationTimer.isFlagged()) {
            durationTimer.update();
            animationTimer.update();

            circle.set(ch.centerX(), ch.centerY(), radius);
            rotateCh();
            if(!durationTimer.isValid()) {
                durationTimer.unflag();
                durationTimer.reset();
                animationTimer.reset();
                rotation = 0;
            }
        }
    }

    private void rotateCh() {
        float rotation1 = 90 / (1 / Gdx.graphics.getDeltaTime() * durationTimer.endTimer);
        if(clockwise)
            rotation += rotation1;
        else
            rotation -= rotation1;

        if (rotation >= 90) {
            rotation = 90;
        }
        if (rotation <= -90) {
            rotation = -90;
        }
        ch.sprite.setRotation(rotation);
    }
}
