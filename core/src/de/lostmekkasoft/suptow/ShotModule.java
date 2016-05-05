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
public class ShotModule {

	public final float damage;
	private final Vector2 velocity;
	
	public ShotModule(Vector2 start, Vector2 target, float velocity, float damage) {
		this.damage = damage;
		this.velocity = new Vector2(target)
				.sub(start)
				.setLength2(velocity * velocity);
	}
	
	public void init(Entity e) {
		e.physicsBody.setLinearVelocity(velocity);
	}
	
}
