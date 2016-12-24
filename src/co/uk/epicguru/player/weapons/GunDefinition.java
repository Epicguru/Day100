package co.uk.epicguru.player.weapons;

import java.lang.reflect.Field;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Day100;

public abstract class GunDefinition {

	@Exclude
	public static final String TAG = "Gun Def";

	/**
	 * The name and identifier of the gun.
	 **/
	@Exclude
	public String name;

	/**
	 * The texture of the gun.
	 */
	@Exclude
	public TextureRegion texture;

	/**
	 * The modes in which the gun can fire.
	 **/
	public FiringMode[] firingModes = new FiringMode[]{ FiringMode.SEMI };

	/**
	 * The distance at which the gun is rendered from the player. In meters.
	 */
	public float distanceFromPlayer = 1f;

	/**
	 * The minimum time between shots, in seconds.
	 */
	public float shotInterval = 0.1f;

	/**
	 * The minimum amount of time between bursts, in seconds. Only applies in burst mode.
	 */
	public float shotBurstInterval = 0.05f;

	/**
	 * The sounds that are played when shooting. A random element is picked.
	 */
	@Exclude
	public Sound[] shotSounds = new Sound[]{ GunManager.getGunSound("Silenced.mp3") };

	/**
	 * the minimum volume at which the gun sound will be played at.
	 */
	public float minVolume = 1.0f;

	/**
	 * The maximum volume at which the gun sound will be played at.
	 */
	public float maxVolume = 1.0f;

	/**
	 * The minimum pitch at which the gun sound will be played at.
	 */
	public float minPitch = 1.0f;

	/**
	 * The maximum pitch at which the gun sound will be played at.
	 */
	public float maxPitch = 1.07f;

	/**
	 * Do not set.
	 */
	@Exclude
	public int firingModeSelected = 0;

	/**
	 * The recoil in degrees that each shot applies. The actual value will be more because this is applied over various frames whilst
	 * getting smaller. The gun offset velocity is gets 1.0 - {@link #recovery}% smaller every frame, so the final offset can be calculated given seconds.
	 */
	public float recoil = 10f;

	/**
	 * The recovery multiplier. Will multiply the angle offset value by this every frame. Default is 0.65
	 */
	public float recovery = 0.65f;

	/**
	 * The point at which the weapon is held relative to the player (THE ORIGIN). This is a vector with values between 0 and 1.
	 * Each x and y value represents a percentage of the width or height. (0.5, 0.5) would mean that the gun is held and rotated around the centre of the texture. 
	 */
	public Vector2 holdPoint = new Vector2(0, 0.5f);

	/**
	 * The point of this gun that shoots (spawns) the bullets from. It has the same format as {@link #holdPoint}. 
	 */
	public Vector2 bulletSpawn = new Vector2(1f, 0.5f);

	/**
	 * The amount of bullets in one burst in burst fire mode.
	 */
	public int burstBullets = 3;
	
	
	/**
	 * The range of the weapon in meters. The visual flash may be longer than this amount but it will not hit any targets beyond this range.
	 */
	public float range = 10f;
	
	/**
	 * The damage that this weapon deals in one hit.
	 */
	public float damage = 20;
	
	/**
	 * How many objects a bullets can penetrate in one shot. This includes enemies, and buildables.
	 */
	public int collaterals = 3;
	
	/*
	 * SEPARATOR : METHODS BELOW
	 */
	
	
	/**
	 * Loads the texture associated with this name --> Day100.gunsAtlas.findRegion(name)
	 * @param name the name of the gun, also the name of the texture.
	 */
	public GunDefinition(String name){
		this.name = name;
		this.texture = Day100.gunsAtlas.findRegion(name);
	}
	
	/**
	 * Tests if this gun is ALL of the given firing modes.
	 * @param modes The modes to test for. One or more, only returns true if all are met.
	 */
	public boolean isFiringModeAll(FiringMode... modes){
		int found = 0;
		for(FiringMode mode : modes){
			for(FiringMode mode2 : this.firingModes){
				if(mode2 == mode){
					found++;
					break;
				}
			}
		}
		return found == modes.length;
	}
	
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
			reader.getValue(name, type, f.get(this), total);
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
				if(key.equals("firingModes")){
					try {
						String[] names = reader.readString(key, "SEMI,").split(",");
						firingModes = new FiringMode[names.length];
						int index = 0;
						for(String name : names){
							if(name.trim().equals(""))
								continue;
							firingModes[index++] = FiringMode.valueOf(name.trim());							
						}
					} catch (Exception e){
						e.printStackTrace();
					}
				}else{
					// Some other string
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
					}else if(f2.getType().getSimpleName().equals("FiringMode[]")){
						String firingModes = "";
						for(FiringMode mode : this.firingModes){
							firingModes += mode + ",";
						}
						writer.write(f2.getName(), firingModes);						
					}else{
						// UNKNOWN
					}
				}					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the firing mode to the new one IF this gun supports that firing mode.
	 * @param mode The new firing mode.
	 * @return Weather the operation was successful.
	 */
	public boolean setFiringMode(FiringMode mode){
		if(!isFiringModeAll(mode))
			return false;
		
		for(int i = 0; i < firingModes.length; i++){
			if(firingModes[i] == mode)
				firingModeSelected = i;
		}
		
		return true;
	}
	
}
