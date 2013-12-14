package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScene extends Scene implements InputProcessor {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	TiledMap map = new TiledMap();
	OrthogonalTiledMapRenderer mapRenderer;
	final static float tileSize = 32f;
	final static float unitScale = 1f/tileSize;
	World world;
	Box2DDebugRenderer physicsRenderer;
	Texture characterSheet;
	Body charBody;
	//final static float unitScale = 5f;
	@Override
	public void start(AssetManager assetManager) {
		characterSheet = assetManager.get("data/character.png", Texture.class);
		physicsRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
		world = new World(new Vector2(0, -10), true);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(12 * w/h, 12);
		camera.position.set(camera.viewportWidth/2f, 6, 0);
		camera.update();
		
		batch = new SpriteBatch();
		map = new TmxMapLoader().load("data/mapa1.tmx");
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
				nb.createFixture(ps, 1);
				ps.dispose();
			}
		}
		
		bd.type = BodyType.DynamicBody;
		Ellipse charFlag = ((EllipseMapObject)map.getLayers().get(1).getObjects().get(0)).getEllipse();
		bd.position.x = charFlag.x * unitScale;
		bd.position.y = charFlag.y * unitScale;
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(0.5f, 0.5f);
		charBody = world.createBody(bd);
		charBody.createFixture(ps, 2);
		ps.dispose();
		mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);
		super.start(assetManager);
		
		Gdx.input.setInputProcessor(this);
		
	}
	@Override
	public void load(AssetManager assetManager) {
		assetManager.load("data/character.png", Texture.class);
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
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.2f, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.begin();
		Vector2 charPos = charBody.getPosition();
		batch.draw(characterSheet, charPos.x - 0.5f, charPos.y - 0.5f, 1, 1);
		batch.end();
		float charDesiredVel = 0;
		switch(keyState & (KLEFT | KRIGHT))
		{
		case KLEFT:
			charDesiredVel = -5; break;
		case KRIGHT:
			charDesiredVel = 5; break;
		}
		float velChange = charDesiredVel - charBody.getLinearVelocity().x;
		float imp = charBody.getMass() * velChange;
		Vector2 charCenter = charBody.getWorldCenter();
		charBody.applyLinearImpulse(imp, 0, charCenter.x, charCenter.y, true);
		
		physicsRenderer.render(world, camera.combined);
		
		world.step(1f/45f, 2, 4);
	}
	
	final static int KLEFT = 0x1;
	final static int KRIGHT = 0x2;
	final static int KUP = 0x4;
	final static int KDOWN = 0x8;
	int keyState = 0;
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode)
		{
		case Keys.LEFT:
			keyState |= KLEFT; break;

		case Keys.RIGHT:
			keyState |= KRIGHT; break;

		case Keys.UP:
			keyState |= KUP; break;

		case Keys.DOWN:
			keyState |= KDOWN; break;
		}
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
		switch(keycode)
		{
		case Keys.LEFT:
			keyState &= ~KLEFT; break;

		case Keys.RIGHT:
			keyState &= ~KRIGHT; break;

		case Keys.UP:
			keyState &= ~KUP; break;

		case Keys.DOWN:
			keyState &= ~KDOWN; break;
		}
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
