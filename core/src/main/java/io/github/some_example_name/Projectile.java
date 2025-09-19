package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Entity {
    Vector2 direction;
    boolean parryable;
    boolean turned = false;

    public Projectile(Vector2 position, Vector2 direction, Character ch){
        super(500f, ch);
        sprite = new Sprite(new Texture("booper_bullet.png"));
        this.sprite.setX(position.x);
        this.sprite.setY(position.y);
        direction.scl(moveSpeed * Gdx.graphics.getDeltaTime());
        this.direction = direction;

        randomParry();

    }

    public void update(){
        sprite.translate(direction.x, direction.y);
    }

    private void randomParry() {
        int i = GameScreen.random.nextInt(10);
        if(i < 3) {
            sprite.setColor(1.5f, 0f, 1.5f, 1f);
            parryable = true;
        }
    }

    @Override
    public void gotHit(Vector2 vector2, float impact, float damage) {
        if (!hurtCooldown.isFlagged()) {
            hurtCooldown.flag();
            if(parryable){
                direction = new Vector2(-direction.x, -direction.y);
                turned = true;
            }
            markAsDeleted = true;

            if (GameScreen.onKillResetAttack)
                ch.circleAttack.cooldown.finish();
            if (GameScreen.onKillResetDash)
                ch.dashToMouse.cooldown.finish();
        }
    }

    @Override
    public void cleanUpAfterDeletion() {
        if(parryable && turned) {
            GameScreen.friendlyProjectiles.add(this);
            markAsDeleted = false;
        }
    }
}
