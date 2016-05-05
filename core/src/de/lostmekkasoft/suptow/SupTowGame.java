package de.lostmekkasoft.suptow;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class SupTowGame extends ApplicationAdapter {

	private static final float PHYSICS_TIME_STEP = 1f / 60f;

	SpriteBatch batch;
	Texture img;
	World physicsWorld;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;
	EntityList fabbers, towers, enemies;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		physicsWorld = new World(Vector2.Zero, true);
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawContacts(true);
		debugRenderer.setDrawInactiveBodies(true);
		debugRenderer.setDrawVelocities(true);
		camera = new OrthographicCamera(25, 20);
		fabbers = new EntityList();
		towers = new EntityList();
		enemies = new EntityList();
		createFabber(Vector2.Zero)
				.getMovementModule()
				.setTarget(new Vector2(0.5f, 0.5f));
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		
		// update
		for (Entity e : fabbers) e.update(delta);
		for (Entity e : towers) e.update(delta);
		for (Entity e : enemies) e.update(delta);
		fabbers.applyRemoval();
		towers.applyRemoval();
		enemies.applyRemoval();
		doPhysicsStep(delta);
		
		// render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// TODO: render entities here
		batch.end();
		debugRenderer.render(physicsWorld, camera.combined);
	}
	
	private float _physicsStepTimer = 0f;
	private void doPhysicsStep(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		_physicsStepTimer += frameTime;
		while (_physicsStepTimer >= PHYSICS_TIME_STEP) {
			physicsWorld.step(PHYSICS_TIME_STEP, 2, 6);
			_physicsStepTimer -= PHYSICS_TIME_STEP;
		}
	}

	public Entity createFabber(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 1f);
		e.setMovementModule(new MovementModule(1f));
		return fabbers.add(e) ? e : null;
	}
	
	public Entity createTower(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 2f);
		return towers.add(e) ? e : null;
	}
	
	public Entity createEnemy(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 1.5f);
		return enemies.add(e) ? e : null;
	}
	
	public void destroyEntity(Entity e) {
		physicsWorld.destroyBody(e.getPhysicsBody());
		fabbers.markToRemove(e);
		towers.markToRemove(e);
		enemies.markToRemove(e);
	}
	
}
