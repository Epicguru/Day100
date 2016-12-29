package co.uk.epicguru.helpers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class SpriteProjecter {

	private static Vector2 tempReturn = new Vector2();
	private static float[] topRight = new float[2];
	private static float[] bottomRight = new float[2];
	private static float[] topLeft = new float[2];
	private static float[] bottomLeft = new float[2];
	private static float[] interpolationLeft = new float[2];
	private static float[] interpolationRight = new float[2];
	private static float[] interpolationFinal = new float[2];

	public static Vector2 unprojectPosition(Sprite sprite, Vector2 localCoordinates){
		float[] verts = sprite.getVertices();
		
		Interpolation interpolation = Interpolation.linear;
		
		boolean toRight = false;
		if((sprite.getRotation() <= 90 && sprite.getRotation() >= 0) || (sprite.getRotation() >= -90 && sprite.getRotation() <= 0))
			toRight = true;
		
		if(toRight){ // To right
			topRight[0] = verts[SpriteBatch.X3];
			topRight[1] = verts[SpriteBatch.Y3];
			bottomRight[0] = verts[SpriteBatch.X4];
			bottomRight[1] = verts[SpriteBatch.Y4];
			
			topLeft[0] = verts[SpriteBatch.X2];
			topLeft[1] = verts[SpriteBatch.Y2];	
			bottomLeft[0] = verts[SpriteBatch.X1];
			bottomLeft[1] = verts[SpriteBatch.Y1];	
		}else{
			topRight[0] = verts[SpriteBatch.X4];
			topRight[1] = verts[SpriteBatch.Y4];
			bottomRight[0] = verts[SpriteBatch.X3];
			bottomRight[1] = verts[SpriteBatch.Y3];
			
			topLeft[0] = verts[SpriteBatch.X1];
			topLeft[1] = verts[SpriteBatch.Y1];	
			bottomLeft[0] = verts[SpriteBatch.X2];
			bottomLeft[1] = verts[SpriteBatch.Y2];	
		}
		
		interpolationLeft[0] = interpolation.apply(topLeft[0], bottomLeft[0], 1 - localCoordinates.y);
		interpolationLeft[1] = interpolation.apply(topLeft[1], bottomLeft[1], 1 - localCoordinates.y);
		interpolationRight[0] = interpolation.apply(topRight[0], bottomRight[0], 1 - localCoordinates.y);
		interpolationRight[1] = interpolation.apply(topRight[1], bottomRight[1], 1 - localCoordinates.y);
		interpolationFinal[0] = interpolation.apply(interpolationRight[0], interpolationLeft[0], 1 - localCoordinates.x);
		interpolationFinal[1] = interpolation.apply(interpolationRight[1], interpolationLeft[1], 1 - localCoordinates.x);
	
		tempReturn.set(interpolationFinal[0], interpolationFinal[1]);
		return tempReturn;
	}
	
}
