package co.uk.epicguru.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.helpers.CamZoom;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.map.building.BuildingManager;
import co.uk.epicguru.net.NetUtils;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.player.weapons.instances.C4;
import co.uk.epicguru.vehicles.VehiclesManager;

public final class PlayerController extends Entity{

	public PlayerRenderer renderer;
	public GunManager gunManager;
	
	
	/**
	 * Controls a player renderer using keyboard input.
	 */
	public PlayerController(Vector2 spawn){
		super("Player", 100);
		setup(spawn);
		this.gunManager = new GunManager(this);
		if(GunManager.guns.size() == 0)
			GunManager.reset();
		
		// Net create client
		NetUtils.createAndConnectClient();
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
		fixtureDef.filter.groupIndex = -123;
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
		
		if(Input.isKeyJustDown(Keys.E)){
			gunManager.setEquipped(gunManager.next());
			BuildingManager.stopPlacing();
		}
		if(Input.isKeyJustDown(Keys.Q)){
			gunManager.setEquipped(gunManager.previous());
			BuildingManager.stopPlacing();
		}
		if(Input.isKeyJustDown(Keys.F) && !BuildingManager.placing){
			gunManager.nextFiringMode();
		}		
		if(Input.isKeyJustDown(Keys.T) && !BuildingManager.placing){
			new C4(getBody().getPosition());
		}
		
		if(Input.isKeyJustDown(Keys.L) && !BuildingManager.placing && !isMounting()){
			VehiclesManager.find("Helicopter").getInstance(Input.getMouseWorldPos()).addRider(this);
		}
		if(Input.isKeyJustDown(Keys.ESCAPE))
			dismount();
		
		// CHANGE TODO
		if(gunManager.getEquipped() != null){
			switch (gunManager.getEquipped().firingModes[gunManager.getEquippedInstance().getSelectedFireMode()]) {
			case BURST:
				if(Input.clickLeft())
					gunManager.shoot();
				break;
			case FULL:
				if(Input.clickingLeft())
					gunManager.shoot();
				break;
			case SEMI:
				if(Input.clickLeft())
					gunManager.shoot();				
				break;	
			}
		}
		
		gunManager.crosshair.set(Input.getMouseWorldPos());
		gunManager.update(delta);
		
		if(Input.isKeyJustDown(Keys.B)){
			BuildingManager.placing = !BuildingManager.placing;
			
			if(!BuildingManager.placing){
				gunManager.setEquipped(0);
				Day100.assets.get("Audio/SFX/Guns/Cock" + MathUtils.random(1, 2) + ".mp3", Sound.class).play(0.3f, Day100.timeScale, 0);
				gunManager.gunChanged();
			}
		}
		
		if(BuildingManager.placing){
			gunManager.setEquipped(null);
		}
		
		if(gunManager.getEquipped() != null){		
			Day100.setCrosshairCursor();
		}else{			
			Day100.setDefaultCursor();
		}
		
		Day100.camera.zoom += (CamZoom.getZoom() - Day100.camera.zoom) * 0.5f;
	}
	
	public void render(Batch batch){

		this.renderer.position.set(body.getPosition().sub(45f / 32f / 2, 50f / 32f / 2));
		this.renderer.eyeOffset.set(MathUtils.clamp(.22f + body.getLinearVelocity().x / 20, 0, 0.4f), MathUtils.clamp(.6f + body.getLinearVelocity().y / 20, .1f, .8f));
		this.renderer.angle = getBody().getAngle() * MathUtils.radiansToDegrees;
		renderer.render(batch);
		
		// Guns
		gunManager.render(batch);
		
	}
	
	public void renderUI(Batch batch){
		gunManager.renderUI(batch);
	}
	
	public void destroyed(){
		super.destroyed();
		gunManager.dispose();
		Day100.setDefaultCursor();
		
		// Net
		NetUtils.shutDownClient();
	}
	
	/**
	 * Respawns the player at a given position.
	 */
	public static void respawn(Vector2 spawn){
		if(Day100.player == null || (Day100.player != null && Day100.player.isDead())){
			Day100.player = new PlayerController(spawn);
		}
	}
	
}
