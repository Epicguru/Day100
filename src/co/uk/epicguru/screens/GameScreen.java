package co.uk.epicguru.screens;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;

public abstract class GameScreen{

	public String name;
	protected String TAG;
	private Screen[] screens;
	protected static OrthographicCamera camera = Day100.camera;
	protected static BitmapFont font; // Set in ScreenManager
	protected static BitmapFont smallFont; // Set in ScreenManager
	
	public GameScreen(String name, Screen... screens){
		Log.info("Screen Manager", "Created new Screen called " + name);
		this.screens = screens;
		this.name = name;
		this.TAG = name;
	}
	
	public void init(){
		
	}
	
	/**
	 * For debugging purposes only. Gets the lines of text that appear in the debug view.
	 * @param lines The lines. Use lines.add("text").
	 */
	public void getDebugLines(ArrayList<String> lines){
		
	}
	
	public abstract void update(float delta);
	
	public abstract void render(Batch batch);	
	
	public void renderUI(Batch batch){
		
	}
	
	public final boolean isScreen(Screen screen){
		for (Screen screen2 : screens) {
			if(screen == screen2)
				return true;
		}
		return false;
	}
	
	public void enter(Screen oldScreen){
		
	}
	
	public void exit(Screen newScreen){
		
	}

	public void dispose() {
		
	}

	public void resize(int width, int height) {
		
	}
	
}
