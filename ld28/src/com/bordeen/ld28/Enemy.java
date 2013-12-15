package com.bordeen.ld28;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class Enemy {
	Body body;
	TextureRegion tr;
	Enemies type;
	public boolean flipDir = false;
	public int[] sensorTouching = new int[3];
	Vector2 lnVel;
	Vector2 worldCenter;
	GameScene gs;
	public boolean died;
	public void calcVars()
	{
		lnVel = body.getLinearVelocity();
		worldCenter = body.getWorldCenter();
	}
	public Enemy(GameScene gs, Spawner spawner)
	{
		this.gs = gs;
		for(int i = 0; i < 3; ++i)
		{
			sensorTouching[i] = 0;
		}
		BodyDef bd = new BodyDef();
		bd.position.set(spawner.position);
		bd.fixedRotation = true;
		bd.type = BodyType.DynamicBody;
		body = gs.world.createBody(bd);
		body.setUserData(this);
		PolygonShape sp = new PolygonShape();
		type = spawner.enemy;
		tr = gs.enemyTR[type.ordinal()];
		float hy = tr.getRegionHeight() * 0.5f * GameScene.unitScale - 0.01f;
		float hx = tr.getRegionWidth() * 0.5f * GameScene.unitScale - 0.01f;
		sp.setAsBox(hx, hy);
		Fixture f = body.createFixture(sp, 1);
		Filter fd = f.getFilterData();
		fd.categoryBits = Filters.enemy;
		fd.maskBits &= ~Filters.clock;
		f.setFilterData(fd);
		
		sp.setAsBox(0.15f, 0.1f, new Vector2(0f, -hy), 0);
		f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(0));
		f = body.createFixture(sp, 1);
		fd = f.getFilterData();
		fd.categoryBits = Filters.enemy;
		fd.maskBits &= ~Filters.clock;
		f.setFilterData(fd);
		
		sp.setAsBox(0.1f, 0.15f, new Vector2(-hx, 0), 0);
		f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(1));
		fd = f.getFilterData();
		fd.categoryBits = Filters.enemy;
		fd.maskBits &= ~Filters.clock;
		f.setFilterData(fd);
		sp.setAsBox(0.1f, 0.15f, new Vector2(hx, 0), 0);
		f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(2));
		fd = f.getFilterData();
		fd.categoryBits = Filters.enemy;
		fd.maskBits &= ~Filters.clock;
		f.setFilterData(fd);
		
		sp.dispose();
		flipDir = MathUtils.randomBoolean();
	}
	boolean alreadyDied = false;
	float diedTime = 0;
	public final static float dieInterval = 1.0f;
	public void update(float dt)
	{
		if(died)
		{
			if(!alreadyDied)
			{
				Array<Fixture> fixList = body.getFixtureList();
				for(int i = 0; i < fixList.size; ++i)
				{
					Fixture f = fixList.get(i);
					Filter fd = f.getFilterData();
					fd.maskBits = Filters.scenary;
					f.setFilterData(fd);
				}
				alreadyDied = true;
			}
			diedTime += dt;
			return;
		}
		float charDesiredVel = 0;
		switch(type)
		{
		case Sheep:
			if(sensorTouching[1] > 0)
				flipDir = false;
			else if(sensorTouching[2] > 0)
				flipDir = true;
			if(sensorTouching[0] > 0)
			{
				body.applyLinearImpulse(0, 3f * body.getMass(), worldCenter.x, worldCenter.y, true);
			}
			else
			{
				charDesiredVel = flipDir ? -5 : 5;
			}
			break;
		case Cup:
			flipDir = worldCenter.x - gs.character.worldCenter.x > 0;
			charDesiredVel = flipDir ? -2 : 2;
			if(Math.abs(gs.character.worldCenter.x - worldCenter.x)  < 5 && (gs.character.worldCenter.y - worldCenter.y) > 1 && sensorTouching[0] > 0)
			{
				body.applyLinearImpulse(0, 1.8f * body.getMass(), worldCenter.x, worldCenter.y, true);
			}
			break;
		case Pillow:
			if(sensorTouching[1] > 0)
				flipDir = false;
			else if(sensorTouching[2] > 0)
				flipDir = true;
			float rel = gs.character.worldCenter.x - worldCenter.x;
			if(Math.abs(rel) < 2)
			{
				if( Math.signum(rel) == (flipDir ? 1 : -1) )
					charDesiredVel = flipDir ? -1 : 1;
				
				if(Math.abs(rel) < 1.5f)
				{
					flipDir = rel < 0;
					charDesiredVel = flipDir ? -0.5f : 0.5f;
					if(sensorTouching[0] > 0)
						body.applyLinearImpulse(0, 1.1f * body.getMass(), worldCenter.x, worldCenter.y, true);
				}
			}
			else
			{
				charDesiredVel = flipDir ? -1 : 1;
			}
			
		}
		float velChange = charDesiredVel - lnVel.x;
		float imp = body.getMass() * velChange;
		body.applyLinearImpulse(imp, 0, worldCenter.x, worldCenter.y, true);
	}
	public void render(SpriteBatch batch)
	{
		tr.flip(flipDir, false);
		batch.draw(tr, worldCenter.x - tr.getRegionWidth() * 0.5f * GameScene.unitScale, worldCenter.y - tr.getRegionHeight() * 0.5f * GameScene.unitScale, tr.getRegionWidth() * GameScene.unitScale, tr.getRegionHeight() * GameScene.unitScale);
		tr.flip(flipDir, false);
	}
}
