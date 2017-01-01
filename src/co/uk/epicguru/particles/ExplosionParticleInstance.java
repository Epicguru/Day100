package co.uk.epicguru.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.map.Entity;

public class ExplosionParticleInstance extends ParticleInstance{

	private static Color temp = new Color();
	public float maxTime;
	
	public ExplosionParticleInstance(Vector2 position, Vector2 velocity, float time, TextureRegion region) {
		super(position, velocity, time, region);
		maxTime = time;
	}

	@Override
	public void render(Batch batch) {
		float width = texture.getRegionWidth() / Constants.PPM;
		float height = texture.getRegionHeight() / Constants.PPM;
		temp.set(1,1,1, timer / maxTime);
		batch.setColor(temp);
		batch.draw(texture, body.getPosition().x - width / 2, body.getPosition().y - height / 2, width, height);
		batch.setColor(Color.WHITE);
	}

	public void bodyContact(Body otherBody, Entity otherEntity, Contact contact) {
		slay();
	}
}
