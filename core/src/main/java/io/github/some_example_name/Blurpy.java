package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Blurpy extends Entity {
    Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurtSound.mp3"));

    public Blurpy(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("chara.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 4f;
        this.health = 30f;
    }

    @Override
    public void update() {
        super.update();
        if(markAsDeleted)
            playHurtSound();

    }

    private void playHurtSound() {
        float pitch = 0.9f + GameScreen.random.nextFloat() * 0.2f;
        hurtSound.play(0.6f, pitch, 0f);
    }

}
