/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 *
 * @author fine
 */
public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

	@Override
	public void beginContact(Contact cntct) {
	}

	@Override
	public void endContact(Contact cntct) {
	}

	@Override
	public void preSolve(Contact cntct, Manifold mnfld) {
		Entity e1 = (Entity)cntct.getFixtureA().getUserData();
		Entity e2 = (Entity)cntct.getFixtureB().getUserData();
		boolean e1IsShot = e1.getShotModule() != null;
		boolean e2IsShot = e2.getShotModule() != null;
		if (e1IsShot != e2IsShot) {
			Entity shot = e1IsShot ? e1 : e2;
			Entity target = e1IsShot ? e2 : e1;
			shot.getShotModule().onHit(target);
			cntct.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact cntct, ContactImpulse ci) {
	}
	
}
