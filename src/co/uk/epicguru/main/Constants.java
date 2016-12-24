package co.uk.epicguru.main;

import javax.swing.JFileChooser;

public final class Constants {

	public static final String DOCUMENTS_FOLDER = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\";
	public static final String MY_GAMES_FOLDER = DOCUMENTS_FOLDER + "My Games\\";
	public static final String DAY_100_FOLDER = MY_GAMES_FOLDER + "Day 100\\";
	public static final String MAP_EDITOR_FOLDER = DAY_100_FOLDER + "Map Editor\\";
	public static final String MUSIC = "Audio/Music/";
	public static final float PPM = 32f;
	public static final int RAYS = 200;
	
}
