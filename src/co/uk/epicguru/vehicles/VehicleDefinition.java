package co.uk.epicguru.vehicles;

import java.lang.reflect.Field;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.helpers.Exclude;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.player.weapons.FieldReader;
import co.uk.epicguru.screens.mapeditor.GunEditor;

public abstract class VehicleDefinition {

	@Exclude
	private static final String TAG = "Vehicle Definition";

	@Exclude
	public TextureRegion defaultTexture;
	
	@Exclude
	public String name;
	
	public int maxRiders = 1;

	public VehicleDefinition(String name){
		this.name = name;
		this.defaultTexture = Day100.vehiclesAtlas.findRegion(name + "/" + name);
		if(VehiclesManager.find(name) == null)
			VehiclesManager.vehicles.add(this);
		else
			Log.error(TAG, "Allready a " + name + " registered!");
	}
	
	@Exclude
	protected static Vector2 useMe = new Vector2();
	public Vector2 getPosForRider(int index){
		useMe.set(0, 0);
		return useMe;
	}

	/**
	 * @see {@link #getInstance(Vector2, float, Vector2, float)}}
	 */
	public VehicleInstance getInstance(Vector2 position, float angle){
		return getInstance(position, angle, Vector2.Zero, 0);
	}

	/**
	 * @see {@link #getInstance(Vector2, float, Vector2, float)}}
	 */
	public VehicleInstance getInstance(Vector2 position){
		return getInstance(position, 0, Vector2.Zero, 0);
	}

	/**
	 * Gets a new instance of this vehicle.
	 * @param position THe position to spawn at.
	 * @param angle The angle to spawn it with.
	 * @param linearVelocity The linear velocity to initially apply.
	 * @param angularVelocity The angular velocity to initially apply.
	 * @return The new VehicleInstance corresponding to the definition.
	 */
	public abstract VehicleInstance getInstance(Vector2 position, float angle, Vector2 linearVelocity, float angularVelocity);

	/**
	 * Internal reflection method.
	 */
	public void getFields(FieldReader reader){
		Field[] f = this.getClass().getFields();
		int excludes = 0;
		// Get excluded amount
		for(Field f2 : f){
			if(f2.isAnnotationPresent(Exclude.class))
				excludes++;
		}
		// Process the field
		for(Field f2 : f){
			try {
				if(!f2.isAnnotationPresent(Exclude.class))
					processField(f2, reader, f.length - excludes);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Internal reflection method.
	 */
	private void processField(Field f, FieldReader reader, int total){
		String name = f.getName();
		String type = f.getType().getSimpleName();
		try {
			Object o = f.get(this);

			if(o == null) Log.error(TAG, "Field " + name + " is null!");

			reader.getValue(name, type, o, total);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads internal values from file.
	 * @param reader The reader to read from.
	 */
	public void load(JLineReader reader){
		for(String key : reader.getAll().keySet()){
			Object value = reader.getAll().get(key);
			if(value instanceof Float){
				try {
					this.getClass().getField(key).setFloat(this, reader.readFloat(key, -1f));
				} catch (Exception e){
					e.printStackTrace();
				}
			}else if(value instanceof Vector2){
				try {
					this.getClass().getField(key).set(this, reader.readVector2(key, Vector2.Zero));
				} catch (Exception e){
					e.printStackTrace();
				}
			}else if(value instanceof Integer){
				try {
					this.getClass().getField(key).setInt(this, reader.readInt(key, 0));
				} catch (Exception e){
					e.printStackTrace();
				}
			}else if(value instanceof String){
				// Test for SYS_SOUND and not SYS_SOUND_ARRAY
				if(((String) value).startsWith("SYS_SOUND") && !((String) value).startsWith("SYS_SOUND_ARRAY")){
					String[] split = ((String) value).split(":");
					String soundPath = split[1];
					soundPath = GunEditor.getSoundAssetFromEnding(soundPath);
					if(soundPath.equals("Null")){
						try {
							this.getClass().getField(key).set(this, null);
						} catch (Exception e){
							e.printStackTrace();
						}
					}else{
						try {
							this.getClass().getField(key).set(this, Day100.assets.get(soundPath, Sound.class));
						} catch (Exception e){
							e.printStackTrace();
						}						
					}
				}else if(((String) value).startsWith("SYS_SOUND_ARRAY")){
					String[] split = ((String) value).split(":");
					if(split.length > 1 && split.length > 0){
						String parts = split[1];
						String[] sounds = parts.split(",");
						try {
							this.getClass().getField(key).set(this, new Sound[sounds.length]);
						} catch (Exception e){
							e.printStackTrace();
						}
						Sound[] array = null;
						try {
							array = (Sound[]) this.getClass().getField(key).get(this);
						} catch (Exception e){
							e.printStackTrace();
						}
						for(int i = 0; i < sounds.length; i++){ // -1 because the last item is empty.
							String soundPath = GunEditor.getSoundAssetFromEnding(sounds[i]);
							if(soundPath.equals("Null")){
								array[i] = null;								
							}else{
								array[i] = Day100.assets.get(soundPath, Sound.class);						
							}
						}
					}						
				}else{
					// Normal String
					try {
						this.getClass().getField(key).set(this, (String)value);
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}else{
				// UNKNOWN				
			}
		}
	}

	/**
	 * Saves internal values to file.
	 * @param writer The writer to write to. I'm a poet and I know it.
	 */
	public void save(JLineWriter writer){
		writer.write("name", name);
		
		// Reflection save
		Field[] f = this.getClass().getFields();
		for(Field f2 : f){
			try {
				if(!f2.isAnnotationPresent(Exclude.class)){
					// Save
					if(f2.getType().getSimpleName().equals("float")){
						writer.write(f2.getName(), f2.getFloat(this));
					}else if(f2.getType().getSimpleName().equals("Vector2")){
						Vector2 finalValue = (Vector2) f2.get(this);	
						writer.write(f2.getName(), finalValue);
					}else if(f2.getType().getSimpleName().equals("int")){
						writer.write(f2.getName(), f2.getInt(this));
					}else if(f2.getType().getSimpleName().equals("Sound")){
						String name = GunEditor.getSelectedSound(f2.get(this));
						writer.write(f2.getName(), "SYS_SOUND:" + name);
					}else if(f2.getType().getSimpleName().equals("Sound[]")){
						Sound[] array = (Sound[])f2.get(this);
						String result = "";
						for(Sound s : array){
							String name = GunEditor.getSelectedSound(s);
							result += name + ",";
						}
						writer.write(f2.getName(), "SYS_SOUND_ARRAY:" + result);
					}else if(f2.getType().getSimpleName().equals("String")){
						writer.write(f2.getName(), (String)f2.get(this));
					}else{
						// UNKNOWN
					}
				}					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
