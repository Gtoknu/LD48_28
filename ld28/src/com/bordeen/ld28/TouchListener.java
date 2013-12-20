package com.bordeen.ld28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TouchListener implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
	class PointerTrack
	{
		int startedAs;
		int currentDoing;
		int pointer;
		public PointerTrack(int pointer, int doing)
		{
			startedAs = currentDoing = doing;
			this.pointer = pointer;
		}
	}
	Array<PointerTrack> pointersTracked = new Array<PointerTrack>(false, 3);
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float ny = h - screenY;
		if(ny > ySection)
			return false;
		boolean found0 = false, found1 = false, found2 = false;
		for(int i = 0; i < pointersTracked.size; ++i)
		{
			switch(pointersTracked.get(i).currentDoing)
			{
			case 0:
				found0 = true;
				break;
			case 1:
				found1 = true;
				break;
			case 2:
				found2 = true;
				break;
			}
		}
		if(screenX <= Section1 && !found0)
		{
			character.Press(Character.KLEFT);
			pointersTracked.add(new PointerTrack(pointer, 0));
			return true;
		}
		else if(screenX <= Section2 && !found1)
		{
			character.Press(Character.KRIGHT);
			pointersTracked.add(new PointerTrack(pointer, 1));
			return true;
		}
		else if(screenX >= Section3 && !found2)
		{
			character.Press(Character.KUP);
			pointersTracked.add(new PointerTrack(pointer, 2));
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(int i = 0; i < pointersTracked.size; ++i)
		{
			PointerTrack pt = pointersTracked.get(i);
			if(pt.pointer == pointer)
			{
				Release(pt);
				pointersTracked.removeIndex(i);
				return true;
			}
		}
		return false;
	}
	void Release(PointerTrack pt)
	{
		switch(pt.currentDoing)
		{
		case 0:
			character.Release(Character.KLEFT);
			break;
		case 1:
			character.Release(Character.KRIGHT);
			break;
		case 2:
			character.Release(Character.KUP);
			break;
		}
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		int found0 = -1, found1 = -1, found2 = -1;
		for(int i = 0; i < pointersTracked.size; ++i)
		{
			switch(pointersTracked.get(i).currentDoing)
			{
			case 0:
				found0 = i;
				break;
			case 1:
				found1 = i;
				break;
			case 2:
				found2 = i;
				break;
			}
		}
		for(int i = 0; i < pointersTracked.size; ++i)
		{
			PointerTrack pt = pointersTracked.get(i);
			if(pt.pointer == pointer)
			{
				float ny = h - screenY;
				if(ny > ySection)
				{
					Release(pt);
					pt.currentDoing = -1;
					return true;
				}
				
				if(screenX <= Section1)
				{
					if(found0 == i)
					{
						return true;
					}
					Release(pt);
					if(found0 == -1 && pt.startedAs != 2)
					{
						character.Press(Character.KLEFT);
						pt.currentDoing = 0;
					}
					else
					{
						pt.currentDoing = -1;
					}
					return true;
				}
				else if(screenX <= Section2)
				{
					if(found1 == i)
						return true;
					Release(pt);
					if(found1 == -1 && pt.startedAs != 2)
					{
						character.Press(Character.KRIGHT);
						pt.currentDoing = 1;
					}
					else
					{
						pt.currentDoing = -1;
					}
					return true;
				}
				else if(screenX >= Section3)
				{
					if(found2 == i)
						return true;
					Release(pt);
					if(found2 == -1 && pt.startedAs == 2)
					{
						character.Press(Character.KUP);
						pt.currentDoing = 2;
					}
					else
					{
						pt.currentDoing = -1;
					}
					return true;
				}
				else
				{
					Release(pt);
					pt.currentDoing = -1;
				}
				return true;
			}
		}
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
	TextureRegion arrowUp;
	TextureRegion arrowLeft;
	TextureRegion arrowRight;
	float size;
	float offset;
	float upX;
	float Section1;
	float Section2;
	float Section3;
	float ySection;
	float h;
	Character character;
	public void start(AssetManager assetManager, Character character) {
		this.character = character;
		Texture sheet = assetManager.get("data/arrows.png", Texture.class);
		arrowUp = new TextureRegion(sheet, 66, 0, 31, 31);
		arrowLeft = new TextureRegion(sheet, 33, 0, 31, 31);
		arrowRight = new TextureRegion(sheet, 0, 0, 31, 31);
		size = Gdx.graphics.getDensity() * 80;
		offset = Gdx.graphics.getDensity() * 8;
		upX = Gdx.graphics.getWidth() - size - offset;
		Section1 = size+offset*2;
		Section2 = Section1+size+offset*2;
		Section3 = upX-offset;
		ySection = offset+size+offset;
		h = Gdx.graphics.getHeight();
	}

	public void render(SpriteBatch batch) {
		batch.draw(arrowLeft, offset, offset, size, size);
		batch.draw(arrowRight, offset*3+size, offset, size, size);
		batch.draw(arrowUp, upX, offset, size, size);
	}

}
