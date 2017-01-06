package co.uk.epicguru.settings;

import java.io.File;
import java.io.FileNotFoundException;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Constants;

public class GameSettings {

	private static LightQuality lightQuality;
	private static ShadowQuality shadowQuality;
	
	public static LightQuality getLightQuality() {
		return lightQuality;
	}
	
	public static void setLightQuality(LightQuality lightQuality) {
		GameSettings.lightQuality = lightQuality;
	}
	
	public static ShadowQuality getShadowQuality() {
		return shadowQuality;
	}
	
	public static void setShadowQuality(ShadowQuality shadowQuality) {
		GameSettings.shadowQuality = shadowQuality;
	}
	
	public static void save(){
		/*
		 * Light
		 * Shadow
		 */
		
		JLineWriter writer = new JLineWriter(new File(Constants.DAY_100_FOLDER + "Settings\\Settings.txt"));
		try {
			writer.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		writer.write("Light Q", lightQuality.toString());
		writer.endLine();
		writer.write("Shadow Q", shadowQuality.toString());
		writer.endLine();
		
		// End
		writer.dispose();
	}
	
	public static void load(){
		/*
		 * Light
		 * Shadow
		 */
		
		JLineReader reader = new JLineReader(new File(Constants.DAY_100_FOLDER + "Settings\\Settings.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		GameSettings.lightQuality = LightQuality.valueOf(reader.readString("Light Q", "HIGH"));
		reader.nextLine();
		GameSettings.shadowQuality = ShadowQuality.valueOf(reader.readString("Shadow Q", "HIGH"));
		reader.nextLine();
		
		// End
		reader.dispose();
	}
	
}