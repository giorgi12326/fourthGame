package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;


public class Blupy {
    Sprite sprite;
    private final Rectangle hurtBox = new Rectangle();
    float fallback;
    Character ch;
    List<Blupy> enemies;

    public Blupy(Character ch, List<Blupy> enemies) {
        sprite = new Sprite(new Texture("chara.png"));
        sprite.setOriginCenter();
        this.enemies = enemies;
        this.ch = ch;
        fallback = 10f;
    }
    public void update(){
        moveTowardsCharacter();
    }

    public Rectangle updateHurtBox() {
        hurtBox.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        return hurtBox;
    }

    public void gotHit(Vector2 vector2) {
        Vector2 nor = new Vector2(sprite.getX() - vector2.x, sprite.getY() - vector2.y).nor();
        nor.scl(fallback);
        sprite.translateX(nor.x);
        sprite.translateY(nor.y);
    }
    public void moveTowardsCharacter(){
        Vector2 scl = new Vector2(ch.centerX() - sprite.getX(), ch.centerY() - sprite.getY()).nor().scl(5f);
        boolean shouldReturnX = false;
        boolean shouldReturnY = false;
        sprite.translateX(scl.x);

        for(Blupy enemy: enemies){
            if(enemy != this && enemy.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnX = true;
        }
        if(shouldReturnX)
            sprite.translateX(-scl.x);
as
        sprite.translateY(scl.y);
        for(Blupy enemy: enemies){
            if(enemy != this && enemy.updateHurtBox().overlaps(updateHurtBox()))
                shouldReturnY = true;
        }
        if(shouldReturnY)
            sprite.translateY(-scl.y);


    }
}
