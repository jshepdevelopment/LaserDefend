package com.jshepdevelopment.laserdefend;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jshepdevelopment.laserdefend.LaserDefend;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

		cfg.useAccelerometer = true;

		initialize(new LaserDefend(), config);
	}
}
