package co.uk.epicguru.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.particles.ParticleExplosion;

public class FloorEntity extends Entity{

	public static TextureRegion[] particles;
	
	public FloorEntity(Body ground) {
		super("Ground", 1, ground);
		particles = new TextureRegion[]{
				Day100.gunsAtlas.findRegion("StandardParticle"),
				Day100.gunsAtlas.findRegion("StandardParticle2")
		};
	}

	public boolean isDead(){
		return false; // Never die!
	}

	public void takeDamage(float damage, DamageData data) {
		if(data.dealer != null && data.angle != 0){
			// Spawn particles
			new ParticleExplosion(data.hitPoint, MathUtils.random(90, 180), MathUtils.random(0, 90), 2, 5, 20, 30, 2, 3, particles[MathUtils.random(0, 1)], MathUtils.random(78126312));
		}
	}	
}
