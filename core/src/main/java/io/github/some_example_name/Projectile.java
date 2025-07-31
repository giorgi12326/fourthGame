package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Entity {
    Vector2 direction;
    public Projectile(Vector2 position, Vector2 direction, Character ch){
        super(500f, ch);
        sprite = new Sprite(new Texture("booper_bullet.png"));
        this.sprite.setX(position.x);
        this.sprite.setY(position.y);
        direction.scl(moveSpeed * Gdx.graphics.getDeltaTime());
        this.direction = direction;
    }

    public void update(){
        sprite.translate(direction.x, direction.y);
    }
}
