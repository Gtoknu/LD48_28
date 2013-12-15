package com.bordeen.ld28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CCL implements ContactListener {
	GameScene gs;
	public CCL(GameScene gs)
	{
		this.gs = gs;
	}
	@Override
	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		if(bodyA.getUserData() != null)
		{
			if(bodyA.getUserData().getClass() == Character.class)
			{
				Character character = ((Character)bodyA.getUserData());
				if(character.died) return;
				if(contact.getFixtureA().isSensor())
				{
					character.footTouching++;	
				}
				else
				{
					if(bodyB.getUserData() != null)
					{
						if(bodyB.getUserData().getClass() == Clock.class)
						{
							Clock ck = (Clock)bodyB.getUserData();
							ck.catched = true;
							gs.sclock.play();
						}
						else if(bodyB.getUserData().getClass() == Enemy.class)
						{
							Enemy e =(Enemy)bodyB.getUserData();
							if(e.isDead()) return;
							int index = (Integer)contact.getFixtureA().getUserData();
							if(index == 0)
							{
								character.die();
							}
							else if (index == 1)
							{
								e.die();
								Vector2 impulse = new Vector2(character.worldCenter);
								impulse.sub(e.worldCenter);
								impulse.nor();
								float sx = impulse.x, sy = impulse.y;
								impulse.add(Math.signum(sx), 0);
								impulse.nor().scl(-5.25f).scl(bodyB.getMass());
								bodyB.setLinearVelocity(0, 0);
								bodyB.applyLinearImpulse(impulse, e.worldCenter, true);
								impulse.set(-sx, sy).add(0,  Math.signum(sy));
								impulse.nor().scl(5.25f).scl(bodyA.getMass());
								bodyA.setLinearVelocity(0, 0);
								bodyA.applyLinearImpulse(impulse, character.worldCenter, true);
							}
						}
					}
				}
			}
			else if(bodyA.getUserData().getClass() == Enemy.class)
			{
				Enemy e = (Enemy)bodyA.getUserData();
				if(e.isDead()) return;
				if(contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor())
				{
					int index = (Integer)contact.getFixtureA().getUserData();
					e.sensorTouching[index]++;
				}
			}
		}
		if(bodyB.getUserData() != null)
		{
			if(bodyB.getUserData().getClass() == Character.class)
			{
				Character character = (Character)bodyB.getUserData();
				if(character.died) return;
				if(contact.getFixtureB().isSensor())
				{
					character.footTouching++;
				}
				else
				{
					if(bodyA.getUserData() != null)
					{	
						if(bodyA.getUserData().getClass() == Clock.class)
						{
							Clock ck = (Clock)bodyA.getUserData();
							ck.catched = true;
							gs.sclock.play();
						}
						else if(bodyA.getUserData().getClass() == Enemy.class)
						{
							Enemy e =(Enemy)bodyA.getUserData(); 
							if(e.isDead()) return;
							int index = (Integer)contact.getFixtureB().getUserData();
							if(index == 0)
							{
								character.die();
							}
							else if (index == 1)
							{
								e.die();
								Vector2 impulse = new Vector2(character.worldCenter);
								impulse.sub(e.worldCenter);
								impulse.nor();
								float sx = impulse.x, sy = impulse.y;
								impulse.add(Math.signum(sx), 0);
								impulse.nor().scl(-5.25f).scl(bodyA.getMass());
								bodyA.setLinearVelocity(0, 0);
								bodyA.applyLinearImpulse(impulse, e.worldCenter, true);
								impulse.set(-sx, sy).add(0,  Math.signum(sy));
								impulse.nor().scl(5.25f).scl(bodyB.getMass());
								bodyB.setLinearVelocity(0, 0);
								bodyB.applyLinearImpulse(impulse, character.worldCenter, true);
								
							}
						}
					}
				}
			}
			else if(bodyB.getUserData().getClass() == Enemy.class)
			{
				Enemy e = (Enemy)bodyB.getUserData();
				if(e.isDead()) return;
				if(contact.getFixtureB().isSensor() && !contact.getFixtureA().isSensor())
				{
					int index = (Integer)contact.getFixtureB().getUserData();
					e.sensorTouching[index]++;
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
			else if(bodyA.getUserData().getClass() == Enemy.class)
			{
				Enemy e = (Enemy)bodyA.getUserData();
				if(contact.getFixtureA().isSensor() && (contact.getFixtureB() == null || !contact.getFixtureB().isSensor()))
				{
					e.sensorTouching[(Integer)contact.getFixtureA().getUserData()]--;
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
			else if(bodyB.getUserData().getClass() == Enemy.class)
			{
				if(contact.getFixtureB().isSensor() && (contact.getFixtureA() == null || !contact.getFixtureA().isSensor()))
				{
					((Enemy)bodyB.getUserData()).sensorTouching[(Integer)contact.getFixtureB().getUserData()]--;
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
