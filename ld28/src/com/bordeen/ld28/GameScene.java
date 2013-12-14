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
	TextureRegion[] fends;
	TextureRegion[] enemyTR;
	private Array<Spawner> spawners;
	int mapWidth;
	@Override
	public void start(AssetManager assetManager) {
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
		Texture enemySheet = assetManager.get("data/enemies.png", Texture.class);
		enemyTR = new TextureRegion[]
				{
				new TextureRegion(enemySheet, 34, 0, 14, 28),
				new TextureRegion(enemySheet, 0, 22, 25, 32),
				new TextureRegion(enemySheet, 0, 0, 32, 20)
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
		mapWidth = ((TiledMapTileLayer)map.getLayers().get(2)).getWidth();
		cameraMax = cameraMin + mapWidth - camera.viewportWidth;
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
		assetManager.load("data/fend.png", Texture.class);
		assetManager.load("data/enemies.png", Texture.class);
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
	public final static float bpm = 80f;
	public final static float bps = bpm/60f;
	public final static float spb = 1f/bps;
	float time = 0;
	@Override
	public void render() {
		//float dt = 1f/45f;
		float dt = Math.min(1f/45f, Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0.2f, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		character.calcVars();

		backCounting += dt;
		if(backCounting >= spb)
		{
			backCounting -= spb;
			backIndex = (backIndex+1) % backs.length;
		}
		
		batch.setProjectionMatrix(camera.combined);
		
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
		
		//physicsRenderer.render(world, camera.combined);
		
		world.step(dt, 2, 4);
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
	Array<Enemy> enemies = new Array<Enemy>(false, 15);
	public Enemy createEnemy(Spawner spawner) {
		Enemy e = new Enemy(this, spawner);
		enemies.add(e);
		return e;
	}
	

}
