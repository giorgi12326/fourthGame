package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Blupy {
    Sprite sprite;
    Rectangle hurtBox = new Rectangle();
    float fallback;
    public Blupy() {
        sprite = new Sprite(new Texture("chara.png"));
        sprite.setOriginCenter();
        fallback = 10f;
    }

    public Rectangle updateHurtBox() {
        hurtBox.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        return hurtBox;
    }

    public void gotHit(Vector2 vector2) {
        Vector2 nor = new Vector2(sprite.getX() - vector2.x, sprite.getY() - vector2.y).nor();
        nor.scl(fallback);
        sprite.translateX(nor.x);
        sprite.translateY(nor.y);
    }
}
