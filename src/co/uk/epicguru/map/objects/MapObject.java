package co.uk.epicguru.map.objects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.screens.Shaders;

public abstract class MapObject {
	
	public static final HashMap<String, MapObjectSupplier> objects = new HashMap<String, MapObjectSupplier>();
	public static final String TAG = "Map Object";
	
	private String name;
	public ShaderProgram shader;
	public boolean allowBlood;
	Color color;
	Vector2 position;
	Vector2 size;
	TextureRegion texture;
	public Body body;
	public boolean solid;
	public boolean light;
	
	public static void register(){
		
		objects.clear();
		
		objects.put("Default", new MapObjectSupplier() {
			public MapObject getObject(String name, Vector2 position, Vector2 size, Color color, TextureRegion texture, String mapName) {
				return new DefaultMapObject(name, position, size, texture, color);
			}
		});
		
		objects.put("Street Light", new MapObjectSupplier() {
			public MapObject getObject(String name, Vector2 position, Vector2 size, Color color, TextureRegion texture,
					String mapName) {
				return new StreetLight(mapName, position, size, texture, color);
			}
		});
		
		Log.info(TAG, "Registered " + objects.size() + " object suppliers: ");
		for(String key : objects.keySet()){
			Log.info(TAG, "   -" + key);			
		}
	}
	
	public MapObject(String name, Vector2 position, Vector2 size, TextureRegion texture, Color color){
		this.name = name;
		this.position = new Vector2();
		this.position.set(position);
		this.size = new Vector2();
		this.size.set(size);
		this.texture = texture;
		this.color = new Color();
		this.color.set(color);
	}
	
	/**
	 * Called when all the objects are loaded in the map, before first render.
	 */
	public void start(){
		
	}
	
	/**
	 * The body has been loaded, do stuff to it!
	 * Sets the position.
	 */
	public void bodyLoaded(){
		body.setTransform(position, 0);
		if(!solid){
			for(Fixture f : body.getFixtureList()){
				f.setSensor(true);
			}
		}
		if(!light){
			for(Fixture f : body.getFixtureList()){
				f.getFilterData().groupIndex = -123;
			}
		}
	}
	
	/**
	 * Called once per frame.
	 * @param deltaTime The delta time value.
	 */
	public void update(float deltaTime){
		
	}
	
	/**
	 * Gets the shader that is to be rendered with.
	 * Use this to set attributes if necessary, or to give specific shaders.
	 * @return The ShaderProgram to render with, or null for not shader.
	 */
	public ShaderProgram getShaderPreRender(){
		return shader;
	}
	
	/**
	 * Called once per frame to render this object.
	 * @param batch The Batch to draw with.
	 */
	public void render(Batch batch){
		
		// Set shader. Do not set back to original shader to save time.
		ShaderProgram shader = getShaderPreRender();
		if(shader == null){
			batch.setShader(null);
		}else{
			if(batch.getShader() != shader){
				batch.setShader(shader);	
				shader.begin();
			}
			try{
				shader.setUniformf("u_multi", Shaders.getTextureAtlasShaderMulti(texture));
				shader.setUniformf("u_sub", Shaders.getTextureAtlasShaderSubtract(texture));
				shader.setUniformf("u_time", Day100.map.timer);				
			}catch(Exception e){
				
			}
		}
		
		Color old = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, position.x, position.y, size.x, size.y);
		batch.setColor(old);
	}
	
	public abstract void load();
	
	public abstract void save();
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Vector2 getSize() {
		return size;
	}
	
	public TextureRegion getTexture() {
		return texture;
	}
}
