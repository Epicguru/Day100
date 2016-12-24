package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.math.Vector2;

public class Triangle {

	public Vector2[] points;
	
	public Triangle(Vector2 a, Vector2 b, Vector2 c){
		points = new Vector2[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;
	}
	
	public Triangle set(Vector2 a, Vector2 b, Vector2 c){
		points[0] = a;
		points[1] = b;
		points[2] = c;
		return this;
	}
	
	public Vector2 getPointA(){
		return points[0];
	}
	
	public Vector2 getPointB(){
		return points[1];
	}
	
	public Vector2 getPointC(){
		return points[2];
	}
	
}
