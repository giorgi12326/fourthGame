package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Booper extends Entity {

    Cooldown attackSpeed = new Cooldown(1f);
    int state = 0;
    int rotationDirection;
    float rotationDistance;

    public Booper(Vector2 position, float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        rotationDirection = GameScreen.random.nextInt(2);
        rotationDistance = GameScreen.random.nextInt(200) + 300;

        sprite = new Sprite(new Texture("booper.png"));
        sprite.setPosition(position.x,position.y);
        this.moveSpeed = 4f;
        this.health = 20f;
    }

    @Override
    public void update() {
        hurtCooldown.handleUpdateAndUnFlagging();
        attackSpeed.handleUpdateAndUnFlagging();
        shoot();
        movement();
    }

    private void movement() {
        float len = new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).len();
        if(len < rotationDistance)
            state = 1;
        else if(len > 700)
            state = 0;

        if(state == 1)
            movePerpendicularFromCharacter();
        else
            moveTowardsCharacter();
    }

    public void movePerpendicularFromCharacter(){
        Vector2 scl = new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).nor().scl(moveSpeed);
        Vector2 perpendicular;
        if(rotationDirection == 0)
            perpendicular = new Vector2(scl.y,-scl.x);
        else
            perpendicular = new Vector2(-scl.y,scl.x);
        moveEachDirectionIfCan(perpendicular);
    }

    private void shoot() {
        if(!attackSpeed.isFlagged()){
            GameScreen.projectiles.add(new Projectile(new Vector2(sprite.getX(), sprite.getY()), new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).nor(), ch));
            attackSpeed.flag();
        }
    }
}
