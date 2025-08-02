package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public abstract class Entity {
    Sprite sprite;
    final Rectangle hurtBox = new Rectangle();
    float moveSpeed;
    Character ch;
    float health;
    Cooldown hurtCooldown = new Cooldown(0.1f);

    public boolean markAsDeleted;

    public Entity(float moveSpeed, Character ch) {
        this.ch = ch;
        this.moveSpeed = moveSpeed;
    }
    public void update(){
        moveTowardsCharacter();
        hurtCooldown.handleUpdateAndUnFlagging();
    }

    public Rectangle updateHurtBox() {
        hurtBox.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        return hurtBox;
    }

    public void gotHit(Vector2 vector2, float impact) {
        if(!hurtCooldown.isFlagged()) {
            hurtCooldown.flag();

            knockback(vector2, impact);
            health -= 10f;

            if (health <= 0) {
                ch.circleAttack.cooldown.finish();
                ch.dashToMouse.cooldown.finish();
                markAsDeleted = true;
            }
        }
    }

    public void knockback(Vector2 vector2, float impact) {
        Vector2 scl = new Vector2(sprite.getX() - vector2.x, sprite.getY() - vector2.y).nor().scl(impact);
        moveEachDirectionIfCan(scl);
    }

    public void moveTowardsCharacter(){
        Vector2 scl = new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).nor().scl(moveSpeed);
        moveEachDirectionIfCan(scl);
    }

    public void moveEachDirectionIfCan(Vector2 scl) {
        boolean shouldReturnX = false;

        sprite.translateX(scl.x);
        for(Entity entity : Main.terrains){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnX = true;
        }
        for(Entity entity :  Main.enemies){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnX = true;
        }
        if(shouldReturnX)
            sprite.translateX(-scl.x);

        boolean shouldReturnY = false;

        sprite.translateY(scl.y);
        for(Entity entity : Main.terrains){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnY = true;
        }
        for(Entity entity : Main.enemies){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnY = true;
        }
         if(shouldReturnY)
            sprite.translateY(-scl.y);
    }
}
