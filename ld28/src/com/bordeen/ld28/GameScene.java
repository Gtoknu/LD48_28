package com.bordeen.ld28;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScene extends Scene {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	TiledMap map = new TiledMap();
	OrthogonalTiledMapRenderer mapRenderer;
	final static float tileSize = 16f;
	final static float unitScale = 1f/tileSize;
	World world;
	Box2DDebugRenderer physicsRenderer;
	//final static float unitScale = 5f;
	@Override
	public void start(AssetManager assetManager) {
		physicsRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
		world = new World(new Vector2(0, 10), true);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(12 * w/h, 12);
		camera.position.set(camera.viewportWidth/2f, 6, 0);
		camera.update();
		
		batch = new SpriteBatch();
		map = new TmxMapLoader().load("data/first level.tmx");
		MapLayer layer = map.getLayers().get(1);
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
			}
		}
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1/16f, batch);
		super.start(assetManager);
	}
	@Override
	public void load(AssetManager assetManager) {
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
		physicsRenderer.render(world, camera.combined);
	}
	

}
