package io.github.some_example_name;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Block extends Entity {

    public Block(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("placed_block.png"));
        sprite.setPosition(position.x, position.y);
        this.health = 50f;
    }

    @Override
    public void update() {
        hurtCooldown.handleUpdateAndUnFlagging();

    }
}
