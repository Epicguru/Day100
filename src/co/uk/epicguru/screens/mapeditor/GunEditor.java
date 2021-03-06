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
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextArea;
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
import co.uk.epicguru.player.weapons.FiringMode;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.screens.instances.MapEditor;
import co.uk.epicguru.screens.instances.MapEditor.State;

public final class GunEditor {

	public static boolean open;
	private static VisWindow properties;
	private static VisWindow gun;
	private static VisList<String> gunsList;
	public static int count = 0;
	private static String oldSelected;
	private static ArrayList<ReflectionActor> reflection = new ArrayList<ReflectionActor>();
	private static VisWindow renamer;
	private static VisTextArea renamerPath;
	private static VisTextArea renamerStart;
	private static Array<String> sounds = new Array<String>();

	public static void open(){
		if(open)
			return;

		if(GunManager.guns.isEmpty()){
			MapEditor.state = State.CREATION;
			return;
		}

		// Get updated guns list, load gun data.
		GunManager.reset();

		// Build gun select window
		gun = VisUIHandler.newWindow("Guns - Drag Inside to scroll");
		gun.setPosition(0, 0);
		gun.setMovable(false);
		gun.setSize(210, 300);
		gunsList = new VisList<String>();
		String[] names = new String[GunManager.guns.size()];
		for(int i = 0; i < names.length; i++){
			names[i] = GunManager.guns.get(i).name;
		}		
		gunsList.setItems(names);

		VisScrollPane scroll = new VisScrollPane(gunsList);
		gun.add(scroll);

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
		
		loadGunProperties(GunManager.guns.get(0).name);
		
		renamer();
		
		open = true;
	}

	private static void loadGunProperties(String gun){

		// Build properties window
		properties = VisUIHandler.newWindow(gun);
		properties.setWidth(560);
		properties.setPosition(Gdx.graphics.getWidth() - properties.getWidth(), 0);
		properties.setMovable(false);

		// Reset count
		count = 0;

		// Clear reflection data
		reflection.clear();		

		GunManager.find(gun).getFields(new FieldReader() {
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
					
					// Colour, if virtual gun point
					try {
						if(GunManager.find(gun).getClass().getField(name).isAnnotationPresent(VirtualCoordinate.class)){
							Color color = null;
							String colorName = null;
							try {
								colorName = GunManager.find(gun).getClass().getField(name).getAnnotation(VirtualCoordinate.class).value();
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
				}else if(type.equals("FiringMode[]")){
					VisCheckBox[] boxes = new VisCheckBox[FiringMode.values().length];
					int index = 0;
					for(FiringMode mode : FiringMode.values()){
						VisCheckBox box = new VisCheckBox(mode.toString().toUpperCase());
						GunDefinition gunDef = GunManager.find(gun);
						box.setChecked(gunDef.isFiringModeAll(mode));
						properties.add(box);
						boxes[index++] = box;
					}
					reflection.add(new FiringModeReflectionActor(name, type, boxes));
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
							GunDefinition gun = GunManager.find(properties.getTitleLabel().getText().toString());
							Sound[] oldArray = null;
							try {
								oldArray = (Sound[]) gun.getClass().getField(name).get(gun);
							} catch (Exception e){
								e.printStackTrace();
							}
							Sound[] newArray = new Sound[oldArray.length + 1];
							System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
							try {
								gun.getClass().getField(name).set(gun, newArray);
							} catch (Exception e) {
								e.printStackTrace();
							}
							loadGunProperties(gun.name);
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


					Log.error("Gun Editor", "Unknown type : " + type);
				}
				properties.row();
			}
		});
		properties.setHeight(30 * count);
		Log.info("Gun Editor", "Created new window.");

		// Resize
		if(renamer != null)
			renamer.setPosition(Gdx.graphics.getWidth() - 240, properties.getHeight());
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
				GunDefinition gun = GunManager.find(properties.getTitleLabel().getText().toString());
				try {
					gun.getClass().getField(name).set(gun, new Sound[--count]);
				} catch (Exception e){
					e.printStackTrace();
				}
				myActor.removed = true;
				saveAll();
				properties.fadeOut(0);
				loadGunProperties(properties.getTitleLabel().getText().toString());
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
					Log.error("Gun Editor", "Failed to find sound data for asset " + sound);
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

	private static void saveGun(JLineWriter writer, String name){
		if(Day100.DEVELOPER_MODE){
			GunManager.find(name).save(writer);
			writer.endLine();			
		}
	}

	private static void saveAll(){

		// Save gun data
		JLineWriter writer = null;
		if(Day100.DEVELOPER_MODE){
			writer = new JLineWriter(new File(Gdx.files.getLocalStoragePath().replace("\\desktop", "/core/assets/Cache/Guns.txt")));
			Log.info("Gun Editor", "Saving all data to " + writer.getFile().getAbsolutePath());
			try {
				writer.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
		}


		for(GunDefinition gun : GunManager.guns){
			Log.info("Guns Editor", "Saving data for '" + gun.name + "' ...");
			// Apply gun data to gun that is being edited
			if(properties.getTitleLabel().getText().toString().equals(gun.name))
				apply(gun.name);
			// Save data
			if(Day100.DEVELOPER_MODE)
				saveGun(writer, gun.name);
		}
		if(writer != null)
			writer.dispose();

		try {
			if(Day100.DEVELOPER_MODE)
				FileUtils.copyFile(writer.getFile(), new File(Gdx.files.getLocalStoragePath() + "bin\\cache\\Guns.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void update(float delta){

		if(gun == null)
			return;

		if(oldSelected != null && !oldSelected.equals(gunsList.getSelected()))
			selectedChange(gunsList.getSelected());

		oldSelected = gunsList.getSelected();
	}

	public static void render(Batch batch){
		GunDefinition gun = GunManager.find(gunsList.getSelected());
		batch.draw(gun.texture, 0, 0, gun.texture.getRegionWidth() / Constants.PPM, gun.texture.getRegionHeight() / Constants.PPM); 
		
		
		for(ReflectionActor actor : reflection){
			VisValidatableTextField x = null;
			VisValidatableTextField y = null;
			try {
				if(gun.getClass().getField(actor.fieldName).isAnnotationPresent(VirtualCoordinate.class)){
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
				colorName = gun.getClass().getField(actor.fieldName).getAnnotation(VirtualCoordinate.class).value();
			} catch (Exception e){
				
			}
			
			try{					
				color = (Color) Color.class.getField(colorName).get(null);
			}catch(Exception e){
				
			}
			
			if(color == null)
				color = Color.BLACK;
			
			DevCross.drawCentred(gun.texture.getRegionWidth() / Constants.PPM * xPos, gun.texture.getRegionHeight() / Constants.PPM * yPos, 0.5f * Day100.camera.zoom, color);
		}
	}

	public static void renamer(){
		if(renamer != null){
			renamer.fadeOut();
			renamer = null;
		}else{
			renamer = VisUIHandler.newWindow("Renaming Helper");
			renamer.setSize(240, 170);
			renamer.setPosition(Gdx.graphics.getWidth() - 240, properties.getHeight());
			renamer.add(new VisLabel("Path : "), renamerPath = new VisTextArea("Path goes here"));
			renamer.row();
			renamer.add(new VisLabel("New Start : "), renamerStart = new VisTextArea("GunName"));
			renamer.row();

			renamer.add(new VisTextButton("Go!", new ChangeListener() {
				public void changed(ChangeEvent arg0, Actor arg1) {
					// Go!
					File root = new File(renamerPath.getText().trim());
					if(!root.exists()){
						MapEditor.exportFailures.clear();
						MapEditor.exportFailures.add("The path '" + renamerPath.getText().trim() + "' does not exist!");
						MapEditor.displayErrors();
						return;
					}

					File[] files = root.listFiles();
					if(files.length == 0){
						MapEditor.exportFailures.clear();
						MapEditor.exportFailures.add("The path contains no images!");
						MapEditor.displayErrors();
						return;
					}

					boolean done = false;
					for(File file : files){
						while(!done){
							for(int i = 0; i < files.length; i++){
								if(!file.getName().endsWith(".png")){
									done = true;
									continue;
								}
								if(file.getName().split(".png")[0].equals(i + "")){
									file.renameTo(new File(file.getAbsolutePath().replace(i + ".png", renamerStart.toString().trim() + i + ".png")));
									Log.info("Gun Editor", "Renaming " + file.getName());
									done = true;
								}
							}
							done = true;
						}
						done = false;
					}
				}
			}));
		}
	}

	public static void selectedChange(String selected){
		saveAll();
		disposePropertiesWindow();
		loadGunProperties(selected);
		GunDefinition gun = GunManager.find(selected);
		Day100.camera.position.set(gun.texture.getRegionWidth() / Constants.PPM / 2, gun.texture.getRegionHeight() / Constants.PPM / 2, 0);
	}

	public static void apply(String selected){
		// TODO
		GunDefinition g = GunManager.find(selected);
		for(ReflectionActor actor : reflection){
			try {
				actor.apply(g);
			} catch (Exception e) {
				// Ignore, this will happen a lot.
			}
		}
	}

	public static void close(){
		if(!open)
			return;

		saveAll();
		renamer();

		sounds.clear();
		properties.fadeOut();
		gun.fadeOut();
		open = false;
	}

	public static void resize(int width, int height){
		if(properties == null)
			return;

		properties.setPosition(width - properties.getWidth(), 0);
		if(renamer != null)
			renamer.setPosition(Gdx.graphics.getWidth() - 240, properties.getHeight());
	}

}
