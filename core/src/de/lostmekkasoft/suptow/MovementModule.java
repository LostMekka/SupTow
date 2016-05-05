/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 *
 * @author fine
 */
public class MovementModule {
	
	private final float sqaredMovementForce;
	private final float linearDamping;
	private final float sqaredDefaultMinDistance;
	
	private Vector2 target = null;
	private float squaredMinDistance;

	public MovementModule(float movementForce, float linearDamping, float defaultMinDistance) {
		sqaredMovementForce = movementForce * movementForce;
		sqaredDefaultMinDistance = defaultMinDistance * defaultMinDistance;
		this.linearDamping = linearDamping;
	}

	public void setTarget(Vector2 target) {
		this.target = target;
		squaredMinDistance = sqaredDefaultMinDistance;
	}

	public void setTarget(Vector2 target, float minDistance) {
		this.target = target;
		squaredMinDistance = minDistance * minDistance;
	}
	
	public void unsetTarget() {
		target = null;
		squaredMinDistance = 0f;
	}
	
	public void init(Entity e) {
		e.getPhysicsBody().setLinearDamping(linearDamping);
	}

	public void update(Entity e, float deltaTime) {
		if (target == null) return;
		Body b = e.getPhysicsBody();
		Vector2 diff = new Vector2(target).sub(b.getPosition());
		if (diff.len2() <= squaredMinDistance) {
			unsetTarget();
			return;
		}
		diff.setLength2(sqaredMovementForce);
		e.getPhysicsBody().applyForceToCenter(diff, true);
	}
	
}
