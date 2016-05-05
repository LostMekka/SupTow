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
public class ShotModule {

	public final float damage;
	public final int team;
	private final Vector2 velocity;
	
	private float lifeTime;
	
	public ShotModule(Vector2 start, Vector2 target, float velocity, 
			float damage, int team, float lifeTime) {
		this.velocity = new Vector2(target)
				.sub(start)
				.setLength2(velocity * velocity);
		float r = (float)Math.random();
		this.velocity.rotate(r * 10.0f - 5.0f);
		this.damage = damage;
		this.team = team;
		this.lifeTime = lifeTime;
	}
	
	public void init(Entity e) {
		e.createFixture().setSensor(true);
		e.physicsBody.setBullet(true);
		e.physicsBody.setType(BodyDef.BodyType.DynamicBody);
		e.physicsBody.setLinearDamping(0);
		e.physicsBody.setLinearVelocity(velocity);
	}
	
	public void update(float deltaTime) {
		lifeTime = Math.max(0f, lifeTime - deltaTime);
	}
	
	public boolean isAlive() {
		return lifeTime > 0;
	}
	
	public void onHit(Entity target) {
		HealthModule hm = target.getHealthModule();
		if (hm != null && lifeTime > 0 && target.team != team) {
			hm.dealDamage(damage);
			lifeTime = 0f;
		}
	}
	
}
