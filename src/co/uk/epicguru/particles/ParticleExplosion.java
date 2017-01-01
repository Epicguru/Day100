package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParticleExplosion extends ParticleBurst {

	public ParticleExplosion(Vector2 position, float minVelocity, float maxVelocity, int min, int max, TextureRegion texture, long seed) {
		super(position, 0, 360, minVelocity, maxVelocity, min, max, 0.5f, 2, texture, seed);
	}

	@Override
	public ParticleInstance getParticle(Vector2 position, Vector2 velocity, float time, TextureRegion texture) {
		return new ExplosionParticleInstance(position, velocity, time, texture);
	}

}
