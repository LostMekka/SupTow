/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 *
 * @author fine
 */
public class MovementModule extends EntityModule {
	
	private final float sqaredMovementForce;
	private final float linearDamping;
	private final float sqaredDefaultMinDistance;
	
	private Vector2 target = null;
	private float squaredMinDistance;
	private TargetReachedCallback targetReachedCallback;

	public MovementModule(float movementForce, float linearDamping, float defaultMinDistance) {
		sqaredMovementForce = movementForce * movementForce;
		sqaredDefaultMinDistance = defaultMinDistance * defaultMinDistance;
		this.linearDamping = linearDamping;
	}

	public void setTarget(Vector2 target) {
		setTarget(target, sqaredDefaultMinDistance, null);
	}

	public void setTarget(Vector2 target, TargetReachedCallback cb) {
		setTarget(target, sqaredDefaultMinDistance, cb);
	}

	public void setTarget(Vector2 target, float minDistance) {
		setTarget(target, minDistance, null);
	}
	
	public void setTarget(Vector2 target, float minDistance, TargetReachedCallback cb) {
		this.target = target;
		squaredMinDistance = minDistance * minDistance;
		targetReachedCallback = cb;
	}
	
	public void unsetTarget() {
		target = null;
		squaredMinDistance = 0f;
		targetReachedCallback = null;
	}
	
	void targetReached() {
		if (targetReachedCallback != null) targetReachedCallback.run(target);
		unsetTarget();
	}
	
	@Override
	public void onInit() {
		getBody().setType(BodyDef.BodyType.DynamicBody);
		getBody().setLinearDamping(linearDamping);
	}

	@Override
	public void update(float deltaTime) {
		if (target == null) return;
		Vector2 diff = new Vector2(target).sub(getPosition());
		if (diff.len2() <= squaredMinDistance) {
			targetReached();
			return;
		}
		diff.setLength2(sqaredMovementForce);
		getBody().applyForceToCenter(diff, true);
	}
	
}
