package co.uk.epicguru.map.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.map.Entity;

public abstract class BuildingObject extends Entity {

	public static Color ghostColor = new Color(0, 1, 0, 0.4f);
	public static Color ghostColorRed = new Color(1, 0, 0, 0.4f);
	public TextureRegion texture;
	
	public BuildingObject(String name, float health, TextureRegion texture) {
		super(name, health);
		this.texture = texture;
		BuildingManager.placed.add(this);
	}
	
	public void render(Batch batch){
		batch.draw(texture, body.getPosition().x - texture.getRegionWidth() / Constants.PPM / 2, body.getPosition().y - texture.getRegionHeight() / Constants.PPM / 2, texture.getRegionWidth() / Constants.PPM, texture.getRegionHeight() / Constants.PPM);
	}
	
	public void destroyed(){
		super.destroyed();
		BuildingManager.placed.remove(this);
	}

}
