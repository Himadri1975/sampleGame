package com.sample.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by ehimmaj on 10/20/2016.
 */

public class Enimy {
    private static final float CHANGE_DIRECTION_DELAY=0.5f;
    private static final int UFO_FIRE_SPEED=300;
    private static final float UFO_FIRE_DELAY=0.7f;
    private static final int ACTIVE_FIRE_COUNT = 10;
    public static final float APPEARING_DELAY_AFTER_HIT=3.0f;

    private Texture texture;
    private MyGdxGame game;
    private AnimatedSprite animatedSprite;
    private Sprite enimySprite;
    private Random rnd;
    public boolean isHit = false;

    private float lastDirectionChanged=0f;
    private float lastUFOFireTime=0f;
    private float timeAfterHit=0.0f;

    private List<AnimatedSprite> fires = new ArrayList<AnimatedSprite>();

    private Sound fireSound;
    private Sound explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

    public Enimy(Texture texture, MyGdxGame game) {
        this.texture=texture;
        this.game=game;

        enimySprite = new Sprite(texture);
        animatedSprite=new AnimatedSprite(enimySprite);
        animatedSprite.setDirection(Direction.RIGHT);
        animatedSprite.setPosition(this.game.camera.viewportWidth/2, game.camera.viewportHeight-(animatedSprite.getHeight()/2));
        rnd=new Random(System.currentTimeMillis());

        //fireSound=Gdx.audio.newSound(Gdx.files.internal("Fire.wav"));
    }

    public void Update() {

        if(!isHit && !this.game.animatedCraft.isHit) {
            generateEnimyMove();
            validateEnimyPosition();
            if (shouldFireNow() && fires.size() <= ACTIVE_FIRE_COUNT) {
                fire();
            }

            updateFiredBullets();
        }
    }

    private boolean shouldFireNow() {
        boolean status = false;
        status = lastUFOFireTime>=UFO_FIRE_DELAY && !this.isHit && !this.game.animatedCraft.isHit;

        return status;
    }

    private void updateFiredBullets() {
        lastUFOFireTime += Gdx.graphics.getDeltaTime();

        Iterator<AnimatedSprite> firedBullets = fires.iterator();
        while(firedBullets.hasNext()) {
            AnimatedSprite bullet = firedBullets.next();
            bullet.move();
            if((bullet.getX()>this.game.camera.viewportWidth || bullet.getX()<=0) ||
                    (bullet.getY()>this.game.camera.viewportHeight || bullet.getY()<=0))
            {
                firedBullets.remove();
            }
            else
            {
                Rectangle rec = bullet.getBoundingRectangle();
                if(this.game.IsSpaceshipGotHit(rec)) {
                    firedBullets.remove();
                    break;
                }
            }
        }
        if(this.isHit || this.game.animatedCraft.isHit)
            fires.clear();
    }

    public void clearFires() {
        fires.clear();
    }

    private void fire() {
        lastUFOFireTime=0;

        Texture bullet = new Texture(Gdx.files.internal("ufos_fire.png"));
        Sprite ufoFileSprite = new Sprite(bullet);
        AnimatedSprite ufosFireAnimatedSprite = new AnimatedSprite(ufoFileSprite);
        ufosFireAnimatedSprite.setDirection(Direction.DOWN);
        ufosFireAnimatedSprite.setVelocity(new Vector2(0,-UFO_FIRE_SPEED));
        ufosFireAnimatedSprite.setPosition(animatedSprite.getX(), animatedSprite.getY()+animatedSprite.getHeight()/2);

        //fireSound.play(0.5f);

        fires.add(ufosFireAnimatedSprite);
    }

    private void generateEnimyMove() {
        lastDirectionChanged += Gdx.graphics.getDeltaTime();

        if(lastDirectionChanged>=CHANGE_DIRECTION_DELAY) {
            if (rnd.nextInt(15) > 7) {
                animatedSprite.setDirection(Direction.RIGHT);
            } else {
                animatedSprite.setDirection(Direction.LEFT);
            }
            lastDirectionChanged=0;
        }
        animatedSprite.move();
    }

    private void validateEnimyPosition() {
        if(animatedSprite.getX()<=0) {
            animatedSprite.setDirection(Direction.RIGHT);
            lastDirectionChanged=0;
        }
        else if(animatedSprite.getX()>this.game.camera.viewportWidth-animatedSprite.getWidth()) {
            animatedSprite.setDirection(Direction.LEFT);
            lastDirectionChanged=0;
        }
    }

    public void Draw(SpriteBatch batch) {

        if(!isHit && !this.game.animatedCraft.isHit) {
            animatedSprite.draw(batch);

            Iterator<AnimatedSprite> firedBullets = fires.iterator();
            while (firedBullets.hasNext()) {
                AnimatedSprite bullet = firedBullets.next();
                bullet.draw(batch);
            }
        }
        else {
            timeAfterHit += Gdx.graphics.getDeltaTime();
            if(timeAfterHit>APPEARING_DELAY_AFTER_HIT) {
                timeAfterHit=0f;
                isHit=false;
            }
        }
    }

    public Rectangle getEnimyBoundingRectangle() {
        //Intersector.intersectRectangles()
        return animatedSprite.getBoundingRectangle();
    }

    public void gotHit() {
        this.isHit=true;
        explosion.play();
    }
}
