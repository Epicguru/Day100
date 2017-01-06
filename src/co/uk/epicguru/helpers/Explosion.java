package co.uk.epicguru.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.particles.FlareLight;
import co.uk.epicguru.particles.ParticleExplosion;
import co.uk.epicguru.particles.ParticleTrailerExplosion;

public class Explosion {

	private static TextureRegion[] particles;

	public static void explode(Vector2 position, float scale){

		if(particles == null){
			particles = new TextureRegion[]{
					Day100.particlesAtlas.findRegion("Spark1"),
					Day100.particlesAtlas.findRegion("Spark2"),
					Day100.particlesAtlas.findRegion("Smoke1"),
					Day100.particlesAtlas.findRegion("Smoke2"),
					Day100.particlesAtlas.findRegion("StandardParticle"),
					Day100.particlesAtlas.findRegion("StandardParticle2")
			};
		}		

		for(TextureRegion texture : particles){
			new ParticleExplosion(position, MathUtils.random(10, 20) * scale, MathUtils.random(30, 50) * scale, 20, 40, texture, MathUtils.random(76123786123l));
		}
		new ParticleTrailerExplosion(position, 20 * scale, 30 * scale, 15, 25, particles[2], MathUtils.random(76123786123l));
		Filter f = new Filter();
		f.groupIndex = -123;
		new FlareLight(position, 0.5f, Color.ORANGE, 10 * scale).light.setContactFilter(f);
		new FlareLight(position, 0.5f, Color.RED, 30 * scale);
		
		Interpolation interpolation = Interpolation.linear;
		float MAX_DST = 760;
		float dst = Day100.camera.position.dst2(position.x, position.y, Day100.camera.position.z);
		float multi = 1 - interpolation.apply(dst / MAX_DST);
		CamShake.shake(8f * scale * multi);
	}

}
