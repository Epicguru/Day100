package co.uk.epicguru.main;

import com.badlogic.gdx.Gdx;

public final class Log {

	public static void info(String tag, String text) {
		Gdx.app.log(tag, text);
	}

	public static void debug(String tag, String text) {
		Gdx.app.debug(tag, text);
	}

	public static void error(String tag, String text) {
		Gdx.app.error(tag, text);
	}
	
	public static void error(String tag, String text, Exception e) {
		Gdx.app.error(tag, text, e);
	}

	public static void setLogLevel(int level){
		Gdx.app.setLogLevel(level);
	}	
}
