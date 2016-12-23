package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

import co.uk.epicguru.IO.JLineWriter;
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

		loadGunProperties(GunManager.guns.get(0).name);

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
				properties.add(new VisLabel(name + " : "));
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
					Vector2 vector = (Vector2)value;
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
					// TODO
				}else{
					// UNKNOWN
					Log.error("Gun Editor", "Unknow type : " + type);
				}
				properties.row();
			}
		});
		properties.setHeight(30 * count);
		Log.info("Gun Editor", "Created new window.");
	}

	private static void disposePropertiesWindow(){
		properties.fadeOut(0);
		properties = null;
	}

	private static void saveGun(JLineWriter writer, String name){
		GunManager.find(name).save(writer);
		writer.endLine();
	}

	private static void saveAll(){
		
		// Save gun data
		JLineWriter writer = new JLineWriter(new File(Constants.DAY_100_FOLDER + "Gun Data\\Data TEMP.txt"));
		try {
			writer.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Log.info("Gun Editor", "Saving all data.");
		
		for(GunDefinition gun : GunManager.guns){
			Log.info("Guns Editor", "Saving data for '" + gun.name + "' ...");
			// Apply gun data to gun that is being edited
			if(properties.getTitleLabel().getText().toString().equals(gun.name))
				apply(gun.name);
			// Save data
			saveGun(writer, gun.name);
		}
		writer.dispose();
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
		VisValidatableTextField x = null;
		VisValidatableTextField y = null;
		for(ReflectionActor actor : reflection){
			if(actor.fieldName.equals("bulletSpawn")){
				x = (VisValidatableTextField) actor.actors[0];
				y = (VisValidatableTextField) actor.actors[1];
			}
		}
		if(x == null || y == null)
			return;
		
		float xPos = x.isInputValid() ? Float.parseFloat(x.getText().trim()) : 0;
		float yPos = y.isInputValid() ? Float.parseFloat(y.getText().trim()) : 0;
		
		DevCross.drawCentred(gun.texture.getRegionWidth() / Constants.PPM * xPos, gun.texture.getRegionHeight() / Constants.PPM * yPos, 0.5f * Day100.camera.zoom);
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

		properties.fadeOut();
		gun.fadeOut();
		open = false;
	}

	public static void resize(int width, int height){
		if(properties == null)
			return;

		properties.setPosition(width - properties.getWidth(), 0);

	}

}
