package io.github.some_example_name;

import com.badlogic.gdx.Gdx;

public class Cooldown {
    float currentTimer;
    float duration;

    public Cooldown(float duration) {
        this.duration = duration;
        currentTimer = duration;
    }

    public void update() {
        currentTimer -= Gdx.graphics.getDeltaTime();
    }

    public boolean isValid() {
        return currentTimer <= 0;
    }

    public void reset() {
        currentTimer = duration;
    }
}
