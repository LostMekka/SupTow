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
	
	private Vector2 target = null;
	private float minSquaredDistance;

	public MovementModule(float movementForce) {
		sqaredMovementForce = movementForce * movementForce;
	}

	public void setTarget(Vector2 target) {
		this.target = target;
	}

	public void setTarget(Vector2 target, float minDistance) {
		this.target = target;
		minSquaredDistance = minDistance * minDistance;
	}
	
	public void unsetTarget() {
		target = null;
		minSquaredDistance = 0f;
	}

	public void update(Entity e, float deltaTime) {
		if (target == null) return;
		Body b = e.getPhysicsBody();
		Vector2 diff = target.sub(b.getPosition());
		if (diff.len2() <= minSquaredDistance) {
			unsetTarget();
			System.out.println("reached " + b.getPosition().toString() + " " + target.toString());
			return;
		}
		Vector2 force = diff.setLength2(sqaredMovementForce);
		e.getPhysicsBody().applyForceToCenter(force, true);
	}
	
}
