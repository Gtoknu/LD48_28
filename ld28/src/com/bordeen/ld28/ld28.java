package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap.Entries;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class ld28 implements ApplicationListener {
	AssetManager assetManager;
	ObjectMap<String, Scene> scenes = new ObjectMap<String, Scene>();
	Scene currentScene = null;
	Music[] stLoops;
	int playingLoop = 0;
	@Override
	public void create() {
		assetManager = new AssetManager();
		scenes.put("Main Menu", new MainMenu());
		scenes.put("Game", new GameScene());
		scenes.put("Credits", new Credits());
		scenes.put("Instructions", new Instructions());
		currentScene = scenes.get("Main Menu");
		Iterator<Entry<String, Scene>> entries = scenes.entries().iterator();
		while(entries.hasNext())
		{
			Entry<String, Scene> e = entries.next();
			e.value.load(assetManager);
		}
		
		assetManager.finishLoading();
		stLoops = new Music[] {
			Gdx.audio.newMusic(Gdx.files.internal("data/organ.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("data/piano.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("data/bos.mp3"))
		};
		for(int i = 0; i < stLoops.length; ++i)
		{
			stLoops[i].setLooping(true);
			stLoops[i].setVolume(0.5f);
		}
		
		stLoops[playingLoop].play();
		
		currentScene.start(assetManager);
	}

	@Override
	public void dispose() {
		Iterator<Entry<String, Scene>> entries = scenes.entries().iterator();
		while(entries.hasNext())
		{
			Entry<String, Scene> e = entries.next();
			e.value.dispose();
		}
		assetManager.dispose();
		
		for(int i = 0; i < stLoops.length; ++i)
		{
			stLoops[i].dispose();
		}
	}

	@Override
	public void render() {
		currentScene.render();
		if(currentScene.nextScene)
		{
			Scene nextScene = scenes.get(currentScene.nextSceneName);
			currentScene.end();
			currentScene.nextScene = false;
			currentScene = nextScene;
			currentScene.start(assetManager);
		}
		if(currentScene.changeStLoop)
		{
			currentScene.changeStLoop = false;
			if(currentScene.stLoopToPlay != playingLoop)
			{
				stLoops[playingLoop].stop();
				stLoops[currentScene.stLoopToPlay].play();
				playingLoop = currentScene.stLoopToPlay;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		currentScene.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
