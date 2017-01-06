package co.uk.epicguru.vehicles.definitions.instances;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import co.uk.epicguru.helpers.Explosion;
import co.uk.epicguru.helpers.SpriteProjecter;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.DamageData;
import co.uk.epicguru.particles.ParticleBurst;
import co.uk.epicguru.sound.SoundUtils;
import co.uk.epicguru.vehicles.VehicleInstance;
import co.uk.epicguru.vehicles.definitions.Helicopter;

public class HelicopterInstance extends VehicleInstance {

	public static TextureRegion[] particles;
	private static Sound thud;
	public static Sprite sprite;
	public static TextureRegion[] backBlades;
	public static TextureRegion[] bladesSpinUp;
	public static TextureRegion[] bladesSpin;
	private float timer;
	public boolean ready;
	public float throttle;
	private int frame;
	
	public HelicopterInstance(Helicopter parent, Vector2 position, float angle, Vector2 linear, float angular) {
		super("Helicopter", 300, parent);
		if(sprite == null){
			sprite = new Sprite(parent.defaultTexture);		
			backBlades = new TextureRegion[4];
			for(int i = 0; i < 4; i++){
				backBlades[i] = getLocalTexture("Back" + i);
			}
			
			bladesSpinUp = new TextureRegion[36];
			for(int i = 0; i < 36; i++){
				bladesSpinUp[i] = getLocalTexture("SpinUp" + i);
			}
			
			bladesSpin = new TextureRegion[6];
			for(int i = 0; i < 6; i++){
				bladesSpin[i] = getLocalTexture("Spin" + i);
			}
		}
		setBody(getBodyFromData("Helicopter", position, angle, linear, angular, parent.defaultTexture.getRegionWidth(), sprite.getRegionHeight()));
	
		if(particles == null){
			particles = new TextureRegion[]{
					Day100.particlesAtlas.findRegion("StandardParticle"),
					Day100.particlesAtlas.findRegion("StandardParticle2")
			};
			thud = Day100.assets.get("Audio/SFX/Misc/Thud.mp3", Sound.class);
		}
	}
	
	@Override
	public FixtureDef getBodyFixture() {
		FixtureDef def = new FixtureDef();
		def.density = 50;
		def.filter.groupIndex = -123;
		return def;
	}

	private static Vector2 TEMP = new Vector2();
	public void update(float delta){
		super.update(delta);
		timer += delta;
		if(Input.isKeyDown(Keys.W) && hasRiders()){
			throttle++;
		}
		if(Input.isKeyDown(Keys.S) && hasRiders()){
			throttle--;
		}
		if(throttle > 50)
			throttle = 50;
		if(throttle < 0)
			throttle = 0;
			
		body.setFixedRotation(true);
		body.setBullet(true);
		
		if(ready && throttle > 0){
			if(getBody() != null){
				
				body.setLinearDamping(0.5f);
				float scale = 5;
				body.setGravityScale(scale);
				float maxSpeed = 21; // Aprox
				
				if(Input.isKeyDown(Keys.D) && hasRiders()){
					TEMP.set(10000, 0);
					body.applyForceToCenter(TEMP, true);
				}else if(Input.isKeyDown(Keys.A) && hasRiders()){
					TEMP.set(-10000, 0);					
					body.applyForceToCenter(TEMP, true);
				}
				body.setLinearDamping(0.5f);
				body.applyAngularImpulse(((body.getLinearVelocity().x / maxSpeed) * -30f) * 50 * MathUtils.degreesToRadians, true);
				
				float fall = 7000;
				float max = 13000;
				float value = Interpolation.linear.apply(fall, max, (throttle / 50f)) * scale;
				getBody().applyForceToCenter(new Vector2(0, value), true);
			}
		}	
		
		// Update riders
		sprite.setRegion(parent.defaultTexture);
		float width = sprite.getRegionWidth() / Constants.PPM;
		float height = sprite.getRegionHeight() / Constants.PPM;		
		sprite.setSize(width, height);
		sprite.setPosition(getBody().getPosition().x, getBody().getPosition().y);
		sprite.setOrigin(0, 0);
		sprite.setRotation(getBody().getAngle() * MathUtils.radiansToDegrees);
		updateRiders(sprite);
		
		if(!hasRiders())
			throttle--;
	}
	
	public void render(Batch batch){
		float width, height;
		
		boolean toRight = getBody().getLinearVelocity().x >= 0;
		
		sprite.setFlip(!toRight, false);
		sprite.setRegion(parent.defaultTexture);
		width = sprite.getRegionWidth() / Constants.PPM;
		height = sprite.getRegionHeight() / Constants.PPM;		
		sprite.setSize(width, height);
		sprite.setPosition(getBody().getPosition().x, getBody().getPosition().y);
		sprite.setOrigin(0, 0);
		sprite.setRotation(getBody().getAngle() * MathUtils.radiansToDegrees);
		sprite.draw(batch);
		
		
		Vector2 pos2 = new Vector2(SpriteProjecter.unprojectPosition(sprite, ((Helicopter)parent).rotorPosition));
		Vector2 pos = SpriteProjecter.unprojectPosition(sprite, ((Helicopter)parent).backRotorPosition);
		
		boolean fullThrottle = throttle > 0 && ready;
		if(fullThrottle)
			sprite.setRegion(backBlades[frame % backBlades.length]);
		else
			sprite.setRegion(backBlades[0]);
		width = sprite.getRegionWidth() / Constants.PPM;
		height = sprite.getRegionHeight() / Constants.PPM;		
		sprite.setSize(width, height);
		sprite.setPosition(pos.x - width / 2, pos.y - height / 2);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setRotation(0);
		//DevCross.drawCentred(pos.x, pos.y, 0.5f);
		sprite.draw(batch);
		
		boolean warmingUp = throttle > 0 && !ready;
		boolean stopping = throttle == 0 && ready;

		sprite.setRegion(bladesSpinUp[0]);
		if(stopping){
			ready = false;
			frame = 35;
			timer = 0;
		}
		if(warmingUp){
			while(timer >= 1f / 10f){
				timer -= 1f / 10f;
				frame++;
				if(frame >= bladesSpinUp.length){
					ready = true;
					frame = 0;
				}					
			}
			sprite.setRegion(bladesSpinUp[frame]);
		}else if(fullThrottle){
			while(timer >= 1f / 100f){
				timer -= 1f / 100f;
				frame = ++frame % bladesSpin.length;
			}
			sprite.setRegion(bladesSpin[frame]);
		}else if(throttle == 0){
			while(timer >= 1f / 10f){
				timer -= 1f / 10f;
				frame--;
				if(frame < 0){
					ready = false;
					frame = 0;
				}					
			}
			sprite.setRegion(bladesSpinUp[frame]);
		}
		width = sprite.getRegionWidth() / Constants.PPM;
		height = sprite.getRegionHeight() / Constants.PPM;		
		sprite.setSize(width, height);
		sprite.setPosition(pos2.x - width / 2, pos2.y - height / 2);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setRotation(getBody().getAngle() * MathUtils.radiansToDegrees);
		//DevCross.drawCentred(pos.x, pos.y, 0.5f);
		sprite.draw(batch);
	}

	public void takeDamage(float damage, DamageData data) {
		super.takeDamage(damage, data);
		new ParticleBurst(data.hitPoint, data.angle, data.angle, 2, 5, 20, 30, 2, 3, particles[MathUtils.random(0, 1)], MathUtils.random(78126312));	
		SoundUtils.playSound(data.hitPoint, thud, 1, 1, 20);
	}

	public void destroyed(){
		
		Explosion.explode(body.getPosition().add(parent.defaultTexture.getRegionWidth() / Constants.PPM / 2, parent.defaultTexture.getRegionHeight() / Constants.PPM / 2), 2);
		
		super.destroyed();
	}
}
