package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class
EntityDTO implements Serializable {
    int id;
    Vector2 position;

    public EntityDTO(int id, Vector2 position) {
        this.id = id;
        this.position = position;
    }
}
