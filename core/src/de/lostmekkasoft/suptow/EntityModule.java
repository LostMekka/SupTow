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
public abstract class EntityModule {
	
	private Entity entity = null;

	public final Entity getEntity() {
		return entity;
	}
	
	public final Body getBody() {
		return entity.physicsBody;
	}
	
	public final Vector2 getPosition() {
		return entity.physicsBody.getPosition();
	}
	
	public final SupTowGame getGame() {
		return SupTowGame.getInstance();
	}
	
	public final void init(Entity e) {
		entity = e;
		onInit();
	}
	
	public void onInit() {}
	public void update(float deltaTime) {}
	public boolean isAlive() { return true; }
	
}
