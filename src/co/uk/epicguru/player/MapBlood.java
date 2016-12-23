package co.uk.epicguru.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public final class MapBlood {

	public static TextureRegion blood = Day100.playerAtlas.findRegion("Blood");
	public Vector2 position;
	public float angle;
	
	public MapBlood(Vector2 position, float angle){
		this.angle = angle;
		this.position = new Vector2(position);
		this.position.x -= blood.getRegionWidth() / Constants.PPM;
		this.position.y -= blood.getRegionHeight() / Constants.PPM / 2;
	}

	public void render(Batch batch) {
		batch.draw(blood, position.x, position.y, blood.getRegionWidth() / Constants.PPM, blood.getRegionHeight() / 2 / Constants.PPM, blood.getRegionWidth() / Constants.PPM, blood.getRegionHeight() / Constants.PPM, 1, 1, angle);
	}
	
}
