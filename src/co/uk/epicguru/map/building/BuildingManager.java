package co.uk.epicguru.map.building;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.map.building.instances.WoodenBeam;
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
		if(placingObject instanceof BuildingBeam){
			((BuildingBeam)placingObject).cancelPlace();
		}
		placingObject = null;
	}

	public static void update(float delta){

		if(placing == true){
			if(placingObject == null){
				if(Input.isKeyDown(Keys.SPACE)){
					placingObject = new WoodenFloor(new Vector2(Input.getMouseWorldX(), 0));					
				}else{
					placingObject = new WoodenBeam(new Vector2(Input.getMouseWorldX(), 0));					
				}
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
				if(placingObject instanceof BuildingBeam){
					BuildingBeam beam = (BuildingBeam)placingObject;
					getSnapPosition(Input.getMouseWorldPos(), placed, true);
					beam.place(new Vector2(snapValue.a));
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
			if(placingObject instanceof BuildingBeam){
				// Get snap position
				getSnapPosition(Input.getMouseWorldPos(), placed, false);
				((BuildingBeam)placingObject).ghostRender(batch, snapValue.a, snapValue.b);
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

		// Get snap values...
		// FLOOR
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

						// Within right side bounds
						if(mousePos.x < rightSide + placingWidth / 2 && mousePos.x >= rightSide - placingWidth / 2 && mousePos.y < object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM * 2 && mousePos.y > object.body.getPosition().y - object.texture.getRegionHeight() / Constants.PPM * 2){							
							foundEdge = true;
							snapValue.a.set(rightSide + placingWidth / 2, object.body.getPosition().y - placingObject.texture.getRegionHeight() / Constants.PPM / 2);
							snapValue.b = isValidPos(objects);
							if(place){
								if(snapValue.b){
									//Log.info(TAG, "Set snap object to " + object.toString());
									((BuildingFloor)placingObject).connectedLeft = object;
									((BuildingFloor)object).connectedRight = placingObject;
									// Get left connected
									BuildingObject object2 = getFloorToRight(objects);
									((BuildingFloor)placingObject).connectedRight = object2;
									if(object2 != null && object2 instanceof BuildingFloor){
										((BuildingFloor)object2).connectedLeft = placingObject;
									}
								}
							}
						}else if(mousePos.x > leftSide - placingWidth / 2 && mousePos.x <= leftSide + placingWidth / 2 && mousePos.y < object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM * 2 && mousePos.y > object.body.getPosition().y - object.texture.getRegionHeight() / Constants.PPM * 2){	
							// Left side bounds
							foundEdge = true;
							snapValue.a.set(leftSide - placingWidth / 2, object.body.getPosition().y - placingObject.texture.getRegionHeight() / Constants.PPM / 2);
							snapValue.b = isValidPos(objects);
							if(place){
								if(snapValue.b){
									//Log.info(TAG, "Set snap object to " + object.toString());
									((BuildingFloor)placingObject).connectedRight = object;
									((BuildingFloor)object).connectedLeft = placingObject;
									// Get left connected
									BuildingObject object2 = getFloorToLeft(objects);
									((BuildingFloor)placingObject).connectedLeft = object2;
									if(object2 != null && object2 instanceof BuildingFloor){
										((BuildingFloor)object2).connectedRight = placingObject;
									}
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

		// BEAMS
		if(placingObject instanceof BuildingBeam){
			snapValue.a.set(mousePos.x, 0);
			snapValue.b = false;

			for(BuildingObject object : objects){
				if(object instanceof BuildingFloor){
					// FLOOR
					BuildingFloor floor = (BuildingFloor)object;
					if(mousePos.x > floor.body.getPosition().x){
						if(mousePos.x < floor.body.getPosition().x + floor.texture.getRegionWidth() / Constants.PPM){
							if(mousePos.y > floor.getBody().getPosition().y){
								if(mousePos.y < floor.getBody().getPosition().y + placingObject.texture.getRegionHeight() / Constants.PPM){
									// We are in bounds
									snapValue.a.set(object.body.getPosition().x + object.texture.getRegionWidth() / Constants.PPM / 2, object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM / 2);
									snapValue.b = isValidPos(objects);
								}
							}
						}
					}
					if(mousePos.x < floor.body.getPosition().x){
						if(mousePos.x > floor.body.getPosition().x - floor.texture.getRegionWidth() / Constants.PPM){
							if(mousePos.y > floor.getBody().getPosition().y){
								if(mousePos.y < floor.getBody().getPosition().y + placingObject.texture.getRegionHeight() / Constants.PPM){
									// We are in bounds
									snapValue.a.set(object.body.getPosition().x - object.texture.getRegionWidth() / Constants.PPM / 2, object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM / 2);
									snapValue.b = isValidPos(objects);
								}
							}
						}
					}
				}
				if(object instanceof BuildingBeam){
					if(object == placingObject)
						continue;
					rect.set(object.body.getPosition().x - placingObject.texture.getRegionWidth() / Constants.PPM / 2, object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM / 2, placingObject.texture.getRegionWidth() / Constants.PPM * 2, placingObject.texture.getRegionHeight() / Constants.PPM);
					if(rect.contains(mousePos)){
						// Good to go on top of beam.
						snapValue.a.set(object.body.getPosition().x, object.body.getPosition().y + object.texture.getRegionHeight() / Constants.PPM / 2);
						snapValue.b = isValidPos(objects);
					}
				}
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
