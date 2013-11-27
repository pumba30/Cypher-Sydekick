package com.bls220.cyphersidekick.mapstuff.level;

import pong.client.core.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bls220.cyphersidekick.MySidekick;
import com.bls220.cyphersidekick.comm.MsgHandler;
import com.bls220.cyphersidekick.entities.Enemy;
import com.bls220.cyphersidekick.mapstuff.C;

public class RandomLevel extends Level {

	private final static String EMPTY_MAP_PATH = "maps/blank.tmx";
	private final float TILE_WIDTH, TILE_HEIGHT;

	private static final int WALL_ID = 2;
	private static final int FLOOR_ID = 9;
	private static final int ENEMY_1_ID = 3;

	private static final int PILLAR_OFF_TOP = 19;
	private static final int PILLAR_OFF_BOTTOM = 27;

	public RandomLevel(MySidekick parent, MsgHandler remote, World world) {
		super(parent, EMPTY_MAP_PATH, remote);
		TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(
				C.TileLayer);
		MapLayer objectLayer = map.getLayers().get(C.ObjectLayer);
		TILE_WIDTH = tileLayer.getTileWidth();
		TILE_HEIGHT = tileLayer.getTileHeight();
		generateLevel(map.getTileSets(), tileLayer, objectLayer, world);
	}

	private void generateLevel(TiledMapTileSets tileSets,
			TiledMapTileLayer tileLayer, MapLayer objectLayer, World world) {

		Cell dirtCell = new Cell();
		dirtCell.setTile(tileSets.getTile(FLOOR_ID));

		Cell wallCell = new Cell();
		wallCell.setTile(tileSets.getTile(WALL_ID));

		BodyEditorLoader loader = new BodyEditorLoader(
				Gdx.files.internal("test.json"));

		// Generate ground
		for (int y = 0; y < tileLayer.getHeight() + 4; y++) {
			for (int x = 0; x < tileLayer.getWidth(); x++) {
				tileLayer.setCell(x, y, dirtCell);
			}
		}

		// Generate walls
		for (int y = 0; y < tileLayer.getHeight(); y++) {
			tileLayer.setCell(0, y, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					0 + 0.5f, y + 0.5f, 0);

			tileLayer.setCell(tileLayer.getWidth() - 1, y, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					(tileLayer.getWidth() - 1) + 0.5f, y + 0.5f, 0);
		}
		for (int x = 0; x < tileLayer.getWidth(); x++) {
			tileLayer.setCell(x, 0, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					x + 0.5f, 0 + 0.5f, 0);

			tileLayer.setCell(x, tileLayer.getHeight() - 1, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					x + 0.5f, (tileLayer.getHeight() - 1) + 0.5f, 0);
		}

		// Place enemies
		TextureRegion enemyTexReg = tileSets.getTile(ENEMY_1_ID)
				.getTextureRegion();
		for (int i = 0; i < 300; i++) {
			new Enemy(enemyTexReg,
					MathUtils.random(1, tileLayer.getWidth() - 2),
					MathUtils.random(1, tileLayer.getHeight() - 2), world);
		}

		// Make central room
		generateCentralRoom(tileSets, tileLayer, objectLayer, world, loader);
	}

	private Body createStaticTileBody(BodyEditorLoader loader, World world,
			String name) {

		// 1. Create a BodyDef, as usual.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.friction = 0.5f;
		fd.restitution = 0.3f;

		// 3. Create a Body, as usual.
		Body body = world.createBody(bd);

		// 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(body, name, fd, 1);
		return body;
	}

	private void generateCentralRoom(TiledMapTileSets tileSets,
			TiledMapTileLayer tileLayer, MapLayer objectLayer, World world,
			BodyEditorLoader loader) {
		Cell wallCell = new Cell();
		wallCell.setTile(tileSets.getTile(WALL_ID));

		Cell floorCell = new Cell();
		floorCell.setTile(tileSets.getTile(FLOOR_ID));

		Cell pillarTopCell = new Cell();
		pillarTopCell.setTile(tileSets.getTile(PILLAR_OFF_TOP));

		Cell pillarBottomCell = new Cell();
		pillarBottomCell.setTile(tileSets.getTile(PILLAR_OFF_BOTTOM));

		// Generate walls
		int mapHeight = tileLayer.getHeight();
		int mapWidth = tileLayer.getWidth();
		Vector2 center = new Vector2(mapWidth / 2, mapHeight / 2);
		for (int y = (int) (center.y - 4); y < center.y + 5; y++) {
			tileLayer.setCell((int) (center.x - 5), y, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					(center.x - 5) + 0.5f, y + 0.5f, 0);

			tileLayer.setCell((int) (center.x + 5), y, wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					(center.x + 5) + 0.5f, y + 0.5f, 0);
		}
		for (int x = (int) (center.x - 5); x < center.x + 6; x++) {
			if (x != center.x && x != center.x - 1) {
				tileLayer.setCell(x, (int) (center.y - 5), wallCell);
				createStaticTileBody(loader, world, "square").setTransform(
						x + 0.5f, (center.y - 5) + 0.5f, 0);
			}

			tileLayer.setCell(x, (int) (center.y + 5), wallCell);
			createStaticTileBody(loader, world, "square").setTransform(
					x + 0.5f, (center.y + 5) + 0.5f, 0);
		}

		// Place pillars
		makePillar(center.x, center.y + 2, objectLayer.getObjects(), loader,
				world);
		makePillar(center.x - 2, center.y + 1, objectLayer.getObjects(),
				loader, world);
		makePillar(center.x - 1.5f, center.y - 1, objectLayer.getObjects(),
				loader, world);
		makePillar(center.x + 2f, center.y + 1f, objectLayer.getObjects(),
				loader, world);
		makePillar(center.x + 1.5f, center.y - 1f, objectLayer.getObjects(),
				loader, world);

	}

	private void makePillar(float tileX, float tileY, MapObjects objs,
			BodyEditorLoader loader, World world) {
		MapObject pillarTop = new MapObject();
		MapObject pillarBottom = new MapObject();

		pillarTop.getProperties().put("x", (int) (tileX * TILE_WIDTH));
		pillarTop.getProperties().put("y", (int) ((tileY + 1) * TILE_HEIGHT));
		pillarTop.getProperties().put("gid", PILLAR_OFF_TOP);

		pillarBottom.getProperties().put("x", (int) (tileX * TILE_WIDTH));
		pillarBottom.getProperties().put("y", (int) (tileY * TILE_HEIGHT));
		pillarBottom.getProperties().put("gid", PILLAR_OFF_BOTTOM);
		createStaticTileBody(loader, world, "pillar").setTransform(
				tileX + 0.5f, tileY + 0.5f, 0);

		objs.add(pillarBottom);
		objs.add(pillarTop);
	}
}
