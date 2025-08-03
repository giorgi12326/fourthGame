package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Character extends Entity{
    final DashToMouse dashToMouse;
    CircleAttack circleAttack = new CircleAttack(this);
    Cooldown hurtCooldown = new Cooldown(0.3f);
    boolean invincible = false;
    float wood;

    public Character(float moveSpeed) {
        super(moveSpeed, null);
        this.ch = this;
        sprite = new Sprite(new Texture("block.png"));
        sprite.setOriginCenter();
        dashToMouse = new DashToMouse(this);
        health = 100f;
    }

    public void update() {
        dashToMouse.update();
        circleAttack.update();
        hurtCooldown.handleUpdateAndUnFlagging();
    }

    public void gotHit(){
        if(!hurtCooldown.isFlagged() && !invincible) {
            health -= 10f;
            if(health <= 0){
                markAsDeleted = true;
            }
            hurtCooldown.flag();
        }
    }

    public void moveRight(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged()){
            boolean shouldReturnX = false;

            sprite.translateX(moveSpeed*delta);

            for(Entity entity : Main.terrains){
                if( entity.updateHurtBox().overlaps(updateHurtBox()))
                    shouldReturnX = true;
            }
            if(shouldReturnX)
                sprite.translateX(-moveSpeed * delta);
        }
    }

    public void moveLeft(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged()){
            boolean shouldReturnX = false;

            sprite.translateX(-moveSpeed*delta);

            for(Entity entity : Main.terrains){
                if( entity.updateHurtBox().overlaps(updateHurtBox()))
                    shouldReturnX = true;
            }
            if(shouldReturnX)
                sprite.translateX(moveSpeed * delta);
        }
    }

    public void moveUp(float delta) {
        if(!dashToMouse.jumpTimer.isFlagged()){
            boolean shouldReturnY = false;

            sprite.translateY(moveSpeed*delta);

            for(Entity entity : Main.terrains){
                if( entity.updateHurtBox().overlaps(updateHurtBox()))
                    shouldReturnY = true;
            }
            if(shouldReturnY)
                sprite.translateY(-moveSpeed * delta);
        }
    }

    public void moveDown(float delta) {

        if(!dashToMouse.jumpTimer.isFlagged()) {
            boolean shouldReturnY = false;

            sprite.translateY(-moveSpeed*delta);

            for(Entity entity : Main.terrains){
                if( entity.updateHurtBox().overlaps(updateHurtBox()))
                    shouldReturnY = true;
            }
            if(shouldReturnY)
                sprite.translateY(moveSpeed * delta);
        }
    }

    public float centerX(){
        return sprite.getX() + sprite.getWidth()/2f;
    }

    public float centerY(){
        return sprite.getY() + sprite.getHeight()/2f;
    }
}
