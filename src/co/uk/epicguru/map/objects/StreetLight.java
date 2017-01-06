package co.uk.epicguru.map.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import box2dLight.ConeLight;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.settings.GameSettings;

public final class StreetLight extends MapObject {

	
	public ConeLight light;
	public boolean flickering;
	public float timer = 0;
	
	public StreetLight(String name, Vector2 position, Vector2 size, TextureRegion texture, Color color) {
		super(name, position, size, texture, color);
		this.light = new ConeLight(Day100.map.rayHandler, GameSettings.getLightQuality().getValue(), new Color(1, 1, 0.3f, 0.8f), 12, position.x + 0.35f, position.y + 7.8f, -90, 40);
	}
	
	public void update(float delta){
		if(MathUtils.randomBoolean(0.02f * delta)){
			if(!flickering){
				flickering = true;
				timer = MathUtils.random(0.3f, 1.5f);
			}
		}
		if(flickering){
			timer -= delta;
			if(timer < 0){
				flickering = false;
			}
		}
	}
	
	public void render(Batch batch){
		super.render(batch);		
		// Light flicker
		if(flickering){
			if(MathUtils.randomBoolean(0.7f)){
				light.setColor(1, 1, 0.3f, 0.8f);
			}else{
				light.setColor(1, 1, 0.3f, 0.0f);				
			}
		}else{
			light.setColor(1, 1, 0.3f, 0.8f);
		}
	}

	@Override
	public void load() {
		
	}

	@Override
	public void save() {
		
	}

}
