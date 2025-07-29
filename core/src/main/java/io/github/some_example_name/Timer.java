package io.github.some_example_name;

import com.badlogic.gdx.Gdx;

public class Timer {
    float currentTimer = 0f;
    float endTimer;
    private boolean flag = false;

    public Timer(float duration) {
        endTimer = duration;
    }

    public void update() {
        currentTimer += Gdx.graphics.getDeltaTime();
    }

    public boolean isFlagged() {
        return flag;
    }

    public void flag() {
        flag = true;
    }

    public void unflag() {
        flag = false;
    }

    public boolean isValid() {
        return endTimer - currentTimer > 0;
    }

    public void reset() {
        currentTimer = 0f;
    }
}
