package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;

public class ParticleInstance extends Entity{

	public static int particlesActive = 0;
	protected float timer;
	public TextureRegion texture;
	
	public ParticleInstance(Vector2 position, Vector2 velocity, float time, TextureRegion region){
		super("Particle", 1);
		this.timer = time;
		createBody(velocity, position);
		this.texture = region;
		particlesActive++;
	}
	
	public void createBody(Vector2 velocity, Vector2 position){
		if(body != null)
			return;
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(position);
		def.linearVelocity.set(velocity);
		CircleShape shape = new CircleShape();
		shape.setRadius(0.05f);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.filter.groupIndex = -123;
		setBody(Day100.map.world.createBody(def));
		getBody().createFixture(fixture);
	}
	
	public void update(float delta){
		this.timer -= delta;
	}
	
	public void render(Batch batch){
		float width = texture.getRegionWidth() / Constants.PPM;
		float height = texture.getRegionHeight() / Constants.PPM;
		batch.draw(texture, body.getPosition().x - width / 2, body.getPosition().y - height / 2, width, height);
	}
	
	public boolean isDead(){
		return timer <= 0 || getHealth() < 0;
	}
	
	public void destroyed(){
		super.destroyed();
		particlesActive--;
	}
	
}
