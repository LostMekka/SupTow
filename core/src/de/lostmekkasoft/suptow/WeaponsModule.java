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
public class WeaponsModule {
	
	public final float reloadTime;
	public final float shotVelocity;
	public final float shotLifeTime;
	public final float damage;
	public final float range;
	
	private float timer = 0;
	private Entity self = null;

	public WeaponsModule(float reloadTime, float shotVelocity, 
			float shotLifeTime, float damage, float range) {
		this.reloadTime = reloadTime;
		this.shotVelocity = shotVelocity;
		this.shotLifeTime = shotLifeTime;
		this.damage = damage;
		this.range = range;
	}

	public boolean canTarget(Entity target) {
		return target.team != self.team
				&& self.getDistanceTo(target) <= range
				&& timer <= 0;
	}
	
	public void init(Entity e) {
		self = e;
	}
	
	public void update(float deltaTime) {
		timer = Math.max(0f, timer - deltaTime);
		if (timer == 0f) {
			Collection<Entity> targets = SupTowGame.getInstance().getTargetableEntities();
			Entity nearest = null;
			float distance = Float.MAX_VALUE;
			for (Entity e : targets) {
				float d = self.getDistanceTo(e);
				if (d < distance && e.team != self.team) {
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
		SupTowGame.getInstance().createShot(self, target.physicsBody.getPosition());
		timer += reloadTime;
	}
	
}
