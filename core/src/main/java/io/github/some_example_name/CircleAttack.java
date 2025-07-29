package io.github.some_example_name;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;


public class CircleAttack {
    Circle circle;
    public float radius = 100f;
    Cooldown cooldown = new Cooldown(1f);
    Character ch;

    public CircleAttack(Character ch){
        this.ch = ch;
        circle = new Circle();
    }

    public void overlapsThis(Blupy blupy){
        circle.set(ch.centerX(), ch.centerY() ,radius);
        if(cooldown.isValid() && Intersector.overlaps(circle, blupy.updateHurtBox())) {
            blupy.gotHit(new Vector2(ch.centerX(),ch.centerY()));
            cooldown.reset();
        }
    }

    public void update() {
        cooldown.update();
    }
}
