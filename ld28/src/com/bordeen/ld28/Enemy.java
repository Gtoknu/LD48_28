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
	TextureRegion[] animSheet;
	Enemies type;
	public boolean flipDir = false;
	public int[] sensorTouching = new int[3];
	Vector2 lnVel;
	Vector2 worldCenter = new Vector2();
	GameScene gs;
	private boolean dead;
	void die()
	{
		if(dead) return;
		gs.camera.shake(0.3f, 0.2f, 60);
		gs.kill.play();
		dead = true;
		Array<Fixture> fixList = body.getFixtureList();
		for(int i = 0; i < fixList.size; ++i)
		{
			Fixture f = fixList.get(i);
			Filter fd = f.getFilterData();
			fd.maskBits = Filters.scenary;
			f.setFilterData(fd);
			f.setFriction(0.3f);
			f.setRestitution(0.5f);
		}
	}
	boolean isDead()
	{
		return dead;
	}
	float sprW, sprH;
	public void calcVars()
	{
		lnVel = body.getLinearVelocity();
		worldCenter = body.getWorldCenter();
	}
	int currentFrame = 0;
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
		animSheet = gs.enemyTR[type.ordinal()];
		sprH = animSheet[0].getRegionHeight();
		sprW = animSheet[0].getRegionWidth();
		float hy = sprH * 0.5f * GameScene.unitScale - 0.01f;
		float hx = sprW * 0.5f * GameScene.unitScale - 0.01f;
		sp.setAsBox(hx, hy);
		Fixture f = body.createFixture(sp, 1);
		Filter fd = f.getFilterData();
		fd.categoryBits = Filters.enemy;
		fd.maskBits &= ~Filters.clock;
		f.setFilterData(fd);
		f.setFriction(0);
		
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
	float diedTime = 0;
	public final static float dieInterval = 1.5f;
	float lilTimer = 0;
	public void update(float dt)
	{
		if(worldCenter.y < -2)
		{
			die();
			diedTime = dieInterval;
		}
		if(dead)
		{
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
				lilTimer = 0;
				body.applyLinearImpulse(0, 3f * body.getMass(), worldCenter.x, worldCenter.y, true);
			}
			else
			{
				lilTimer += dt;
				charDesiredVel = flipDir ? -5 : 5;
			}
			currentFrame = lilTimer < 0.2f ? 0 : 1;
			break;
		case Cup:
			flipDir = worldCenter.x - gs.character.worldCenter.x > 0;
			charDesiredVel = flipDir ? -2 : 2;
			if(Math.abs(gs.character.worldCenter.x - worldCenter.x)  < 5 && (gs.character.worldCenter.y - worldCenter.y) > 1 && sensorTouching[0] > 0)
			{
				body.applyLinearImpulse(0, 1.8f * body.getMass(), worldCenter.x, worldCenter.y, true);
			}
			lilTimer += dt;
			if(lilTimer >= (GameScene.spb * 0.5f))
			{
				lilTimer -=(GameScene.spb * 0.5f);
				currentFrame = (currentFrame+1) % 2;
			}
			break;
		case Pillow:
			if(sensorTouching[1] > 0)
				flipDir = false;
			else if(sensorTouching[2] > 0)
				flipDir = true;
			if(sensorTouching[0] == 0)
			{
				currentFrame = 2;
			}
			else
			{
				lilTimer += dt;
				if(lilTimer >= (GameScene.spb * 0.5f))
				{
					lilTimer -=(GameScene.spb * 0.5f);
					currentFrame = (currentFrame+1) % 2;
				}
			}
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
						body.applyLinearImpulse(0, 2.3f * body.getMass(), worldCenter.x, worldCenter.y, true);
				}
				else
				{
					currentFrame = 0;
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
		animSheet[currentFrame].flip(flipDir, dead);
		batch.draw(animSheet[currentFrame], worldCenter.x - sprW * 0.5f * GameScene.unitScale, worldCenter.y - sprH* 0.5f * GameScene.unitScale, sprW * GameScene.unitScale, sprH * GameScene.unitScale);
		animSheet[currentFrame].flip(flipDir, dead);
	}
}
