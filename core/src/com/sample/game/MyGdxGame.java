package com.sample.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter {
	public OrthographicCamera camera;
	private SpriteBatch batch;
	private Sprite spaceShip;
	private Texture img;
	private Texture spaceship;
	private Texture fire;
	private Sprite fileSprite;
	private Texture enimyship;
	public Enimy enimy;

	public AnimatedSprite animatedCraft;
	private AnimatedSprite animatedFire;

	private ShotManager shotManager;

	private BitmapFont bitmapFont;
	private float lastAccYValue = 0;
	private Music music;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		bitmapFont = new BitmapFont();
		batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");
		img = new Texture(Gdx.files.internal("background.png"));

		//spaceship = new Texture(Gdx.files.internal("spacecraft_2.png"));
		spaceship = new Texture(Gdx.files.internal("spacecraft_set.png"));
		spaceShip = new Sprite(spaceship);
		//spaceShip.setPosition(camera.viewportWidth/2-(spaceShip.getWidth()/4), 0);
		enimyship=new Texture(Gdx.files.internal("ufos.png"));

		enimy = new Enimy(enimyship, this);

		animatedCraft=new AnimatedSprite(spaceShip);
		animatedCraft.setPosition(camera.viewportWidth/2, 0);

		fire = new Texture(Gdx.files.internal("fireset.png"));
		shotManager = new ShotManager(fire, this);
		//fileSprite = new Sprite(fire);
		//animatedFire=new AnimatedSprite(fileSprite);
		//animatedFire.setPosition(camera.viewportWidth/2, 0);

		music = Gdx.audio.newMusic(Gdx.files.internal("background_music.wav"));
		music.setVolume(0.8f);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, 0, 0);
		animatedCraft.draw(batch);
		if(animatedCraft.isHit)
		{
			BitmapFont font=new BitmapFont();
			font.setColor(255,0,0,1);
			font.getData().setScale(3.0f);
			GlyphLayout layout = new GlyphLayout();
			String text = "You are Hit! - Will restart soon";
			layout.setText(font, text);
			font.draw(batch, text, camera.viewportWidth/2-layout.width/2, camera.viewportHeight/2-layout.height/2);
		}
		shotManager.draw(batch);

		enimy.Draw(batch);
		batch.end();

		handleInput();

		shotManager.update();
		enimy.Update();
	}

	private void handleInput() {
		if(Gdx.input.isTouched()) {
			Vector3 touchPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPosition);

			if(touchPosition.x>animatedCraft.getX() && animatedCraft.getX()<camera.viewportWidth) {
				animatedCraft.setDirection(Direction.RIGHT);
				animatedCraft.move();
			}
			else if(animatedCraft.getX()>0) {
				animatedCraft.setDirection(Direction.LEFT);
				animatedCraft.move();
			}
			shotManager.firePlayerShot(animatedCraft.getX());
		}
		else if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer) && (Gdx.input.getAccelerometerY()<=-0.3f || Gdx.input.getAccelerometerY()>=0.3f))
		{
			//if(!(Gdx.input.getAccelerometerY()>=lastAccYValue-0.3f && Gdx.input.getAccelerometerY()<=lastAccYValue+0.3f)) {
			if (Gdx.input.getAccelerometerY() > 0 && animatedCraft.getX() < camera.viewportWidth) {
				animatedCraft.setDirection(Direction.RIGHT);
				animatedCraft.move();
			} else if (animatedCraft.getX() > 0) {
				animatedCraft.setDirection(Direction.LEFT);
				animatedCraft.move();
			}

			lastAccYValue = Gdx.input.getAccelerometerY();
			//}
		}

		//displayStatus();
	}

	private void displayStatus() {
		String strValue = String.format("X=%s\nY=%s\nZ=%s",
				Gdx.input.getAccelerometerX(),
				Gdx.input.getAccelerometerY(),
				Gdx.input.getAccelerometerZ());

		//System.out.println(strValue);
		bitmapFont.setColor(1.0f, 0f, 0f, 1.0f);
		batch.begin();
		bitmapFont.draw(batch, strValue, camera.viewportWidth-100, camera.viewportHeight-50);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		if(music.isPlaying())
			music.stop();
		music.dispose();
		spaceship.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public boolean IsSpaceshipGotHit(Rectangle enimyBullet) {
		boolean flag = false;

		try {
			Rectangle spaceShipRectangle = animatedCraft.getBoundingRectangle();
			spaceShipRectangle = new Rectangle((float)(spaceShipRectangle.x*1.1), spaceShipRectangle.y,
					(float) (spaceShipRectangle.width*0.4), (float) (spaceShipRectangle.height*0.70));

			Rectangle interconnectRectangle = new Rectangle();
			flag = Intersector.intersectRectangles(spaceShipRectangle, enimyBullet, interconnectRectangle);
		}
		catch(Exception ex) {
			String message = ex.getMessage();
		}

		if(flag && !animatedCraft.isHit) //spaceShip got hit
		{
			animatedCraft.gotHit();
			//enimy.clearFires();
		}

		return flag;
	}

	public boolean IsEnimyGotHit(Rectangle spaceShipBullet) {
		boolean flag = false;

		try {
			Rectangle enimyRectangle = enimy.getEnimyBoundingRectangle();
			Rectangle interconnectRectangle = new Rectangle();

			flag = Intersector.intersectRectangles(enimyRectangle, spaceShipBullet, interconnectRectangle);
		}
		catch(Exception ex) {
			String message = ex.getMessage();
		}

		if(flag && !enimy.isHit) //spaceShip got hit
			enimy.gotHit();

		return flag;
	}
}
