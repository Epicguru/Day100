package co.uk.epicguru.player.weapons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;

public final class FlashFade extends Entity{

	public static TextureRegion flash;
	public Vector2 position;
	public float angle;
	public float timer;
	public float range;
	
	public FlashFade(Vector2 position, float angle, float range){
		super("Gun Flash", 1);
		this.angle = angle;
		this.position = position;
		if(flash == null){
			flash = Day100.gunsAtlas.findRegion("Flash");
		}
		this.timer = 0.3f;
		this.range = range;
	}
	
	public void update(float delta){
		this.timer -= delta;
		if(this.timer <= 0){
			// Remove
			slay();
		}
	}
	
	public void render(Batch batch){
		float width = flash.getRegionWidth() / Constants.PPM;
		float height = flash.getRegionHeight() / Constants.PPM;
		
		batch.setColor(1, 1, 1, timer / 0.3f);
		batch.draw(flash, position.x, position.y - height / 2, 0, height / 2, (width / 10f) * range, height, 1f, 1f, angle);
		batch.setColor(1, 1, 1, 1);
	}	
}
