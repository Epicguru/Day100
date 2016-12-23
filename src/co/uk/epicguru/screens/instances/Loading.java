package co.uk.epicguru.screens.instances;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.screens.GameScreen;
import co.uk.epicguru.screens.Screen;
import co.uk.epicguru.screens.ScreenManager;

public final class Loading extends GameScreen {

	public NinePatch barGreen, barRed;
	public TextureRegion title, loading;
	public boolean finished = false;
	
	public Loading() {
		super("Loading", Screen.LOADING);
	}
	
	@Override
	public void enter(Screen oldScreen) {

		if(ScreenManager.getScreen() != Screen.LOADING)
			return;
		
		TextureAtlas atlas = Day100.assets.get("Cache/UI.atlas", TextureAtlas.class);
		barGreen = atlas.createPatch("LoadingBarGreen");
		barRed = atlas.createPatch("LoadingBarRed");
		loading = atlas.findRegion("Loading");
		title = atlas.findRegion("Title");
		
		Log.info(TAG, "Got assets for the loading screen");
		
		// This is when the game starts
		Log.info(TAG, "Starting asset loading");
		
	}

	@Override
	public void update(float delta) {
		
		// Day100.assets.update()
		if(Day100.assets.update()){
			finished = true;
			Log.info(TAG, "Asset manager is finished loading!");
			Day100.init();
			
			// Now, if in developer mode pack cache to assets.
			if(Day100.DEVELOPER_MODE){
				Log.info(TAG, "Developer mode activated! Copying cache to assets!");
				String start = Gdx.files.getLocalStoragePath() + "bin\\cache";
				String end = Gdx.files.getLocalStoragePath().replace("\\desktop", "\\core\\assets\\Cache");
				Log.info(TAG, "Start : " + start);
				Log.info(TAG, "End : " + end);
				try {
					long startTime = System.currentTimeMillis();
					FileUtils.copyDirectory(new File(start), new File(end));
					Log.info(TAG, "Copy complete! It took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to process.");
				} catch (IOException e) {
					Log.error(TAG, "Cache copy failed!", e);
				}
			}
		}
		
		if(finished){
			ScreenManager.setScreen(Screen.MAP_EDITOR);
		}
	}

	@Override
	public void render(Batch batch) {
		// This renders in world units which we don't use for UI
	}
	
	public void renderUI(Batch batch){
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		batch.draw(title, screenWidth / 2 - title.getRegionWidth() / 2, screenHeight - title.getRegionHeight() - 0);
		batch.draw(loading, 20, 150);
		int height = 90;
		barRed.draw(batch, 10, 50, screenWidth - 20, height);
		float percentNotDone = 1f - Day100.assets.getProgress();
		float width = screenWidth - 20 - (screenWidth * percentNotDone);
		if(width < 65)
			width = 65;
		barGreen.draw(batch, 10, 50, width, height);
	}

}
