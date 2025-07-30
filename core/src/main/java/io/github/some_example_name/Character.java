package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Character {
    final DashToMouse dashToMouse;
    CircleAttack circleAttack = new CircleAttack(this);
    float moveSpeed = 500;
    Sprite sprite;

    public Character() {
        sprite = new Sprite(new Texture("block.png"));
        sprite.setOriginCenter();
        dashToMouse = new DashToMouse(this);
    }

    public void update() {
        dashToMouse.update();
        circleAttack.update();
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

    public float centerX(){
        return sprite.getX() + sprite.getWidth()/2f;
    }

    public float centerY(){
        return sprite.getY() + sprite.getHeight()/2f;
    }
}
