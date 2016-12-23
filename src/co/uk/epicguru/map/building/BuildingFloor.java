package co.uk.epicguru.map.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public abstract class BuildingFloor extends BuildingObject {

	public boolean placed = false;
	public BuildingObject connectedRight;
	public BuildingObject connectedLeft;
	
	/**
	 * A new floor thing. Yea.
	 * @param textures The texture variations. 0 = connected, 1 = disconnected, 2 = connected right, 3 = connected left
	 */
	public BuildingFloor(String name, float health, TextureRegion[] textures, Vector2 position) {
		super(name, health, textures[0]);
		createBody(position.add(0, texture.getRegionHeight() / Constants.PPM / 2));
	}
	
	public void createBody(Vector2 position){
		if(body != null)
			return;		
		BodyDef def = new BodyDef();
		def.position.set(position);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(texture.getRegionWidth() / Constants.PPM / 2, texture.getRegionHeight() / Constants.PPM / 2);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.filter.groupIndex = -123;
		setBody(Day100.map.world.createBody(def));
		getBody().createFixture(fixture);
	}
	
	public void updateTexture(){
		boolean left = connectedLeft != null;
		boolean right = connectedRight != null;
		
		if(left && right){
			texture = getTextures()[0];
		}else if(left && !right){
			texture = getTextures()[3];
		}else if(right && !left){
			texture = getTextures()[2];
		}else{
			// None
			texture = getTextures()[1];
		}
		
	}
	
	public abstract TextureRegion[] getTextures();
	
	public void ghostRender(Batch batch, Vector2 position, boolean valid){
		getBody().setTransform(position, 0);
		batch.setColor(valid ? ghostColor : ghostColorRed);
		batch.draw(texture, position.x - texture.getRegionWidth() / 2 / Constants.PPM, position.y, texture.getRegionWidth() / Constants.PPM, texture.getRegionHeight() / Constants.PPM);
		batch.setColor(Color.WHITE);
	}
	
	public void place(Vector2 position){
		getBody().setTransform(position.set(position.x, position.y + texture.getRegionHeight() / 2 / Constants.PPM), 0);
		placed = true;
	}
	
	public void update(float delta){
		if(connectedRight != null && connectedRight.isDead()){
			connectedRight = null;
		}
		if(connectedLeft != null && connectedLeft.isDead()){
			connectedLeft = null;
		}
		updateTexture();
	}
	
	public void render(Batch batch){
		if(placed)
			super.render(batch);
	}
	
	public void cancelPlace(){
		slay();
	}
}
