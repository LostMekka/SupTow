package de.lostmekkasoft.suptow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class InputProcessor extends InputAdapter {

	SupTowGame game;
	boolean isPan;
	Vector3 touchStart;
	Vector3 touchLast;
	
	enum CommandMode {
		BuildTower,
		None
	}
	CommandMode commandMode = CommandMode.None;
	
	Set<Entity> selection = new HashSet<Entity>(8);
	
	public InputProcessor(SupTowGame game) {
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
			isPan				= false;
			selectAndCommand(screenX, screenY);
		}
		return true;
	}
	
	
	
	void selectAndCommand(int screenX, int screenY) {
		Vector2 click		= new Vector2(screenX, screenY);
		game.viewport.unproject(click);
		
		Set<Entity> entities = entitiesBelow(click);
		List<Entity> clickedFabbers = new LinkedList<Entity>(entities);
		filterForFabbers(clickedFabbers);

		if (clickedFabbers.size() > 0 && selection.size() >= 0) {
			if (isShiftPressed()) {
				selection.addAll(clickedFabbers);
			} else {
				selection.clear();
				selection.addAll(clickedFabbers);
			}
			System.out.println("selected "+selection.size());
		}
		else if (clickedFabbers.size() == 0 && selection.size() > 0) {
			switch (commandMode) {
			case BuildTower:
				game.orderBuildTower(selection, click);
				break;
			case None:
				selection.clear();
				System.out.println("selection cleared");
				break;
			}
		}
	}
	
	
	
	boolean isShiftPressed() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}
	
	
	
	void filterForFabbers(Collection<Entity> entities) {
		Iterator<Entity> iter 	= entities.iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			if (e.getFabberModule() == null) {
				iter.remove();
			}
		}
	}
	
	
	
	Set<Entity> entitiesBelow(Vector2 pos) {
		float size 		= 0.6f;
		final Vector2 a = pos.cpy().add(-size, -size);
		final Vector2 b = pos.cpy().add( size,  size);
		final Vector2 c = pos.cpy().add( size, -size);
		final Vector2 d = pos.cpy().add(-size,  size);
		
		game.addDebugRenderTask(new Runnable() {
			@Override
			public void run() {
				game.shapeRenderer.line(a, b);
				game.shapeRenderer.line(c, d);
			}
		});
		
		RayCastForEntitiesBelow raycast = new RayCastForEntitiesBelow();
		game.physicsWorld.rayCast(raycast, a, b);
		game.physicsWorld.rayCast(raycast, c, d);
		return raycast.entities;
	}
	
	class RayCastForEntitiesBelow implements RayCastCallback {
		
		Set<Entity> entities = new HashSet<Entity>(8);
		
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point,
				Vector2 normal, float fraction) {
			Object data = fixture.getUserData();
			if (data != null) {
				Entity e = (Entity)data;
				entities.add(e);
			}
			return 1;
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
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Input.Keys.ESCAPE:
			if (commandMode == CommandMode.None) {
				Gdx.app.exit();
			} else {
				setCommandMode(CommandMode.None);
			}
			return true;
		case Input.Keys.T:
			setCommandMode(CommandMode.BuildTower);
			return true;
		default:
			return false;
		}
	}

	private void setCommandMode(CommandMode mode) {
		commandMode = mode;
	}
	
}
