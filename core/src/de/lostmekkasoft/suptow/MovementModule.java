/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;

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
	private TargetReachedCallback targetReachedCallback;

	public TargetReachedCallback getTargetReachedCallback() {
		return targetReachedCallback;
	}

	public void setTargetReachedCallback(TargetReachedCallback targetReachedCallback) {
		this.targetReachedCallback = targetReachedCallback;
	}

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
	
	void targetReached() {
		if (targetReachedCallback != null) {
			targetReachedCallback.run(target);
		}
		unsetTarget();
	}
	
	public void init(Entity e) {
		e.physicsBody.setLinearDamping(linearDamping);
	}

	public void update(Entity e, float deltaTime) {
		if (target == null) return;
		Vector2 diff = new Vector2(target).sub(e.physicsBody.getPosition());
		if (diff.len2() <= squaredMinDistance) {
			targetReached();
			return;
		}
		diff.setLength2(sqaredMovementForce);
		e.physicsBody.applyForceToCenter(diff, true);
	}
	
}
