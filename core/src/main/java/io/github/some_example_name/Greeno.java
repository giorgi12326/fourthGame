package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
public class Greeno extends Entity{
    public Greeno(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("greeno.png"));
        sprite.setPosition(position.x,  position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 5f;
        this.health = 50f;
    }

    @Override
    public void update() {
        hurtCooldown.handleUpdateAndUnFlagging();
    }

    public void gotHit(Vector2 vector2, float impact, float damage) {
        if(!hurtCooldown.isFlagged()) {
            hurtCooldown.flag();
            health -= damage;
            ch.wood +=10f;
            if (health <= 0)
                markAsDeleted = true;
        }
    }
}
