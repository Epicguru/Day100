package co.uk.epicguru.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.building.BuildingManager;
import co.uk.epicguru.map.objects.MapObject;
import co.uk.epicguru.map.objects.MapObjectSupplier;
import co.uk.epicguru.player.MapBlood;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.screens.Shaders;

public final class Map {

	public static final String TAG = "Map";
	public File sourceFolder;
	public File saveFolder;
	public TextureAtlas atlas;

	private String name;
	private int width;
	private int height;
	private TextureRegion floor;
	public World world;
	public RayHandler rayHandler;
	public DirectionalLight sun;
	public float timer;

	public ArrayList<MapObject> objects = new ArrayList<MapObject>();
	public ArrayList<MapBlood> blood = new ArrayList<MapBlood>();

	public Map(File source){
		if(!source.exists())
			return;
		if(!source.isDirectory()){
			return;
		}

		this.sourceFolder = source;
		this.saveFolder = new File(source.getAbsoluteFile() + "\\Game Data\\");
		this.name = source.getName();
		this.floor = Day100.UIAtlas.findRegion("Ground");
		Entity.clearAllEntities();
	}

	public void loadFromFile(){
		if(width != 0 || !loadProperties())
			return;

		JLineReader reader = new JLineReader(new File(sourceFolder.getAbsoluteFile() + "\\Placeables.txt"));
		JLineReader properties = new JLineReader(new File(sourceFolder.getAbsoluteFile() + "\\Properties.txt"));
		try {
			reader.open();
			properties.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		if(reader.isLineEmpty()){
			reader.dispose();
			properties.dispose();
			Log.error(TAG, "The placeables file was empty");
			return;
		}

		// STUFF!
		setupWorld();
		setupLighting();
		Log.info(TAG, "Setup world and lighting");
		
		Shaders.getAllShaders();
		atlas = new TextureAtlas(new FileHandle(new File(sourceFolder.getAbsoluteFile() + "\\Cache\\Packed.atlas")));

		do{
			if(!reader.isLineEmpty()){
				String ID = reader.readString("ID", "ERROR");
				MapObjectSupplier supplier = MapObject.objects.get(reader.readString("Supplier", "Default"));
				if(supplier == null)
					supplier = MapObject.objects.get("Default");
				MapObject object = supplier.getObject(ID, reader.readVector2("Position", Vector2.Zero), reader.readVector2("Size", Vector2.Zero), reader.readColor("Colour", Color.WHITE), atlas.findRegion(ID), name);
				String shader = reader.readString("Shader", null);
				object.shader = shader == null ? null : Shaders.compliedShadersNames.contains(shader) ? Shaders.compliedShaders.get(Shaders.compliedShadersNames.indexOf(shader)) : null;
				object.allowBlood = reader.readBoolean("Blood", true);
				objects.add(object);
			}
		}while(!reader.nextLine());	

		reader.dispose();
		properties.dispose();
	}

	private void setupWorld(){
		// World creation
		world = new World(new Vector2(0, -9.803f), true);
		
		// Create floor
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		def.position.y = -.4f;
		def.position.x = width / 2;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, .5f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0.05f;
		fixtureDef.friction = 1f;
		Body body = world.createBody(def);
		body.createFixture(fixtureDef);
	}

	private void setupLighting(){
		rayHandler = new RayHandler(world);
		sun = new DirectionalLight(rayHandler, Constants.RAYS * 2, new Color(0.0f, 0.0f, 0.0f, 0.9f), -90);
	}

	public boolean loadProperties(){
		JLineReader reader = new JLineReader(new File(sourceFolder.getAbsoluteFile() + "\\Properties.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		this.width = (int) reader.readVector2("Size", new Vector2(300, 25)).x;
		this.height = (int) reader.readVector2("Size", new Vector2(300, 25)).y;
		Log.info(TAG, "Width : " + width + ", Height : " + height);
		reader.dispose();
		return true;
	}

	public void dispose(){
		objects.clear();
		blood.clear();
		GunManager.cleanTextures();
		Entity.clearAllEntities();
		BuildingManager.reset();
		System.gc();
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void renderFloor(Batch batch){
		for(int x = 0; x < width; x++){
			batch.draw(floor, x, -1, 1.01f, 1.01f);
		}
	}

	public void render(Batch batch) {

		batch.end();
		batch.setShader(null);	
		batch.enableBlending();
		batch.begin();
		
		for(MapObject object : objects){
			object.render(batch);
		}
		
		// Set shader back to normal if not already.
		if(batch.getShader() != null){
			batch.setShader(null);
		}
		
		// Draw the floor tiles
		renderFloor(batch);
			
		//TEST
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		for(MapBlood blood : blood){
			blood.render(batch);
		}
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		Entity.renderAll(batch);
		
		BuildingManager.render(batch);
	}
	
	public void renderLighting(Batch batch){
		batch.end();
		// Lighting
		rayHandler.setCombinedMatrix(Day100.camera);
		rayHandler.updateAndRender();			

		batch.begin();
	}
	
	public void update(float delta) {

		timer += delta;
		
		world.step(Day100.timeScale == 1 ? 1f / 60f : Day100.timeScale / 60f, 8, 3);

		for(MapObject object : objects){
			object.update(delta);
		}
		
		BuildingManager.update(delta);
		
		// TEST
		if(MathUtils.randomBoolean(0.5f * delta)){
			//new BotController(new Vector2(Day100.player.body.getPosition().x + MathUtils.random(-50, 50), 2));
		}	
		
		Entity.updateAll(delta);
	}

	public void renderUI(Batch batch) {
		Entity.renderAllUI(batch);
		BuildingManager.renderUI(batch);
	}

}
