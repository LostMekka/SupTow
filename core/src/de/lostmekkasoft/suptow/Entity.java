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
	
	private final Body physicsBody;
	
	private MovementModule movementModule = null;

	public static Entity create(World world, Vector2 position, float radius){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.linearDamping = 0.5f;
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
		return new Entity(body);
	}
	
	private Entity(Body body) {
		this.physicsBody = body;
	}

	public Body getPhysicsBody() {
		return physicsBody;
	}

	public MovementModule getMovementModule() {
		return movementModule;
	}

	public void setMovementModule(MovementModule movementModule) {
		this.movementModule = movementModule;
	}
	
	public void update(float delta) {
		if (movementModule != null) movementModule.update(this, delta);
	}
	
}
