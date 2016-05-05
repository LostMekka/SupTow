package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author fine
 */
public class Entity {
	
	public final Body physicsBody;
	public final int team;
	
	private MovementModule movementModule = null;
	private HealthModule healthModule = null;
	private WeaponsModule weaponsModule = null;

	public static Entity create(World world, Vector2 position, float radius, int team){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position.x, position.y);
		Body body = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.3f;
		body.createFixture(fixtureDef);
		circle.dispose();
		return new Entity(body, team);
	}
	
	public Entity(Body physicsBody, int team) {
		this.physicsBody = physicsBody;
		this.team = team;
	}

	public MovementModule getMovementModule() {
		return movementModule;
	}

	public void setMovementModule(MovementModule movementModule) {
		this.movementModule = movementModule;
		movementModule.init(this);
	}

	public HealthModule getHealthModule() {
		return healthModule;
	}

	public void setHealthModule(HealthModule healthModule) {
		this.healthModule = healthModule;
	}

	public WeaponsModule getWeaponsModule() {
		return weaponsModule;
	}

	public void setWeaponsModule(WeaponsModule weaponsModule) {
		this.weaponsModule = weaponsModule;
	}
	
	public void update(float deltaTime) {
		if (movementModule != null) movementModule.update(this, deltaTime);
		if (weaponsModule != null) weaponsModule.update(deltaTime);
	}
	
	public boolean needsToBeRemoved() {
		if (healthModule != null && !healthModule.isAlive()) return true;
		return false;
	}
	
}
