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
public class ResourcePointModule extends EntityModule {
	
	public final float maxResource;
	
	private float resource;

	public ResourcePointModule(float resource, float maxResource) {
		this.maxResource = maxResource;
		this.resource = resource;
	}

	@Override
	public void onInit() {
		getEntity().createFixture().setSensor(true);
	}
	
	@Override
	public boolean isAlive() {
		return resource > 0;
	}
	
	public float extract(float max) {
		float ans = Math.min(resource, max);
		resource -= ans;
		return ans;
	}
	
	public float getResource() {
		return resource;
	}
	
	public float getResourcePercentage() {
		return resource / maxResource;
	}
	
}
