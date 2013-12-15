package com.bordeen.ld28;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.FloatArray;

public class CameraController {
	public OrthographicCamera camera;
	float cameraMin, cameraMax;
	
	FloatArray shakeXPoints = new FloatArray(true, 50);
	FloatArray shakeYPoints = new FloatArray(true, 50);
	float fixedY = 0;
	
	float ninarFreq = 3;
	float ninar = ninarFreq;
	float ninarStren = 0.1f;
	float nx0;
	float nx1;
	float ny0;
	float ny1;
	public CameraController(float w, float h, float mapWidth)
	{
		camera = new OrthographicCamera(11 * w/h, 11);
		camera.position.set(cameraMin = camera.viewportWidth/2f, fixedY = 6f, 0);
		camera.update();
		cameraMax = cameraMin + mapWidth - camera.viewportWidth;
	}
	
	public Matrix4 combined()
	{
		return camera.combined;
	}
	
	public void update(float dt, Character character)
	{
		if(ninar >= ninarFreq)
		{
			ninar = 0;
			nx0 = nx1;
			ny0 = ny1;
			nx1 = MathUtils.random(-ninarStren, ninarStren);
			ny1 = MathUtils.random(-ninarStren, ninarStren);
		}
		float nt = ninar / ninarFreq;
		
		float nx = nx0 + (nx1-nx0) * nt;
		float ny = ny0 + (ny1-ny0) * nt;
		
		ninar += dt;
		if(shaking)
		{
			if(shakePassed >= shakeAmount)
			{
				shaking = false;
				shakeX = 0;
				shakeY = 0;
			}
			else
			{
				float t = shakePassed / shakeAmount;
				float sample = t * (shakeSamples-1);
				int iSample = (int)sample;
				float fSample = sample - iSample;
				float x0 = shakeXPoints.get(iSample);
				float x1 = shakeXPoints.get(iSample+1);
				float y0 = shakeYPoints.get(iSample);
				float y1 = shakeYPoints.get(iSample+1);
				shakeX = x0 + (x1-x0) * fSample;
				shakeY = y0 + (y1-y0) * fSample;
				float decay = 1-t;
				shakeX *= decay;
				shakeY *= decay;
			}
			shakePassed += dt;
		}
		camera.position.set(Math.max(cameraMin, Math.min(cameraMax, character.pos.x)) + shakeX + nx, fixedY + shakeY + ny, 0);
		camera.update();
	}
	
	float shakeAmount;
	float shakeStrength;
	float shakeFrequency;
	float shakePassed = 0;
	float shakeX = 0, shakeY = 0;
	int shakeSamples;
	boolean shaking = false; 
	public void shake(float amount, float strength, float frequency)
	{
		shakeAmount = amount;
		shakeStrength = strength;
		shakeFrequency = frequency;
		shakePassed = 0;
		
		shakeSamples = (int)Math.ceil(shakeFrequency * shakeAmount);
		shakeXPoints.clear();
		shakeYPoints.clear();
		for(int i = 0; i < shakeSamples; ++i)
		{
			shakeXPoints.add(MathUtils.random(-shakeStrength, shakeStrength));
			shakeYPoints.add(MathUtils.random(-shakeStrength, shakeStrength));
		}
		shaking = true;
	}
}
