package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Zimmer extends Entity{
    public Zimmer(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("zimmer.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 6f;
        this.health = 30f;
    }
}
