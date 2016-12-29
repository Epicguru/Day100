package co.uk.epicguru.helpers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import co.uk.epicguru.map.Entity;

public class ContactManager implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		
		if(a == null || b == null || a.getUserData() == null || b.getUserData() == null || !(a.getUserData() instanceof Entity) || !(b.getUserData() instanceof Entity)){
			return;
		}
		
		Entity aEntity = (Entity) a.getUserData();
		Entity bEntity = (Entity) b.getUserData();
		
		aEntity.bodyContact(b, bEntity, contact);
		bEntity.bodyContact(a, aEntity, contact);
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

}
