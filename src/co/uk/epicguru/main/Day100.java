package co.uk.epicguru.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.map.Map;
import co.uk.epicguru.player.PlayerController;
import co.uk.epicguru.screens.Screen;
import co.uk.epicguru.screens.ScreenManager;
import co.uk.epicguru.screens.instances.Loading;
import co.uk.epicguru.screens.instances.MapEditor;
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
	public static Map map;
	public static PlayerController player;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		UIcamera = new OrthographicCamera();
		UIcamera.setToOrtho(false);
		assets = new AssetManager();


		// Add all game screens
		registerScreens();

		// Pack textures
		if(DEVELOPER_MODE)
			packTextures();

		// Load assets
		loadAssets();

		// Set screen
		ScreenManager.setScreen(Screen.LOADING);

	}

	public void registerScreens(){		
		ScreenManager.screens.add(new Loading());
		ScreenManager.screens.add(new MapEditor());
	}

	public void loadAssets(){
		// This needs to be loaded before anything else.
		long start = System.currentTimeMillis();
		Log.info(TAG, "Starting UI atlas load...");
		assets.load("Cache/UI.atlas", TextureAtlas.class);
		assets.finishLoading();			
		Log.info(TAG, "Done! It took " + ((float)(System.currentTimeMillis() - start) / 1000f) + " seconds.");
		
		// Other assets
		assets.load("Fonts/Xolonium.fnt", BitmapFont.class);
		assets.load("Fonts/Small.fnt", BitmapFont.class);
		assets.load("Cache/Player.atlas", TextureAtlas.class);
		
		// Loads all sounds
		SoundUtils.loadAllSounds();
		
		// Atlas
		assets.load("Cache/Guns.atlas", TextureAtlas.class);
		assets.load("Cache/Building.atlas", TextureAtlas.class);
		assets.load("Cache/Vehicles.atlas", TextureAtlas.class);
	}

	public void packTextures(){
		Settings settings = new Settings();
		settings.fast = false; // Set up other settings here
		settings.paddingX = 4;
		settings.paddingY = 4;
		settings.maxWidth = 4096; // POW!
		settings.maxHeight = 4096;
		TexturePacker.process(settings, "bin/Textures/UI", "bin/Cache", "UI");	
		TexturePacker.process(settings, "bin/Textures/Player", "bin/Cache", "Player");	
		TexturePacker.process(settings, "bin/Textures/Guns", "bin/Cache", "Guns");	
		TexturePacker.process(settings, "bin/Textures/Building", "bin/Cache", "Building");	
		TexturePacker.process(settings, "bin/Textures/Vehicles", "bin/Cache", "Vehicles");	
	}

	public static void init(){

		// Fonts
		font = assets.get("Fonts/Xolonium.fnt", BitmapFont.class);
		smallFont = assets.get("Fonts/Small.fnt", BitmapFont.class);
		UIAtlas = assets.get("Cache/UI.atlas", TextureAtlas.class);
		playerAtlas = assets.get("Cache/Player.atlas", TextureAtlas.class);
		gunsAtlas = assets.get("Cache/Guns.atlas", TextureAtlas.class);
		buildingAtlas = assets.get("Cache/Building.atlas", TextureAtlas.class);
		vehiclesAtlas = assets.get("Cache/Vehicles.atlas", TextureAtlas.class);

		ScreenManager.init();
	}

	public void update(){
		
		// Time scale lerp
		if(timeScale != targetTimeScale){
			float dst = targetTimeScale - timeScale;
			timeScale += dst * 0.05f;
		}
		targetTimeScale = 1f;
		deltaTime = Gdx.graphics.getDeltaTime() * timeScale;
		camera.update();
		UIcamera.update();
		Input.update();

		ScreenManager.update();
	}

	@Override
	public void render () {

		update();

		if(ScreenManager.getScreen() == Screen.LOADING)
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		else
			Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Normal render mode
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		ScreenManager.render();	
		batch.end();

		// UI render mode
		batch.setProjectionMatrix(UIcamera.combined);
		batch.begin();
		ScreenManager.renderUI();
		batch.end();
	}

	public void resize(int width, int height){
		Log.info(TAG, "Resized to " + width + " by " + height);
		UIcamera.setToOrtho(false, width, height);
		camera.viewportWidth = width / Constants.PPM;
		camera.viewportHeight = height / Constants.PPM;
		ScreenManager.resize(width, height);
	}

	@Override
	public void dispose () {
		try{
			ScreenManager.dispose();
			batch.dispose();
			assets.dispose();
			UIAtlas.dispose();
			playerAtlas.dispose();
			gunsAtlas.dispose();
		}catch(Exception e){
			
		}
		
	}
}
