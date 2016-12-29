package co.uk.epicguru.helpers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.sound.SoundUtils;

public class GunShell extends Entity{

	private static Sound shellSound;
	private static TextureRegion texture;
	public float timer = 0;
	public float scale;
	
	public GunShell(Vector2 position, float angle, float scale) {
		super("Gun Shell", 1);
		timer = 30;
		setBody(createBody(position, angle));
		if(texture == null)
			texture = Day100.gunsAtlas.findRegion("Shell");
		this.scale = scale;
		if(shellSound == null) shellSound = Day100.assets.get("Audio/SFX/Misc/Shell.mp3");
	}
	
	private static Body createBody(Vector2 position, float angle){
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.type = BodyType.DynamicBody;
		def.angle = angle * MathUtils.degreesToRadians;
		def.angularDamping = 0.5f;
		def.linearVelocity.set(MathUtils.cosDeg(angle + MathUtils.random(-10, 10)) * -5, MathUtils.sinDeg(angle + MathUtils.random(-10, 10)) * -5);
		def.angularVelocity = def.linearVelocity.x * -5;
		CircleShape shape = new CircleShape();
		shape.setRadius(0.05f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.groupIndex = -123;
		fixtureDef.restitution = 0.5f;
		fixtureDef.friction = 1;
		Body b = Day100.map.world.createBody(def);
		b.createFixture(fixtureDef);
		shape.dispose();
		return b;
	}
	
	public void update(float delta){
		timer -= delta;
	}
	
	public boolean isDead(){
		return timer <= 0 || getHealth() <= 0;
	}
	
	private static Sprite sprite;
	public void render(Batch batch){
		float width = texture.getRegionWidth() / Constants.PPM;
		float height = texture.getRegionHeight() / Constants.PPM;
		if(sprite == null)
			sprite = new Sprite(texture);
		sprite.setPosition(body.getPosition().x - (width * scale) / 2, body.getPosition().y - (height * scale) / 2);
		sprite.setOrigin(scale * width / 2, scale * height / 2);
		sprite.setSize(width * scale, height * scale);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		sprite.draw(batch);
	}

	public void bodyContact(Body otherBody, Entity otherEntity, Contact contact) {
		float speed = Math.abs(getBody().getLinearVelocity().x) + Math.abs(getBody().getLinearVelocity().y);
		speed /= 10;
		if(speed > 0.2)
			SoundUtils.playSound(this, shellSound, speed / 2, 1.3f + speed / 5, 30);
	}
}
