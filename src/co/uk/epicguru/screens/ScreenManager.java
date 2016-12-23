package co.uk.epicguru.screens;

import java.util.ArrayList;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;

public final class ScreenManager {

	public static ArrayList<GameScreen> screens = new ArrayList<GameScreen>();
	private static Screen screen;
	
	public static void setScreen(Screen screen){
		
		Screen old = ScreenManager.screen;
		
		for (GameScreen gameScreen : screens){
				gameScreen.exit(screen);
		}
		
		ScreenManager.screen = screen;
		
		for (GameScreen gameScreen : screens){
				gameScreen.enter(old);
		}
		
		Log.info("Screen Manager", "Set screen to " + screen + " from " + old);
	}
	
	public static Screen getScreen(){
		return screen;
	}
	
	public static void update(){
		for (GameScreen gameScreen : screens){
			if(gameScreen.isScreen(screen)){
				gameScreen.update(Day100.deltaTime);
			}
		}
	}
	
	public static void render(){
		for (GameScreen gameScreen : screens){
			if(gameScreen.isScreen(screen)){
				gameScreen.render(Day100.batch);
			}
		}
	}
	
	public static void renderUI(){
		for (GameScreen gameScreen : screens){
			if(gameScreen.isScreen(screen)){
				gameScreen.renderUI(Day100.batch);
			}
		}
	}
	
	public static void init(){
		// TODO
		GameScreen.font = Day100.font;
		GameScreen.smallFont = Day100.smallFont;
		for (GameScreen gameScreen : screens) {
			gameScreen.init();
		}
	}

	public static void resize(int width, int height){
		for(GameScreen screen : screens){
			screen.resize(width, height);
		}
	}
	
	public static void dispose(){
		for(GameScreen screen : screens){
			screen.dispose();
		}
	}
}
