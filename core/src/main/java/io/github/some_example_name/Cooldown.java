package io.github.some_example_name;

import com.badlogic.gdx.Gdx;

public class Cooldown {
    float currentTimer;
    float duration;
    private boolean flag;
    public Cooldown(float duration) {
        this.duration = duration;
        currentTimer = duration;
    }

    public void update() {
        currentTimer -= Gdx.graphics.getDeltaTime();
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
        return currentTimer <= 0;
    }

    public void reset() {
        currentTimer = duration;
    }

    public void handleUpdateAndFlagging() {
        if(isFlagged()) {
            update();
            if(isValid()) {
                reset();
                unflag();
            }
        }
    }
}
