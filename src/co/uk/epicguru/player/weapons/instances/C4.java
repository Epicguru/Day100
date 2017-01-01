package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.helpers.CamShake;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.particles.FlareLight;
import co.uk.epicguru.particles.ParticleExplosion;
import co.uk.epicguru.particles.ParticleTrailerExplosion;

public class C4 extends Entity {

	public static Sprite sprite;
	private static TextureRegion[] particles;
	
	public C4(Vector2 position) {
		super("C4", 20);
		if(sprite == null){
			sprite = new Sprite(Day100.gunsAtlas.findRegion("C4"));
			particles = new TextureRegion[]{
					Day100.particlesAtlas.findRegion("Spark1"),
					Day100.particlesAtlas.findRegion("Spark2"),
					Day100.particlesAtlas.findRegion("Smoke1"),
					Day100.particlesAtlas.findRegion("Smoke2"),
					Day100.particlesAtlas.findRegion("StandardParticle"),
					Day100.particlesAtlas.findRegion("StandardParticle2")
			};
		}
		
		createBody(position);
	}
	
	public void createBody(Vector2 position){
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(position);
		float angle = MathUtils.atan2(Input.getMouseWorldY() - position.y, Input.getMouseWorldX() - position.x);
		float velocity = 10;
		float xVel = MathUtils.cos(angle) * velocity;
		float yVel = MathUtils.sin(angle) * velocity;
		def.linearVelocity.set(xVel, yVel);
		PolygonShape shape = new PolygonShape();
		float width = sprite.getRegionWidth() / Constants.PPM;
		float height = sprite.getRegionHeight() / Constants.PPM;
		shape.setAsBox(width / 2, height / 2);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.filter.groupIndex = -123;
		setBody(Day100.map.world.createBody(def));
		getBody().createFixture(fixture);
	}
	
	public void update(float delta){
		if(Input.isKeyJustDown(Keys.Y)){
			// Explode
			explode();
		}
	}
	
	public void explode(){
		for(TextureRegion texture : particles){
			new ParticleExplosion(body.getPosition(), MathUtils.random(10, 20), MathUtils.random(30, 50), 20, 40, texture, MathUtils.random(76123786123l));
		}
		new ParticleTrailerExplosion(body.getPosition(), 20, 30, 15, 25, particles[2], MathUtils.random(76123786123l));
		new FlareLight(body.getPosition(), 0.5f, Color.ORANGE, 20);
		CamShake.shake(5);
		slay();
	}
	
	public void render(Batch batch){
		float width = sprite.getRegionWidth() / Constants.PPM;
		float height = sprite.getRegionHeight() / Constants.PPM;		
		sprite.setSize(width, height);
		sprite.setPosition(getBody().getPosition().x - width / 2, getBody().getPosition().y - height / 2);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setRotation(getBody().getAngle() * MathUtils.radiansToDegrees);
		sprite.draw(batch);
	}
}
