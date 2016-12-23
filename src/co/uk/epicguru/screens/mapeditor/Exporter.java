package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public final class Exporter {
	private Exporter(){}
	
	public static void exportTextures(File mapSource){
		TexturePacker.process(mapSource.getAbsolutePath() + "\\Raw Objects\\", mapSource.getAbsolutePath() + "\\Export\\Cache\\", "Packed");
	}
	
	public static void exportData(File fileSource){
		try {
			FileUtils.copyFile(new File(fileSource.getAbsoluteFile() + "\\Map\\Placeables.txt"), new File(fileSource.getAbsoluteFile() + "\\Export\\Placeables.txt"));
			FileUtils.copyFile(new File(fileSource.getAbsoluteFile() + "\\Properties.txt"), new File(fileSource.getAbsoluteFile() + "\\Export\\Properties.txt"));
			FileUtils.copyDirectory(new File(fileSource.getAbsoluteFile() + "\\Physics\\"), new File(fileSource.getAbsoluteFile() + "\\Export\\Physics\\"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
