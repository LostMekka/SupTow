/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

/**
 *
 * @author fine
 */
public class WeaponsModule {
	
	public final float reloadTime;
	public final float shotVelocity;
	public final float damage;
	public final float range;
	
	private Entity currentTarget = null;
	private float timer = 0;

	public WeaponsModule(float reloadTime, float shotVelocity, float damage, float range) {
		this.reloadTime = reloadTime;
		this.shotVelocity = shotVelocity;
		this.damage = damage;
		this.range = range;
	}

	public Entity getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(Entity currentTarget) {
		this.currentTarget = currentTarget;
	}
	
	public void update(float deltaTime) {
		timer = Math.max(0f, timer - deltaTime);
		if (timer == 0f) {
			// TODO: shoot stuff!
		}
	}
	
}
