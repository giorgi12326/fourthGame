package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Blurpy extends Enemy{

    public Blurpy(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("chara.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 5f;
        this.health = 30f;
    }
}
