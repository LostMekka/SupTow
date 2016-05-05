package de.lostmekkasoft.suptow;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

public class SupTowGame extends ApplicationAdapter {

	private static final float PHYSICS_TIME_STEP = 1f / 60f;
	
	private static SupTowGame instance = null;
	public static SupTowGame getInstance() {
		return instance;
	}

	SpriteBatch batch;
	Texture img;
	World physicsWorld;
	Box2DDebugRenderer debugRenderer;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Viewport viewport;
	EntityList fabbers, towers, enemies, shots;

	public SupTowGame() {
		instance = this;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		physicsWorld = new World(Vector2.Zero, true);
		physicsWorld.setContactListener(new ContactListener());
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawContacts(true);
		debugRenderer.setDrawInactiveBodies(true);
		debugRenderer.setDrawVelocities(true);
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(25, 20, camera);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		fabbers = new EntityList();
		towers = new EntityList();
		enemies = new EntityList();
		shots = new EntityList();
		createFabber(Vector2.Zero)
				.getMovementModule()
				.setTarget(new Vector2(2f, 1f));
		createEnemy(new Vector2(-6f, -4f));
		
		Gdx.input.setInputProcessor(new SupTowInputProcessor(this));
		
		Timer.schedule(new DebugPrint(), 1, 1);
	}
	
	class DebugPrint extends Task {
		@Override
		public void run() {
			//System.out.println("camera: " + camera.combined);
		}
	}

	@Override
	public void render () {
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		// update
		for (Entity e : fabbers) updateEntity(e, deltaTime);
		for (Entity e : towers) updateEntity(e, deltaTime);
		for (Entity e : enemies) updateEntity(e, deltaTime);
		for (Entity e : shots) updateEntity(e, deltaTime);
		fabbers.applyRemoval();
		towers.applyRemoval();
		enemies.applyRemoval();
		shots.applyRemoval();
		doPhysicsStep(deltaTime);
		
		// render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// TODO: render entities here
		batch.end();
		
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		// TODO: render additional shapes here
		shapeRenderer.end();
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
	
	private void updateEntity(Entity e, float deltaTime) {
		e.update(deltaTime);
		if (e.needsToBeRemoved()) destroyEntity(e);
	} 

	public Entity createFabber(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 1f, 0, false);
		e.setMovementModule(new MovementModule(3.5f, 2f, 0.25f));
		e.setHealthModule(new HealthModule(100f));
		return fabbers.add(e) ? e : null;
	}
	
	public Entity createTower(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 2f, 0, true);
		e.setHealthModule(new HealthModule(500f));
		e.setWeaponsModule(new WeaponsModule(1f, 8f, 3f, 20f, 6f));
		return towers.add(e) ? e : null;
	}
	
	public Entity createEnemy(Vector2 position) {
		Entity e = Entity.create(physicsWorld, position, 1.5f, 1, false);
		e.setMovementModule(new MovementModule(3.5f, 1f, 1f));
		e.setHealthModule(new HealthModule(200f));
		e.setWeaponsModule(new WeaponsModule(1f, 8f, 3f, 20f, 16f));
		return enemies.add(e) ? e : null;
	}
	
	public Entity createShot(Entity source, Vector2 target) {
		WeaponsModule w = source.getWeaponsModule();
		if (w == null) return null;
		if (target == null) return null;
		Vector2 start = source.physicsBody.getPosition();
		float vel = w.shotVelocity;
		float dmg = w.damage;
		int team = source.team;
		float timer = w.shotLifeTime;
		
		Entity e = Entity.create(physicsWorld, start, 0.3f, source.team, false);
		e.setShotModule(new ShotModule(start, target, vel, dmg, team, timer));
		return shots.add(e) ? e : null;
	}
	
	public void destroyEntity(Entity e) {
		physicsWorld.destroyBody(e.physicsBody);
		fabbers.markToRemove(e);
		towers.markToRemove(e);
		enemies.markToRemove(e);
		shots.markToRemove(e);
	}
	
	public Collection<Entity> getTargetableEntities() {
		LinkedList<Entity> answer = new LinkedList<Entity>();
		fabbers.addAllToCollection(answer);
		towers.addAllToCollection(answer);
		enemies.addAllToCollection(answer);
		// shots are not targetable
		return answer;
	}
	
}
