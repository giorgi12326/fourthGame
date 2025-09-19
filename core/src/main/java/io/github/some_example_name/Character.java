package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Character extends Entity{
    final DashToMouse dashToMouse;
    public int shells;
    CircleAttack circleAttack = new CircleAttack(this);
    Cooldown hurtCooldown = new Cooldown(0.3f);
    boolean invincible = false;
    float wood;
    public List<Timer> slimes = new ArrayList<>();

    public Character(float moveSpeed) {
        super(moveSpeed, null);
        this.ch = this;
        sprite = new Sprite(new Texture("block.png"));
        sprite.setOriginCenter();
        dashToMouse = new DashToMouse(this);
        health = 100f;
        shells = 50;
    }

    public void update() {
        dashToMouse.update();
        circleAttack.update();
        hurtCooldown.handleUpdateAndUnFlagging();
        handleSlimes();
    }

    private void handleSlimes() {
        for (int i = slimes.size()-1; i >=0 ; i--) {
            Timer timer = slimes.get(i);
            timer.update();
            if(!timer.isValid())
                slimes.remove(i);
            ch.moveSpeed = Math.max(50,200 - slimes.size()*40f);
        }
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

            for(Entity entity : GameScreen.terrains){
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

            for(Entity entity : GameScreen.terrains){
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

            for(Entity entity : GameScreen.terrains){
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

            for(Entity entity : GameScreen.terrains){
                if( entity.updateHurtBox().overlaps(updateHurtBox()))
                    shouldReturnY = true;
            }
            if(shouldReturnY)
                sprite.translateY(moveSpeed * delta);
        }
    }


}
