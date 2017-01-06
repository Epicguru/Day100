package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import co.uk.epicguru.helpers.Explosion;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;

public class C4 extends Entity {

	public static Sprite sprite;
	
	public C4(Vector2 position) {
		super("C4", 20);
		if(sprite == null){
			sprite = new Sprite(Day100.gunsAtlas.findRegion("C4"));			
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
		Explosion.explode(body.getPosition(), 1);
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
