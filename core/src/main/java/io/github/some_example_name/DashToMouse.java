package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class DashToMouse {
    private final Character ch;
    float jumpDistance = 30f;
    Vector2 direction;

    public DashToMouse(Character ch) {
        this.ch = ch;
    }

    Timer jumpTimer = new Timer(0.2f);

    public void update() {
        handleJump();
    }

    public void jumpToMouse() {
        if (!jumpTimer.isFlagged() && jumpTimer.isValid()) {
            jumpTimer.flag();
            float mouseX = Gdx.input.getX() - 960;
            float mouseY = 540 - Gdx.input.getY();

            direction = new Vector2(mouseX, mouseY);
            direction.nor();
            float scalar = Math.min(jumpDistance, new Vector2(mouseX, mouseY).len()/8);
            direction.scl(scalar);
            System.out.println(scalar);
            System.out.println(jumpDistance + " " +  new Vector2(mouseX, mouseY).len());

        }

        //  if (!jumpTimer.isFlagged() && jumpTimer.isValid()) {
        //            jumpTimer.flag();
        //            float mouseX = Gdx.input.getX() - 960;
        //            float mouseY = 540 - Gdx.input.getY();
        //
        //            direction = new Vector2(mouseX, mouseY);
        //            System.out.println( direction.dst(new Vector2(0,0)));
        //
        //            direction.nor();
        //            direction.scl(Math.min(jumpDistance, direction.dst(new Vector2(0,0))));
        //
        //        }
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
}
