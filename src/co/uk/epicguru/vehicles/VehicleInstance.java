package co.uk.epicguru.vehicles;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import co.uk.epicguru.helpers.BodyEditorLoader;
import co.uk.epicguru.helpers.SpriteProjecter;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.Entity;

public abstract class VehicleInstance extends Entity {

	public static ArrayList<Entity> ridersBin = new ArrayList<Entity>();
	public VehicleDefinition parent;
	public ArrayList<Entity> riders = new ArrayList<Entity>();
	
	public VehicleInstance(String name, float startingHealth, VehicleDefinition parent) {
		super(name, startingHealth);
		this.parent = parent;
	}
	
	public TextureRegion getLocalTexture(String name){
		Log.info(parent.name, "Getting texture : " + parent.name + "/" + name);
		return Day100.vehiclesAtlas.findRegion(parent.name + "/" + name);
	}
	
	/**
	 * Safe to ignore.
	 */
	public Body getBasicBody(Vector2 position, float angle, Vector2 linearVelocity, float angularVelocity){
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.angle = angle;
		def.type = BodyType.DynamicBody;
		def.linearVelocity.set(0, 0);
		def.angularVelocity = 0;
		def.fixedRotation = false;
		return Day100.map.world.createBody(def);
	}
	
	/**
	 * Used in the method {@link #getBodyFromData(String, Vector2, float, Vector2, float, int, int)} when loading the body from disk.
	 * All of the properties of the returned fixture are applied to the final body.
	 * Override this to set properties such as friction and the such.
	 * @return The Fixture to base the body off.
	 */
	public FixtureDef getBodyFixture(){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 50;
		return fixtureDef;
	}
	
	/**
	 * Loads a box2d body from the external editor.
	 * @param rigidName The name of the rigidbody, as specified in the editor.
	 * @param position The position to spawn the body at.
	 * @param angle The angle to spawn the body at.
	 * @param linearVelocity The linear velocity to apply.
	 * @param angularVelocity The angular velocity to apply.
	 * @param width The width, in PIXELS.
	 * @param height The height, in PIXELS.
	 * @return The Box2D body.
	 */
	public Body getBodyFromData(String rigidName, Vector2 position, float angle, Vector2 linearVelocity, float angularVelocity, int width, int height){
		
		float scaleX = (float)width / Constants.PPM;
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("Bodies/Vehicles/" + parent.name + "/Data.json"));
		Body body = getBasicBody(position, angle, linearVelocity, angularVelocity);
		loader.attachFixture(body, rigidName, getBodyFixture(), 1, scaleX, scaleX);
		return body;
	}

	public boolean addRider(Entity rider){
		if(riders.size() < parent.maxRiders){
			if(!riders.contains(rider)){
				riders.add(rider);
				rider.mount = this;
				Log.info(rider.getName(), "Now riding a " + getName() + " mount - " + (rider.mount == this));
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean hasRiders(){
		return !riders.isEmpty();
	}
	
	public boolean removeRider(Entity oldRider){
		if(!riders.contains(oldRider)){
			return false;
		}else{
			riders.remove(oldRider);
			oldRider.mount = null;
			if(oldRider.getBody() != null){
				oldRider.getBody().setActive(true);
				if(this.getBody() != null)
					oldRider.getBody().setLinearVelocity(this.getBody().getLinearVelocity());
			}
			return true;
		}
	}
	
	public void update(float delta){
		for(Entity e : riders){
			if(e.isDead() || e.getBody() == null || e.mount != this){
				e.mount = null;
				ridersBin.add(e);
				Log.info(this.getName(), "Removing rider " + e.getName() + ". -Dead:" + e.isDead() + " -Null Body:" + (e.getBody() == null) + " -Mount Not This:" + (e.mount != this));
			}
		}
		for(Entity e : ridersBin){
			riders.remove(e);
		}
		ridersBin.clear();	
	}
	
	public void updateRiders(Sprite sprite){
		int index = 0;
		for(Entity e : riders){
			Vector2 position = parent.getPosForRider(index++);
			Vector2 finalPos = SpriteProjecter.unprojectPosition(sprite, position);
			e.getBody().setTransform(finalPos.x, finalPos.y, MathUtils.degreesToRadians * (sprite.getRotation()));
		}
	}


	public void destroyed() {
		for(Entity e : riders){
			ridersBin.add(e);		
		}
		for(Entity e : ridersBin){
			removeRider(e);
		}
		ridersBin.clear();
		super.destroyed();
	}
	
}
