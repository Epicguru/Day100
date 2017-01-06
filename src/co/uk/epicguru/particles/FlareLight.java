package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import box2dLight.PointLight;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.settings.GameSettings;

public class FlareLight extends Entity {

	public float timer;
	public PointLight light;
	
	private float timerStart;
	private float dstStart;
	
	public FlareLight(Vector2 position, float time, Color color, float startDst) {
		super("Flare Light", 1);
		timerStart = time;
		timer = timerStart;
		dstStart = startDst;
		if(Day100.map.rayHandler == null){
			slay();
			return;
		}
		light = new PointLight(Day100.map.rayHandler, GameSettings.getLightQuality().getValue(), color, startDst, position.x, position.y);
		light.setSoftnessLength(6);
	}
	
	public void update(float delta){
		this.timer -= delta;
		float alpha = timer / timerStart;
		light.setDistance(dstStart * alpha);
	}
	
	public boolean isDead(){
		return timer <= 0;
	}
	
	public void destroyed(){
		super.destroyed();
		try{			
			//light.remove();
		}catch(Exception e){
			
		}
	}

}
