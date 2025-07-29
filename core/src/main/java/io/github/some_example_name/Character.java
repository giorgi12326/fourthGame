package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Character {
    final DashToMouse dashToMouse;
    float moveSpeed = 500;
    Sprite sprite;

    public Character() {
        sprite = new Sprite(new Texture("block.png"));
        sprite.setOriginCenter();
        dashToMouse = new DashToMouse(this);
    }

    public void update() {
        dashToMouse.update();
    }

    public void moveRight(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged())
            sprite.translateX(moveSpeed * delta);
    }

    public void moveLeft(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged())
            sprite.translateX(-moveSpeed * delta);
    }

    public void moveUp(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged())
            sprite.translateY(moveSpeed * delta);
    }

    public void moveDown(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged())
            sprite.translateY(-moveSpeed * delta);
    }
}
