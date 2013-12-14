package com.bordeen.ld28;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CCL implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		if(bodyA.getUserData() != null)
		{
			if(bodyA.getUserData().getClass() == Character.class)
			{
				if(contact.getFixtureA().isSensor())
				{
					((Character)bodyA.getUserData()).footTouching++;
				}
				else
				{
					if(bodyB.getUserData() != null)
					{
						if(bodyB.getUserData().getClass() == Clock.class)
						{
							Clock ck = (Clock)bodyB.getUserData();
							ck.catched = true;
						}
					}
				}
			}
		}
		if(bodyB.getUserData() != null)
		{
			if(bodyB.getUserData().getClass() == Character.class)
			{
				if(contact.getFixtureB().isSensor())
				{
					((Character)bodyB.getUserData()).footTouching++;
				}
				else
				{
					if(bodyA.getUserData() != null)
					{	
						if(bodyA.getUserData().getClass() == Clock.class)
						{
							Clock ck = (Clock)bodyA.getUserData();
							ck.catched = true;
						}
					}
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA() != null ? contact.getFixtureA().getBody() : null;
		Body bodyB = contact.getFixtureB() != null ? contact.getFixtureB().getBody() : null;
		if(bodyA != null && bodyA.getUserData() != null)
		{
			if(bodyA.getUserData().getClass() == Character.class)
			{
				if(contact.getFixtureA().isSensor())
				{
					((Character)bodyA.getUserData()).footTouching--;
				}
			}
		}
		if(bodyB != null && bodyB.getUserData() != null)
		{
			if(bodyB.getUserData().getClass() == Character.class)
			{
				if(contact.getFixtureB().isSensor())
				{
					((Character)bodyB.getUserData()).footTouching--;
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
