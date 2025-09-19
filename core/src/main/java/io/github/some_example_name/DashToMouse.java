package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DashToMouse {
    private final Character ch;
    float jumpDistance = 30f;
    Vector2 direction;
    Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("jumpSound.mp3"));

    public Timer jumpTimer = new Timer(0.2f);
    Cooldown cooldown = new Cooldown(0.5f);

    public DashToMouse(Character ch) {
        this.ch = ch;
    }

    public void update() {
        handleJump();
        cooldown.update();
    }

    public void jumpToMouse() {
        if (cooldown.isValid() && !jumpTimer.isFlagged()) {
            playAttackSound();
            if(GameScreen.onKillResetAttack)
                ch.circleAttack.cooldown.finish();
            if(GameScreen.onKillResetDash)
                ch.dashToMouse.cooldown.finish();
            jumpTimer.flag();
            ch.invincible = true;
            cooldown.reset();
            Vector3 project = GameScreen.camera.project(new Vector3(centerX(), centerY(), 0));

            float mouseX = Gdx.input.getX() - project.x;
            float mouseY = Gdx.graphics.getHeight() - project.y - Gdx.input.getY();

            direction = new Vector2(mouseX, mouseY);
            direction.nor();
            float scalar = Math.min(jumpDistance, new Vector2(mouseX, mouseY).len()/8);
            direction.scl(scalar);

        }
    }
    private void playAttackSound() {
        float pitch = 0.9f + GameScreen.random.nextFloat() * 0.2f;
        jumpSound.play(1.0f, pitch, 0f);
    }


    public void handleJump() {
        if (jumpTimer.isFlagged()) {
            boolean shouldReturnX = false;

            ch.sprite.translateX(direction.x);
            for(Entity entity : GameScreen.terrains){
                if(entity != ch && entity.updateHurtBox().overlaps(ch.updateHurtBox()))
                    shouldReturnX = true;
            }

            if(shouldReturnX)
                ch.sprite.translateX(-direction.x);

            boolean shouldReturnY = false;

            ch.sprite.translateY(direction.y);
            for(Entity entity : GameScreen.terrains){
                if(entity != ch && entity.updateHurtBox().overlaps(ch.updateHurtBox()))
                    shouldReturnY = true;
            }

            if(shouldReturnY)
                ch.sprite.translateY(-direction.y);
            jumpTimer.update();
            if (!jumpTimer.isValid()) {
                ch.invincible = false;
                jumpTimer.unflag();
                jumpTimer.reset();
            }
        }
    }

    public float centerX(){
        return ch.sprite.getX() + ch.sprite.getWidth()/2;
    }

    public float centerY(){
        return ch.sprite.getY() + ch.sprite.getHeight()/2;
    }
}
