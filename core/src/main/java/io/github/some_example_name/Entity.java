package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public abstract class Entity {
    public int id;
    Sprite sprite;
    final Rectangle hurtBox = new Rectangle();
    float moveSpeed;
    Character ch;
    float health;
    Cooldown hurtCooldown = new Cooldown(0.1f);
    Timer stunCooldown = new Timer(0.2f);

    public boolean markAsDeleted;

    public Entity(float moveSpeed, Character ch) {
        this.ch = ch;
        this.moveSpeed = moveSpeed;

    }

    public Entity(float moveSpeed, Character ch, int id) {
        this.ch = ch;
        this.moveSpeed = moveSpeed;
        this.id = id;
    }

    public void update(){
        moveTowardsCharacter();
        hurtCooldown.handleUpdateAndUnFlagging();
    }

    public Rectangle updateHurtBox() {
        hurtBox.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        return hurtBox;
    }

    public void gotHit(Vector2 vector2, float impact, float damage) {
        if(!hurtCooldown.isFlagged()) {
            hurtCooldown.flag();

            knockback(vector2, impact);
            stunCooldown.flag();
            health -= damage;
            if (health <= 0) {
                if(GameScreen.onKillResetAttack)
                    ch.circleAttack.cooldown.finish();
                if(GameScreen.onKillResetDash)
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
        for(Entity entity : GameScreen.terrains){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnX = true;
        }
        for(Entity entity :  GameScreen.enemies){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnX = true;
        }
        if(shouldReturnX)
            sprite.translateX(-scl.x);

        boolean shouldReturnY = false;

        sprite.translateY(scl.y);
        for(Entity entity : GameScreen.terrains){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnY = true;
        }
        for(Entity entity : GameScreen.enemies){
            if(entity != this && entity.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnY = true;
        }
         if(shouldReturnY)
            sprite.translateY(-scl.y);
    }

    public float centerX(){
        return sprite.getX() + sprite.getWidth()/2f;
    }

    public float centerY(){
        return sprite.getY() + sprite.getHeight()/2f;
    }

    public void cleanUpAfterDeletion(){

    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
