package co.uk.epicguru.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public final class PlayerRenderer {

	public Sprite player;
	public Sprite eyes;
	public Color color;
	public Color eyeColor;
	public Vector2 position;
	public Vector2 eyeOffset;
	public float angle;
	
	public void loadTexture(){
		player = new Sprite(Day100.playerAtlas.findRegion("TEMP"));
		eyes = new Sprite(Day100.playerAtlas.findRegion("Eyes"));
	}
	
	public void setDefaults(){
		color = Color.WHITE;
		eyeColor = Color.WHITE;
		position = new Vector2();
		eyeOffset = new Vector2(0.1f, 0.5f);
	}
	
	public void render(Batch batch){
		batch.setColor(color);
		float width = player.getRegionWidth() / Constants.PPM;
		float height = player.getRegionHeight() / Constants.PPM;
		player.setSize(width, height);
		player.setColor(color);
		player.setOrigin(width / 2, height / 2);
		player.setPosition(position.x, position.y);
		player.setRotation(angle);
		player.draw(batch);
		
		width = eyes.getRegionWidth() / Constants.PPM;
		height = eyes.getRegionHeight() / Constants.PPM;
		eyes.setSize(width, height);
		eyes.setColor(eyeColor);
		eyes.setOrigin(width / 2, height / 2);
		eyes.setPosition(position.x + eyeOffset.x, position.y + eyeOffset.y);
		eyes.setRotation(angle);
		eyes.draw(batch);
	}
	
}
