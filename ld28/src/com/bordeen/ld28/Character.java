package com.bordeen.ld28;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Character implements InputProcessor {
	Body body;
	Texture sheet;
	/**
	 * @param map
	 * @param world
	 * @param sheet
	 */
	void create(TiledMap map, World world, Texture sheet)
	{
		this.sheet = sheet;
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.fixedRotation = true;
		Ellipse charFlag = ((EllipseMapObject)map.getLayers().get(1).getObjects().get(0)).getEllipse();
		bd.position.x = charFlag.x * GameScene.unitScale;
		bd.position.y = charFlag.y * GameScene.unitScale;
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(0.315f, 0.49f);
		body = world.createBody(bd);
		body.setUserData(this);
		body.createFixture(ps, 2);
		ps.setAsBox(0.15f, 0.1f, new Vector2(0f, -0.49f), 0);
		Fixture f = body.createFixture(ps, 1);
		f.setSensor(true);
		ps.dispose();
	}
	int footTouching = 0;
	Vector2 pos;
	Vector2 lnVel;
	Vector2 worldCenter;
	void calcVars()
	{
		pos = body.getPosition();
		lnVel = body.getLinearVelocity();
		worldCenter = body.getWorldCenter();
	}
	
	void draw(SpriteBatch batch)
	{
		batch.draw(sheet, pos.x - 0.5f, pos.y - 0.5f, 1, 1, 0, 0, 32, 32, flipX, false);
	}
	int keyState = 0;
	final static int KLEFT = 0x1;
	final static int KRIGHT = 0x2;
	final static int KUP = 0x4;
	final static int KDOWN = 0x8;
	void update()
	{
		float charDesiredVel = 0;
		switch(keyState & (KLEFT | KRIGHT))
		{
		case KLEFT:
			charDesiredVel = -5; break;
		case KRIGHT:
			charDesiredVel = 5; break;
		}
		float velChange = charDesiredVel - lnVel.x;
		float imp = body.getMass() * velChange;
		body.applyLinearImpulse(imp, 0, worldCenter.x, worldCenter.y, true);
	}
	boolean flipX = false;
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode)
		{
		case Keys.LEFT:
			keyState |= KLEFT; flipX = true; break;

		case Keys.RIGHT:
			keyState |= KRIGHT;  flipX = false; break;

		case Keys.UP:
			if(footTouching > 0)
			{
				body.applyLinearImpulse(0, 6 * body.getMass(), worldCenter.x, worldCenter.y, true);
			}
			keyState |= KUP;
			break;
		case Keys.DOWN:
			keyState |= KDOWN; break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode)
		{
		case Keys.LEFT:
			keyState &= ~KLEFT; break;

		case Keys.RIGHT:
			keyState &= ~KRIGHT; break;

		case Keys.UP:
			keyState &= ~KUP; break;

		case Keys.DOWN:
			keyState &= ~KDOWN; break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
