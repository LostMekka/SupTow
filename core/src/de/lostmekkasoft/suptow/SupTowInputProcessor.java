package de.lostmekkasoft.suptow;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SupTowInputProcessor extends InputAdapter {

	SupTowGame game;
	Vector3 touchStart;
	Vector3 touchLast;
	
	public SupTowInputProcessor(SupTowGame game) {
		this.game = game;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 touchScreen	= new Vector3(screenX, screenY, 0);
		touchStart 			= touchScreen;
		touchLast 			= touchScreen;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// not correct for a zoomed camera
		// do I need to use .unproject()?
		// this didn't work for the first time
		Vector3 touchScreen	= new Vector3(screenX, screenY, 0);
		Vector3 diff = touchLast.sub(touchScreen);
		float   w = game.viewport.getWorldWidth();
		float   h = game.viewport.getWorldHeight();
		float  sw = game.viewport.getScreenWidth();
		float  sh = game.viewport.getScreenHeight();
		Vector2 d = new Vector2(diff.x * (w/sw), -diff.y * (h/sh));
		game.camera.translate(d);
		touchLast 			= touchScreen;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
		
}
