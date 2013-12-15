package com.bordeen.ld28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Credits extends Scene implements InputProcessor {
	TextureRegion toShow;
	OrthographicCamera uiCam;
	SpriteBatch batch;
	@Override
	public void start(AssetManager assetManager) {
		Texture sheet = assetManager.get("data/inscreds.png", Texture.class);
		toShow = new TextureRegion(sheet, 0, 335, 768, 486);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		uiCam = new OrthographicCamera(800, 800 * h/w);
		uiCam.position.set(uiCam.viewportWidth/2f, uiCam.viewportHeight/2f, 0);
		uiCam.update();
		batch = new SpriteBatch(2);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void load(AssetManager assetManager) {
		assetManager.load("data/inscreds.png", Texture.class);
	}

	@Override
	public void end() {
		batch.dispose();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.898f, 0.901f, 0.780f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(uiCam.combined);
		batch.begin();
		batch.draw(toShow, uiCam.viewportWidth * 0.5f - 768 * 0.5f,  uiCam.viewportHeight - 20 - 486);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		float h = height;
		float w = width;
		uiCam = new OrthographicCamera(800, 800 * h/w);
		uiCam.position.set(uiCam.viewportWidth/2f, uiCam.viewportHeight/2f, 0);
		uiCam.update();
	}

	@Override
	public boolean keyDown(int keycode) {
		nextScene = true;
		nextSceneName = "Main Menu";
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
