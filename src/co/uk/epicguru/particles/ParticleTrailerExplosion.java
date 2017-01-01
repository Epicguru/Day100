package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParticleTrailerExplosion extends ParticleExplosion{

	public ParticleTrailerExplosion(Vector2 position, float minVelocity, float maxVelocity, int min, int max,
			TextureRegion texture, long seed) {
		super(position, minVelocity, maxVelocity, min, max, texture, seed);
	}
	
	@Override
	public ParticleInstance getParticle(Vector2 position, Vector2 velocity, float time, TextureRegion texture) {
		return new ParticleTrailer(position, velocity, time, texture);
	}

}
