package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author fine
 */
public class Entity {
	
	public final Body physicsBody;
	public final int team;
	
	private MovementModule 	movementModule 	= null;
	private HealthModule 	healthModule 	= null;
	private WeaponsModule 	weaponsModule 	= null;
	private ShotModule 		shotModule 		= null;
	private FabberModule	fabberModule	= null;

	public FabberModule getFabberModule() {
		return fabberModule;
	}

	public void setFabberModule(FabberModule fabberModule) {
		this.fabberModule = fabberModule;
	}

	public static Entity create(World world, Vector2 position, float radius, 
			int team, boolean isStatic){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = isStatic 
				? BodyDef.BodyType.StaticBody 
				: BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position.x, position.y);
		Body body = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.3f;
		Fixture fixture = body.createFixture(fixtureDef);
		circle.dispose();
		Entity e = new Entity(body, team);
		fixture.setUserData(e);
		return e;
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

	public ShotModule getShotModule() {
		return shotModule;
	}

	public void setShotModule(ShotModule shotModule) {
		this.shotModule = shotModule;
		shotModule.init(this);
	}
	
	public void update(float deltaTime) {
		if (movementModule != null) movementModule.update(this, deltaTime);
		if (weaponsModule != null) weaponsModule.update(deltaTime);
	}
	
	public boolean needsToBeRemoved() {
		if (healthModule != null && !healthModule.isAlive()) return true;
		if (shotModule != null && !shotModule.isAlive()) return true;
		return false;
	}
	
}
