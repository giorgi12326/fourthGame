package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Wood extends Entity{
    public Wood(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("greeno.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 5f;
        this.health = 100f;
    }

    @Override
    public void moveTowardsCharacter() {

    }

    @Override
    public void gotHit(Vector2 vector2, float impact) {
        super.gotHit(vector2, impact);
    }
}
