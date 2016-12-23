package co.uk.epicguru.map.building.instances;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.building.BuildingFloor;

public class WoodenFloor extends BuildingFloor {

	public static TextureRegion[] textures;

	static{
		textures = new TextureRegion[4];
		textures[0] = Day100.buildingAtlas.findRegion("Wood_Base_Connected");
		textures[1] = Day100.buildingAtlas.findRegion("Wood_Base_Disconnected");
		textures[2] = Day100.buildingAtlas.findRegion("Wood_Base_Connected_Right");
		textures[3] = Day100.buildingAtlas.findRegion("Wood_Base_Connected_Left");
	}

	public WoodenFloor(Vector2 position) {
		super("Wooden Floor", 50, textures, position);
	}

	public TextureRegion[] getTextures() {
		return textures;
	}

}
