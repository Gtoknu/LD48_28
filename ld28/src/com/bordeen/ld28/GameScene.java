package com.bordeen.ld28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScene extends Scene {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	TiledMap map = new TiledMap();
	OrthogonalTiledMapRenderer mapRenderer;
	final static float tileSize = 16f;
	final static float unitScale = 1f/tileSize;
	@Override
	public void start(AssetManager assetManager) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(1, h/w);
		
		batch = new SpriteBatch();
		map = assetManager.get("first level.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);
		super.start(assetManager);
	}
	@Override
	public void load(AssetManager assetManager) {
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load("first level.tmx", TiledMap.class);
	}
	@Override
	public void end() {
		batch.dispose();
		mapRenderer.dispose();
		map.dispose();
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.end();
	}
	

}
