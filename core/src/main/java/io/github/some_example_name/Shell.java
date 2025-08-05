package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Shell extends Entity{
    Vector2 direction = new Vector2();
    public boolean isMoving = false;
    public Shell(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("shell.png"));
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        this.moveSpeed = 6f;
        this.health = 3000f;
    }

    @Override
    public void moveTowardsCharacter() {
        sprite.translate(direction.x, direction.y);
    }

    public void gotHit(Vector2 vector2, float impact) {
        if(!hurtCooldown.isFlagged()) {
            hurtCooldown.flag();

            knockback(vector2, impact);

            if(Main.onKillResetAttack)
                ch.circleAttack.cooldown.finish();
            if(Main.onKillResetDash)
                ch.dashToMouse.cooldown.finish();
        }
    }

    @Override
    public void knockback(Vector2 vector2, float impact) {
        Vector2 toCharacter = new Vector2(ch.centerX() - centerX(), ch.centerY() - centerY()).nor();

        Vector2 moveDir = direction.cpy().nor();

        float dot = moveDir.dot(toCharacter);

        if(dot >= 0 && !direction.isZero()) {
            direction = new Vector2();
            moveSpeed = 6;
            isMoving = false;
        }
        else{
            direction = new Vector2(centerX() - vector2.x, centerY() - vector2.y).nor().scl(moveSpeed);
            moveSpeed += 6;
            isMoving = true;
        }

    }
}
