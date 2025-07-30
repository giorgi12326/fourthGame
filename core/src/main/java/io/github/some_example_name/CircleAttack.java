package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;


public class CircleAttack {
    Circle circle;
    public float radius = 100f;
    Cooldown cooldown = new Cooldown(1f);
    Timer durationTimer = new Timer(0.1f);
    Character ch;
    Animation<TextureRegion> animation;
    Timer animationTimer = new Timer(0.1f);

    public CircleAttack(Character ch){
        this.ch = ch;
        circle = new Circle();

        TextureRegion[][] tmp = TextureRegion.split(new Texture("circleAttack.png"), 32, 32);

        TextureRegion[] walkFrames = new TextureRegion[3];

        for (int i = 0; i < 3; i++) {
            walkFrames[i] = tmp[0][i];
        }

        animation = new Animation<>(0.03f, walkFrames);
    }

    public void update() {
        cooldown.update();
        handleHitbox();
    }

    public void activate(){
        if(cooldown.isValid() && !durationTimer.isFlagged()) {
            durationTimer.flag();
            cooldown.reset();
        }
    }

    public void handleHitbox(){
        if(durationTimer.isFlagged()) {
            durationTimer.update();
            animationTimer.update();

            circle.set(ch.centerX(), ch.centerY(), radius);
            if(!durationTimer.isValid()) {
                durationTimer.unflag();
                durationTimer.reset();
                animationTimer.reset();
            }

        }
    }
}
