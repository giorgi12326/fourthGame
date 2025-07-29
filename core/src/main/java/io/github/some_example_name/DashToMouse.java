package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DashToMouse {
    private final Character ch;
    float jumpDistance = 30f;
    Vector2 direction;

    public Timer jumpTimer = new Timer(0.2f);
    Cooldown jumpCooldown = new Cooldown(0.5f);

    public DashToMouse(Character ch) {
        this.ch = ch;
    }

    public void update() {
        handleJump();
        jumpCooldown.update();
    }

    public void jumpToMouse() {
        if (jumpCooldown.isValid() && !jumpTimer.isFlagged()) {
            jumpTimer.flag();
            jumpCooldown.reset();
            Vector3 project = Main.camera.project(new Vector3(centerX(), centerY(), 0));

            float mouseX = Gdx.input.getX() - project.x;
            float mouseY = 1080 - project.y - Gdx.input.getY();

            direction = new Vector2(mouseX, mouseY);
            direction.nor();
            float scalar = Math.min(jumpDistance, new Vector2(mouseX, mouseY).len()/8);
            direction.scl(scalar);

        }
    }

    public void handleJump() {
        if (jumpTimer.isFlagged()) {
            ch.sprite.translateX(direction.x);
            ch.sprite.translateY(direction.y);
            jumpTimer.update();
            if (!jumpTimer.isValid()) {
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
