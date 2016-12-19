package com.jshepdevelopment.laserdefend;

import com.badlogic.gdx.Game;
import com.jshepdevelopment.laserdefend.screens.MenuScreen;

public class LaserDefend extends Game {


	@Override
	public void create () {
		setScreen(new MenuScreen(this));
	}

}
