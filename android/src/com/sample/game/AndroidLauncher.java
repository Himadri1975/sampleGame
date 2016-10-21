package com.sample.game;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer=true;
		config.useCompass=true;
		//initialize(new MyGdxGame(), config);

		View mainGameView = initializeForView(new MyGdxGame(), config);
		layout.addView(mainGameView);

		setContentView(layout);
	}
}
