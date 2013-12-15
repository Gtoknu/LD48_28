package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GameScene extends Scene implements InputProcessor {
	CameraController camera;
	private SpriteBatch batch;
	TiledMap map;
	OrthogonalTiledMapRenderer mapRenderer;
	public final static float tileSize = 32f;
	public final static float unitScale = 1f/tileSize;
	World world;
	Box2DDebugRenderer physicsRenderer;
	Texture characterSheet;
	Character character;
	InputMultiplexer imux;
	Array<Body> clocks;
	//final static float unitScale = 5f;
	Texture clock;
	float cameraMin;
	float cameraMax;
	int currentLevel = 3;
	AssetManager assetManager;
	TextureRegion[] backs;
	float backCounting;
	int backIndex;
	TextureRegion[] fends;
	TextureRegion[][] enemyTR;
	private Array<Spawner> spawners;
	Sound jump;
	Sound kill;
	Sound die;
	Sound sclock;
	int mapWidth;
	@Override
	public void start(AssetManager assetManager) {
		clocks = new Array<Body>(false, 5);
		backCounting = 0;
		backIndex = 0;
		map = new TiledMap();
		this.assetManager = assetManager;
		spawners = new Array<Spawner>(false, 10);
		characterSheet = assetManager.get("data/character.png", Texture.class);
		clock = assetManager.get("data/clock.png", Texture.class);
		Texture backSheet = assetManager.get("data/background.png", Texture.class);
		backs = new TextureRegion[]
				{
					new TextureRegion(backSheet, 0, 0, 640, 448),
					new TextureRegion(backSheet, 0, 448, 640, 448)
				};
		Texture fendSheet = assetManager.get("data/fend.png", Texture.class);
		fends = new TextureRegion[]
				{
					new TextureRegion(fendSheet, 0, 0, 32, 32),
					new TextureRegion(fendSheet, 34, 0, 32, 32),
					new TextureRegion(fendSheet, 68, 0, 32, 32),
					new TextureRegion(fendSheet, 0, 34, 32, 32),
					new TextureRegion(fendSheet, 34, 34, 32, 32),
					new TextureRegion(fendSheet, 0, 68, 32, 32)
				};
		Texture cupSheet = assetManager.get("data/cupsheet.png", Texture.class);
		Texture pillowSheet = assetManager.get("data/pillowsheet.png", Texture.class);
		Texture sheepSheet = assetManager.get("data/sheepsheet.png", Texture.class);
		enemyTR = new TextureRegion[][]
				{
					new TextureRegion[] { new TextureRegion(cupSheet, 0, 0, 14, 28), new TextureRegion(cupSheet, 16, 0, 14, 28) },
					new TextureRegion[] { new TextureRegion(pillowSheet, 0, 0, 25, 32), new TextureRegion(pillowSheet, 27, 0, 25, 32), new TextureRegion(pillowSheet, 54, 0, 25, 32) },
					new TextureRegion[] { new TextureRegion(sheepSheet, 0, 0, 32, 20), new TextureRegion(sheepSheet, 0, 22, 32, 20) }
				};
		physicsRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
		world = new World(new Vector2(0, -10), true);
		world.setContactListener(new CCL(this));
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();
		map = new TmxMapLoader().load("data/mapa" + currentLevel +".tmx");
		mapWidth = ((TiledMapTileLayer)map.getLayers().get(2)).getWidth();
		MapLayer layer = map.getLayers().get(0);
		
		MapObjects objs = layer.getObjects();
		Iterator<MapObject> objIt = objs.iterator();
		Vector2 tmp = new Vector2();
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		PolygonShape ps = new PolygonShape();
		while(objIt.hasNext())
		{
			MapObject obj = objIt.next();
			if(obj.getClass() == RectangleMapObject.class)
			{
				RectangleMapObject rmo = (RectangleMapObject)obj;
				Rectangle rect = rmo.getRectangle();
				ps.setAsBox(rect.getWidth() * unitScale / 2f, rect.getHeight() * unitScale/ 2f, rect.getCenter(tmp).scl(unitScale), 0);
				Body nb = world.createBody(bd);
				Fixture f = nb.createFixture(ps, 1);
				Filter fd = f.getFilterData();
				fd.categoryBits = Filters.scenary;
				f.setFilterData(fd);
			}
		}
		if(map.getLayers().getCount() > 4)
		{
			layer = map.getLayers().get(4);
			
			objs = layer.getObjects();
			objIt = objs.iterator();
			bd.type = BodyType.StaticBody;
			while(objIt.hasNext())
			{
				MapObject obj = objIt.next();
				if(obj.getClass() == RectangleMapObject.class)
				{
					RectangleMapObject rmo = (RectangleMapObject)obj;
					Rectangle rect = rmo.getRectangle();
					ps.setAsBox(rect.getWidth() * unitScale / 2f, rect.getHeight() * unitScale/ 2f, rect.getCenter(tmp).scl(unitScale), 0);
					Body nb = world.createBody(bd);
					Fixture f = nb.createFixture(ps, 1);
					Filter fd = f.getFilterData();
					fd.categoryBits = Filters.scenary;
					fd.maskBits = Filters.enemy;
					f.setFilterData(fd);
				}
			}
		}
		
		layer = map.getLayers().get(3);
		objs = layer.getObjects();
		objIt = objs.iterator();
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
				ps.setAsBox(0.265625f, 0.25f, tmp.set(0, -0.25f), 0);
				Body b = world.createBody(bd);
				b.setUserData(new Clock());
				clocks.add(b);
				Fixture f = b.createFixture(ps, 1);
				f.setSensor(true);
				Filter fd = f.getFilterData();
				fd.categoryBits = Filters.clock;
				fd.maskBits = Filters.character;
				f.setFilterData(fd);
			}
			else if(type.compareTo("spawn") == 0)
			{
				Spawner s = new Spawner();
				((RectangleMapObject)obj).getRectangle().getCenter(s.position);
				s.position.scl(unitScale);
				s.maximum = Integer.parseInt(props.get("maximum", String.class));
				s.onlyClose = Boolean.parseBoolean(props.get("onlyClose", "0", String.class));
				s.interval = Float.parseFloat(props.get("interval", String.class));
				String enemy = props.get("enemy", String.class);
				if(enemy.compareTo("cup") == 0)
				{
					s.enemy = Enemies.Cup;
				}
				else if(enemy.compareTo("pillow") == 0)
				{
					s.enemy = Enemies.Pillow;
				}
				else if(enemy.compareTo("sheep") == 0)
				{
					s.enemy = Enemies.Sheep;
				}
				s.start(this);
				spawners.add(s);
			}
		}
		
		bd.type = BodyType.StaticBody;
		bd.position.set(-0.5f, 10);
		Body bound = world.createBody(bd);
		ps.setAsBox(0.5f, 20);
		Fixture f = bound.createFixture(ps, 1);
		f.setRestitution(0.1f);
		Filter fd = f.getFilterData();
		fd.categoryBits = Filters.scenary;
		f.setFilterData(fd);
		bd.position.set(mapWidth+0.5f, 10);
		bound = world.createBody(bd);
		ps.setAsBox(0.5f, 20);
		f = bound.createFixture(ps, 1);
		f.setFriction(0);
		f.setRestitution(0.1f);
		fd = f.getFilterData();
		fd.categoryBits = Filters.scenary;
		f.setFilterData(fd);
		ps.dispose();
		character = new Character();
		character.create(this, map, world, characterSheet);
		mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);
		camera = new CameraController(w, h, mapWidth);
		
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
		assetManager.load("data/fend.png", Texture.class);
		assetManager.load("data/pillowsheet.png", Texture.class);
		assetManager.load("data/sheepsheet.png", Texture.class);
		assetManager.load("data/cupsheet.png", Texture.class);

		jump = Gdx.audio.newSound(Gdx.files.internal("data/jump.wav"));
		kill= Gdx.audio.newSound(Gdx.files.internal("data/kill.wav"));
		die = Gdx.audio.newSound(Gdx.files.internal("data/die.wav"));
		sclock = Gdx.audio.newSound(Gdx.files.internal("data/clock.wav"));
	}
	@Override
	public void end() {
		batch.dispose();
		mapRenderer.dispose();
		map.dispose();
		
		world.dispose();
		physicsRenderer.dispose();
		enemies.clear();
		spawners.clear();
		camera = null;
	}
	@Override
	public void dispose() {
		jump.dispose();
		kill.dispose();
		die.dispose();
		sclock.dispose();
	}
	public final static float bpm = 80f;
	public final static float bps = bpm/60f;
	public final static float spb = 1f/bps;
	float time = 0;
	@Override
	public void render() {
		//float dt = 1f/45f;
		float dt = Math.min(1f/45f, Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0.898f, 0.901f, 0.780f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		character.calcVars();
		for(int i = 0; i < enemies.size; ++i)
		{
			enemies.get(i).calcVars();
		}

		backCounting += dt;
		if(backCounting >= spb)
		{
			backCounting -= spb;
			backIndex = (backIndex+1) % backs.length;
		}
		
		batch.setProjectionMatrix(camera.combined());
		
		batch.disableBlending();
		batch.begin();
		for(int x = 0; x < mapWidth; x += backs[0].getRegionWidth() * unitScale)
		{
			batch.draw(backs[backIndex], x, 0, 0, 0, backs[backIndex].getRegionWidth() * unitScale, backs[backIndex].getRegionHeight() * unitScale, 1, 1, 0);
		}
		batch.end();
		batch.enableBlending();
		
		batch.begin();
		for(int i = 0; i < spawners.size; ++i)
		{
			Spawner s = spawners.get(i);
			batch.draw(fends[(int)(Math.min(s.anim * fends.length, fends.length-1))], s.position.x - 0.5f, s.position.y - 0.5f, 1, 1);
		}
		for(int i = 0; i < enemies.size; ++i)
		{
			Enemy e = enemies.get(i);
			e.render(batch);
		}
		batch.end();
		
		mapRenderer.setView(camera.camera);
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
		if(character.diedTime > Character.dieInterval)
		{
			end();
			start(assetManager);
			return;
		}

		for(int i = 0; i < enemies.size; ++i)
		{
			enemies.get(i).update(dt);
		}
		
		for(int i = enemies.size-1; i >= 0; --i)
		{
			if(enemies.get(i).diedTime > Enemy.dieInterval)
			{
				enemies.removeIndex(i);
			}
		}
		
		camera.update(dt, character);
		
		
		for(int i = 0; i < spawners.size; ++i)
		{
			spawners.get(i).update(dt);
		}
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
		
		//physicsRenderer.render(world, camera.combined());
		
		world.step(dt, 2, 4);
	}
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
		camera.shake(0.3f, 0.2f, 60);
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
	Array<Enemy> enemies = new Array<Enemy>(false, 15);
	public Enemy createEnemy(Spawner spawner) {
		Enemy e = new Enemy(this, spawner);
		enemies.add(e);
		return e;
	}
	

}
