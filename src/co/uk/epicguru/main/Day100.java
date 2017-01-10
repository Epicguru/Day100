package co.uk.epicguru.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import co.uk.epicguru.helpers.CamShake;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.map.Map;
import co.uk.epicguru.net.NetUtils;
import co.uk.epicguru.net.client.GameClient;
import co.uk.epicguru.net.server.GameServer;
import co.uk.epicguru.player.PlayerController;
import co.uk.epicguru.screens.PostProcessing;
import co.uk.epicguru.screens.Screen;
import co.uk.epicguru.screens.ScreenManager;
import co.uk.epicguru.screens.instances.Loading;
import co.uk.epicguru.screens.instances.MapEditor;
import co.uk.epicguru.settings.GameSettings;
import co.uk.epicguru.sound.SoundUtils;

public class Day100 extends ApplicationAdapter {

	// COPY CACHE!!!!
	public static boolean DEVELOPER_MODE = true;
	public static final String TAG = "Day 100";
	public static SpriteBatch batch;
	public static OrthographicCamera camera;
	public static OrthographicCamera UIcamera;
	public static AssetManager assets;
	public static float timeScale = 1; // Lerps at 90% per second to targetTimeScale (below)
	public static float targetTimeScale = 1;
	public static float deltaTime = 1f / 60;
	public static BitmapFont font; // Xoloium
	public static BitmapFont smallFont; // Some default font on windows
	public static TextureAtlas UIAtlas;
	public static TextureAtlas playerAtlas;
	public static TextureAtlas gunsAtlas;
	public static TextureAtlas buildingAtlas;
	public static TextureAtlas vehiclesAtlas;
	public static TextureAtlas particlesAtlas;
	public static Map map;
	public static PlayerController player;
	public static Cursor cursor;
	public static Cursor cursorCrosshair;
	public static GameServer server;
	public static GameClient client;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		UIcamera = new OrthographicCamera();
		UIcamera.setToOrtho(false);
		assets = new AssetManager();

		// Load game settings
		GameSettings.load();

		// Add all game screens
		registerScreens();

		// Pack textures
		if(DEVELOPER_MODE)
			packTextures();

		// Load assets
		loadAssets();

		// Set screen
		ScreenManager.setScreen(Screen.LOADING);
		
		NetUtils.createServer();

	}

	public void registerScreens(){		
		ScreenManager.screens.add(new Loading());
		ScreenManager.screens.add(new MapEditor());
	}

	public void loadAssets(){
		// This needs to be loaded before anything else.
		long start = System.currentTimeMillis();
		Log.info(TAG, "Starting UI atlas load...");
		assets.load("Fonts/Small.fnt", BitmapFont.class);
		assets.load("Cache/UI.atlas", TextureAtlas.class);
		assets.finishLoading();			
		Log.info(TAG, "Done! It took " + ((float)(System.currentTimeMillis() - start) / 1000f) + " seconds.");
		
		// Other assets
		assets.load("Fonts/Xolonium.fnt", BitmapFont.class);
		assets.load("Cache/Player.atlas", TextureAtlas.class);
		
		// Loads all sounds
		SoundUtils.loadAllSounds();
		
		// Atlas
		assets.load("Cache/Guns.atlas", TextureAtlas.class);
		assets.load("Cache/Building.atlas", TextureAtlas.class);
		assets.load("Cache/Vehicles.atlas", TextureAtlas.class);
		assets.load("Cache/Particles.atlas", TextureAtlas.class);
		
		// Needs to be done
		PostProcessing.start();
	}

	public void packTextures(){
		Settings settings = new Settings();
		settings.fast = false; // Set up other settings here
		settings.paddingX = 4;
		settings.paddingY = 4;
		settings.maxWidth = 4096; // POW!
		settings.maxHeight = 4096;
		Log.error(TAG, "Packing all atlases...");
		long start = System.currentTimeMillis();
		TexturePacker.process(settings, "bin/Textures/UI", "bin/Cache", "UI");	
		TexturePacker.process(settings, "bin/Textures/Player", "bin/Cache", "Player");	
		TexturePacker.process(settings, "bin/Textures/Guns", "bin/Cache", "Guns");	
		TexturePacker.process(settings, "bin/Textures/Building", "bin/Cache", "Building");	
		TexturePacker.process(settings, "bin/Textures/Vehicles", "bin/Cache", "Vehicles");	
		TexturePacker.process(settings, "bin/Textures/Particles", "bin/Cache", "Particles");
		Log.error(TAG, "Done! It took " + ((float)(System.currentTimeMillis() - start) / 1000f) + " seconds.");
	}

	public static void init(){

		// Fonts
		font = assets.get("Fonts/Xolonium.fnt", BitmapFont.class);
		smallFont = assets.get("Fonts/Small.fnt", BitmapFont.class);
		
		// Atlas
		UIAtlas = assets.get("Cache/UI.atlas", TextureAtlas.class);
		playerAtlas = assets.get("Cache/Player.atlas", TextureAtlas.class);
		gunsAtlas = assets.get("Cache/Guns.atlas", TextureAtlas.class);
		buildingAtlas = assets.get("Cache/Building.atlas", TextureAtlas.class);
		vehiclesAtlas = assets.get("Cache/Vehicles.atlas", TextureAtlas.class);
		particlesAtlas = assets.get("Cache/Particles.atlas", TextureAtlas.class);

		// Load cursors
		loadCursors();
		
		ScreenManager.init();
	}
	
	public static void loadCursors(){
		// Default
		TextureRegion texture = UIAtlas.findRegion("Cursor");
		Pixmap cursor = new Pixmap(texture.getRegionWidth(), texture.getRegionHeight(), Format.RGBA8888);
		texture.getTexture().getTextureData().prepare();
		Pixmap p2 = texture.getTexture().getTextureData().consumePixmap();
		cursor.drawPixmap(p2, 0, 0, texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight());
	
		// Crosshair version
		texture = UIAtlas.findRegion("CursorCrosshair");
		Pixmap cursorCrosshair = new Pixmap(texture.getRegionWidth(), texture.getRegionHeight(), Format.RGBA8888);
		texture.getTexture().getTextureData().prepare();
		p2 = texture.getTexture().getTextureData().consumePixmap();
		cursorCrosshair.drawPixmap(p2, 0, 0, texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight());
	
		// Apply
		Day100.cursor = Gdx.graphics.newCursor(cursor, 0, 0);
		Day100.cursorCrosshair = Gdx.graphics.newCursor(cursorCrosshair, 16, 16);
		
		// Set default
		setDefaultCursor();
	}
	
	public static void setDefaultCursor(){
		Gdx.graphics.setCursor(cursor);
	}
	
	public static void setCrosshairCursor(){
		Gdx.graphics.setCursor(cursorCrosshair);		
	}

	public void update(){
		
		// Time scale lerp
		if(timeScale != targetTimeScale){
			float dst = targetTimeScale - timeScale;
			timeScale += dst * 0.05f;
		}
		
		targetTimeScale = 1f;
		deltaTime = Gdx.graphics.getDeltaTime() * timeScale;
		
		// Camera shake
		if(ScreenManager.getScreen() == Screen.MAP_EDITOR){
			CamShake.update(deltaTime);
			camera.position.add(CamShake.addition.x, CamShake.addition.y, 0);
		}else{
			CamShake.addition.set(0, 0);
		}
		
		// Camera update
		camera.update();
		UIcamera.update();
		Input.update();

		ScreenManager.update();
	}

	@Override
	public void render () {

		update();

		// Start batch using the post processor
		PostProcessing.beginRender(batch);
		
		// Normal render mode		
		ScreenManager.render();	
		
		// End batch
		PostProcessing.endRender(batch);
		
		// Render light if in map or map editor
		ScreenManager.renderLight();
		
		// UI render mode
		batch.setProjectionMatrix(UIcamera.combined);
		ScreenManager.renderUI();
		batch.end();
	}

	public void resize(int width, int height){
		Log.info(TAG, "Resized to " + width + " by " + height);
		UIcamera.setToOrtho(false, width, height);
		camera.viewportWidth = width / Constants.PPM;
		camera.viewportHeight = height / Constants.PPM;
		PostProcessing.resize(width, height);
		ScreenManager.resize(width, height);
	}

	@Override
	public void dispose () {
		try{
			ScreenManager.dispose();
			GameSettings.save();
			PostProcessing.end();
			batch.dispose();
			assets.dispose();
			UIAtlas.dispose();
			playerAtlas.dispose();
			gunsAtlas.dispose();
		}catch(Exception e){
			
		}
		
	}
}
