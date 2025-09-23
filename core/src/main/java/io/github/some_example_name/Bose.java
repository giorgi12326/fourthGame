package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bose extends Entity{
    Timer one = new Timer(3f);
    Timer reset = new Timer(1.5f);
    Timer two = new Timer(1.5f);
    Timer reset2 = new Timer(1.5f);


    public Bose(float moveSpeed, Character ch) {
        super(moveSpeed, ch);
        sprite = new Sprite(new Texture("block.png"));
        health = 100f;
        one.flag();
    }

    @Override
    public void update() {
        hurtCooldown.handleUpdateAndUnFlagging();
        one.handleUpdateAndUnFlagging(
            this:: moveTowardsCharacter,
            ()->reset.flag());
        reset.handleUpdateAndUnFlagging(()->{}, ()-> two.flag());
        two.handleUpdateAndUnFlagging(
            ()->{
                moveSpeed = 10f;
                moveTowardsCharacter();
                sprite.setColor(0f, 0f, 1f, 1f);
            },
            ()->{
                reset2.flag();
                sprite.setColor(1f, 1f, 1f, 1f);
                moveSpeed = 5f;

            });
        reset2.handleUpdateAndUnFlagging(()-> sprite.setColor(0f, 0f, 1f, 1f), ()-> {sprite.setColor(1f, 1f, 1f, 1f);
            sprite.setPosition(0,0);
            one.flag();});




        stunCooldown.handleUpdateAndUnFlagging();

    }

    @Override
    public void moveTowardsCharacter() {
        if(stunCooldown.isFlagged()) return;
        super.moveTowardsCharacter();
    }
}
