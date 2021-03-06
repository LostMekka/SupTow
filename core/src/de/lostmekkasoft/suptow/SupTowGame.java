package de.lostmekkasoft.suptow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;



public class SupTowGame extends ApplicationAdapter {

	public static final Random random = new Random();

	private static final float PHYSICS_TIME_STEP = 1f / 60f;
	
	private static SupTowGame instance = null;
	public static SupTowGame getInstance() {
		return instance;
	}

	InputProcessor inputProcessor;
	BitmapFont font;
	SpriteBatch batch, hudBatch;
	Texture img;
	World physicsWorld;
	Box2DDebugRenderer debugRenderer;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera, hudCamera;
	ScreenViewport viewport, hudViewport;
	EntityList fabbers, towers, enemies, shots, resourcePoints;
	ArrayList<Runnable> debugRenderTasks = new ArrayList<Runnable>(8);
	float resources = 0;

	public SupTowGame() {
		instance = this;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		hudViewport.update(width, height);
		hudCamera.translate(width / 2f, height / 2f);
	}
	
	static class Textures {
		static class Element {
			final TextureRegion textureRegion;
			final float scaleFactor;
			public Element(TextureRegion textureRegion, int size) {
				this.textureRegion = textureRegion;
				scaleFactor = size / 32f;
			}
		}
		static Texture sprites;
		static Element fabber;
		static Element tower;
		static Element shot;
		static Element enemy;
		static Element resourcePoint;
	}

	@Override
	public void create () {
		batch    = new SpriteBatch();
		hudBatch = new SpriteBatch();
		
		Textures.sprites = new Texture("sprites.png");
		TextureRegion[][] regions = TextureRegion.split(Textures.sprites, 32, 32);
		Textures.fabber        = new Textures.Element(regions[0][0], 18);
		Textures.tower	       = new Textures.Element(regions[0][1], 28);
		Textures.shot          = new Textures.Element(regions[1][0], 10);
		Textures.enemy	       = new Textures.Element(regions[1][1], 20);
		Textures.resourcePoint = new Textures.Element(regions[0][2], 20);
		
		physicsWorld = new World(Vector2.Zero, true);
		physicsWorld.setContactListener(new ContactListener());
		
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawContacts(true);
		debugRenderer.setDrawInactiveBodies(true);
		
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		viewport.setUnitsPerPixel(1.0f / 8.0f);
		hudCamera = new OrthographicCamera();
		hudViewport = new ScreenViewport(hudCamera);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		
		font = new BitmapFont();
		
		fabbers = new EntityList();
		towers = new EntityList();
		enemies = new EntityList();
		shots = new EntityList();
		resourcePoints = new EntityList();
		
		createFabber(new Vector2(3f, 3f))
				.getMovementModule()
				.setTarget(new Vector2(2f, 1f));
		for (float i = 0; i < 20; i++) {
			float x = -60.0f + 34.0f * (float)Math.random();
			float y = 40.0f - i * 4.0f;
			createEnemy(new Vector2(x, y));
		}
		
		createTower(new Vector2(4f, -1f));
		
		inputProcessor = new InputProcessor(this);
		Gdx.input.setInputProcessor(inputProcessor);
		
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
		viewport.apply();
		for (Entity e : fabbers) updateEntity(e, deltaTime);
		for (Entity e : towers) updateEntity(e, deltaTime);
		for (Entity e : enemies) updateEntity(e, deltaTime);
		for (Entity e : shots) updateEntity(e, deltaTime);
		for (Entity e : resourcePoints) updateEntity(e, deltaTime);
		fabbers.applyRemoval();
		towers.applyRemoval();
		enemies.applyRemoval();
		shots.applyRemoval();
		resourcePoints.applyRemoval();
		doPhysicsStep(deltaTime);
		camera.update();
		
		// render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// sprites
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (Entity e : fabbers)        renderEntity(e, Textures.fabber);
		for (Entity e : towers)         renderEntity(e, Textures.tower);
		for (Entity e : enemies)        renderEntity(e, Textures.enemy);
		for (Entity e : shots)          renderEntity(e, Textures.shot);
		for (Entity e : resourcePoints) renderEntity(e, Textures.resourcePoint);
		batch.end();
		// shapes
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		for (Runnable task : debugRenderTasks) task.run();
		debugRenderTasks.clear();
		shapeRenderer.end();
		// overlay
		hudViewport.apply();
		hudBatch.setProjectionMatrix(hudCamera.combined);
		hudBatch.begin();
		font.draw(hudBatch, "resources: " + resources, 5, 20);
		font.draw(hudBatch, "command mode: " + inputProcessor.commandMode.name(), 5, 40);
		hudBatch.end();
		
		// set gameplay viewport for input, which is handled between render calls
		viewport.apply();
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
	
	private void renderEntity(Entity e, Textures.Element element) {
		Vector2 pos = e.physicsBody.getPosition();
		float r = e.radius / element.scaleFactor;
		batch.draw(element.textureRegion, pos.x-r, pos.y-r, r*2, r*2);
	}

	public Entity createFabber(Vector2 position) {
		System.out.println("createFabber");
		Entity e = Entity.create(physicsWorld, position, 1f, 0);
		e.createFixture();
		e.setMovementModule(new MovementModule(8.5f, 2f, 0.4f));
		e.setHealthModule(new HealthModule(100f));
		e.setFabberModule(new FabberModule(4f, 10f));
		return fabbers.add(e) ? e : null;
	}
	
	public Entity createTower(Vector2 position) {
		System.out.println("createTower");
		Entity e = Entity.create(physicsWorld, position, 2f, 0);
		e.createFixture();
		e.setHealthModule(new HealthModule(500f));
		e.setWeaponsModule(new WeaponsModule(0.1f, 8f, 3f, 20f, 16f));
		return towers.add(e) ? e : null;
	}
	
	public Entity createEnemy(Vector2 position) {
		System.out.println("createEnemy");
		Entity e = Entity.create(physicsWorld, position, 1.5f, 1);
		e.createFixture();
		e.setMovementModule(new MovementModule(5.5f, 1f, 1f));
		e.setHealthModule(new HealthModule(200f));
		e.setWeaponsModule(new WeaponsModule(0.2f, 8f, 3f, 20f, 16f));
		return enemies.add(e) ? e : null;
	}
	
	public Entity createShot(Entity source, Vector2 target) {
		WeaponsModule wm = source.getWeaponsModule();
		if (wm == null || target == null) return null;
		Vector2 start = source.physicsBody.getPosition();
		float vel = wm.shotVelocity;
		float dmg = wm.damage;
		int team = source.team;
		float timer = wm.shotLifeTime;
		
		Entity e = Entity.create(physicsWorld, start, 0.3f, source.team);
		e.createFixture();
		e.setShotModule(new ShotModule(start, target, vel, dmg, team, timer));
		return shots.add(e) ? e : null;
	}
	
	public Entity createResourcePointFromEntity(Entity source) {
		HealthModule hm = source.getHealthModule();
		if (hm == null) return null;
		Vector2 pos = source.physicsBody.getPosition();
		float integrity = random.nextFloat();
		float rad = source.radius * (integrity * 0.3f + 0.4f);
		float max = hm.maxHealth;
		float res = max * (integrity * 0.4f + 0.2f);
		
		Entity e = Entity.create(physicsWorld, pos, rad, -1);
		e.setResourcePointModule(new ResourcePointModule(res, max));
		return resourcePoints.add(e) ? e : null;
	}
	
	public void destroyEntity(Entity e) {
		physicsWorld.destroyBody(e.physicsBody);
		if (e.getResourcePointModule() == null) {
			// no resource point
			fabbers.markToRemove(e);
			towers.markToRemove(e);
			enemies.markToRemove(e);
			shots.markToRemove(e);
			// create a resource point as wreckage
			createResourcePointFromEntity(e);
		} else {
			// resource point
			resourcePoints.markToRemove(e);
		}
	}
	
	public Collection<Entity> getTargetableEntities() {
		LinkedList<Entity> answer = new LinkedList<Entity>();
		fabbers.addAllToCollection(answer);
		towers.addAllToCollection(answer);
		enemies.addAllToCollection(answer);
		// shots are not targetable
		// resource points are not targetable
		return answer;
	}

	public void addDebugRenderTask(Runnable runnable) {
		debugRenderTasks.add(runnable);
	}

	public void orderBuildTower(Collection<Entity> entities, Vector2 pos) {
		System.out.println("orderBuildTower");
		final Vector2 target = pos.cpy();
		for (Entity e : entities) {
			MovementModule movement = e.getMovementModule();
			movement.setTarget(target.cpy(), 3.5f, new TargetReachedCallback() {
				@Override
				public void run(Vector2 target) {
					// TODO: use FabberModule instead, somehow
					createTower(target);
				}
			});
		}
	}

	public void orderMove(Collection<Entity> entities, Vector2 pos) {
		System.out.println("orderMove");
		final Vector2 target 	= pos.cpy();
		final float radius		= entities.size() * 0.5f;
		for (Entity e : entities) {
			MovementModule movement = e.getMovementModule();
			movement.setTarget(target.cpy(), radius);
		}
	}
}
