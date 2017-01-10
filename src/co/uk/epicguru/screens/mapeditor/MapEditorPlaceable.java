package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.IO.JLineWriter;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.screens.instances.MapEditor;

public class MapEditorPlaceable {

	public static Color WHITE = new Color(1, 1, 1, 1);
	public MapEditorObject parent;
	public Vector2 position;
	public Vector2 size;
	public Color color;
	public String shader;
	public boolean solid;
	public boolean light;
	public String bodyName;
	// Physics data, loaded from parent and editable
	// Colour
	// Attached lights
	// Custom override system
	
	public MapEditorPlaceable(MapEditorObject parent, Vector2 position){
		this.parent = parent;	
		this.size = new Vector2(parent.texture.getRegionWidth() / Constants.PPM, parent.texture.getRegionHeight() / Constants.PPM);
		this.position = position;
		this.color = new Color(parent.setupColour);
		this.shader = parent.shader;
	}
	
	public MapEditorPlaceable(JLineReader reader){
		this.parent = getObject(reader.readString("ID", "ERROR"));
		if(this.parent == null)	{	
			Log.error("TODO", "RETURNED BECAUSE STRING WAS "+ reader.readString("ID", "ERROR"));
			return;
		}
		
		this.color = reader.readColor("Colour", WHITE);
		this.position = reader.readVector2("Position", Vector2.Zero);
		this.size = reader.readVector2("Size", Vector2.Zero);
		this.shader = reader.readString("Shader", "Default");
	}
	
	public boolean verify(){
		return this.parent != null;
	}
	
	public static MapEditorObject getObject(final String ID){
		for(MapEditorObject object : MapEditor.loadedObjects){
			if(object.ID.equals(ID))
				return object;
		}
		return null;
	}
	
	public void setParent(MapEditorObject newParent){
		// If the size is unchanged, then resize to the new size. Otherwise retain size and do not rescale.
		if(this.size.x == parent.texture.getRegionWidth() && this.size.y == parent.texture.getRegionHeight()){
			this.size.set(newParent.texture.getRegionWidth() / Constants.PPM, newParent.texture.getRegionHeight() / Constants.PPM);			
		}
		
		this.parent = newParent;
	}
	
	public void render(Batch batch){
		Color old = batch.getColor();
		batch.setColor(color);
		batch.draw(parent.texture, position.x, position.y, size.x, size.y);
		batch.setColor(old);
	}

	public void save(JLineWriter writer) {	
		// TODO Save texture name.
		writer.write("ID", parent.ID);
		writer.write("Position", position);
		writer.write("Size", size);
		if(!color.equals(WHITE))
			writer.write("Colour", color);	
		if(!parent.supplier.equals("Default"))
			writer.write("Supplier", parent.supplier);
		if(!shader.equals("Default"))
			writer.write("Shader", shader);
		if(!parent.body.equals("None"))
			writer.write("Body", parent.body);
		if(solid == false)
			writer.write("Solid", false);	
		if(light == false)
			writer.write("Light", false);	
		
	}
}
