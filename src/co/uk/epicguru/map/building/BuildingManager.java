package co.uk.epicguru.map.building;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.building.instances.WoodenFloor;

public class BuildingManager {

	public static final String TAG = "Building Manager";
	private static ObjectPair<Vector2, Boolean> snapValue;
	public static ArrayList<BuildingObject> placed = new ArrayList<BuildingObject>();
	public static boolean placing = false;
	public static BuildingObject placingObject = null;

	/**
	 * Clears the placed objects.
	 */
	public static void clearPlaced(){
		for(BuildingObject object : placed){
			object.slay();
		}
	}

	/**
	 * Does a full reset including clearing all placed objects and reseting all variables.
	 */
	public static void reset(){
		stopPlacing();
		clearPlaced();
		snapValue = null;
	}

	public static void stopPlacing(){
		placing = false;
		if(placingObject instanceof BuildingFloor){
			((BuildingFloor)placingObject).cancelPlace();
		}
		placingObject = null;
	}

	public static void update(float delta){

		if(placing == true){
			if(placingObject == null){
				placingObject = new WoodenFloor(new Vector2(Input.getMouseWorldX(), 0));
			}else{
				// Do nothing, already set.
			}
		}else{
			if(placingObject != null){
				stopPlacing();
			}else{
				// Do nothing, already removed or null.
			}
		}

		if(Input.clickLeft()){
			if(placing && snapValue != null && snapValue.b){
				if(placingObject instanceof BuildingFloor){
					BuildingFloor floor = (BuildingFloor)placingObject;
					getSnapPosition(Input.getMouseWorldPos(), placed, true);
					floor.place(new Vector2(snapValue.a));
				}
				placingObject = null;
			}
		}		
	}

	public static void render(Batch batch){
		if(placing){
			if(placingObject instanceof BuildingFloor){
				// Get snap position
				getSnapPosition(Input.getMouseWorldPos(), placed, false);
				((BuildingFloor)placingObject).ghostRender(batch, snapValue.a, snapValue.b);
			}
		}
	}

	private static Rectangle rect = new Rectangle();
	private static Rectangle rect2 = new Rectangle();
	public static ObjectPair<Vector2, Boolean> getSnapPosition(Vector2 mousePos, ArrayList<BuildingObject> objects, boolean place){
		// X = right / left -+ half

		if(snapValue == null){
			snapValue = new ObjectPair<Vector2, Boolean>(Vector2.Zero, false);
		}

		// TODO collision detection
		// Get snap values...
		if(placingObject instanceof BuildingFloor){
			// FLOOR
			boolean foundEdge = false;
			for(BuildingObject object : objects){
				// FLOOR
				if(object instanceof BuildingFloor){
					if(object != placingObject){					
						// GO!
						float width = object.texture.getRegionWidth() / Constants.PPM;
						float halfWidth = width / 2f;
						float rightSide = object.body.getPosition().x + halfWidth;
						float leftSide = object.body.getPosition().x - halfWidth;
						float placingWidth = placingObject.texture.getRegionWidth() / Constants.PPM;

						// Within 
						if(mousePos.x < rightSide + placingWidth / 2 && mousePos.x >= rightSide - placingWidth / 2){							
							foundEdge = true;
							snapValue.a.set(rightSide + placingWidth / 2, 0);
							snapValue.b = isValidPos(objects);
							if(Input.clickLeft()){
								if(snapValue.b){
									Log.info(TAG, "Set!");
									((BuildingFloor)placingObject).connectedRight = object;
									// Get left connected
									((BuildingFloor)placingObject).connectedLeft = getFloorToLeft(objects);
								}
							}
						}else if(mousePos.x > leftSide - placingWidth / 2 && mousePos.x <= leftSide + placingWidth / 2){							
							foundEdge = true;
							snapValue.a.set(leftSide - placingWidth / 2, 0);
							snapValue.b = isValidPos(objects);
							if(Input.clickLeft()){
								if(snapValue.b){
									((BuildingFloor)placingObject).connectedLeft = object;
									// Get right connected
									((BuildingFloor)placingObject).connectedRight = getFloorToRight(objects);
								}
							}
						}
					}
				}				
			}
			if(!foundEdge){
				snapValue.a.set(mousePos.x, 0);
				snapValue.b = isValidPos(objects);
			}
		}

		return snapValue;
	}

	public static boolean isValidPos(ArrayList<BuildingObject> objects){
		boolean validPos = true;
		for(BuildingObject object : objects){
			if(object != placingObject){
				float width = object.texture.getRegionWidth() / Constants.PPM;
				float height = object.texture.getRegionHeight() / Constants.PPM;
				rect.set(object.body.getPosition().x - width / 2, object.body.getPosition().y - height / 2, width, height);
				width = placingObject.texture.getRegionWidth() / Constants.PPM;
				height = placingObject.texture.getRegionHeight() / Constants.PPM;
				if(rect.overlaps(rect2.set(placingObject.body.getPosition().x - width / 2, placingObject.body.getPosition().y - height / 2, width, height))){
					validPos = false;
					break;
				}

			}					
		}
		return validPos;
	}
	
	private static BuildingObject getFloorToLeft(ArrayList<BuildingObject> objects){
		float x = placingObject.body.getPosition().x;
		for(BuildingObject object : objects){
			if(object != placingObject){
				if(x - placingObject.texture.getRegionWidth() / Constants.PPM / 2 == object.body.getPosition().x + object.texture.getRegionWidth() / Constants.PPM / 2){
					return object;
				}
			}					
		}
		return null;
	}
	
	private static BuildingObject getFloorToRight(ArrayList<BuildingObject> objects){
		float x = placingObject.body.getPosition().x;
		for(BuildingObject object : objects){
			if(object != placingObject){
				if(x + placingObject.texture.getRegionWidth() / Constants.PPM / 2 == object.body.getPosition().x - object.texture.getRegionWidth() / Constants.PPM / 2){
					return object;
				}
			}					
		}
		return null;
	}

	public static void renderUI(Batch batch){

	}
}
