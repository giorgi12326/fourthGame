package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Blupy {
    Sprite sprite;

    public Blupy() {
        sprite = new Sprite(new Texture("chara.png"));
        sprite.setOriginCenter();
    }
}
