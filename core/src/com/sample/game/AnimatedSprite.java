package com.sample.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by ehimmaj on 10/19/2016.
 */

public class AnimatedSprite {
    public static final int FRAME_COL=2;
    public static final int FRAME_ROW=2;
    public static final int SPEED = 300;
    public static final float APPEARING_DELAY_AFTER_HIT=3.0f;

    private Sprite sprite;
    private TextureRegion[] frames;
    private TextureRegion currentFrame;
    private Animation animation;
    private Vector2 velocity=new Vector2();
    private Direction direction=Direction.RIGHT;
    public boolean isHit = false;

    private float stateTime=0.0f;
    private float timeAfterHit=0.0f;
    private Sound explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

    public AnimatedSprite(Sprite sprite) {
        this.sprite = sprite;

        Texture texture = sprite.getTexture();
        frames = new TextureRegion[FRAME_COL*FRAME_ROW];

        TextureRegion[][] temp= TextureRegion.split(texture, texture.getWidth()/FRAME_COL, texture.getHeight()/FRAME_ROW);
        int index = 0;

        for(int i=0;i<FRAME_ROW;i++) {
            for(int j=0;j<FRAME_COL; j++) {
                frames[index++] = temp[i][j];
            }
        }

        animation = new Animation(0.1f, frames);
        stateTime = 0.0f;
    }

    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        if(!isHit) {
            currentFrame = animation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, sprite.getX(), sprite.getY());
        }
        else
        {
            timeAfterHit += Gdx.graphics.getDeltaTime();
            if(timeAfterHit>APPEARING_DELAY_AFTER_HIT) {
                isHit=false;
                timeAfterHit=0.0f;
            }
        }
    }

    public void setPosition(float x, float y) {
        float offsetFrameWidth = sprite.getWidth()/FRAME_COL;
        float offsetFrameHeight = sprite.getHeight()/FRAME_ROW;
        if(x>0)
            x = x - offsetFrameWidth/2;
        if(y>0)
            y = y - offsetFrameHeight/2;

        sprite.setPosition(x,y);
    }

    public float getX() {
        float offsetFrameWidth = sprite.getWidth()/FRAME_COL;
        return sprite.getX()+offsetFrameWidth/2;
    }
    public float getY() {
        return sprite.getY();
    }

    public void setDirection(Direction direction)
    {
        this.direction=direction;

        if(direction==Direction.LEFT) {
            velocity=new Vector2(-SPEED, 0);
        }
        else if(direction==Direction.RIGHT) {
            velocity=new Vector2(SPEED, 0);
        }
        else if(direction==Direction.UP) {
            velocity=new Vector2(0, SPEED);
        }
        else if(direction==Direction.DOWN) {
            velocity=new Vector2(0, -SPEED);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void move() {
        float x = sprite.getX()+(velocity.x*Gdx.graphics.getDeltaTime());
        float y = sprite.getY()+(velocity.y*Gdx.graphics.getDeltaTime());
        sprite.setPosition(x, y);
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public int getWidth() {
        return (int) sprite.getWidth()/FRAME_COL;
    }

    public int getHeight() {
        return (int) sprite.getHeight()/FRAME_ROW;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(sprite.getX(), sprite.getY(), getWidth(), getHeight());
    }

    public void gotHit() {
        this.isHit=true;
        timeAfterHit=0.0f;

        explosion.play();
    }
}
