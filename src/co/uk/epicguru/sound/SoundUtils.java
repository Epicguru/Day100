package co.uk.epicguru.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;

public final class SoundUtils {

	public static Interpolation interpolation = Interpolation.pow3In;
	private static float DEFAULT = 0.5f;
	private static float MIN_PAN = 0.2f;
	private static float MIN_PITCH = 0.6f;
	
	public static float getVolume(Vector2 position, float maxDst){
		if(Day100.player == null || Day100.player.getBody() == null)
			return DEFAULT;
		
		float dst = Day100.player.getBody().getPosition().dst(position);
		return MathUtils.clamp(1 - interpolation.apply(dst / maxDst), 0, 1);
	}
	
	public float getVolume(Entity e, float maxDst){
		if(e.getBody() == null)
			return DEFAULT;
		return getVolume(e.getBody().getPosition(), maxDst);
	}
	
	public static float getPan(float x, float maxDst){
		
		if(Day100.player == null || Day100.player.getBody() == null)
			return DEFAULT;
		
		float dst = Day100.player.getBody().getPosition().x - x;
		boolean toRight = Day100.player.getBody().getPosition().x < x;
		if(toRight){
			float pan = Math.abs(dst) / maxDst;
			if(pan > 1 - MIN_PAN)
				pan -= MIN_PAN;
			return MathUtils.clamp(pan, -1, 1);
		}else{
			float pan = -(Math.abs(dst) / maxDst);
			if(pan < -1 + MIN_PAN)
				pan += MIN_PAN;
			return MathUtils.clamp(pan, -1, 1);
		}
	}
	
	public static float getPan(Vector2 position, float maxDst){
		return getPan(position.x, maxDst);
	}
	
	public static float getPan(Entity e, float maxDst){
		if(e.getBody() == null)
			return DEFAULT;
		return getPan(e.getBody().getPosition(), maxDst);
	}

	public static float getPitch(Vector2 position, float maxDst){
		if(Day100.player == null || Day100.player.getBody() == null)
			return DEFAULT;
		
		float d = Day100.player.getBody().getPosition().dst(position);
		float p = 1 - interpolation.apply(d / maxDst);
		float pitch = p;
		if(pitch < MIN_PITCH)
			pitch = MIN_PITCH;
		return pitch * Day100.timeScale;
	}
	
	public static float getPitch(Entity e, float maxDst){
		if(e.getBody() == null)
			return DEFAULT;
		return getPitch(e.getBody().getPosition(), maxDst);
	}

	public static void playSound(Vector2 position, Sound sound, float baseVolume, float basePitch, float maxDst){
		sound.play(getVolume(position, maxDst) * baseVolume, getPitch(position, maxDst) * basePitch, getPan(position, maxDst));
	}
	
	public static void playSound(Entity e, Sound sound, float baseVolume, float basePitch, float maxDst){
		if(e.getBody() == null)
			return;
		playSound(e.getBody().getPosition(), sound, baseVolume, basePitch, maxDst);
	}
	
}
