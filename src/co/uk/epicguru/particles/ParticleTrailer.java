package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ParticleTrailer extends ParticleInstance{

	private float timer2;
	private static Vector2 TEMP = new Vector2();
	
	public ParticleTrailer(Vector2 position, Vector2 velocity, float time, TextureRegion region) {
		super(position, velocity, time, region);
	}
	
	public void update(float delta){
		timer2 += delta;
		float interval = 0.01f;
		while(timer2 >= interval){
			timer2 -= interval;
			TEMP.set(MathUtils.random(-1, 1), MathUtils.random(-1, 1));
			new ExplosionParticleInstance(getBody().getPosition(), TEMP, 0.4f, this.texture);
		}
		super.update(delta);
	}
	
	public void render(Batch batch){
		return; // None of that!
	}

}
