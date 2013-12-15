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
			if(e.diedTime > Enemy.dieInterval)
			{
				spawned.removeIndex(i);
				gs.world.destroyBody(e.body);
			}
		}
		if(spawned.size < maximum)
		{
			time += dt;
			anim = Math.max(0, Math.min(1, time - (interval-1)));
			if(time >= interval)
			{
				time -= interval;
				spawned.add(gs.createEnemy(this));
			}
		}
		else
		{
			anim = 0;
		}
	}
	GameScene gs;
	public void start(GameScene gs)
	{
		this.gs = gs;
		interval = interval * GameScene.spb;
		spawned = new Array<Enemy>(false, maximum);
	}

}
