package co.uk.epicguru.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import co.uk.epicguru.main.Day100;

public final class Input {
	
	static Vector2 tempVec = new Vector2();
	static Vector3 tempVec3 = new Vector3();
	static boolean oldMousePressed;
	static boolean mousePressed;
	static boolean rightPressed;
	static boolean oldRightPressed;
	
	public static void update(){
		
		oldMousePressed = mousePressed;
		oldRightPressed = rightPressed;
		mousePressed = Gdx.input.isButtonPressed(Buttons.LEFT);
		rightPressed = Gdx.input.isButtonPressed(Buttons.RIGHT);
		
	}
	
	public static int getScreenWidth(){
		return Gdx.graphics.getWidth();
	}
	
	public static int getScreenHeight(){
		return Gdx.graphics.getHeight();
	}
	
	public static int random(int start, int end){
		
		if(start < end)
			return MathUtils.random(start, end);
		else
			return MathUtils.random(end, start);
	}
	
	public static boolean isKeyDown(int key){
		return Gdx.input.isKeyPressed(key);
	}
	
	public static boolean isKeyJustDown(int key){
		return Gdx.input.isKeyJustPressed(key);
	}
	
	public static boolean clickingLeft(){
		return mousePressed;
	}
	
	public static boolean clickLeft(){
		return mousePressed && !oldMousePressed;
	}
	
	public static boolean clickingRight(){
		return rightPressed;
	}
	
	public static boolean clickRight(){
		return rightPressed && !oldRightPressed;
	}
	
	public static int getMouseX(){
		return Gdx.input.getX();
	}
	
	public static int getMouseY(){
		return Gdx.input.getY();
	}
	
	public static Vector2 getMousePos(){
		return tempVec.set(getMouseX(), getMouseY());
	}
	
	public static Vector2 getMousePosYFlip(){
		return tempVec.set(getMouseX(), Gdx.graphics.getHeight() - getMouseY());
	}
	
	public static float getMouseWorldX(){
		
		tempVec3.set(getMousePos(), 1);
		Day100.camera.unproject(tempVec3);
		return tempVec3.x;
		
	}
	
	public static float getMouseWorldY(){
		
		tempVec3.set(getMousePos(), 1);
		Day100.camera.unproject(tempVec3);
		return tempVec3.y;
		
	}
	
	public static Vector2 getMouseWorldPos(){
		tempVec.set(getMouseWorldX(), getMouseWorldY());
		return tempVec;
	}
	
}
