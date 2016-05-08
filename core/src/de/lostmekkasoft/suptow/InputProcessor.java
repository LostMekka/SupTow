package de.lostmekkasoft.suptow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import java.util.ArrayList;

public class InputProcessor extends InputAdapter {

	SupTowGame game;
	boolean isPan;
	Vector3 touchLast;
	
	enum CommandMode {
		BuildTower,
		None, Move, PlaceFabber, Place, PlaceTower
	}
	CommandMode commandMode = CommandMode.None;
	
	Set<Entity> selection = new HashSet<Entity>(8);
	
	public InputProcessor(SupTowGame game) {
		this.game = game;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (isSpacePressed() || button == Input.Buttons.MIDDLE) {
			isPan     = true;
			touchLast = new Vector3(screenX, screenY, 0);
		} else {
			isPan            = false;
			Vector2 position = new Vector2(screenX, screenY);
			game.viewport.unproject(position);
			
			if (button == Input.Buttons.LEFT) {
				onLeftClick(position);
			} else if (button == Input.Buttons.RIGHT) {
				onRightClick(position);
			}
		}
		return true;
	}
	
	
	
	void onLeftClick(Vector2 position) {
		List<Entity> clickedFabbers = entitiesBelow(position);
		filterForFabbers(clickedFabbers);
		
		if (selection.isEmpty()) {
			selectEntities(clickedFabbers);
		} else {
			switch (commandMode) {
			case BuildTower:
				game.orderBuildTower(selection, position);
				break;
			case Place:
				break;
			case PlaceTower:
				game.createTower(position);
				break;
			case PlaceFabber:
				game.createFabber(position);
				break;
			case Move:
				game.orderMove(selection, position);
			case None:
				if (clickedFabbers.isEmpty()) game.orderMove(selection, position);
				else selectEntities(clickedFabbers);
				break;
			}
			if (!isShiftPressed()) commandMode = CommandMode.None;
		}
	}
	
	void selectEntities(List<Entity> entities) {
		if (!isShiftPressed())  selection.clear();
		selection.addAll(entities);
		System.out.println("selected "+selection.size());
	}
	
	
	
	void onRightClick(Vector2 position) {
		if (!selection.isEmpty()) {
			if (commandMode == CommandMode.None) {
				List<Entity> entities = entitiesBelow(position);
				filterForResourcePoints(entities);
				if (entities.isEmpty()) {
					for (Entity e : selection) {
						MovementModule mm = e.getMovementModule();
						if (mm != null) {
							FabberModule fm = e.getFabberModule();
							if (fm != null) fm.stop();
							mm.setTarget(position);
						}
					}
				} else {
					System.out.println("reclaiming resource point");
					Entity resourcePoint = entities.get(0);
					for (Entity e : selection) {
						FabberModule fm = e.getFabberModule();
						if (fm != null) fm.harvestResources(resourcePoint);
					}
				}
			} else {
				commandMode = CommandMode.None;
			}
		}
	}
	
	
	
	boolean isShiftPressed() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}
	
	
	
	boolean isSpacePressed() {
		return Gdx.input.isKeyPressed(Input.Keys.SPACE);
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
	
	void filterForResourcePoints(Collection<Entity> entities) {
		Iterator<Entity> iter 	= entities.iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			if (e.getResourcePointModule() == null) {
				iter.remove();
			}
		}
	}
	
	
	
	List<Entity> entitiesBelow(Vector2 pos) {
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
		
		final Set<Entity> entities = new HashSet<Entity>(8);
		QueryCallback queryCallback = new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fxtr) {
				Object data = fxtr.getUserData();
				if (data != null) {
					Entity e = (Entity)data;
					entities.add(e);
				}
				return true;
			}
		};
		float s = 0.1f;
		game.physicsWorld.QueryAABB(queryCallback, pos.x - s, pos.y - s, pos.x + s, pos.y + s);
		
		return new ArrayList<Entity>(entities);
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
			Gdx.app.exit();
			return true;
		case Input.Keys.T:
			if (commandMode == CommandMode.Place) {
				setCommandMode(CommandMode.PlaceTower);
			} else {
				if (!selection.isEmpty()) setCommandMode(CommandMode.BuildTower);
			}
			return true;
		case Input.Keys.M:
			if (!selection.isEmpty()) setCommandMode(CommandMode.Move);
			return true;
		case Input.Keys.F:
			if (commandMode == CommandMode.Place) setCommandMode(CommandMode.PlaceFabber);
			return true;
		case Input.Keys.P:
			setCommandMode(CommandMode.Place);
			return true;
		default:
			return false;
		}
	}

	private void setCommandMode(CommandMode mode) {
		commandMode = mode;
	}
	
}
