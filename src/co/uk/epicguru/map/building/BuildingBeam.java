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

public abstract class BuildingBeam extends BuildingObject {
	
	public boolean placed = false;
	
	public BuildingBeam(String name, float health, TextureRegion texture, Vector2 position) {
		super(name, health, texture);
		createBody(position);
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
		fixture.isSensor = true;
		setBody(Day100.map.world.createBody(def));
		getBody().createFixture(fixture);
	}
	
	public void ghostRender(Batch batch, Vector2 position, boolean valid){
		getBody().setTransform(position.set(position.x, position.y + texture.getRegionHeight() / 2 / Constants.PPM), 0);
		batch.setColor(valid ? ghostColor : ghostColorRed);
		batch.draw(texture, position.x - texture.getRegionWidth() / 2 / Constants.PPM, position.y - texture.getRegionHeight() / 2 / Constants.PPM, texture.getRegionWidth() / Constants.PPM, texture.getRegionHeight() / Constants.PPM);
		batch.setColor(Color.WHITE);
	}
	
	public void place(Vector2 position){
		getBody().setTransform(position.set(position.x, position.y + texture.getRegionHeight() / 2 / Constants.PPM), 0);
		placed = true;
		getBody().getFixtureList().get(0).setSensor(false);
	}
	
	public void update(float delta){

	}
	
	public void render(Batch batch){
		if(placed)
			super.render(batch);
	}
	
	public void cancelPlace(){
		slay();
	}	
}
