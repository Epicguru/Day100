package co.uk.epicguru.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import co.uk.epicguru.main.Log;


/**
 * This class allows for easy and simple single and multi-line IO saving of basic java types.
 * @author James Billy
 *
 */
public class JLineWriter implements Disposable{
	
	private File file;
	private PrintStream stream;
	private boolean written = false;
	private HashMap<String, Object> map = new HashMap<String, Object>();
	private StringBuilder stringBuilder = new StringBuilder();
	private boolean stringOnly = false;
	
	/**
	 * Creates a new JLineWriter class given a file to write to.
	 * @param file
	 */
	public JLineWriter(File file){
		this.file = file;
	}
	
	/**
	 * Creates a new JLineWriter given a StringBuilder to write ONE LINE ONLY to.
	 * @param output The StringBuilder object that will be written to.
	 */
	public JLineWriter(StringBuilder output){
		this.stringBuilder = output;
		this.stringOnly = true;
	}
	
	/**
	 * Gets the internal StringBuilder.
	 * @return The instance used in this class.
	 */
	public StringBuilder getBuilder(){
		return stringBuilder;
	}

	/**
	 * Gets the file. Only works with File constructor.
	 * @return The File, or null if not using the file constructor.
	 */
	public File getFile(){
		return file;
	}

	/**
	 * Opens the IO PrintStream and initializes this JLineWriter. Only use with File constructor.
	 * @throws FileNotFoundException Make sure that the file provided in constructor exists.
	 */
	public JLineWriter open() throws FileNotFoundException{
		
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
		stream = new PrintStream(file);
		return this;
	}
	
	/**
	 * Ends the line. If this is a file constructor writer, it writes the line to the file. If not, it saves the line in the StringBuilder object provided.
	 * (NOTE : This is what actually does the IO write, so this must be called for the written data to be saved)
	 */
	public JLineWriter endLine(){
		
		if(!written)
			return this;
		
		// Start the loop
		Set<String> keys = map.keySet();
		int index = 0;
		for(String key : keys){;
			Object value = map.get(key);
			
			// Now we get the type and print using print stream
			if(value instanceof Float){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<F>");
				stringBuilder.append('[');
				stringBuilder.append((float)value);
				stringBuilder.append(']');
			}
			if(value instanceof String){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<S>");
				stringBuilder.append('[');
				stringBuilder.append((String)value);
				stringBuilder.append(']');
			}	
			if(value instanceof Integer){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<I>");
				stringBuilder.append('[');
				stringBuilder.append((int)value);
				stringBuilder.append(']');
			}
			if(value instanceof Long){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<L>");
				stringBuilder.append('[');
				stringBuilder.append((long)value);
				stringBuilder.append(']');
			}	
			if(value instanceof Double){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<D>");
				stringBuilder.append('[');
				stringBuilder.append((double)value);
				stringBuilder.append(']');
			}	
			if(value instanceof Boolean){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<B>");
				stringBuilder.append('[');
				stringBuilder.append((boolean)value);
				stringBuilder.append(']');
			}	
			if(value instanceof Byte){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<By>");
				stringBuilder.append('[');
				stringBuilder.append((byte)value);
				stringBuilder.append(']');
			}
			if(value instanceof Color){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<C>");
				stringBuilder.append('[');
				stringBuilder.append(((Color)value).r + "/" + ((Color)value).g + "/" + ((Color)value).b + "/" + ((Color)value).a);
				stringBuilder.append(']');
			}
			if(value instanceof Vector2){
				stringBuilder.append("-");
				stringBuilder.append(key);			
				stringBuilder.append("<V>");
				stringBuilder.append('[');
				stringBuilder.append(((Vector2)value).x + "/" + ((Vector2)value).y);
				stringBuilder.append(']');
			}
			
			if(!stringOnly){
				// Print line in file
				if(index == keys.size() - 1)
					stream.println(stringBuilder.toString());
				else
					stream.print(stringBuilder.toString());
				
				// First clear StringBuilder which will hold the data for the line.
				clearStringBuilder();
			}else{
				// Nope
			}			
			
			index++;
		}
		
		map.clear();
		written = false;
		return this;
	}
	
	/**
	 * Only use if you know what you are doing!
	 */
	public void clearStringBuilder(){
		stringBuilder.replace(0, stringBuilder.length(), "");
	}

	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, boolean value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, int value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, float value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, String value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, long value){
		written = true;
		map.put(key, value);
		return this;
	}

	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, double value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, byte value){
		written = true;
		map.put(key, value);
		return this;
	}

	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, Color value){
		written = true;
		map.put(key, value);
		return this;
	}
	
	/**
	 * Adds a new value ready to be written.
	 * @param key The identifier that will be used to load this variable later.
	 * @param value The value to save.
	 */
	public JLineWriter write(String key, Vector2 value){
		written = true;
		map.put(key, value);
		return this;
	}

	
	@Override
	public void dispose() {
		if(stream != null){
			stream.flush();
			stream.close();
		}
		map.clear();
	}
}
