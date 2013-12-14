package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GameScene extends Scene implements InputProcessor {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	TiledMap map = new TiledMap();
	OrthogonalTiledMapRenderer mapRenderer;
	public final static float tileSize = 32f;
	public final static float unitScale = 1f/tileSize;
	World world;
	Box2DDebugRenderer physicsRenderer;
	Texture characterSheet;
	Character character;
	InputMultiplexer imux;
	Array<Body> clocks = new Array<Body>(false, 5);
	//final static float unitScale = 5f;
	Texture clock;
	float cameraMin;
	float cameraMax;
	int currentLevel = 1;
	AssetManager assetManager;
	TextureRegion[] backs;
	float backCounting = 0;
	int backIndex = 0;
	@Override
	public void start(AssetManager assetManager) {
		this.assetManager = assetManager;
		characterSheet = assetManager.get("data/character.png", Texture.class);
		clock = assetManager.get("data/clock.png", Texture.class);
		Texture backSheet = assetManager.get("data/background.png", Texture.class);
		backs = new TextureRegion[]
				{
					new TextureRegion(backSheet, 0, 0, 640, 448),
					new TextureRegion(backSheet, 0, 448, 640, 448),
				};
		physicsRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
		world = new World(new Vector2(0, -10), true);
		world.setContactListener(new CCL());
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(12 * w/h, 12);
		camera.position.set(cameraMin = camera.viewportWidth/2f, 6, 0);
		camera.update();
		
		batch = new SpriteBatch();
		map = new TmxMapLoader().load("data/mapa" + currentLevel +".tmx");
		cameraMax = cameraMin + ((TiledMapTileLayer)map.getLayers().get(2)).getWidth();
		MapLayer layer = map.getLayers().get(0);
		
		MapObjects objs = layer.getObjects();
		Iterator<MapObject> objIt = objs.iterator();
		Vector2 tmp = new Vector2();
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		while(objIt.hasNext())
		{
			MapObject obj = objIt.next();
			if(obj.getClass() == RectangleMapObject.class)
			{
				RectangleMapObject rmo = (RectangleMapObject)obj;
				Rectangle rect = rmo.getRectangle();
				PolygonShape ps = new PolygonShape();
				ps.setAsBox(rect.getWidth() * unitScale / 2f, rect.getHeight() * unitScale/ 2f, rect.getCenter(tmp).scl(unitScale), 0);
				Body nb = world.createBody(bd);
				Fixture f = nb.createFixture(ps, 1);
				f.setRestitution(0.3f);
				f.setFriction(0f);
				ps.dispose();
			}
		}
		
		layer = map.getLayers().get(3);
		objs = layer.getObjects();
		objIt = objs.iterator();
		PolygonShape ps = new PolygonShape();
		while(objIt.hasNext())
		{
			MapObject obj = objIt.next();
			MapProperties props = obj.getProperties();
			String type = props.get("type", String.class);
			if(type == null) continue;
			if(type.compareTo("clock") == 0)
			{
				bd.type = BodyType.KinematicBody;
				((RectangleMapObject)obj).getRectangle().getCenter(bd.position);
				bd.position.scl(unitScale);
				ps.setAsBox(0.5f, 0.5f);
				Body b = world.createBody(bd);
				b.setUserData(new Clock());
				clocks.add(b);
				Fixture f = b.createFixture(ps, 1);
				f.setSensor(true);
			}
		}
		ps.dispose();
		character = new Character();
		character.create(map, world, characterSheet);
		mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);
		super.start(assetManager);
		
		imux = new InputMultiplexer();
		imux.addProcessor(character);
		imux.addProcessor(this);
		Gdx.input.setInputProcessor(imux);
		
	}
	@Override
	public void load(AssetManager assetManager) {
		assetManager.load("data/character.png", Texture.class);
		assetManager.load("data/clock.png", Texture.class);
		assetManager.load("data/background.png", Texture.class);
	}
	@Override
	public void end() {
		batch.dispose();
		mapRenderer.dispose();
		map.dispose();
		
		world.dispose();
		physicsRenderer.dispose();
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}
	final static float bpm = 80;
	final static float bps = bpm/60;
	@Override
	public void render() {
		float dt = 1f/45f;
		Gdx.gl.glClearColor(0.2f, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		character.calcVars();

		backCounting += dt;
		if(backCounting >= bps)
		{
			backCounting -= bps;
			backIndex = (backIndex+1) % backs.length;
		}
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.disableBlending();
		batch.begin();
		batch.draw(backs[backIndex], 0, 0, 0, 0, backs[backIndex].getRegionWidth() * unitScale, backs[backIndex].getRegionHeight() * unitScale, 1, 1, 0);
		batch.end();
		batch.enableBlending();
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.begin();
		character.draw(batch);
		
		for(int i = 0; i < clocks.size; ++i)
		{
			Body b = clocks.get(i);
			Vector2 p = b.getPosition();
			batch.draw(clock, p.x - 0.5f, p.y - 0.5f, 1, 1);
		}
		batch.end();
		
		character.update(dt);
		
		camera.position.x = Math.max(cameraMin, Math.min(cameraMax, character.pos.x));
		camera.update();
		
		for(int i = clocks.size-1; i >= 0; --i)
		{
			Body b = clocks.get(i);
			Clock c = (Clock)b.getUserData();
			if(c.catched)
			{
				clocks.removeIndex(i);
				world.destroyBody(b);
			}
		}
		if(clocks.size <= 0)
		{
			currentLevel++;
			end();
			start(assetManager);
			return;
		}
		
		//physicsRenderer.render(world, camera.combined);
		
		world.step(1f/45f, 2, 4);
	}
	
	int keyState = 0;
	boolean charFlipX = false;
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
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
