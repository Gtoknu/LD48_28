package com.bordeen.ld28;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy {
	Body body;
	TextureRegion tr;
	boolean flipDir = false;
	public Enemy(GameScene gs, Spawner spawner)
	{
		BodyDef bd = new BodyDef();
		bd.position.set(spawner.position);
		bd.fixedRotation = true;
		bd.type = BodyType.DynamicBody;
		body = gs.world.createBody(bd);
		
		PolygonShape sp = new PolygonShape();
		tr = gs.enemyTR[spawner.enemy.ordinal()];
		float hy = tr.getRegionHeight() * 0.5f * GameScene.unitScale - 0.01f;
		float hx = tr.getRegionWidth() * 0.5f * GameScene.unitScale - 0.01f;
		sp.setAsBox(hx, hy);
		body.createFixture(sp, 1);
		sp.setAsBox(0.15f, 0.1f, new Vector2(0f, -hy), 0);
		Fixture f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(0));
		sp.setAsBox(0.1f, 0.15f, new Vector2(-hx, 0), 0);
		f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(1));
		sp.setAsBox(0.1f, 0.15f, new Vector2(hx, 0), 0);
		f = body.createFixture(sp, 1);
		f.setSensor(true);
		f.setUserData(new Integer(2));
		flipDir = MathUtils.randomBoolean();
	}
	public void update()
	{
		
	}
	public void render(SpriteBatch batch)
	{
		Vector2 pos = body.getPosition();
		tr.flip(flipDir, false);
		batch.draw(tr, pos.x - tr.getRegionWidth() * 0.5f * GameScene.unitScale, pos.y - tr.getRegionHeight() * 0.5f * GameScene.unitScale, tr.getRegionWidth() * GameScene.unitScale, tr.getRegionHeight() * GameScene.unitScale);
		tr.flip(flipDir, false);
	}
}
