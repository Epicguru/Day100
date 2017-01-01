package co.uk.epicguru.particles;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ParticleBurst {

	private static Random random = new Random();
	private static Vector2 temp = new Vector2();
	
	/**
	 * Causes a bunch of particles to spawn. Includes optimisation. 
	 * @param position The position to spawn at.
	 * @param minAngle The minimum angle for velocity, in degrees.
	 * @param maxAngle The maximum angle for velocity, in degrees.
	 * @param velocity The velocity in meters per second.
	 * @param min The minimum amount of particles.
	 * @param max The maximum amount of particles (Inclusive).
	 * @param minTime The minimum time that each particle is alive for.
	 * @param maxTime The maximum time that each particle is alive for.
	 */
	public ParticleBurst(Vector2 position, float minAngle, float maxAngle, float minVelocity, float maxVelocity, int min, int max, float minTime, float maxTime, TextureRegion texture, long seed){
		// Cause a rain of particles
		
		if(Gdx.graphics.getFramesPerSecond() <= 30 || ParticleInstance.particlesActive >= 1000){
			//Log.error("ERROR", ParticleInstance.particlesActive + " >= 1000 !");
			return;
		}
		
		random.setSeed(seed);
		int amount = min + random.nextInt(max + 1 - min);
		for(int i = 0; i < amount; i++){
			float angle = minAngle + random.nextFloat() * (maxAngle - minAngle);
			float velocity = minVelocity + random.nextFloat() * (maxVelocity - minVelocity);
			float time = minTime + random.nextFloat() * (maxTime - minTime);
			temp.set(MathUtils.cosDeg(angle) * velocity, MathUtils.sinDeg(angle) * velocity);
			getParticle(position, temp, time, texture);
		}
	}
	
	public ParticleInstance getParticle(Vector2 position, Vector2 velocity, float time, TextureRegion texture){		
		return new ParticleInstance(position, temp, time, texture);
	}
	
}
