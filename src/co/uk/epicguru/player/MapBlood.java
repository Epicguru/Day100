package co.uk.epicguru.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;

public final class MapBlood extends Entity{

	public static TextureRegion blood = Day100.playerAtlas.findRegion("Blood");
	public Vector2 position;
	public float angle;
	private float timer;
	
	public MapBlood(Vector2 position, float angle){
		super("Blood", 1);
		this.angle = angle;
		this.position = new Vector2(position);
		this.position.x -= blood.getRegionWidth() / Constants.PPM;
		this.position.y -= blood.getRegionHeight() / Constants.PPM / 2;
		timer = 0.3f;
	}
	
	public void update(float delta){
		this.timer -= delta;			
	}
	
	public boolean isDead(){
		return timer <= 0;
	}

	private static Color color = new Color();
	public void render(Batch batch) {
		color.set(1, 1, 1, timer / 0.3f);
		batch.setColor(color);
		batch.draw(blood, position.x, position.y, blood.getRegionWidth() / Constants.PPM, blood.getRegionHeight() / 2 / Constants.PPM, blood.getRegionWidth() / Constants.PPM * timer / 0.3f, blood.getRegionHeight() / Constants.PPM, 1, 1, angle);
		batch.setColor(Color.WHITE);
	}
	
}
