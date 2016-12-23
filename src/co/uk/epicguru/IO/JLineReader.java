package co.uk.epicguru.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import co.uk.epicguru.main.Log;

/**
 * Class that works alongside JLineWriter to read variables from file. Uses default value system for compatibility.
 * @author James Billy
 */
public class JLineReader implements Disposable{

	private File file;
	private BufferedReader reader;
	private String line = "";
	private HashMap<String, Object> map = new HashMap<String, Object>();
	private boolean stringOnly = false;

	/**
	 * Creates a new reader that is set up to read lines from the file provided.
	 * @param file The File to read from.
	 */
	public JLineReader(File file){		
		this.file = file;
	}

	/**
	 * Creates a JLineReader that reads from this line of JCode only. Some methods in this object only work with the file constructor, they are fairly obvious.
	 * @param input The single line of JCode input. Therefore, methods such as {@link #nextLine()} will not work.
	 */
	public JLineReader(String input){
		stringOnly = true;
		this.line = input;
		readVars();
	}

	/**
	 * Sets the line of JCode and reads all the data from it.
	 * This works with both constructors, but it is better to use it with the {@link #JLineReader(String)} constructor.
	 * @param line
	 */
	public void setLine(final String line){
		this.line = line;
		readVars();
	}
	
	/**
	 * If using the file constructor, resets the reader to line 0.
	 */
	public void restart(){
		try {
			reader.reset();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		nextLine();
	}

	/**
	 * Gets the file.
	 * @return The File.
	 */
	public File getFile(){
		return file;
	}

	/**
	 * Opens the reader. Only works with File constructor.
	 * @throws FileNotFoundException Make sure that the file exists!
	 */
	public JLineReader open() throws FileNotFoundException{

		if(stringOnly)
			return this;

		if(!file.exists()){
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				Log.error("JLineWriter", "File : " + file.getAbsolutePath());
				Log.error("JLineWriter", "Parent : " + file.getParentFile().getAbsolutePath());
				throw new FileNotFoundException();
			}
		}

		reader = new BufferedReader(new FileReader(file));
		nextLine();
		return this;
	}

	/**
	 * Is the current line empty?
	 * @return The value.
	 */
	public boolean isLineEmpty(){
		return line == null || line.isEmpty();
	}

	/**
	 * Moves the IO on to the next line of variables.
	 * @return True if this call has made the reader reach the end of the file.
	 */
	public boolean nextLine(){

		if(stringOnly)
			return true;

		try {
			line = reader.readLine();
			if(line == null)
				return true;
			readVars();
			return false;
		} catch (IOException e) {
			return true;			
		}

	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public float readFloat(final String key, final float defaultValue){
		if(map.containsKey(key))
			return (float)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public String readString(final String key, final String defaultValue){
		if(map.containsKey(key))
			return (String)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public int readInt(final String key, int defaultValue){
		if(map.containsKey(key))
			return (int)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public long readLong(final String key, long defaultValue){
		if(map.containsKey(key))
			return (long)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public double readDouble(final String key, double defaultValue){
		if(map.containsKey(key))
			return (double)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public boolean readBoolean(final String key, boolean defaultValue){
		if(map.containsKey(key))
			return (boolean)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public byte readByte(final String key, byte defaultValue){
		if(map.containsKey(key))
			return (byte)map.get(key);
		else
			return defaultValue;
	}
	
	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public Vector2 readVector2(final String key, Vector2 defaultValue){
		if(map.containsKey(key))
			return (Vector2)map.get(key);
		else
			return defaultValue;
	}
	
	/**
	 * Gets a value from the current line.
	 * @param key The key that was used when writing the value.
	 * @param defaultValue The default value if the variable could not be found.
	 * @return The value that is associated with the key. If the value was not found, defaultValue is returned.
	 */
	public Color readColor(final String key, Color defaultValue){
		if(map.containsKey(key))
			return (Color)map.get(key);
		else
			return defaultValue;
	}

	/**
	 * Gets all of the variables for this line, in a HashMap format. The Key is a String value and the Value is an Object. 
	 * Use 'value instanceof ...' to find the type of the variable and cast.
	 * @return The whole hashmap of variables for the current line.
	 */
	public HashMap<String, Object> getAll(){
		return map;
	}

	/**
	 * Does the current line contain the key?
	 * @param key The key string.
	 */
	public boolean lineContains(final String key){
		for(String key2 : getAll().keySet()){
			if(key.equals(key2))
				return true;
		}
		return false;
	}

	private ArrayList<String> parts = new ArrayList<String>();
	private StringBuilder stringBuilder = new StringBuilder();
	private void readVars(){

		map.clear();

		// Get the different parts (Separated by the '-' symbol)
		if(line.trim().equals(""))
			return;

		char[] chars = new char[line.length()];
		line.getChars(0, line.length(), chars, 0);
		boolean afterMinus = false;
		for(char c : chars){

			if(c == '-')
				afterMinus = true;

			if(c == ']'){
				// Add that
				stringBuilder.append(c);
				parts.add(stringBuilder.toString().trim());
				clearB();
				afterMinus = false;
			}else{
				if(afterMinus)
					stringBuilder.append(c);
			}
		}

		for(String s : parts){

			int start = s.indexOf('<') + 1;
			int end = s.indexOf('>');
			String type = s.substring(start, end);

			start = s.indexOf('-') + 1;
			end = s.indexOf('<');
			String name = s.substring(start, end);

			start = s.indexOf('[') + 1;
			end = s.indexOf(']');
			String value = s.substring(start, end);

			if(type.equals("F")){
				map.put(name, Float.parseFloat(value));				
			}
			if(type.equals("S")){
				map.put(name, value);				
			}
			if(type.equals("I")){
				map.put(name, Integer.parseInt(value));				
			}
			if(type.equals("L")){
				map.put(name, Long.parseLong(value));				
			}
			if(type.equals("D")){
				map.put(name, Double.parseDouble(value));				
			}
			if(type.equals("B")){
				map.put(name, Boolean.parseBoolean(value));				
			}
			if(type.equals("By")){
				map.put(name, Byte.parseByte(value));				
			}
			if(type.equals("V")){
				String[] split = splitVector(value, '/');
				map.put(name, new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1])));
			}
			if(type.equals("C")){
				String[] split = splitColour(value, '/');
				map.put(name, new Color(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
			}
		}

		clearB();
		parts.clear();
	}
	
	private String[] splitVector(final String text, final char separator){
		String[] returnValue = new String[2];
		int indexOfSep = text.indexOf(separator);
		returnValue[0] = text.substring(0, indexOfSep);
		returnValue[1] = text.substring(indexOfSep + 1, text.length());
		
		return returnValue;
	}
	
	private String[] splitColour(final String text, final char separator){
		String[] returnValue = new String[4];
		int indexOfSep = text.indexOf(separator);
		int indexOfSep1 = text.indexOf(separator, indexOfSep + 1);
		int indexOfSep2 = text.indexOf(separator, indexOfSep1 + 1);
		returnValue[0] = text.substring(0, indexOfSep);
		returnValue[1] = text.substring(indexOfSep + 1, indexOfSep1);
		returnValue[2] = text.substring(indexOfSep1 + 1, indexOfSep2);
		returnValue[3] = text.substring(indexOfSep2 + 1, text.length());
		
		return returnValue;
	}

	private void clearB(){
		stringBuilder.replace(0, stringBuilder.length(), "");
	}

	@Override
	public void dispose() {
		if(reader != null){
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
