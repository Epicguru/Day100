package co.uk.epicguru.map;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.particles.ParticleBurst;
import co.uk.epicguru.sound.SoundUtils;

public class FloorEntity extends Entity{

	public static TextureRegion[] particles;
	private static Sound thud;
	
	public FloorEntity(Body ground) {
		super("Ground", 1, ground);
		particles = new TextureRegion[]{
				Day100.particlesAtlas.findRegion("StandardParticle"),
				Day100.particlesAtlas.findRegion("StandardParticle2")
		};
		thud = Day100.assets.get("Audio/SFX/Misc/Thud.mp3", Sound.class);
	}

	public boolean isDead(){
		return false; // Never die!
	}

	public void takeDamage(float damage, DamageData data) {
		
		if(data.dealer != null && data.angle != 0){
			// Spawn particles
			new ParticleBurst(data.hitPoint, MathUtils.random(90, 180), MathUtils.random(0, 90), 2, 5, 20, 30, 2, 3, particles[MathUtils.random(0, 1)], MathUtils.random(78126312));	
			SoundUtils.playSound(data.hitPoint, thud, 1, 1, 20);
		}
	}	
}
