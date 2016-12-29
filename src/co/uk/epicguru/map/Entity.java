package co.uk.epicguru.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import co.uk.epicguru.main.Day100;

public class Entity {

	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	private static ArrayList<Entity> entitiesBin = new ArrayList<Entity>();
	private static ArrayList<Entity> entitiesPending = new ArrayList<Entity>();
	
	private String name;
	private float startHealth;
	/**
	 * NULL WARNING! See {@link #setBody(Body)} for special body setup.
	 */
	public Body body;
	float health;
	
	/**
	 * Creates a new entity for a Day100 world. An entity auto updates and renders until the {@link #isDead()} condition is met.
	 */
	public Entity(String name, float startingHealth){
		this(name, startingHealth, null);
	}
	
	/**
	 * Creates a new entity for a Day100 world. An entity auto updates and renders until the {@link #isDead()} condition is met.
	 */
	public Entity(String name, float startingHealth, Body body){
		this.startHealth = startingHealth;
		this.health = startingHealth;
		this.name = name;
		setBody(body);
		entitiesPending.add(this);
	}
	
	/**
	 * Gets the name of this entity. It is not specifically unique or any kind of identifier.
	 * It is just a name!
	 * @return The name of this entity as expressed in the constructor.
	 */
	public final String getName(){
		return name;
	}
	
	/**
	 * Sets the box2D body for this entity but also sets the user object to this entity, allowing for collision detection 
	 * and taking damage from environment.
	 * @param body The new body to set to.
	 */
	public void setBody(Body body){
		this.body = body;
		if(this.body != null){
			this.body.setUserData(this);			
		}
	}
	
	/**
	 * Gets the amount of health that this entity originally started with.
	 * @return The health use to start this entity.
	 */
	public float getStartHealth(){
		return startHealth;
	}
	
	/**
	 * Gets the body of this entity. May be null.
	 * @return The Box2D body.
	 */
	public Body getBody(){
		return body;
	}
	
	/**
	 * Applies damage to the entity by directly lowering health. Also calls {@link #isDead()}.
	 * @param damage The abstract damage to apply. May result in the entity being considered dead or having a negative health value.
	 * @param data The OPTIONAL data about the damage being dealt. Includes data such as the dealer of the damage and the angle. THIS MAY BE NULL, OR THE DATA MAY BE INCOMPLETE OR NULL!
	 */
	public void takeDamage(float damage, DamageData data){
		this.health -= damage;
		isDead();
	}
	
	/**
	 * Gets the abstract health of this entity. Has no minimum or limit.
	 * @return The health float.
	 */
	public float getHealth(){
		return health;
	}
	
	/**
	 * Updates the dead status. Returns true if the entity has less than or exactly 0 health.
	 * Override this to stop this entity from being removed or removing upon specific event.
	 * You can always
	 * @return Is this entity considered dead.
	 */
	public boolean isDead(){
		return health <= 0;
	}
	
	/**
	 * Sets health to 0. By default this kills and removes an entity. Also calls {@link #isDead()}.
	 * However custom entities may survive by overriding {@link #isDead()}.
	 */
	public void slay(){
		this.health = 0;
		isDead();
	}
	
	/**
	 * Called when the entity is destroyed. By default destroys box2D body if not null.
	 * If entity mechanics are untouched, then this should call if health is lower than 0 or if the world is quit.
	 */
	public void destroyed(){
		if(body != null){
			Day100.map.world.destroyBody(body);
			body = null;
		}
	}
	
	/**
	 * Sends this entity to the back of the array to be rendered behind all others.
	 */
	public void toBack(){
		if(!entities.contains(this))
			return;
		
		entities.remove(this);
		entities.add(0, this);
	}
	
	/**
	 * Sends this entity to the front of the array to be rendered in front of all others.
	 */
	public void toFront(){
		if(!entities.contains(this))
			return;
		
		entities.remove(this);
		entities.add(this);
	}
	
	/**
	 * Gets the name of the entity, as well as it's health and max health.
	 */
	public String toString(){
		return getName() + " : " + body == null ? "" : body.getPosition() + " (" + health + "/" + startHealth + "hp)";
	}
	
	/**
	 * Updates the entity, called once per frame, before render. By default does nothing.
	 * @param delta The delta time.
	 */
	public void update(float delta){
		
	}
	
	/**
	 * Use to render your entity. By default does nothing.
	 * @param batch The Batch to draw with.
	 */
	public void render(Batch batch){
		
	}
	
	/**
	 * Use to render any UI elements necessary. By default does nothing.
	 * @param batch The Batch to draw with.
	 */
	public void renderUI(Batch batch){
		
	}
	
	/**
	 * Called when the {@link #body} comes into contact with another body that IS OWNED BY AN ENTITY.
	 * By default does nothing.
	 * @param otherBody The other Box2D body that was part of the collision.
	 * @param otherEntity The Entity who owns the other body.
	 * @param contact The contact data.
	 */
	public void bodyContact(Body otherBody, Entity otherEntity, Contact contact){
		
	}
	
	/*
	 * STATIC METHODS
	 */
	
	/**
	 * Clears all entities.
	 */
	public static void clearAllEntities(){
		for(Entity e : entities){
			e.destroyed();
		}
		entities.clear();
		for(Entity e : entitiesBin){
			e.destroyed();
		}
		entitiesBin.clear();
		entitiesPending.clear();
	}
	
	/**
	 * Updates all entities with the given delta time.
	 */
	public static void updateAll(float delta){
		for(Entity e : entities){
			e.update(delta);
			if(e.isDead()){
				entitiesBin.add(e);
			}
		}
		for(Entity e : entitiesPending){
			entities.add(e);
			e.update(delta);
		}	
		entitiesPending.clear();
		for(Entity e : entitiesBin){
			e.destroyed();
			entities.remove(e);
		}		
		entitiesBin.clear();
	}
	
	/**
	 * Renders all entities.
	 * @param batch The Batch to render with.
	 */
	public static void renderAll(Batch batch){
		for(Entity e : entities){
			e.render(batch);
		}
		for(Entity e : entitiesPending){
			entities.add(e);
			e.render(batch);
		}	
		entitiesPending.clear();
		for(Entity e : entitiesBin){
			e.destroyed();
			entities.remove(e);
		}		
		entitiesBin.clear();
	}
	
	/**
	 * Renders the UI for all entities.
	 * @param batch The Batch to render with.
	 */
	public static void renderAllUI(Batch batch){
		for(Entity e : entities){
			e.renderUI(batch);
		}
		for(Entity e : entitiesPending){
			entities.add(e);
			e.renderUI(batch);
		}	
		entitiesPending.clear();
		for(Entity e : entitiesBin){
			e.destroyed();
			entities.remove(e);
		}		
		entitiesBin.clear();
	}
	
}
