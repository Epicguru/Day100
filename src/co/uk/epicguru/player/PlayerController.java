package co.uk.epicguru.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.map.building.BuildingManager;
import co.uk.epicguru.player.weapons.GunManager;

public final class PlayerController extends Entity{

	public PlayerRenderer renderer;
	public Body body;
	//private boolean TEMP_TIME_FLIP = false;
	
	
	/**
	 * Controls a player renderer using keyboard input.
	 */
	public PlayerController(Vector2 spawn){
		super("Player", 100);
		setup(spawn);
		GunManager.reset();
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
		fixtureDef.filter.groupIndex = -321;
		body.createFixture(fixtureDef);
		shape.dispose();
		setBody(body);
	}
	
	public void update(float delta){
		
		float acceleration = 30;
		if(Input.isKeyJustDown(Keys.W)){
			body.setLinearVelocity(body.getLinearVelocity().x, 5);
		}
		if(Input.isKeyDown(Keys.D)){
			body.applyForceToCenter(acceleration, 0, true);
		}
		if(Input.isKeyDown(Keys.A)){
			body.applyForceToCenter(-acceleration, 0, true);
		}
		float topSpeedX = 10;
		if(body.getLinearVelocity().x > topSpeedX){
			body.setLinearVelocity(topSpeedX, body.getLinearVelocity().y);
		}
		if(body.getLinearVelocity().x < -topSpeedX){
			body.setLinearVelocity(-topSpeedX, body.getLinearVelocity().y);
		}
		
		GunManager.update(delta);
		if(Input.isKeyJustDown(Keys.E)){
			GunManager.equipped = GunManager.next();
			GunManager.gunChanged();
			Day100.assets.get("Audio/SFX/Guns/Cock" + MathUtils.random(1, 2) + ".mp3", Sound.class).play(0.3f, Day100.timeScale, 0);
			BuildingManager.stopPlacing();
		}
		if(Input.isKeyJustDown(Keys.Q)){
			GunManager.equipped = GunManager.previous();
			GunManager.gunChanged();
			Day100.assets.get("Audio/SFX/Guns/Cock" + MathUtils.random(1, 2) + ".mp3", Sound.class).play(0.3f, Day100.timeScale, 0);
			BuildingManager.stopPlacing();
		}
		if(Input.isKeyJustDown(Keys.F) && !BuildingManager.placing){
			GunManager.nextFiringMode();
		}
		
		// Slow-Mo test
//		if(Input.isKeyJustDown(Keys.P)){
//			TEMP_TIME_FLIP = !TEMP_TIME_FLIP;
//			if(TEMP_TIME_FLIP)
//				Day100.targetTimeScale = 0.1f;
//			else
//				Day100.targetTimeScale = 1f;
//		}
		
		if(Input.isKeyJustDown(Keys.B)){
			BuildingManager.placing = !BuildingManager.placing;
			
			if(!BuildingManager.placing){
				GunManager.equipped = GunManager.guns.get(0);
				Day100.assets.get("Audio/SFX/Guns/Cock" + MathUtils.random(1, 2) + ".mp3", Sound.class).play(0.3f, Day100.timeScale, 0);
				GunManager.gunChanged();
			}
		}
		
		if(BuildingManager.placing){
			GunManager.equipped = null;
		}
	}
	
	public void render(Batch batch){

		this.renderer.position.set(body.getPosition().sub(45f / 32f / 2, 50f / 32f / 2));
		this.renderer.eyeOffset.set(MathUtils.clamp(.22f + body.getLinearVelocity().x / 20, 0, 0.4f), MathUtils.clamp(.6f + body.getLinearVelocity().y / 20, .1f, .8f));
		renderer.render(batch);
		
		// Guns
		GunManager.render(batch);
		
	}
	
	public void renderUI(Batch batch){
		GunManager.renderUI(batch);
	}
	
}
