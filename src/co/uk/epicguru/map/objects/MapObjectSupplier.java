package co.uk.epicguru.map.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public interface MapObjectSupplier {
	
	public abstract MapObject getObject(String name, Vector2 position, Vector2 size, Color color, TextureRegion texture, String mapName);
	
}
