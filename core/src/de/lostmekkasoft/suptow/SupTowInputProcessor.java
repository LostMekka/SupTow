package de.lostmekkasoft.suptow;

import com.badlogic.gdx.Gdx;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class SupTowInputProcessor extends InputAdapter {

	SupTowGame game;
	boolean isPan;
	Vector3 touchStart;
	Vector3 touchLast;
	
	Set<Entity> selection = new HashSet<Entity>(8);
	
	public SupTowInputProcessor(SupTowGame game) {
		this.game = game;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.RIGHT) {
			isPan 				= true;
			Vector3 touchScreen	= new Vector3(screenX, screenY, 0);
			touchStart 			= touchScreen;
			touchLast 			= touchScreen;
		}
		if (button == Input.Buttons.LEFT) {
			isPan					= false;
			Vector2 click			= new Vector2(screenX, screenY);
			game.viewport.unproject(click);
			float size 		= 0.6f;
			final Vector2 a = click.cpy().add(-size, -size);
			final Vector2 b = click.cpy().add( size,  size);
			final Vector2 c = click.cpy().add( size, -size);
			final Vector2 d = click.cpy().add(-size,  size);
			
			game.addDebugRenderTask(new Runnable() {
				@Override
				public void run() {
					game.shapeRenderer.line(a, b);
					game.shapeRenderer.line(c, d);
				}
			});
			
			SelectFabber selectFabber = new SelectFabber();
			game.physicsWorld.rayCast(selectFabber, a, b);
			game.physicsWorld.rayCast(selectFabber, c, d);

			int selected = selectFabber.fabbers.size();
			if (selected > 0) {
				selection.clear();
				selection.addAll(selectFabber.fabbers);
				System.out.println("selected "+selection.size());
			}
			if (selected == 0 && selection.size() > 0) {
				game.orderBuildTower(selection, click);
				selection.clear();
			}
		}
		return false;
	}
	
	class SelectFabber implements RayCastCallback {
		
		Set<Entity> fabbers = new HashSet<Entity>(8);
		
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point,
				Vector2 normal, float fraction) {
			Object data = fixture.getUserData();
			if (data != null) {
				Entity e = (Entity)data;
				if (e.getFabberModule() != null) {
					fabbers.add(e);
				}
			}
			return 0;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (isPan) {
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
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE) Gdx.app.exit();
		return false;
	}
	
}
