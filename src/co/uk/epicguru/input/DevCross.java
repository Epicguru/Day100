package co.uk.epicguru.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;

public final class DevCross {

	private static TextureRegion texture;
	
	/**
	 * Draws a new cross. Note that it is rendered with the origin as the bottom left corner.
	 */
	public static void draw(float x, float y, float width, float height, Color color){
		
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		
		Day100.batch.setColor(color);
		Day100.batch.draw(texture, x, y, width, height);
		Day100.batch.setColor(Color.WHITE);
	}
	
	/**
	 * Draws the cross, with the default width and height IN METERS.
	 */
	public static void draw(float x, float y){
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		draw(x, y, (float)texture.getRegionWidth() / Constants.PPM / 2f, (float)texture.getRegionHeight() / Constants.PPM / 2f, Color.RED);
	}
	
	/**
	 * Draws the cross, with the default width and height IN METERS.
	 * Centres the cross on the coords.
	 */
	public static void drawCentred(float x, float y){
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		float width = (float)texture.getRegionWidth() / Constants.PPM / 2f;
		float height = (float)texture.getRegionHeight() / Constants.PPM / 2f;
		draw(x - width / 2, y - height / 2, width, height, Color.RED);
	}
	
	/**
	 * Draws the cross, with the default width and height IN METERS.
	 * Centres the cross on the coords.
	 */
	public static void drawCentred(float x, float y, float scale){
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		float width = (float)texture.getRegionWidth() / Constants.PPM / 2f * scale;
		float height = (float)texture.getRegionHeight() / Constants.PPM / 2f * scale;
		draw(x - width / 2, y - height / 2, width, height, Color.RED);
	}
	
	/**
	 * Draws the cross, with the default width and height IN METERS.
	 * Centres the cross on the coords.
	 */
	public static void drawCentred(float x, float y, float scale, Color color){
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		float width = (float)texture.getRegionWidth() / Constants.PPM / 2f * scale;
		float height = (float)texture.getRegionHeight() / Constants.PPM / 2f * scale;
		draw(x - width / 2, y - height / 2, width, height, color);
	}
	
	/**
	 * Draws the cross, for use when rendering UI.
	 */
	public static void drawUI(float x, float y){
		if(texture == null){
			texture = Day100.UIAtlas.findRegion("DevCross");
		}
		draw(x, y, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2, Color.RED);
	}
	
}
