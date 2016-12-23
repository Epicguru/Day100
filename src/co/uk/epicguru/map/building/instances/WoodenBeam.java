package co.uk.epicguru.map.building.instances;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.building.BuildingBeam;

public class WoodenBeam extends BuildingBeam {

	public static TextureRegion[] textures;
	
	static{
		textures = new TextureRegion[2];
		textures[0] = Day100.buildingAtlas.findRegion("Wood_Beam");
		textures[1] = Day100.buildingAtlas.findRegion("Wood_Beam-2");
	}
	
	public WoodenBeam(Vector2 position) {
		super("Wooden Beam", 75, textures[MathUtils.random(0, 1)], position);
	}

}
