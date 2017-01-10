package co.uk.epicguru.screens.mapeditor;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.objects.MapObject;
import co.uk.epicguru.screens.Shaders;
import co.uk.epicguru.screens.instances.MapEditor;

public final class VisUIHandler {

	public static ColorPicker picker;
	public static VisTextField widthInput, heightInput;
	public static Stage stage;
	public static VisWindow propertiesWindow;
	public static Table propertiesTable;
	public static VisTextArea propertiesName;
	private static boolean started = false;
	public static boolean propertiesOpen;
	public static VisSlider propertiesXOffset;
	public static VisSlider propertiesYOffset;
	public static boolean colourOpen;
	public static VisSelectBox<String> suppliers;
	public static VisSelectBox<String> shaders;
	public static VisSelectBox<String> bodies;
	public static VisList<String> physicsFolders;
	public static VisWindow physicsList;
	public static VisWindow individualProperties;
	public static boolean physicsListOpen = false;
	public static boolean creatingNewPhysics = false;
	public static VisTable physicsOptions;
	public static boolean changingImage;
	public static VisWindow changeImageOption;
	public static VisList<String> changeImageList;
	public static VisCheckBox solid;
	public static VisCheckBox castShadows;
	public static VisCheckBox light;
	
	private VisUIHandler(){}
	
	public static void start(){
		
		if(started)
			return;
		
		VisUI.load();
		
		// RESET
		propertiesOpen = false;		
		colourOpen = false;
		
		// Colour
		picker = new ColorPicker("Choose a colour.", new ColorPickerListener() {
				
			@Override
			public void finished(Color color) {
				MapEditor.propertiesEdit.setupColour.set(color);
				colourOpen = false;
			}
			
			public void reset(Color arg0, Color arg1){}
			public void changed(Color color){
				MapEditor.propetiesEditColour.set(color);
			}
			public void canceled(Color arg0) {
				colourOpen = false;
			}
		});
		picker.fadeOut(0);
	
		stage = new Stage();
		stage.addActor(picker);
		
		propertiesWindow = new VisWindow("Properties for " + "ERROR");
		propertiesWindow.setResizable(false);
		Table table = new Table();
		
		propertiesWindow.add(table);
		
		propertiesWindow.setWidth(500);
		propertiesWindow.setHeight(300);
		propertiesWindow.setPosition(200, 20);
		
		// Setup
		table.row();
		
		table.add(new VisLabel("Name :"));
		table.add(propertiesName = new VisTextArea("DEFAULT_NAME"));
		table.row();
		
		table.add(new VisLabel("Placeable class : "));	
		table.add(suppliers = new VisSelectBox<String>());
		table.row();
		
		table.add(new VisLabel("Shader : "));
		table.add(shaders = new VisSelectBox<String>());
		table.row();
		
		table.add(new VisLabel("Physics Body : "));
		table.add(bodies = new VisSelectBox<String>());
		table.row();
		
		table.add(propertiesXOffset = new VisSlider(-1f, 1, 1f / 32f, false));
		table.add(propertiesYOffset = new VisSlider(-1f, 1, 1f / 32f, false));
		
		table.row();	
		
		propertiesXOffset.addListener(new EventListener() {
			public boolean handle(Event arg0) {
				return false;
			}
		});
		propertiesYOffset.addListener(new EventListener() {
			public boolean handle(Event arg0) {
				return false;
			}
		});
		propertiesXOffset.setValue(0);
		propertiesYOffset.setValue(0);
		table.row();
		
		table.add(new VisTextButton("Change Colour", new ChangeListener() {
			public void changed(ChangeEvent arg0, Actor arg1) {
				openColorSelector();
			}
		}));
		table.row();
		table.add(new VisTextButton("Close & Save", new ChangeListener() {
			public void changed(ChangeEvent arg0, Actor arg1) {
				closeProperties();
			}
		}));
		propertiesWindow.fadeOut(0);
		
		//propertiesWindow.fadeOut(0);
		
		stage.addActor(propertiesWindow);
		
		// Physics folder select.
		physicsList = new VisWindow("Loaded physics data");
		physicsList.add(physicsFolders = new VisList<String>());
		physicsList.row();
		physicsList.add(new VisTextButton("New...", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				Log.info("VISUI", "New...");
				creatingNewPhysics = true;
				VisWindow window = new VisWindow("Create new physics data");
				window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				window.setWidth(200);
				window.setHeight(140);
				VisTextArea name = new VisTextArea("Name");
				window.add(new VisLabel("Name of new physics data"));
				window.row();
				window.add(name);
				window.row();
				window.add(new VisTextButton("Confirm", new ChangeListener(){
					public void changed(ChangeEvent arg0, Actor arg1) {
						creatingNewPhysics = false;
						Log.info("VISUI", "Created new physics data called " + name.getText().trim());
						window.setVisible(false);
						newPhysics(name.getText().trim());
						reloadPhysicsList();
					}					
				}));
				stage.addActor(window);
			}			
		}));
		stage.addActor(physicsList);
		physicsList.fadeOut(0);
		
		started = true;
	
	}
	
	public static void reloadPhysicsList(){
		
		PhysicsData.refreshData(Constants.MAP_EDITOR_FOLDER + MapEditor.selectedMap, true);
		
		String[] names = new String[PhysicsData.loaded.size()];
		int index = 0;
		for(PhysicsData data : PhysicsData.loaded){
			names[index++] = data.file.getName();
			Log.info("VISUI", "Got " + names[--index]);
			index++;
		}
		
		physicsFolders.setItems(names);
	}
	
	public static void newPhysics(String name){
		
		if(new File(Constants.MAP_EDITOR_FOLDER + MapEditor.selectedMap + "\\Physics\\" + name + "\\").exists()){
			return;
		}
		
		new File(Constants.MAP_EDITOR_FOLDER + MapEditor.selectedMap + "\\Physics\\" + name + "\\").mkdirs();
		try {
			new File(Constants.MAP_EDITOR_FOLDER + MapEditor.selectedMap + "\\Physics\\" + name + "\\Properties.txt").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void openProperties(){
		if(propertiesOpen || MapEditor.propertiesEdit == null)
			return;
		
		// Set values
		propertiesWindow.getTitleLabel().setText("Properties for " + MapEditor.propertiesEdit.name);
		propertiesName.setText(MapEditor.propertiesEdit.name);
		propertiesXOffset.setValue(MapEditor.propertiesEdit.setupSnapOffset.x);
		propertiesYOffset.setValue(MapEditor.propertiesEdit.setupSnapOffset.y);
		
		int index = 0;
		suppliers.clearItems();
		String[] items = new String[MapObject.objects.size()];
		for(String supplier : MapObject.objects.keySet()){
			items[index++] = supplier;
		}
		suppliers.setItems(items);
		suppliers.setSelected(MapEditor.propertiesEdit.supplier);
		
		index = 0;
		shaders.clearItems();
		items = new String[Shaders.compliedShaders.size()];
		for(String name : Shaders.compliedShadersNames){
			if(name.equals("Outline")) // Do not add outline
				continue;
			items[++index] = name;
		}
		items[0] = "Default";
		shaders.setItems(items);
		shaders.setSelected(MapEditor.propertiesEdit.shader);
		
		index = 1;
		bodies.clearItems();
		items = new String[PhysicsData.loaded.size() + 1];
		for(PhysicsData data : PhysicsData.loaded){
			items[index++] = data.file.getName();
		}
		items[0] = "None";
		bodies.setItems(items);
		bodies.setSelected(MapEditor.propertiesEdit.body);
		
		stage.addActor(propertiesWindow);
		propertiesWindow.fadeIn();
		
		propertiesOpen = true;
		Log.info("VISUI", "Opening properties for " + MapEditor.propertiesEdit.name);
	}
	
	public static void closeProperties(){
		if(!propertiesOpen)
			return;
		propertiesWindow.fadeOut();
		propertiesOpen = false;
		
		// Save values
		MapEditor.propertiesEdit.name = propertiesName.getText().trim();
		MapEditor.propertiesEdit.setupSnapOffset.set(propertiesXOffset.getValue(), propertiesYOffset.getValue());
		MapEditor.propertiesEdit.supplier = suppliers.getSelected();
		MapEditor.propertiesEdit.shader = shaders.getSelected();
		MapEditor.propertiesEdit.body = bodies.getSelected();
		closeColorSelector();
	}
	
	public static void stop(){
		if(!started)
			return;
		VisUI.dispose();
		started = false;
	}
	
	public static void openColorSelector(){
		//if(colourOpen)
			//return;
		stage.addActor(picker);
		picker.fadeIn();
		picker.fadeIn();
		picker.closeOnEscape();
		colourOpen = true;
		picker.setColor(MapEditor.propertiesEdit.setupColour);
		MapEditor.propetiesEditColour.set(MapEditor.propertiesEdit.setupColour);
		Log.info("TEST", "OPEN");
	}
	
	public static void closeColorSelector(){
		picker.fadeOut();
	}
	
	public static VisWindow newWindow(String title){
		VisWindow window = new VisWindow(title);
		stage.addActor(window);
		window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		return window;
	}
	
	public static void resized(int width, int height){
		start();
		stage.getViewport().setCamera(Day100.UIcamera);
		stage.getViewport().setScreenSize(width, height);
		stage.getViewport().setWorldSize(width, height);
		if(physicsOptions != null)
			physicsOptions.setPosition(width - 100, height - 200);
	}
	
	public static void renderUI(Batch batch){
		
		// Don't know why. Only quick solution. Creates a flicker effect when opening, but better than not opening at all...
		if(colourOpen && !stage.getActors().contains(picker, false))
			stage.addActor(picker);
		
		batch.end();
		stage.act();
		stage.draw();
		batch.begin();
	}
	
	public static void openPhysicsList(){
		if(physicsListOpen)
			return;
		
		if(!stage.getActors().contains(physicsList, true))
			stage.addActor(physicsList);
		
		reloadPhysicsList();
		
		stage.getActors().removeValue(physicsOptions, true);
		
		physicsOptions = new VisTable();
		int width = 100;
		int height = 200;
		physicsOptions.setPosition(Gdx.graphics.getWidth() - width, Gdx.graphics.getHeight() - height);
		physicsOptions.setWidth(width);
		physicsOptions.setHeight(height);
		
		VisLabel label = new VisLabel("Options");
		label.setColor(Color.BLACK);
		physicsOptions.add(label);
		physicsOptions.row();
		VisTextButton setImage = new VisTextButton("Set Image", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				if(changingImage)
					return;
				VisWindow newWindow = new VisWindow("Change Image");
				newWindow.setSize(200, 300);
				newWindow.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 50);
				VisList<String> images = new VisList<String>();
				changeImageList = images;
				VisScrollPane scrollPane = new VisScrollPane(images);
				newWindow.add(scrollPane);
				newWindow.row();
				String[] items = new String[MapEditor.loadedObjects.size()];
				for(int i = 0 ; i < MapEditor.loadedObjects.size(); i++){
					items[i] = MapEditor.loadedObjects.get(i).ID;
				}
				images.setItems(items);
				newWindow.add(new VisTextButton("Confirm", new ChangeListener(){
					public void changed(ChangeEvent arg0, Actor arg1) {
						changingImage = false;
						// Set new image
						Log.info("VISUI", "Setting image to " + changeImageList.getSelected());
						PhysicsData data = PhysicsData.find(physicsFolders.getSelected());
						if(data != null){
							data.image = changeImageList.getSelected();
							data.setTextureFromImage();
						}
						// Close
						changeImageOption.fadeOut();
					}					
				}));
				stage.addActor(newWindow);
				changeImageOption = newWindow;
				changingImage = true;
			}			
		});
		VisTextButton clear = new VisTextButton("Clear Points", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				if(changingImage)
					return;
				PhysicsData data = PhysicsData.find(physicsFolders.getSelected());
				data.points = new Vector2[0];
				data.editing.clear();
			}			
		});
		physicsOptions.add(setImage);
		physicsOptions.row();
		physicsOptions.add(clear);
		stage.addActor(physicsOptions);
				
		physicsList.setResizable(true);
		
		physicsList.fadeIn();
		physicsListOpen = true;
	}
	
	public static void closePhysicsList(){
		if(!physicsListOpen)
			return;
		
		physicsList.fadeOut();
		stage.getActors().removeValue(physicsOptions, true);
		
		physicsListOpen = false;
		
	}
	
	public static void openIndividualProperties(MapEditorPlaceable object){
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		solid = new VisCheckBox("Solid", object.solid);
		solid.setPosition(width - 200, height - 50);
		stage.addActor(solid);
		
		light = new VisCheckBox("Sun Light", object.light);
		light.setPosition(width - 200, height - 70);
		stage.addActor(light);
	}
	
	public static void closeIndividualProperties(){
		if(solid == null)
			return;
		
		solid.remove();
		solid = null;
		light.remove();
		light = null;
	}
	
	public static void updateIndividualProperties(MapEditorPlaceable object){
		if(solid == null)
			return;
		
		object.solid = solid.isChecked();
		object.light = light.isChecked();
	}
	
	public static void openErrorDialog(final String message){
		VisDialog dialog = new VisDialog("An Error Ocurred");
		dialog.text(message);
		dialog.pack();
		dialog.setHeight(dialog.getHeight() + 30);
		dialog.setWidth(dialog.getWidth() + 50);
		dialog.closeOnEscape();
		dialog.add(new VisTextButton("Close", new ChangeListener(){
			public void changed(ChangeEvent arg0, Actor arg1) {
				dialog.fadeOut();
			}			
		}));
		dialog.centerWindow();
		stage.addActor(dialog);
		Log.info("VISUI", "Displaying");
	}
	
}
