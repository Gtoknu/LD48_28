package com.bordeen.ld28;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy {
	Body body;
	TextureRegion tr;
	public Enemy(GameScene gs, Spawner spawner)
	{
		BodyDef bd = new BodyDef();
		bd.position.set(spawner.position);
		bd.fixedRotation = true;
		bd.type = BodyType.DynamicBody;
		body = gs.world.createBody(bd);
		
		PolygonShape sp = new PolygonShape();
		tr = gs.enemyTR[spawner.enemy.ordinal()];
		sp.setAsBox(tr.getRegionWidth() * 0.5f * GameScene.unitScale - 0.01f, tr.getRegionHeight() * 0.5f * GameScene.unitScale - 0.01f);
		body.createFixture(sp, 1);
	}
	
	public void render(SpriteBatch batch)
	{
		Vector2 pos = body.getPosition();
		batch.draw(tr, pos.x - tr.getRegionWidth() * 0.5f * GameScene.unitScale, pos.y - tr.getRegionHeight() * 0.5f * GameScene.unitScale, tr.getRegionWidth() * GameScene.unitScale, tr.getRegionHeight() * GameScene.unitScale);
	}
}
