package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Booper extends Enemy{
    Cooldown attackSpeed = new Cooldown(1f);
    public Booper(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("booper.png"));
        this.moveSpeed = 5f;
        this.health = 30f;
    }

    @Override
    public void update() {
        super.update();
        attackSpeed.handleUpdateAndUnFlagging();

        shoot();
    }

    private void shoot() {
        if(!attackSpeed.isFlagged()){
            Main.projectiles.add(new Projectile(new Vector2(sprite.getX(), sprite.getY()), new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).nor(), ch));
            attackSpeed.flag();
        }
    }
}
