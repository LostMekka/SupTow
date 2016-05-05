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
public class HealthModule {
	
	public final float maxHealth;
	
	private float health;

	public HealthModule(float maxHealth) {
		this.maxHealth = maxHealth;
		health = maxHealth;
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getHealthPercentage() {
		return health / maxHealth;
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	/**
	 * deals damage and returns true, if the entity should die afterwards.
	 * @param amount
	 * @return 
	 */
	public boolean dealDamage(float amount) {
		health = Math.max(0f, health - amount);
		return !isAlive();
	}
	
}
