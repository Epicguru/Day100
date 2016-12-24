package co.uk.epicguru.screens.instances;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.Map;
import co.uk.epicguru.map.objects.MapObject;
import co.uk.epicguru.player.PlayerController;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.screens.GameScreen;
import co.uk.epicguru.screens.Screen;
import co.uk.epicguru.screens.ScreenManager;
import co.uk.epicguru.screens.Shaders;
import co.uk.epicguru.screens.mapeditor.Exporter;
import co.uk.epicguru.screens.mapeditor.GunEditor;
import co.uk.epicguru.screens.mapeditor.MapEditorInput;
import co.uk.epicguru.screens.mapeditor.MapEditorObject;
import co.uk.epicguru.screens.mapeditor.MapEditorPlaceable;
import co.uk.epicguru.screens.mapeditor.PhysicsData;
import co.uk.epicguru.screens.mapeditor.VisUIHandler;


public final class MapEditor extends GameScreen {

	public static File saveFolder = new File(Constants.MAP_EDITOR_FOLDER);
	public static Vector2 size = new Vector2(300, 35);
	public static String selectedMap;
	public static final String TAG = "Map Editor";
	public static State state = State.SELECT_MAP;
	public static int selectedMapIndex = 0;
	public static int maps = 0;
	public static ArrayList<MapEditorObject> loadedObjects = new ArrayList<MapEditorObject>();
	public static ArrayList<MapEditorPlaceable> placedObjects = new ArrayList<MapEditorPlaceable>();
	private static MapEditorInput input;
	public static float objectsRenderX = 160;
	private static float objectsUIOffset = 0;
	public static float objectsUIOffsetTarget = 0;
	public static MapEditorObject placing;
	public static ShapeRenderer shapes = new ShapeRenderer();
	public static MapEditorPlaceable selected;
	public static ShaderProgram outlineShader = Shaders.getShader("Outline");
	public static float timer = 0;
	public static String exportStatus = "UNKNOWN";

	public static TextureRegion exit, refresh, run, physics, objectBox, cover, xAxis, yAxis, ground, gunsButton;
	public static TextureRegion newButton;
	public static NinePatch boxInner, boxOuter;
	public static Vector2 ghostRenderPos = new Vector2();
	public static MapEditorObject propertiesEdit;
	public static Color propetiesEditColour = new Color(1, 1, 1, 1);
	public static ArrayList<MapEditorPlaceable> stack = new ArrayList<MapEditorPlaceable>();
	public static int stackIndex = 0;
	public static Vector2 dragStartPos = new Vector2();
	public static Vector2 dragObjectStartPos = new Vector2();
	public static boolean draggingX;
	public static boolean draggingY;
	public static RayHandler lighting;
	public static World world;
	public static DirectionalLight sun;
	public static Box2DDebugRenderer debugRenderer;
	public static boolean renderDebug = false;
	public static boolean doneExporting = false;
	public static boolean largeSelection = false;
	public static Vector2 largeSelectionStart = new Vector2();
	public static ArrayList<MapEditorPlaceable> largeSelectionObjects = new ArrayList<MapEditorPlaceable>();
	public static Vector3 returnToFromPhysics = new Vector3();
	public static ArrayList<String> exportFailures = new ArrayList<String>();
	public static Music[] music;
	public static int musicIndex;
	public static float musicVolume = 0.5f;

	public static Map runMap = null;

	// Config and state
	public static float gridFadeDistance = 4;

	public MapEditor() {
		super("Map Editor", Screen.MAP_EDITOR);

		// Make fir if it does not exist.
		createDir();
		debugRenderer = new Box2DDebugRenderer();
		Shaders.getAllShaders();

		Log.info(TAG, "There are " + getMaps().length + " valid maps.");
	}

	public void enter(Screen oldScreen){
		if(ScreenManager.getScreen() != Screen.MAP_EDITOR){
			VisUIHandler.stop();
			selected = null;
			propertiesEdit = null;
			stack.clear();
			Day100.map = null;
			Day100.player = null;
			if(music != null)
				music[musicIndex % music.length].stop();
			return;
		}
		objectsRenderX = 160;
		Day100.camera.zoom = 1;
		state = State.SELECT_MAP;
		objectsUIOffsetTarget = 0;
		loadedObjects.clear();
		placedObjects.clear();
		placing = null;
		selected = null;
		propertiesEdit = null;
		propetiesEditColour = new Color();
		stackIndex = 0;
		stack.clear();
		draggingX = false;
		draggingY = false;
		largeSelection = false;

		// Music - For the chills :D
		music = new Music[]{
				Day100.assets.get(Constants.MUSIC + "Chill.mp3"),
				Day100.assets.get(Constants.MUSIC + "Sunrise Z.mp3"),
				Day100.assets.get(Constants.MUSIC + "The Day After.mp3")
		};
	}

	public void init(){
		refresh = Day100.UIAtlas.findRegion("Refresh");
		exit = Day100.UIAtlas.findRegion("Exit");
		run = Day100.UIAtlas.findRegion("Run");
		objectBox = Day100.UIAtlas.findRegion("ObjectBox");
		cover = Day100.UIAtlas.findRegion("Cover");
		xAxis = Day100.UIAtlas.findRegion("Right");
		yAxis = Day100.UIAtlas.findRegion("Up");
		ground = Day100.UIAtlas.findRegion("Ground");
		physics = Day100.UIAtlas.findRegion("Physics");
		newButton = Day100.UIAtlas.findRegion("New");
		gunsButton = Day100.UIAtlas.findRegion("Gun");

		boxInner = Day100.UIAtlas.createPatch("InnerBox");
		boxOuter = Day100.UIAtlas.createPatch("Box");

		input = new MapEditorInput();
		Gdx.input.setInputProcessor(input);
	}
	
	public void updateMusic(){
		music[musicIndex % music.length].setVolume(musicVolume);
		if(!music[musicIndex % music.length].isPlaying()){
			music[++musicIndex % music.length].play();
		}
	}

	public void createDir(){
		if(saveFolder.exists())
			return;
		saveFolder.mkdirs();
	}

	public void saveSetup(){
		for(MapEditorObject object : loadedObjects){
			object.saveSetup();
		}		
	}

	public static boolean hasID(final String ID){
		for(MapEditorObject object : loadedObjects){
			if(object.ID.equals(ID))
				return true;
		}
		return false;
	}

	public void saveMap(){

		if(selectedMap == null || loadedObjects.size() == 0 || placedObjects.size() == 0)
			return;

		JLineWriter writer = new JLineWriter(new File(Constants.MAP_EDITOR_FOLDER + selectedMap + "\\Map\\Placeables.txt"));

		try {
			writer.open();
		} catch (FileNotFoundException e) {
			return;
		}

		int amount = 0;
		for(MapEditorPlaceable placed : placedObjects){
			placed.save(writer);
			writer.endLine();
			amount++;
		}
		Log.info(TAG, "Saved " + amount + " placed MAP EDITOR objects to the map folder.");

		amount = 0;
		for(MapEditorObject object : loadedObjects){
			object.saveSetup();
			amount++;
		}
		Log.info(TAG, "Saved " + amount + " static objects from the raw folder to the setup files in the same folder.");

		writer.dispose();
	}

	public void loadMap(){
		if(selectedMap == null)
			return;

		JLineReader reader = new JLineReader(new File(Constants.MAP_EDITOR_FOLDER + selectedMap + "\\Map\\Placeables.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		do{
			if(!reader.isLineEmpty()){
				MapEditorPlaceable placeable = new MapEditorPlaceable(reader);
				placedObjects.add(placeable);
			}
		}while(!reader.nextLine());	
		Log.info(TAG, "Loaded placed objects, there were " + placedObjects.size());

	}

	public void saveState(){
		JLineWriter writer = new JLineWriter(new File(Constants.MAP_EDITOR_FOLDER + selectedMap + "\\Properties.txt"));
		try {
			writer.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		writer.write("Size", size);
		writer.endLine();
		writer.write("Camera Position", new Vector2(Day100.camera.position.x, Day100.camera.position.y));
		writer.write("Camera Zoom", Day100.camera.zoom);
		writer.endLine();

		writer.write("Grid Fade Distance", gridFadeDistance);
		writer.endLine();

		writer.dispose();

		MapObject.objects.clear();

	}

	public void loadState(){

		JLineReader reader = new JLineReader(new File(Constants.MAP_EDITOR_FOLDER + selectedMap + "\\Properties.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		size = reader.readVector2("Size", new Vector2(300,  25));
		reader.nextLine();
		Day100.camera.position.set(reader.readVector2("Camera Position", Vector2.Zero), 0);
		input.zoom = reader.readFloat("Camera Zoom", 1);

		renderDebug = false;
		largeSelection = false;

		MapObject.register();

	}

	public File[] getMaps(){
		File[] files = saveFolder.listFiles();
		File[] returnValue = new File[files.length];
		int index = 0;
		for(File file : files){
			if(!file.isDirectory())
				continue;
			for(File file2 : file.listFiles()){
				if(file2.getName().equals("Properties.txt")){
					returnValue[index++] = file;
					break;
				}
			}
		}
		File[] finalArray = new File[index];
		System.arraycopy(returnValue, 0, finalArray, 0, index);

		return finalArray;
	}

	@Override
	public void update(float delta) {

		updateMusic();
		
		switch(state){
		case SELECT_MAP:

			if(Input.isKeyJustDown(Keys.UP)){
				if(selectedMapIndex > 0)
					selectedMapIndex--;		
			}
			if(Input.isKeyJustDown(Keys.DOWN)){
				if(selectedMapIndex < maps)
					selectedMapIndex++;
			}
			if(Input.isKeyJustDown(Keys.ENTER)){
				selectedMap = getMaps()[selectedMapIndex].getName();
				state = State.CREATION;

				// Load
				load();

				Log.info(TAG, "The map '" + selectedMap + "' has been selected to edit.");
			}

			break;
		case GUNS:

			// Camera and mouse movement
			if(Day100.camera.zoom != input.zoom)
				Day100.camera.zoom += (input.zoom - Day100.camera.zoom) * 0.2f;
			if(Gdx.input.isButtonPressed(Buttons.FORWARD)){
				input.zoom = 1;
			}
			if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
				Day100.camera.position.add(-Gdx.input.getDeltaX() / Constants.PPM * Day100.camera.zoom, Gdx.input.getDeltaY() / Constants.PPM * Day100.camera.zoom, 0);
			}

			GunEditor.update(delta);

			break;
		case CREATION:

			timer += delta;

			//Setp world.
			world.step(1f / 60f, 8, 3);

			if(Day100.camera.zoom != input.zoom)
				Day100.camera.zoom += (input.zoom - Day100.camera.zoom) * 0.2f;

			// Move with mouse
			if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
				Day100.camera.position.add(-Gdx.input.getDeltaX() / Constants.PPM * Day100.camera.zoom, Gdx.input.getDeltaY() / Constants.PPM * Day100.camera.zoom, 0);
			}	

			if(objectsUIOffset != objectsUIOffsetTarget){
				objectsUIOffset += (objectsUIOffsetTarget - objectsUIOffset) * 0.3f;
			}

			if(Gdx.input.isButtonPressed(Buttons.FORWARD)){
				input.zoom = 1;
			}

			// Go to top
			if(selected != null){
				if(Input.isKeyJustDown(Keys.RIGHT)){
					placedObjects.remove(selected);
					placedObjects.add(selected);
				}else if(Input.isKeyJustDown(Keys.LEFT)){
					placedObjects.remove(selected);
					placedObjects.add(0, selected);
				}
			}

			if(placing != null){

				// Drag off screen
				Vector2 pos = Input.getMousePosYFlip();
				float speed = Constants.PPM / 50;
				if(pos.x < 32){
					Day100.camera.position.x -= speed;
				}else if(pos.x > Gdx.graphics.getWidth() - 32){
					Day100.camera.position.x += speed;
				}
				if(pos.y < 32){
					Day100.camera.position.y -= speed;
				}else if(pos.y > Gdx.graphics.getHeight() - 32){
					Day100.camera.position.y += speed;
				}

				if(!Input.clickingLeft()){

					if(Input.getMouseX() < objectsRenderX){
						placing = null;
						return;
					}

					// Drop and place the object
					MapEditorPlaceable placeable = new MapEditorPlaceable(placing, new Vector2(snap().add(placing.setupSnapOffset)));
					placeable.createBody();
					placedObjects.add(placeable);
					placing = null;
				}
			}

			if(selected != null && VisUIHandler.propertiesOpen)
				selected = null;

			if(Input.clickLeft() && Input.getMouseX() > objectsRenderX && !VisUIHandler.propertiesOpen){			

				stack.clear();
				boolean found = false;
				for(MapEditorPlaceable object : placedObjects){
					rect.set(object.position.x, object.position.y, object.size.x, object.size.y);
					if(rect.contains(Input.getMouseWorldPos())){
						found = true;	
						stack.add(object);
					}
				}
				// Clicked, but not on a object
				if(!found || (selected != null && (inXAxis() || inYAxis()))){
					if(selected != null){
						// If selected and not clicking on arrow, no longer selecting						
						if(!inXAxis()){
							// Clicked somewhere else, no longer selecting
							if(!inYAxis()){
								draggingX = false;
								draggingY = false;
								selected = null;
							}
						}else{
							// Clicked on X arrow
							dragStartPos.set(Input.getMouseWorldPos());
							dragObjectStartPos.set(selected.position);
							draggingX = true;
						}

						// If selected and not clicking on arrow, no longer selecting
						if(selected != null){							
							if(!inYAxis()){
								// Clicked somewhere else, no longer selecting
								if(!inXAxis()){
									draggingX = false;
									draggingY = false;									
									selected = null;
								}
							}else{
								// Clicked on Y arrow
								dragStartPos.set(Input.getMouseWorldPos());
								dragObjectStartPos.set(selected.position);
								draggingY = true;
							}
						}						
					}else{
						// Clicked somewhere else
						draggingX = false;
						draggingY = false;
						selected = null;
					}										
				}else{
					if(stackIndex >= stack.size())
						stackIndex = 0;
					if(selected != null){
						if(!inXAxis() && !inYAxis()){
							selected = stack.get(stackIndex);
							stackIndex++;						
						}
					}else{
						selected = stack.get(stackIndex);
						stackIndex++;
					}
				}
			}			
			// DELETE SELECTED
			if(selected != null && Input.isKeyJustDown(Keys.DEL) && !largeSelection){
				placedObjects.remove(selected);
				selected.destroyBody();
				selected = null;
				draggingX = false;
				draggingY = false;
			}

			// Start large selection
			if(Input.clickRight() && Input.getMouseX() > objectsRenderX){
				largeSelection = true;
				largeSelectionObjects.clear();
				largeSelectionStart.set(Input.getMouseWorldPos());
			}

			// Cancel large selection by stopping clicking
			if(!Input.clickingRight() && largeSelection){				
				largeSelection = false;
			}			

			// Cancel large selection by left clicking, if not right clicking (largeSelection variable)
			if(Input.clickLeft() && !largeSelection){
				// Does not matter where
				largeSelection = false;
				largeSelectionObjects.clear();
			}

			// Large selection selecting
			Rectangle newRect = new Rectangle();
			if(largeSelection && Input.clickingRight()){
				rect.set(largeSelectionStart.x, largeSelectionStart.y, (Input.getMouseWorldX() - largeSelectionStart.x) < 0.5f ? 0.5f : (Input.getMouseWorldX() - largeSelectionStart.x), (Input.getMouseWorldY() - largeSelectionStart.y) < 0.5f ? 0.5f : (Input.getMouseWorldY() - largeSelectionStart.y));
				largeSelectionObjects.clear();
				for(MapEditorPlaceable object : placedObjects){
					newRect.set(object.position.x, object.position.y, object.size.x, object.size.y);
					if(rect.overlaps(newRect)){
						if(!largeSelectionObjects.contains(object))
							largeSelectionObjects.add(object);
					}
				}

			}

			// Delete large selection
			if(largeSelectionObjects.size() > 1 && Input.isKeyDown(Keys.DEL)){
				for(MapEditorPlaceable object : largeSelectionObjects){
					placedObjects.remove(object);
					object.destroyBody();
				}
				largeSelectionObjects.clear();
			}

			// MOVE SELECTED
			if(selected != null && draggingX){
				if(!Input.clickingLeft()){
					draggingX = false;					
				}else{
					selected.position.x = Input.getMouseWorldX() - dragStartPos.x + dragObjectStartPos.x;					
				}
			}
			if(selected != null && draggingY){
				if(!Input.clickingLeft()){
					draggingY = false;					
				}else{
					selected.position.y = Input.getMouseWorldY() - dragStartPos.y + dragObjectStartPos.y;					
				}
			}

			// Drag when moving placed object
			float speed = Constants.PPM / 50;
			if(draggingX && Input.getMouseX() > Gdx.graphics.getWidth() - 32){
				Day100.camera.position.x += speed;
			}
			if(draggingX && Input.getMouseX() < 32){
				Day100.camera.position.x -= speed;
			}
			if(draggingY && Input.getMouseY() > Gdx.graphics.getHeight() - 32){
				Day100.camera.position.y -= speed;
			}
			if(draggingY && Input.getMouseY() < 32){
				Day100.camera.position.y += speed;
			}

			if(selected != null && Input.isKeyJustDown(Keys.UP)){
				// Go up one
				int index = placedObjects.indexOf(selected);
				if(index < placedObjects.size() - 1){
					placedObjects.remove(selected);
					placedObjects.add(++index, selected);
				}
			}
			if(selected != null && Input.isKeyJustDown(Keys.DOWN)){
				// Go down one
				int index = placedObjects.indexOf(selected);
				if(index > 0){
					placedObjects.remove(selected);
					placedObjects.add(--index, selected);
				}
			}

			if(Input.isKeyJustDown(Keys.X))
				renderDebug = !renderDebug;

			break;
		case RUNNING:

			if(Day100.camera.zoom != input.zoom)
				Day100.camera.zoom += (input.zoom - Day100.camera.zoom) * 0.2f;

			// Move with mouse
			if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
				Day100.camera.position.add(-Gdx.input.getDeltaX() / Constants.PPM * Day100.camera.zoom, Gdx.input.getDeltaY() / Constants.PPM * Day100.camera.zoom, 0);
			}	

			if(Gdx.input.isButtonPressed(Buttons.FORWARD)){
				input.zoom = 1;
			}
			runMap.update(delta);
			if(!Day100.player.isDead())
				Day100.camera.position.set(Day100.player.body.getPosition().x, Day100.player.body.getPosition().y, 0);

			break;
		case EXPORTING:

			if(doneExporting){
				state = State.RUNNING;
			}

			break;
		case PHYSICS:
			if(Day100.camera.zoom != input.zoom)
				Day100.camera.zoom += (input.zoom - Day100.camera.zoom) * 0.2f;

			// Move with mouse
			if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
				Day100.camera.position.add(-Gdx.input.getDeltaX() / Constants.PPM * Day100.camera.zoom, Gdx.input.getDeltaY() / Constants.PPM * Day100.camera.zoom, 0);
			}

			if(Input.clickRight()){
				PhysicsData data = PhysicsData.find(VisUIHandler.physicsFolders.getSelected());
				if(data != null){
					data.addPoint(Input.getMouseWorldPos());
				}					
			}
			break;
		default:
			break;
		}
	}

	public static boolean inXAxis(){
		rect.set(selected.position.x + selected.size.x + .32f, selected.position.y + selected.size.y / 2 - .5f, 2, 1);
		return rect.contains(Input.getMouseWorldPos());
	}

	public static boolean inYAxis(){
		rect.set(selected.position.x + selected.size.x / 2 - .5f, selected.position.y + selected.size.y + .32f, 1, 2);
		return rect.contains(Input.getMouseWorldPos());
	}

	@Override
	public void render(Batch batch) {

		if(state == State.RUNNING){
			runMap.render(batch);
			runMap.renderLighting(batch);
		}
		if(state == State.PHYSICS){

			batch.end();
			shapes.begin(ShapeType.Line);
			float c = Day100.camera.zoom / gridFadeDistance;
			if(c > 0.9f)
				c = 0.9f;
			int w = 20;
			int h = 20;
			shapes.setColor(c, c, c, 1);
			shapes.setProjectionMatrix(Day100.camera.combined);
			for(int x = 0; x < w + 1; x++){
				shapes.line(x, 0, x, h);
			}
			for(int y = 0; y < h + 1; y++){
				shapes.line(0, y, w, y);
			}	

			// Now the points that are editing.
			shapes.setColor(Color.RED);

			PhysicsData data = PhysicsData.find(VisUIHandler.physicsFolders.getSelected());
			if(data != null && data.points.length > 0){
				for(int i = 0; i < data.points.length; i++){
					shapes.rectLine(data.points[i], i == data.points.length - 1 ? data.points[0] : data.points[i + 1], 1f / Constants.PPM);
				}
			}			

			shapes.end();
			batch.begin();

			if(data != null && data.textureInEditor != null){
				batch.setColor(1, 1, 1, 0.5f);
				batch.draw(data.textureInEditor, 0, 0, data.textureInEditor.getRegionWidth() / Constants.PPM, data.textureInEditor.getRegionHeight() / Constants.PPM);
				batch.setColor(1, 1, 1, 1);
			}
		}

		if(state == State.GUNS){
			GunEditor.render(batch);
		}

		if(state != State.CREATION)
			return;

		// Lines
		batch.end();
		shapes.begin(ShapeType.Line);
		float c = Day100.camera.zoom / gridFadeDistance;
		if(c > 0.9f)
			c = 0.9f;
		int w = (int) size.x;
		int h = (int) size.y;
		shapes.setColor(c, c, c, 1);
		shapes.setProjectionMatrix(Day100.camera.combined);
		for(int x = 0; x < w + 1; x++){
			shapes.line(x, 0, x, h);
		}
		for(int y = 0; y < h + 1; y++){
			shapes.line(0, y, w, y);
		}	
		shapes.end();
		batch.begin();

		for(MapEditorPlaceable object : placedObjects){

			// Render outline if selected
			if(selected == object || largeSelectionObjects.contains(object)){
				batch.setShader(outlineShader);
				outlineShader.setUniformf("u_time", timer);
			}else if(batch.getShader() != null){
				batch.setShader(null);
			}
			object.render(batch);				
		}
		if(batch.getShader() == outlineShader)
			batch.setShader(null);

		if(placing != null){

			Vector2 pos = snap().add(placing.setupSnapOffset);
			ghostRenderPos.add((pos.x - ghostRenderPos.x) * 0.3f, (pos.y - ghostRenderPos.y) * 0.3f);

			batch.setColor(1, 1, 1, 0.5f);
			float multi = 1;
			batch.draw(placing.texture, ghostRenderPos.x, ghostRenderPos.y, placing.texture.getRegionWidth() / Constants.PPM * multi, placing.texture.getRegionHeight() / Constants.PPM * multi);
			batch.setColor(1, 1, 1, 1);
		}

		if(selected != null){
			batch.draw(xAxis, selected.position.x + selected.size.x + .32f, selected.position.y + selected.size.y / 2 - .5f, 2, 1);
			batch.draw(yAxis, selected.position.x + selected.size.x / 2 - .5f, selected.position.y + selected.size.y + .32f, 1, 2);
		}

		// Ground
		renderGround(batch);

		batch.end();
		// Lighting
		if(!renderDebug){
			lighting.setCombinedMatrix(Day100.camera);
			lighting.updateAndRender();			
		}else{
			debugRenderer.render(world, Day100.camera.combined);
		}

		batch.begin();

	}

	public void renderGround(Batch batch){
		for(int x = 0; x < size.x; x++){
			batch.draw(ground, x, -1, 1.01f, 1.01f);
		}
	}

	public File[] getObjectFiles(final String ending){


		File folder = new File(saveFolder + "\\" + selectedMap + "\\Raw Objects\\");
		if(!folder.exists()){
			folder.mkdirs();
			Log.info(TAG, "The folder does not exist!");
			return new File[0];
		}

		if(!folder.isDirectory()){
			Log.info(TAG, "The folder is not a directory!");
			return new File[0];
		}

		ArrayList<File> files = new ArrayList<File>();

		for(File file : folder.listFiles()){
			if(file.getAbsolutePath().endsWith(ending)){
				files.add(file);
			}
		}

		return (File[])files.toArray(new File[files.size()]);
	}

	public void refreshObjects(){

		saveSetup();

		GunManager.reset();

		loadedObjects.clear();
		exportFailures.clear();

		File[] images = getObjectFiles(".png");

		for(File file : images){
			MapEditorObject object = new MapEditorObject(file);
			loadedObjects.add(object);
		}

		// Give placed objects new parents
		for(MapEditorPlaceable placedObject : placedObjects){
			for(MapEditorObject object : loadedObjects){
				if(placedObject.parent.ID.equals(object.ID)){
					placedObject.setParent(object);
				}
			}
		}

		// Recreate all bodies
		createAllBodies();

		Log.info(TAG, "Found " + loadedObjects.size() + " objects for the map " + selectedMap + ".");
	}

	private Vector2 temp = new Vector2();
	public Vector2 snap(){
		temp.set(Input.getMouseWorldPos());
		temp.x = (int)temp.x;
		temp.y = (int)temp.y;
		return temp;
	}

	private static Rectangle rect = new Rectangle();
	public void renderUI(Batch batch){	

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		switch(state){
		case SELECT_MAP:
			smallFont.setColor(Color.RED);
			smallFont.draw(batch, "Select a map to edit. Use UP & DOWN arrow keys to select and ENTER to confirm.", 10, height - 20);

			int index = 0;
			maps = 0;
			for(File file : getMaps()){
				smallFont.setColor(index == selectedMapIndex ? Color.GREEN : Color.BLUE);
				smallFont.draw(batch, file.getName(), 10, height - 50 - (index * 30));
				index++;
				maps++;
			}

			break;
		case CREATION:

			objectsRenderX = 160;

			// Large selection
			if(largeSelection){
				Vector3 position = Day100.camera.project(new Vector3(largeSelectionStart.x, largeSelectionStart.y, 0));
				float swidth = (Input.getMouseWorldX() - largeSelectionStart.x) < 0.5f ? 0.5f : (Input.getMouseWorldX() - largeSelectionStart.x);
				float sheight = (Input.getMouseWorldY() - largeSelectionStart.y) < 0.5f ? 0.5f : (Input.getMouseWorldY() - largeSelectionStart.y);
				swidth /= Day100.camera.zoom;
				sheight /= Day100.camera.zoom;
				boxInner.setColor(new Color(1, 1, 1, 0.5f));
				boxInner.draw(batch, position.x, position.y, swidth * Constants.PPM, sheight * Constants.PPM);
			}

			// Box
			boxOuter.draw(batch, objectsRenderX - 160, 0, 160, height);				

			// Objects
			float size = 64;
			float borders = 5f / 2;
			float padding = 40;
			index = 0;
			float x = 30 - (160 - objectsRenderX);
			float y = objectsUIOffset;
			for(MapEditorObject object : loadedObjects){
				y = (size + padding) * index + 50 + objectsUIOffset;
				batch.draw(objectBox, x , y);
				batch.draw(object.texture, x + borders, y + borders, size, size);

				smallFont.setColor(Color.BLACK);
				smallFont.draw(batch, object.name, x - 20, y - 5);
				rect.set(x, y, 64, 64);
				if(rect.contains(Input.getMousePosYFlip())){
					if(Input.clickLeft()){
						if(!VisUIHandler.propertiesOpen)
							placing = object;	
						ghostRenderPos.set(Input.getMouseWorldPos());
					}else if(Input.clickRight() && !VisUIHandler.propertiesOpen){
						propertiesEdit = object;
						VisUIHandler.openProperties();
					}
				}
				index++;
			}


			// Top cover
			batch.draw(cover, 1 - (160 - objectsRenderX), height - cover.getRegionHeight() / 2, 158, cover.getRegionHeight() / 2);

			// Bottom cover
			cover.flip(false, true);
			batch.draw(cover, 1 - (160 - objectsRenderX), 0, 158, cover.getRegionHeight() / 2);
			cover.flip(false, true);

			// Refresh
			int rX = 165;
			int rY = height - 37;
			batch.draw(refresh, rX, rY);
			rect.set(rX, rY, 32, 32);
			if(rect.contains(Input.getMousePosYFlip())){
				if(Input.clickLeft()){
					Log.info(TAG, "Refreshing the list of objects.");
					refreshObjects();
					MapObject.register();
					Shaders.getAllShaders();
					// Shader refresh : debug only
					outlineShader = Shaders.getShader("Outline");
					objectsUIOffsetTarget = 0;
				}
			}

			// Run
			int runX = 205;
			int runY = height - 37;
			batch.draw(run, runX, runY);
			rect.set(runX, runY, 32, 32);
			if(rect.contains(Input.getMousePosYFlip())){
				if(Input.clickLeft() && !VisUIHandler.propertiesOpen && !VisUIHandler.colourOpen){
					run();
				}
			}

			// Physics
			int pX = 245;
			int pY = height - 37;
			batch.draw(physics, pX, pY);
			rect.set(pX, pY, 32, 32);
			if(rect.contains(Input.getMousePosYFlip())){
				if(Input.clickLeft() && !VisUIHandler.propertiesOpen && !VisUIHandler.colourOpen){
					// Open physics
					state = State.PHYSICS;
					PhysicsData.refreshData(true);
					VisUIHandler.openPhysicsList();
					returnToFromPhysics.set(Day100.camera.position.x, Day100.camera.position.y, Day100.camera.zoom);
					input.zoom = 1;
					if(PhysicsData.find(VisUIHandler.physicsFolders.getSelected()) != null && !PhysicsData.find(VisUIHandler.physicsFolders.getSelected()).image.equals("None")){
						Day100.camera.position.set(MapEditorPlaceable.getObject(PhysicsData.find(VisUIHandler.physicsFolders.getSelected()).image).texture.getRegionWidth() / 2f / Constants.PPM, MapEditorPlaceable.getObject(PhysicsData.find(VisUIHandler.physicsFolders.getSelected()).image).texture.getRegionHeight() / 2f / Constants.PPM, 1);	
					}else{
						Day100.camera.position.set(10, 10, 1);						
					}
				}
			}

			// Guns editor
			int gX = 285;
			int gY = height - 37;
			batch.draw(gunsButton, gX, gY);
			rect.set(gX, gY, 32, 32);
			if(rect.contains(Input.getMousePosYFlip())){
				if(Input.clickLeft() && !VisUIHandler.propertiesOpen && !VisUIHandler.colourOpen){
					// Open gun editor
					state = State.GUNS;
					GunEditor.open();
					returnToFromPhysics.set(Day100.camera.position.x, Day100.camera.position.y, 0);
					returnToFromPhysics.z = Day100.camera.zoom;
					input.zoom = .3f;
					Day100.camera.position.set(0, 0, 0);
				}
			}

			// Exit
			int eX = width - 50;
			int eY = height - 50;
			batch.draw(exit, eX, eY);
			rect.set(eX, eY, 32, 32);
			if(Input.clickLeft() && rect.contains(Input.getMousePosYFlip())){
				Log.info(TAG, "Quitting and saving");

				// Save
				save();

				ScreenManager.setScreen(Screen.MAP_EDITOR);
			}

			// Individual Properties
			if(selected != null && !largeSelection && largeSelectionObjects.size() == 0){
				smallFont.setColor(Color.BLUE);
				smallFont.draw(batch, "Individual Properties : \n" + selected.parent.name, width - 220, height - 60);
			}			

			// Render preview if open
			renderPreview(batch);		

			// FPS			
			Day100.smallFont.setColor(Color.GREEN);
			Day100.smallFont.draw(batch, "FPS : " + Gdx.graphics.getFramesPerSecond(), width - 80, 20);	
			Day100.smallFont.draw(batch, "Selected : " + largeSelectionObjects.size(), width - 135, 60);	

			// UI
			VisUIHandler.renderUI(batch);

			break;
		case EXPORTING:

			font.draw(batch, "Exporting and Loading...", Gdx.graphics.getWidth() / 2 - 210, Gdx.graphics.getHeight() / 2);
			font.draw(batch, exportStatus, Gdx.graphics.getWidth() / 2 - 210, Gdx.graphics.getHeight() / 2 - 40);

			break;
		case RUNNING:

			// exit
			eX = width - 50;
			eY = height - 50;
			batch.draw(exit, eX, eY);
			rect.set(eX, eY, 32, 32);
			if(Input.clickLeft() && rect.contains(Input.getMousePosYFlip())){
				Log.info(TAG, "Quitting run map");

				runMap.dispose();
				runMap = null;
				state = State.CREATION;
				return;
			}

			runMap.renderUI(batch);

			break;
		case PHYSICS:

			// exit
			eX = width - 50;
			eY = height - 50;
			batch.draw(exit, eX, eY);
			rect.set(eX, eY, 32, 32);
			if(Input.clickLeft() && rect.contains(Input.getMousePosYFlip())){
				Log.info(TAG, "Quitting physics editor and saving");
				PhysicsData.saveData();
				VisUIHandler.closePhysicsList();
				state = State.CREATION;
				Day100.camera.position.set(returnToFromPhysics.x, returnToFromPhysics.y, 0);
				input.zoom = returnToFromPhysics.z;
			}

			// Title
			font.setColor(Color.WHITE);
			font.draw(batch, "Editing " + VisUIHandler.physicsFolders.getSelected(), 10, height - 10);
			smallFont.setColor(Color.BLACK);
			smallFont.draw(batch, "Draw points ANTI-CLOCKWISE. \nIf a body does not show up, try pressing refresh\nIf it still does not show up, something is wrong with the points.", 10, height - 50);

			VisUIHandler.renderUI(batch);

			break;
		case GUNS:

			// exit
			eX = width - 50;
			eY = height - 50;
			batch.draw(exit, eX, eY);
			rect.set(eX, eY, 32, 32);
			if(Input.clickLeft() && rect.contains(Input.getMousePosYFlip())){
				// TODO Save
				GunEditor.close();
				state = State.CREATION;
				Day100.camera.position.set(returnToFromPhysics);
				input.zoom = returnToFromPhysics.z;
			}

			VisUIHandler.renderUI(batch);

			break;
		}

	}

	private void run() {

		if(state == State.EXPORTING || state != State.CREATION)
			return;

		new Thread(new Runnable() {
			public void run() {

				if(placedObjects.size() == 0){
					VisUIHandler.openErrorDialog("You cannot compile this map, there are no placed objects!");
				}

				exportFailures.clear();

				doneExporting = false;
				state = State.EXPORTING;

				Log.info(TAG, "Running map from editor");

				exportStatus = "Saving data...";
				sleep(60);
				saveMap();

				exportStatus = "Packing textures...";
				sleep(60);
				Exporter.exportTextures(new File(saveFolder + "\\" + selectedMap));

				exportStatus = "Copying data...";
				sleep(60);
				Exporter.exportData(new File(saveFolder + "\\" + selectedMap));

				exportStatus = "Creating map structure...";
				sleep(60);
				runMap = new Map(new File(saveFolder.getAbsoluteFile() + "\\" + selectedMap + "\\Export"));
				Day100.map = runMap;

				exportStatus = "Building map...";
				sleep(60);
				Gdx.app.postRunnable(new Runnable() {					
					public void run() {
						runMap.loadFromFile();						
					}
				});

				exportStatus = "Creating player...";
				sleep(60);
				Day100.player = new PlayerController(new Vector2(10, 5));

				exportStatus = "Done!";

				state = State.RUNNING;
				doneExporting = true;

				displayErrors();
			}
		}).start();
	}

	public void displayErrors(){

		if(exportFailures.size() == 0){
			Log.info(TAG, "No errors to display.");
			return;
		}

		String message = "";
		Log.error(TAG, "Exporting and compiling found the following issues:");
		for(String string : exportFailures){
			message += string + "\n";
			Log.error(TAG, "    " + string);
		}
		VisUIHandler.openErrorDialog(message);
	}

	public void sleep(long time){
		try {
			Thread.sleep(time);
			// Just so that the text can refresh
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resize(int width, int height){
		VisUIHandler.resized(width, height);
		GunEditor.resize(width, height);
		if(lighting != null)
			lighting.resizeFBO(width / 2, height / 2);
	}

	public void renderPreview(Batch batch){
		if(!VisUIHandler.propertiesOpen || propertiesEdit == null)	
			return;

		float x =  VisUIHandler.propertiesWindow.getX() + VisUIHandler.propertiesWindow.getWidth();
		float y = VisUIHandler.propertiesWindow.getY() + VisUIHandler.propertiesWindow.getHeight() / 2 - propertiesEdit.texture.getRegionHeight() / 2;
		boxOuter.draw(batch, x, y, propertiesEdit.texture.getRegionWidth() + 64, propertiesEdit.texture.getRegionHeight() + 64);
		if(Shaders.compliedShadersNames.contains(VisUIHandler.shaders.getSelected())){
			batch.setShader(Shaders.compliedShaders.get(Shaders.compliedShadersNames.indexOf(VisUIHandler.shaders.getSelected())));
			try{
				batch.getShader().setUniformf("u_multi", Shaders.getTextureAtlasShaderMulti(propertiesEdit.texture));
				batch.getShader().setUniformf("u_sub", Shaders.getTextureAtlasShaderSubtract(propertiesEdit.texture));
				batch.getShader().setUniformf("u_time", timer);				
			}catch(Exception e){

			}
		}
		batch.getShader().begin();

		batch.setColor(VisUIHandler.colourOpen ? propetiesEditColour : propertiesEdit.setupColour);
		batch.draw(propertiesEdit.texture, x + 32 + VisUIHandler.propertiesXOffset.getValue() * Constants.PPM, y + 32 + VisUIHandler.propertiesYOffset.getValue() * Constants.PPM);
		batch.setColor(Color.WHITE);

		batch.setShader(null);
	}

	public void loadPhysics(){
		world = new World(new Vector2(0, -9.803f), true);
		lighting = new RayHandler(world);	
		sun = new DirectionalLight(lighting, Constants.RAYS * 2, new Color(0.0f, 0.0f, 0.0f, 1.0f), -90);
		//new PointLight(lighting, Constants.RAYS, Color.RED, 20, 0, 0);
		// Load world

		// Create floor
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		def.position.y = -.5f;
		def.position.x = size.x / 2;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size.x / 2, .5f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0.05f;
		fixtureDef.friction = 0.2f;
		Body body = world.createBody(def);
		body.createFixture(fixtureDef);

		// Map bodies
		createAllBodies();

		Log.info(TAG, "Loaded physics");
	}

	public void createAllBodies(){
		// Load all map editor objects bodies.
		Log.info(TAG, "Creating all bodies...");
		for(MapEditorPlaceable object : placedObjects){
			object.createBody();
		}
		displayErrors();
	}

	public void savePhysics(){
		if(world == null)
			return;
		lighting.dispose();
		world.dispose();
		world = null;
		lighting = null;
		sun = null;
	}

	public void save(){
		saveSetup();
		saveMap();
		saveState();
		VisUIHandler.stop();
		savePhysics();

		placedObjects.clear();
		loadedObjects.clear();
	}

	public void load(){
		refreshObjects();
		loadMap();
		loadState();
		loadPhysics();
		VisUIHandler.start();
		PhysicsData.refreshData(true);
	}

	public void dispose(){
		save();
	}

	public enum State{
		SELECT_MAP,
		CREATION,
		EXPORTING,
		RUNNING,
		PHYSICS,
		GUNS
	}

}
