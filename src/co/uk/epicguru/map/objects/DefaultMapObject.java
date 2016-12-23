package co.uk.epicguru.map.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class DefaultMapObject extends MapObject {

	/**
	 * A default map object implementation that does nothing more than the MapObject class does.
	 * @param name The name of the object being placed. This will be the same for all objects of the same type unless explicitly changed/
	 * @param position The position in meters of the object in the map.
	 * @param size The size in meters of the object.
	 * @param texture The TextureRegion that this object uses.
	 */
	public DefaultMapObject(String name, Vector2 position, Vector2 size, TextureRegion texture, Color color) {
		super(name, position, size, texture, color);
	}

	@Override
	public void load() {
		
	}

	@Override
	public void save() {
		
	}

}
