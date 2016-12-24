package co.uk.epicguru.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.DamageData;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.FiringMode;
import co.uk.epicguru.player.weapons.GunManager;

public class BotController extends Entity{

	public PlayerRenderer renderer;
	public Body body;
	public GunManager gun;
	
	/**
	 * Controls a player renderer using a simple AI.
	 */
	public BotController(Vector2 spawn, boolean gunner){
		super("Bot", 100);
		setup(spawn);
		if(gunner){
			gun = new GunManager(this);
			gun.equipped = GunManager.guns.get(MathUtils.random(GunManager.guns.size() - 1));
			gun.gunChanged();
		}
	}
	/**
	 * Creates body.
	 */
	public void setup(Vector2 spawn){
		createBody(spawn);
		renderer = new PlayerRenderer();
		renderer.loadTexture();
		renderer.setDefaults();
	}
	
	/**
	 * Creates a box2d body in Day100.map.world
	 */
	public void createBody(Vector2 spawn){
		BodyDef def = new BodyDef();
		def.position.set(spawn);
		def.bullet = true;
		def.type = BodyType.DynamicBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(45f / 32f / 2, 50f / 32f / 2);
		this.body = Day100.map.world.createBody(def);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 1.5f;
		body.createFixture(fixtureDef);
		shape.dispose();
		setBody(body);
	}
	
	public void update(float delta){
		float acceleration = 30;
		
		if(!Day100.player.isDead()){
			if(Day100.player.body.getPosition().x > body.getPosition().x){
				body.applyForceToCenter(acceleration, 0, true);
			}else{
				body.applyForceToCenter(-acceleration, 0, true);			
			}
			
			float topSpeedX = 5;
			if(body.getLinearVelocity().x > topSpeedX){
				body.setLinearVelocity(topSpeedX, body.getLinearVelocity().y);
			}
			if(body.getLinearVelocity().x < -topSpeedX){
				body.setLinearVelocity(-topSpeedX, body.getLinearVelocity().y);
			}
			
			gun.crosshair.set(Day100.player.getBody().getPosition().x, Day100.player.getBody().getPosition().y);
			if(gun.getOffset() < 5)
				gun.shoot();
		}
		
		gun.equipped.setFiringMode(FiringMode.FULL);
		
		if(gun != null) gun.update(delta);
	}
	
	public void render(Batch batch){
		this.renderer.position.set(body.getPosition().sub(45f / 32f / 2, 50f / 32f / 2));
		this.renderer.eyeOffset.set(MathUtils.clamp(.22f + body.getLinearVelocity().x / 20, 0, 0.4f), MathUtils.clamp(.6f + body.getLinearVelocity().y / 20, .1f, .8f));
		renderer.render(batch);
		if(gun != null) gun.render(batch);
	}

	public void takeDamage(float damage, DamageData data) {
		super.takeDamage(damage, data);
		if(data == null)
			return;
		
		new MapBlood(body.getPosition(), data.angle);
		
		if(isDead()){
			Log.info(getName(), "Killed by " + data.dealer == null ? "UNKNOWN" : data.dealer.toString());
		}
	}
	
}
