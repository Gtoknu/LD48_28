package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
	@Override
	public void create() {		
		assetManager = new AssetManager();
		currentScene = scenes.put("Main Menu", new MainMenu());
		scenes.put("Game", new GameScene());
		currentScene = scenes.get("Game");
		Iterator<Entry<String, Scene>> entries = scenes.entries().iterator();
		while(entries.hasNext())
		{
			Entry<String, Scene> e = entries.next();
			e.value.load(assetManager);
		}
		
		assetManager.finishLoading();
		
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
	}

	@Override
	public void render() {
		currentScene.render();
		if(currentScene.nextScene)
		{
			Scene nextScene = scenes.get(currentScene.nextSceneName);
			currentScene.end();
			currentScene = nextScene;
			currentScene.start(assetManager);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
