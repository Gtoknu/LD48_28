package com.bordeen.ld28;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Spawner {

	public float interval;
	public boolean onlyClose;
	public int maximum;
	public Enemies enemy;
	public Vector2 position = new Vector2();
	float time = 0;
	public float anim = 0;
	public Array<Enemy> spawned;
	public void update(float dt) {
		for(int i = spawned.size-1; i >= 0; --i)
		{
			Enemy e =spawned.get(i); 
			if(e.isDead())
			{
				spawned.removeIndex(i);
			}
		}
		if(spawned.size < maximum && (!onlyClose || Math.abs(gs.character.pos.x - position.x) < gs.camera.camera.viewportWidth * 0.8f))
		{
			time += dt;
			anim = Math.max(0, Math.min(1, time - (interval-1)));
			if(interval-time < 0.5f && !shaken)
			{
				if(Math.abs(gs.character.pos.x - position.x) <= gs.camera.camera.viewportWidth / 2f)
					gs.camera.shake(0.8f, 0.1f, 20);
				shaken = true;
			}
			if(time >= interval)
			{
				shaken = false;
				time -= interval;
				spawned.add(gs.createEnemy(this));
			}
		}
		else
		{
			anim = 0;
		}
	}
	boolean shaken = false;
	GameScene gs;
	public void start(GameScene gs)
	{
		this.gs = gs;
		interval = interval * GameScene.spb;
		spawned = new Array<Enemy>(false, maximum);
	}

}
