package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Explosion extends Entity {
    Animation<TextureRegion> animation;
    Timer animationTimer = new Timer(1f);
    Circle hurtBox = new Circle();

    public Explosion(float moveSpeed, Character ch, Vector2 position) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("zimmer.png"));
        sprite.setOriginCenter();
        sprite.setPosition(position.x, position.y);

        TextureRegion[][] tmp = TextureRegion.split(new Texture("zimmer_attack.png"), 256, 256);
        TextureRegion[] walkFrames = new TextureRegion[5];
        System.arraycopy(tmp[0], 0, walkFrames, 0, 5);
        animation = new Animation<>(0.2f, walkFrames);

        animationTimer.flag();
    }


    @Override
    public void update() {
        handleTimer();
    }

    public Circle updateHurtBoxCircle() {
        hurtBox.set(sprite.getX(), sprite.getY(),96f);
        return hurtBox;
    }

    private void handleTimer() {
        if(animationTimer.isFlagged()) {
            animationTimer.update();
            if(!animationTimer.isValid()) {
                markAsDeleted = true;
                animationTimer.reset();
                animationTimer.unflag();
            }
        }
    }
}
