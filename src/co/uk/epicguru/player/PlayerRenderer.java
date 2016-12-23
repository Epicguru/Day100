package co.uk.epicguru.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public final class PlayerRenderer {

	public TextureRegion player;
	public TextureRegion eyes;
	public Color color;
	public Color eyeColor;
	public Vector2 position;
	public Vector2 eyeOffset;
	
	public void loadTexture(){
		player = Day100.playerAtlas.findRegion("TEMP");
		eyes = Day100.playerAtlas.findRegion("Eyes");
	}
	
	public void setDefaults(){
		color = Color.WHITE;
		eyeColor = Color.WHITE;
		position = new Vector2();
		eyeOffset = new Vector2(0.1f, 0.5f);
	}
	
	public void render(Batch batch){
		batch.setColor(color);
		batch.draw(player, position.x, position.y, player.getRegionWidth() / Constants.PPM, player.getRegionHeight() / Constants.PPM);
		batch.setColor(eyeColor);
		batch.draw(eyes, position.x + eyeOffset.x, position.y + eyeOffset.y, eyes.getRegionWidth() / Constants.PPM, eyes.getRegionHeight() / Constants.PPM);
		batch.setColor(Color.WHITE);
	}
	
}
