package com.sample.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ehimmaj on 10/20/2016.
 */

public class ShotManager {
    private static final int SHOT_OFFSET_Y=100;
    private static final int SHOT_VELOCITY = 300;
    private static final float MIN_TIMEDELAY_BETWEEN_SHOT = 1.0f;
    private float timeSinceLastShot=0f;
    private BitmapFont bitmapFont=new BitmapFont();
    private MyGdxGame game;

    private Texture fireTexture;
    private List<AnimatedSprite> shots=new ArrayList<AnimatedSprite>();
    private Sound sound;

    public ShotManager(Texture fireTexture, MyGdxGame game) {
        this.fireTexture = fireTexture;
        this.game = game;

        try {
            this.sound = Gdx.audio.newSound(Gdx.files.internal("Fire2.ogg"));
        }
        catch(Exception ex) {
            String strValue = ex.getMessage();
        }
    }

    public void firePlayerShot(float shipCenterXLocation) {
        if(canFireShot())
        {
            Sprite newShot = new Sprite(this.fireTexture);
            AnimatedSprite animatedShot = new AnimatedSprite(newShot);
            animatedShot.setPosition(shipCenterXLocation, SHOT_OFFSET_Y);
            animatedShot.setVelocity(new Vector2(0,SHOT_VELOCITY));
            animatedShot.setDirection(Direction.UP);
            shots.add(animatedShot);
            timeSinceLastShot = 0;
            this.sound.play(0.5f);
        }
    }

    private boolean canFireShot() {
        return (timeSinceLastShot>MIN_TIMEDELAY_BETWEEN_SHOT) && !this.game.animatedCraft.isHit ;
    }
    
    public void update() {
        Iterator<AnimatedSprite> spriteIterator=shots.iterator();

        AnimatedSprite shot=null;
        while(spriteIterator.hasNext())
        {
            shot = spriteIterator.next();
            shot.move();
            if(shot.getY()>this.game.camera.viewportHeight)
                spriteIterator.remove();
            else {
                if(this.game.IsEnimyGotHit(shot.getBoundingRectangle()))
                {
                    spriteIterator.remove();
                    break;
                }
            }
        }

        if(this.game.animatedCraft.isHit || this.game.enimy.isHit)
        {
            this.game.enimy.clearFires();
        }

        timeSinceLastShot += Gdx.graphics.getDeltaTime();
    }

    public void draw(SpriteBatch batch) {
        for (AnimatedSprite shot : shots) {
            shot.draw(batch);
        }
    }
}
