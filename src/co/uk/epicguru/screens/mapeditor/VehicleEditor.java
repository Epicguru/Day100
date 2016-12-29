package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.helpers.AlphabeticalHelper;
import co.uk.epicguru.helpers.VirtualCoordinate;
import co.uk.epicguru.input.DevCross;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.player.weapons.FieldReader;
import co.uk.epicguru.screens.instances.MapEditor;
import co.uk.epicguru.screens.instances.MapEditor.State;
import co.uk.epicguru.vehicles.VehicleDefinition;
import co.uk.epicguru.vehicles.VehiclesManager;

public final class VehicleEditor {

	private static final String TAG = "Vehicle Editor";
	public static boolean open;
	private static VisWindow properties;
	private static VisWindow vehicle;
	private static VisList<String> vehiclesList;
	public static int count = 0;
	private static String oldSelected;
	private static ArrayList<ReflectionActor> reflection = new ArrayList<ReflectionActor>();
	private static Array<String> sounds = new Array<String>();

	public static void open(){
		if(open)
			return;

		// Get updated vehicle list, load vehicle data.
		VehiclesManager.reset();
		
		if(VehiclesManager.vehicles.isEmpty()){
			MapEditor.state = State.CREATION;
			Log.error(TAG, "Exiting, no vehicles.");
			return;
		}

		// Build vehicle select window
		vehicle = VisUIHandler.newWindow("Vehicles - Drag Inside to scroll");
		vehicle.setPosition(0, 0);
		vehicle.setMovable(false);
		vehicle.setSize(210, 300);
		vehiclesList = new VisList<String>();
		String[] names = new String[VehiclesManager.vehicles.size()];
		for(int i = 0; i < names.length; i++){
			names[i] = VehiclesManager.vehicles.get(i).name;
		}		
		vehiclesList.setItems(names);

		VisScrollPane scroll = new VisScrollPane(vehiclesList);
		vehicle.add(scroll);

		Log.info(TAG, "Created window.");
		
		// Get sounds
		sounds.clear();
		Array<String> allAssets = Day100.assets.getAssetNames();
		for(String string : allAssets){

			if(!string.endsWith(".mp3"))
				continue;			
			if(Day100.assets.get(string) instanceof Music)
				continue;

			String[] split = string.split("/");
			String name = split[split.length - 1];
			sounds.add(name);
		}
		sounds.add("Null");
		String[] unsorted = sounds.toArray(String.class);
		AlphabeticalHelper.sort(unsorted);
		sounds.clear();
		sounds.addAll(unsorted); // Unsorted, despite its name, is now sorted.
		
		Log.info(TAG, "Loaded and sorted sound.");
		
		loadVehiclesProperties(VehiclesManager.vehicles.get(0).name);
		
		open = true;
		Log.info(TAG, "Opened");
	}

	private static void loadVehiclesProperties(String vehicle){

		// Build properties window
		properties = VisUIHandler.newWindow(vehicle);
		properties.setWidth(560);
		properties.setPosition(Gdx.graphics.getWidth() - properties.getWidth(), 0);
		properties.setMovable(false);

		// Reset count
		count = 0;

		// Clear reflection data
		reflection.clear();		

		VehiclesManager.find(vehicle).getFields(new FieldReader() {
			public void getValue(String name, String type, Object value, int total) {

				count++;

				// Get value
				VisLabel fieldLabel = new VisLabel(name + " : ");
				properties.add(fieldLabel);
				if(type.equals("float")){
					VisValidatableTextField text = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String string) {
							try{
								Float.parseFloat(string);
								return true;
							}catch(Exception e){
								return false;
							}
						}
					});
					text.setText(value.toString());
					properties.add(text);
					reflection.add(new FloatReflectionActor(name, type, text));
				}else if(type.equals("Vector2")){
					
					// Colour, if virtual texture point
					try {
						if(VehiclesManager.find(vehicle).getClass().getField(name).isAnnotationPresent(VirtualCoordinate.class)){
							Color color = null;
							String colorName = null;
							try {
								colorName = VehiclesManager.find(vehicle).getClass().getField(name).getAnnotation(VirtualCoordinate.class).value();
							} catch (Exception e){
								
							}
							
							try{					
								color = (Color) Color.class.getField(colorName).get(null);
							}catch(Exception e){
								
							}
							
							if(color == null)
								color = Color.WHITE;
							
							fieldLabel.setColor(color);
						}
					} catch (Exception e){
						
					}
					
					
					Vector2 vector = (Vector2)value;
					if(vector == null)
						vector = new Vector2();
					VisValidatableTextField text = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String string) {
							try{
								Float.parseFloat(string);
								return true;
							}catch(Exception e){
								return false;
							}
						}
					});
					VisValidatableTextField text2 = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String string) {
							try{
								Float.parseFloat(string);
								return true;
							}catch(Exception e){
								return false;
							}
						}
					});
					text.setText(vector.x + "");
					text2.setText(vector.y + "");
					properties.add(text);
					properties.add(text2);
					reflection.add(new VectorReflectionActor(name, type, text, text2));
				}else if(type.equals("int")){
					VisValidatableTextField text = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String string) {
							try{
								Integer.parseInt(string);
								return true;
							}catch(Exception e){
								return false;
							}
						}
					});
					properties.add(text);
					reflection.add(new IntReflectionActor(name, type, text));
					text.setText(value.toString());
				}else if(type.equals("Sound")){
					// Compact!
					newSound(name, type, value);
				}else if(type.equals("Sound[]")){
					// Sound array
					Sound[] array = (Sound[])value;
					int length = array.length;
					properties.add(new VisTextButton("New sound...", new ChangeListener(){
						public void changed(ChangeEvent arg0, Actor arg1) {
							// New sound.
							saveAll();
							properties.fadeOut(0);
							VehicleDefinition vehicle = VehiclesManager.find(properties.getTitleLabel().getText().toString());
							Sound[] oldArray = null;
							try {
								oldArray = (Sound[]) vehicle.getClass().getField(name).get(vehicle);
							} catch (Exception e){
								e.printStackTrace();
							}
							Sound[] newArray = new Sound[oldArray.length + 1];
							System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
							try {
								vehicle.getClass().getField(name).set(vehicle, newArray);
							} catch (Exception e) {
								e.printStackTrace();
							}
							loadVehiclesProperties(vehicle.name);
						}						
					}));
					properties.row();
					for(int i = 0; i < length; i++){
						newSound(name, type, array[i], i);
						properties.row();
						count++;
					}
				}else{
					// UNKNOWN
					VisLabel label = new VisLabel("UNKNOWN TYPE - " + type);
					label.setColor(Color.RED);
					properties.add(label);


					Log.error(TAG, "Unknown type : " + type);
				}
				properties.row();
			}
		});
		properties.setHeight(30 * count + 30);
	}

	private static void newSound(String name, String type, Object value){
		VisList<String> soundNames = new VisList<String>();
		soundNames.setItems(sounds);
		VisLabel label;
		label = new VisLabel(getSelectedSound(value));
		if(label.getText().toString().equals("Null"))
			label.setColor(Color.RED);
		else
			label.setColor(Color.GREEN);

		VisTextButton button = new VisTextButton("Change sound", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				VisWindow newSound = VisUIHandler.newWindow("Select a new Sound");
				newSound.add(soundNames);
				newSound.row();
				newSound.add(new VisTextButton("Confirm new Sound", new ChangeListener(){
					public void changed(ChangeEvent arg0, Actor arg1) {
						newSound.fadeOut();
						// Apply
						label.setText(soundNames.getSelected());
						if(label.getText().toString().equals("Null"))
							label.setColor(Color.RED);
						else
							label.setColor(Color.GREEN);
					}								
				}));
				newSound.pack();
				newSound.center();
			}						
		});					

		properties.add(label);
		properties.add(button);
		reflection.add(new SoundReflectionActor(name, type, new Actor[]{label}));
	}

	private static void newSound(String name, String type, Object value, int index){
		VisList<String> soundNames = new VisList<String>();
		soundNames.setItems(sounds);
		VisLabel label;
		label = new VisLabel(getSelectedSound(value));
		if(label.getText().toString().equals("Null"))
			label.setColor(Color.RED);
		else
			label.setColor(Color.GREEN);

		VisTextButton button = new VisTextButton("Change sound", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				VisWindow newSound = VisUIHandler.newWindow("Select a new Sound");
				newSound.add(soundNames);
				newSound.row();
				newSound.add(new VisTextButton("Confirm new Sound", new ChangeListener(){
					public void changed(ChangeEvent arg0, Actor arg1) {
						newSound.fadeOut();
						// Apply
						label.setText(soundNames.getSelected());
						if(label.getText().toString().equals("Null"))
							label.setColor(Color.RED);
						else
							label.setColor(Color.GREEN);
					}								
				}));
				newSound.pack();
				newSound.center();
			}						
		});	
		SoundArrayItemReflectionActor myActor = new SoundArrayItemReflectionActor(name, type, new Actor[]{label}, index);
		VisTextButton delete = new VisTextButton("Remove", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				int count = 0;
				for(ReflectionActor actor : reflection){
					if(actor.fieldName.equals(name)){
						count++;
						SoundArrayItemReflectionActor actor2 = (SoundArrayItemReflectionActor)actor;
						if(actor2 != myActor){
							if(actor2.index > index){
								actor2.index--;
							}
						}						
					}
				}
				VehicleDefinition vehicleDefinition = VehiclesManager.find(properties.getTitleLabel().getText().toString());
				try {
					vehicleDefinition.getClass().getField(name).set(vehicleDefinition, new Sound[--count]);
				} catch (Exception e){
					e.printStackTrace();
				}
				myActor.removed = true;
				saveAll();
				properties.fadeOut(0);
				loadVehiclesProperties(properties.getTitleLabel().getText().toString());
			}			
		});

		VisLabel item = new VisLabel("Item " + index + " - ");
		item.setColor(new Color(0.3f, 0.8f, 1, 1));
		properties.add(item);
		properties.add(label);
		properties.add(button);
		properties.add(delete);
		reflection.add(myActor);
	}

	public static String getSoundAssetFromEnding(final String ending){
		for(String asset : Day100.assets.getAssetNames()){
			if(asset.endsWith(ending)){
				String temp = new String(asset);
				if(!temp.replace(ending, "").endsWith("/"))
					continue;
				return asset;
			}
		}
		return "Null";
	}

	public static String getSelectedSound(Object value){
		String selected = "Null";
		// Get selected
		if(value == null){
			selected = "Null";
		}else{
			for(String sound : sounds){
				if(sound.equals("Null"))
					continue;
				String assetName = null;
				boolean found = false;
				for(String asset : Day100.assets.getAssetNames()){
					if(asset.endsWith(sound)){
						String temp = new String(asset);
						if(!temp.replace(sound, "").endsWith("/"))
							continue;
						assetName = asset;
						found = true;
						break;
					}
				}
				if(!found)
					Log.error(TAG, "Failed to find sound data for asset " + sound);
				Object loadedAsset = Day100.assets.get(assetName);
				if(loadedAsset instanceof Music){
					continue;
				}
				//Log.info("Got sound for ", assetName);
				if(loadedAsset == value){
					selected = sound;
					break;
				}
			}
		}
		return selected;
	}

	private static void disposePropertiesWindow(){
		properties.fadeOut(0);
		properties = null;
	}

	private static void saveVehicle(JLineWriter writer, String name){
		if(Day100.DEVELOPER_MODE){
			VehiclesManager.find(name).save(writer);
			writer.endLine();	
			Log.info(TAG, "Saved " + name);
		}
	}

	private static void saveAll(){

		// Save vehicle data
		JLineWriter writer = null;
		if(Day100.DEVELOPER_MODE){
			writer = new JLineWriter(new File(Gdx.files.getLocalStoragePath().replace("\\desktop", "/core/assets/Cache/Vehicles.txt")));
			Log.info(TAG, "Saving all data to " + writer.getFile().getAbsolutePath());
			try {
				writer.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
		}


		for(VehicleDefinition vehicleDefinition : VehiclesManager.vehicles){
			Log.info(TAG, "Saving data for '" + vehicleDefinition.name + "' ...");
			// Apply vehicle data to vehicle that is being edited
			if(properties.getTitleLabel().getText().toString().equals(vehicleDefinition.name))
				apply(vehicleDefinition.name);
			// Save data
			if(Day100.DEVELOPER_MODE)
				saveVehicle(writer, vehicleDefinition.name);
		}
		if(writer != null)
			writer.dispose();

		try {
			if(Day100.DEVELOPER_MODE)
				FileUtils.copyFile(writer.getFile(), new File(Gdx.files.getLocalStoragePath() + "bin\\cache\\Vehicles.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void update(float delta){

		if(vehicle == null)
			return;

		if(oldSelected != null && !oldSelected.equals(vehiclesList.getSelected()))
			selectedChange(vehiclesList.getSelected());

		oldSelected = vehiclesList.getSelected();
	}

	public static void render(Batch batch){
		VehicleDefinition vehicleDefinition = VehiclesManager.find(vehiclesList.getSelected());
		batch.draw(vehicleDefinition.defaultTexture, 0, 0, vehicleDefinition.defaultTexture.getRegionWidth() / Constants.PPM, vehicleDefinition.defaultTexture.getRegionHeight() / Constants.PPM); 
		
		
		for(ReflectionActor actor : reflection){
			VisValidatableTextField x = null;
			VisValidatableTextField y = null;
			try {
				if(vehicleDefinition.getClass().getField(actor.fieldName).isAnnotationPresent(VirtualCoordinate.class)){
					x = (VisValidatableTextField) actor.actors[0];
					y = (VisValidatableTextField) actor.actors[1];
				}
			} catch (Exception e){
				
			}
			if(x == null || y == null)
				continue;

			float xPos = x.isInputValid() ? Float.parseFloat(x.getText().trim()) : 0;
			float yPos = y.isInputValid() ? Float.parseFloat(y.getText().trim()) : 0;

			Color color = null;
			
			String colorName = null;
			try {
				colorName = vehicleDefinition.getClass().getField(actor.fieldName).getAnnotation(VirtualCoordinate.class).value();
			} catch (Exception e){
				
			}
			
			try{					
				color = (Color) Color.class.getField(colorName).get(null);
			}catch(Exception e){
				
			}
			
			if(color == null)
				color = Color.BLACK;
			
			DevCross.drawCentred(vehicleDefinition.defaultTexture.getRegionWidth() / Constants.PPM * xPos, vehicleDefinition.defaultTexture.getRegionHeight() / Constants.PPM * yPos, 0.5f * Day100.camera.zoom, color);
		}
	}

	public static void selectedChange(String selected){
		saveAll();
		disposePropertiesWindow();
		loadVehiclesProperties(selected);
		VehicleDefinition vehicleDefinition = VehiclesManager.find(selected);
		Day100.camera.position.set(vehicleDefinition.defaultTexture.getRegionWidth() / Constants.PPM / 2, vehicleDefinition.defaultTexture.getRegionHeight() / Constants.PPM / 2, 0);
	}

	public static void apply(String selected){
		// TODO
		Log.info(TAG, "Applying to " + selected);
		VehicleDefinition v = VehiclesManager.find(selected);
		for(ReflectionActor actor : reflection){
			Log.info(TAG, actor.fieldName);
			try {
				actor.apply(v);
			} catch (Exception e) {
				// Ignore, this will happen a lot.
				// Log.error(TAG, "ERROR", e);
			}
		}
	}

	public static void close(){
		if(!open)
			return;

		saveAll();

		sounds.clear();
		properties.fadeOut();
		vehicle.fadeOut();
		open = false;
	}

	public static void resize(int width, int height){
		if(properties == null)
			return;

		properties.setPosition(width - properties.getWidth(), 0);
	}

}
