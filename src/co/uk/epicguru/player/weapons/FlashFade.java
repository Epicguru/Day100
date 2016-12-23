package co.uk.epicguru.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public final class FlashFade {

	public static TextureRegion flash;
	public Vector2 position;
	public float angle;
	public float timer;
	public boolean done;
	public float range;
	
	public FlashFade(Vector2 position, float angle, float range){
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
			done = true;
		}
	}
	
	public void render(Batch batch){
		float width = flash.getRegionWidth() / Constants.PPM;
		float height = flash.getRegionHeight() / Constants.PPM;
		
		batch.setColor(1, 1, 1, timer / 0.3f);
		batch.draw(flash, position.x, position.y - height / 2, 0, height / 2, (width / 10f) * range, height, 1f, 1f, angle);
		batch.setColor(1, 1, 1, 1);
	}
	
	public static void updateAll(float delta){
		
		for(FlashFade flash : GunManager.flashes){
			flash.update(delta);
		}
		
		binDoneFlashes();
	}
	
	public static void renderAll(Batch batch){
		for(FlashFade flash : GunManager.flashes){
			flash.render(batch);
		}
	}
	
	public static void clearAll(){
		for(FlashFade flash : GunManager.flashes){
			flash.done = true;
		}
		binDoneFlashes();
	}
	
	private static ArrayList<FlashFade> bin = new ArrayList<FlashFade>();
	private static void binDoneFlashes(){
		bin.clear();
		for(FlashFade flash : GunManager.flashes){
			if(flash.done)
				bin.add(flash);
		}
		for(FlashFade flash : bin){
			GunManager.flashes.remove(flash);
		}
		bin.clear();
	}
	
}
