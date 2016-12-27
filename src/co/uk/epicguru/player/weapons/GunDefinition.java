package co.uk.epicguru.player.weapons;

import java.lang.reflect.Field;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.screens.mapeditor.GunEditor;

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
	 * The sounds that are played when shooting. A random element is picked.
	 */
	public Sound[] shotSounds = new Sound[]{ GunManager.getGunSound("Silenced.mp3") };
	
	/**
	 * The max distance that the shot from this gun can be heard from.
	 */
	public float shotSoundDistance = 35;
	
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
		if(GunManager.find(name) == null)
			GunManager.guns.add(this);
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
	
	/**
	 * Indicates that this gun has been equipped by this entity, using this GunManager. By default does nothing.
	 */
	public void equipped(GunInstance instance, GunManager gunManager, Entity e){
		
	}
	
	/**
	 * This gun was shot by this entity, using this GunManager. By default does nothing.
	 */
	public void shot(GunInstance instance, GunManager gunManager, Entity e){
		
	}
	
	/**
	 * Called when equipped. Passes the equipper (The entity) and the gun manager.
	 */
	public void update(GunInstance instance, Entity e, GunManager gunManager, float delta){
		
	}
	
	/**
	 * Called when equipped. DOES NOT RENDER GUN, BY DEFAULT DOES NOTHING.
	 * Passes the equipper (The entity) and the gun manager.
	 * This method is called AFTER the gun has been rendered so you can use this method to reset animation textures.
	 */
	public void render(GunInstance instance, Entity e, GunManager gunManager, Batch batch){

	}
	
	/**
	 * Called when equipped.
	 * Passes the equipper (The entity) and the gun manager.
	 */
	public void renderUI(GunInstance instance, Entity e, GunManager gunManager, Batch batch){
		
	}
	
	/**
	 * Gets a virtual instance of this gun.
	 */
	public GunInstance getInstance(GunManager gunManager, Entity entity){
		return new GunInstance(gunManager, entity);
	}	
}
