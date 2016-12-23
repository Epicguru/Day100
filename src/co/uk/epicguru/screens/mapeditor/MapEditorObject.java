package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Log;

public class MapEditorObject {

	public String name;
	public TextureRegion texture;
	public File file;
	public File setup;
	public Color setupColour;
	public String ID;
	public Vector2 setupSnapOffset;
	public String supplier;
	public String shader;
	public String body = "None";
	
 	public MapEditorObject(File image){
		this.file = image;
		// IDK
		this.texture = new TextureRegion(new Texture(new FileHandle(image)));
		File setupFile = getSetupFile();
		
		if(setupFile == null){
			this.setup = generateSetupFile(image.getName().replace(".png", ""));
		}else{
			this.setup = getSetupFile();
			readValuesFromSetup();
		}
	}
	
	public String genID(){
		return file.getName().replace(".png", "");
	}
	
	public File getSetupFile(){
		String name = file.getName();
		for(File file2 : file.getParentFile().listFiles()){
			if(file2.getName().equals(name.replace(".png", "") + "_setup.txt")){
				return file2;
			}
		}
		return null;
	}
	
	public File saveSetup(){
		File newFile = new File(this.file.getParentFile().getAbsolutePath() + "\\" + name.replace(".png", "") + "_setup.txt");
		
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			Log.error("Editor Object", "Could not create file.");
			e.printStackTrace();
			return null;
		}
		
		JLineWriter writer = new JLineWriter(newFile);
		try {
			writer.open();
		} catch (FileNotFoundException e) {
			Log.error("Editor Object", "Unexpected error.");
			e.printStackTrace();
			return null;
		}
		
		writer.write("Name", name);
		writer.endLine();
		writer.write("Colour", setupColour);
		writer.endLine();
		writer.write("Snap Offset", setupSnapOffset);
		writer.endLine();
		writer.write("Supplier", supplier);
		writer.endLine();
		writer.write("ID", ID);
		writer.endLine();
		writer.write("Shader", shader);
		writer.endLine();
		writer.write("Body", body);
		writer.endLine();
		writer.dispose();
		
		return newFile;
	}
	
	public File generateSetupFile(String name){
	
		this.name = name;
		this.setupColour = new Color(1, 1, 1, 1);
		this.setupSnapOffset = new Vector2(0, 0);
		this.ID = genID();
		this.supplier = "Default";
		this.shader = "Default";
		this.body = "None";
		
		return saveSetup();		
	}
	
	private static Color WHITE = new Color(1, 1, 1, 1);
	public void readValuesFromSetup(){
		JLineReader reader = new JLineReader(this.setup);
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		this.name = reader.readString("Name", "ERROR_IN_NAME");
		reader.nextLine();
		this.setupColour = reader.readColor("Colour", WHITE);
		reader.nextLine();
		this.setupSnapOffset = reader.readVector2("Snap Offset", Vector2.Zero);
		reader.nextLine();
		this.supplier = reader.readString("Supplier", "Default");
		reader.nextLine();
		this.ID = reader.readString("ID", "ERROR_MISSING_KEY");
		reader.nextLine();
		this.shader = reader.readString("Shader", "Default");
		reader.nextLine();
		this.body = reader.readString("Body", "None");
		reader.dispose();
		
	}	
}
