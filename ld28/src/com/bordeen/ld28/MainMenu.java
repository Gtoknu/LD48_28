package com.bordeen.ld28;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainMenu extends Scene implements InputProcessor {
	TextureRegion[] logo;
	OrthographicCamera uiCam;
	SpriteBatch batch;
	GameScene gs;
	public MainMenu(GameScene gs)
	{
		this.gs = gs;
	}
	@Override
	public void start(AssetManager assetManager) {
		Texture sheet = assetManager.get("data/logonap.png", Texture.class);
		logo = new TextureRegion[]
				{
					new TextureRegion(sheet, 0, 259, 525, 70),
					new TextureRegion(sheet, 0, 331, 250, 130),
					new TextureRegion(sheet, 0, 0, 786, 257)
				};
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		uiCam = new OrthographicCamera(800, 800 * h/w);
		uiCam.position.set(uiCam.viewportWidth/2f, uiCam.viewportHeight/2f, 0);
		uiCam.update();
		batch = new SpriteBatch(3);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void load(AssetManager assetManager) {
		assetManager.load("data/logonap.png", Texture.class);
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
	public void resize(int width, int height) {
		float h = height;
		float w = width;
		uiCam = new OrthographicCamera(800, 800 * h/w);
		uiCam.position.set(uiCam.viewportWidth/2f, uiCam.viewportHeight/2f, 0);
		uiCam.update();
	}
	float time = 0;
	float mul = 1;
	@Override
	public void render() {
		float dt = Math.min(1f/60f, Gdx.graphics.getDeltaTime());
		time += dt * mul;
		if(time > GameScene.spb || time < -GameScene.spb)
		{
			mul *= -1;
		}
		Gdx.gl.glClearColor(0.898f, 0.901f, 0.780f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(uiCam.combined);
		batch.begin();
		batch.draw(logo[0], uiCam.viewportWidth * 0.5f - 525f * 0.5f, uiCam.viewportHeight * 0.90f - 70, 525, 70);
		batch.draw(logo[1], uiCam.viewportWidth * 0.5f - 250f * 0.5f, uiCam.viewportHeight * 0.70f - 130, 250 * 0.5f, 130 * 0.5f, 250, 130, 1 + Math.abs(time) * 0.1f, 1 + Math.abs(time) * 0.1f, time * 20);
		batch.draw(logo[2], uiCam.viewportWidth * 0.5f - 628.8f * 0.5f, 40, 628.8f, 205.6f);
		batch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode)
		{
		case Keys.R:
			Preferences pref = Gdx.app.getPreferences("YOGONAP");
			pref.clear();
			pref.flush();
			gs.currentLevel = 1;
			nextScene = true;
			nextSceneName = "Game";
			break;
		case Keys.SPACE:
			nextScene = true;
			nextSceneName = "Game";
			break;
		case Keys.C:
			nextScene = true;
			nextSceneName = "Credits";
			break;
		case Keys.V:
			nextScene = true;
			nextSceneName = "Instructions";
			break;
		case Keys.Q:
			Gdx.app.exit();
			break;
		default:
			return false;
		}
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
		if(Gdx.app.getType() ==ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS)
		{
			if(screenY < Gdx.graphics.getHeight() * 0.5f)
			{
				Preferences pref = Gdx.app.getPreferences("YOGONAP");
				pref.clear();
				pref.flush();
				gs.currentLevel = 1;	
			}
			nextScene = true;
			nextSceneName = "Game";
			
		}
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
