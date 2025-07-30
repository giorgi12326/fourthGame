package io.github.some_example_name;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;


public class CircleAttack {
    Circle circle;
    public float radius = 100f;
    Cooldown cooldown = new Cooldown(1f);
    Timer durationTimer = new Timer(0.1f);
    Character ch;

    public CircleAttack(Character ch){
        this.ch = ch;
        circle = new Circle();
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
            circle.set(ch.centerX(), ch.centerY(), radius);
            if(!durationTimer.isValid()) {
                durationTimer.unflag();
                durationTimer.reset();
            }

        }
    }
}
