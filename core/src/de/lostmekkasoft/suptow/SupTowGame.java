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
	SpriteBatch batch;
	Texture img;
	World physicsWorld;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		physicsWorld = new World(Vector2.Zero, true);
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawContacts(true);
		debugRenderer.setDrawInactiveBodies(true);
		debugRenderer.setDrawVelocities(true);
		camera = new OrthographicCamera();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// stuff
		batch.end();
		debugRenderer.render(physicsWorld, camera.combined);
	}
}
