package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Zimmer extends Entity{
    Animation<TextureRegion> animation;
    Timer animationTimer = new Timer(1f);
    Cooldown attackSpeed = new Cooldown(1f);

    public Zimmer(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("zimmer.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 6f;
        this.health = 30f;

        TextureRegion[][] tmp = TextureRegion.split(new Texture("zimmer_attack.png"), 256, 256);

        TextureRegion[] walkFrames = new TextureRegion[5];
        System.arraycopy(tmp[0], 0, walkFrames, 0, 5);

        animation = new Animation<>(0.2f, walkFrames);
    }

    @Override
    public void update() {
        super.update();
        animationTimer.update();
        hurtCooldown.handleUpdateAndUnFlagging();
        attackSpeed.handleUpdateAndUnFlagging();
        attack();
        handleHitbox();
    }

    public void handleHitbox(){
        if(animationTimer.isFlagged()) {
            animationTimer.update();

            if(!animationTimer.isValid()) {
                animationTimer.unflag();
                animationTimer.reset();
            }
        }
    }

    private void attack() {
        if(!attackSpeed.isFlagged()){
            attackSpeed.flag();
            animationTimer.flag();

        }
    }
}
