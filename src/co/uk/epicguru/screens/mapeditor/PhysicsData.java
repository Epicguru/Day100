package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.screens.instances.MapEditor;

public final class PhysicsData {

	/**
	 * The loaded {@link #PhysicsData(File)} objects.
	 */
	public static ArrayList<PhysicsData> loaded = new ArrayList<PhysicsData>();
	public BodyType type = BodyType.StaticBody;
	public Vector2[] points = new Vector2[0];
	public File file;
	public String image;
	public TextureRegion textureInEditor;
	public ArrayList<Vector2> editing = new ArrayList<Vector2>();
	
	/**
	 * Contains the data necessary to set up a Box2D physics body using {@link #toBody(World)}.
	 * @param file The root folder for the object.
	 */
	public PhysicsData(File file){
		this.file = file;	
	}
	
	/**
	 * Internal method that is called once upon load to verify the state of the data.
	 */
	public boolean setup(){
		for(File file : file.listFiles()){
			if(file.getName().equals("Properties.txt") && !file.isDirectory()){
				// This looks correct
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets the texture if in map editor mode. Do not call if not in that mode.
	 */
	public void setTextureFromImage(){
		MapEditorObject object = MapEditorPlaceable.getObject(image);
		if(object == null)
			return;
		
		this.textureInEditor = object.texture;
	}
	
	/**
	 * Internal method that is called once upon load.
	 */
	public void start(){
		JLineReader reader = new JLineReader(new File(file.getAbsolutePath() + "\\Properties.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		this.image = reader.readString("Image", "None");
		reader.dispose();	
			
		setFromArray(readPointsFromFile());
	}
	
	/**
	 * Creates a new array list and adds points read from the file to it.
	 * @return the ArrayList<String> of points or null if something went wrong.
	 */
	public ArrayList<Vector2> readPointsFromFile(){
		File file = new File(this.file.getAbsoluteFile() + "\\Points.JCode");
		if(!file.exists() || file.isDirectory())
			return new ArrayList<Vector2>();
		JLineReader reader2 = new JLineReader(file);
		try {
			reader2.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		if(reader2.isLineEmpty())
			return new ArrayList<Vector2>();
		
		ArrayList<Vector2> points = new ArrayList<Vector2>();
		
		int index = 0;
		Vector2 point = null;
		do{
			point = reader2.readVector2("" + index++, null);
			if(point != null){
				points.add(point);
			}			
		}while(point != null);
		
		reader2.dispose();
		
		return points;
	}
	
	/**
	 * Add a point to the internal array of points.
	 * @param vector The new point.
	 */
	public void addPoint(Vector2 vector){
		if(editing == null){
			editing = new ArrayList<Vector2>();		
			for(Vector2 vector2 : points){
				editing.add(new Vector2(vector2));
			}
		}
		
		editing.add(new Vector2(vector));
		setFromArray(editing);
	}
	
	/**
	 * Saves the data to disk.
	 */
	public void save(){
		JLineWriter writer = new JLineWriter(new File(file.getAbsoluteFile() + "\\Properties.txt"));
		try {
			writer.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		writer.write("Image", image);
		writer.endLine();
		writer.dispose();
		
		JLineWriter writer2 = new JLineWriter(new File(file.getAbsoluteFile() + "\\Points.JCode"));
		try {
			writer2.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		int index = 0;
		for(Vector2 vector : points){
			writer2.write("" + index++, vector);
		}
		writer2.endLine();
		writer2.dispose();
	}
	
	/**
	 * Sets the vertices from an array of vectors.
	 * @param array the array that is read from.
	 */
	public void setFromArray(ArrayList<Vector2> array){
		this.points = new Vector2[array.size()];
		int index = 0;
		for(Vector2 point : array){
			points[index++] = new Vector2(point);
		}
	}
	
	/**
	 * Gets the physics body, places it at 0,0 with 0 rotation.
	 * @param world The world to create the body in.
	 * @return The Box2D Body object.
	 */
	public Body toBody(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.allowSleep = true;
		bodyDef.type = this.type;
		PolygonShape shape = new PolygonShape();
		shape.set(points);
		//shape.set(new Vector2[]{new Vector2(0, 0), new Vector2(0.05f, 0.75f), new Vector2(0.5f, 1), new Vector2(0.95f, 0.75f), new Vector2(1, 0),});
		Body body = world.createBody(bodyDef);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		body.createFixture(fixture);
		shape.dispose();
		return body;
	}
	
	/**
	 * Refreshes the loaded data array. Only folders with a properties file will be added.
	 * @param getTextures Should this get the textrues for the physics? Only should be true if in editor mode.
	 */
	public static void refreshData(boolean getTextures){
		File data = new File(Constants.MAP_EDITOR_FOLDER + MapEditor.selectedMap + "\\Physics\\");
		if(!data.exists() || !data.isDirectory()){
			data.mkdirs();
			return;
		}
		
		loaded.clear();
		
		for(File file : data.listFiles()){
			if(!file.isDirectory())
				continue;	
			PhysicsData PD = new PhysicsData(file);
			boolean good = PD.setup();
			if(good){
				loaded.add(PD);	
				PD.start();
			}else{
				// Did not work
			}
		}
		
		if(!getTextures)
			return;
		
		for(PhysicsData data2 : loaded){
			data2.setTextureFromImage();
		}
	}
	
	/**
	 * Gets physics data from the loaded array.
	 * @param fileName the name of its file.
	 * @return The PhysicsData object or null if it was not found.
	 */
	public static PhysicsData find(String fileName){
		for(PhysicsData data : loaded){
			if(data.file.getName().equals(fileName))
				return data;
		}
		return null;
	}
	
	/**
	 * Saves all data loaded.
	 */
	public static void saveData(){
		for(PhysicsData data : loaded){
			data.save();
		}
	}
	
	/**
	 * (Badly) disposes the data loaded in {@link #loaded}.
	 */
	public void flushData(){
		// Meh
		loaded.clear();
	}
	
}
