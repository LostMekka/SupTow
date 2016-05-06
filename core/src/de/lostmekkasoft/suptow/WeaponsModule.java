/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import java.util.Collection;

/**
 *
 * @author fine
 */
public class WeaponsModule extends EntityModule {
	
	public final float reloadTime;
	public final float shotVelocity;
	public final float shotLifeTime;
	public final float damage;
	public final float range;
	
	private float timer = 0;

	public WeaponsModule(float reloadTime, float shotVelocity, 
			float shotLifeTime, float damage, float range) {
		this.reloadTime = reloadTime;
		this.shotVelocity = shotVelocity;
		this.shotLifeTime = shotLifeTime;
		this.damage = damage;
		this.range = range;
	}

	public boolean canTarget(Entity target) {
		return target.team != getEntity().team
				&& getEntity().getDistanceTo(target) <= range
				&& timer <= 0;
	}
	
	@Override
	public void update(float deltaTime) {
		timer = Math.max(0f, timer - deltaTime);
		if (timer == 0f) {
			Collection<Entity> targets = getGame().getTargetableEntities();
			Entity nearest = null;
			float distance = Float.MAX_VALUE;
			for (Entity e : targets) {
				float d = getEntity().getDistanceTo(e);
				if (d < distance && e.team != getEntity().team) {
					distance = d;
					nearest = e;
				}
			}
			if (nearest != null && distance <= range) {
				shootAt(nearest);
			}
		}
	}
	
	private void shootAt(Entity target) {
		getGame().createShot(getEntity(), target.physicsBody.getPosition());
		timer += reloadTime;
	}
	
}
