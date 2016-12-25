package co.uk.epicguru.map.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.map.DamageData;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.particles.ParticleExplosion;

public abstract class BuildingObject extends Entity {

	public static Color ghostColor = new Color(0, 1, 0, 0.4f);
	public static Color ghostColorRed = new Color(1, 0, 0, 0.4f);
	public TextureRegion texture;
	
	public BuildingObject(String name, float health, TextureRegion texture) {
		super(name, health);
		this.texture = texture;
		BuildingManager.placed.add(this);
	}
	
	public void render(Batch batch){
		batch.draw(texture, body.getPosition().x - texture.getRegionWidth() / Constants.PPM / 2, body.getPosition().y - texture.getRegionHeight() / Constants.PPM / 2, texture.getRegionWidth() / Constants.PPM, texture.getRegionHeight() / Constants.PPM);
	}
	
	@Override
	public void takeDamage(float damage, DamageData data) {		
		super.takeDamage(damage, data);
		int particleSize = 5; // In pixels
		int x = this.texture.getRegionX();
		int y = this.texture.getRegionY();
		int width = this.texture.getRegionWidth();
		int height = this.texture.getRegionHeight();
		this.texture.setRegion(MathUtils.random(x, x + width), MathUtils.random(y, y + height), particleSize, particleSize);
		new ParticleExplosion(data.hitPoint, data.angle, data.angle + MathUtils.random(-50, 50), 5, 12, 2, 10, 1, 3, new TextureRegion(this.texture), MathUtils.random(987123897123L));
		this.texture.setRegion(x, y, width, height);
	}

	public void destroyed(){
		super.destroyed();
		BuildingManager.placed.remove(this);
	}

}
