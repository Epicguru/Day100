package co.uk.epicguru.sound;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.helpers.AssetDataTag;
import co.uk.epicguru.helpers.AssetHelper;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.Entity;

public final class SoundUtils {

	private static final String TAG = "Sound Utils";
	public static Interpolation interpolation = Interpolation.pow3In;
	private static float MIN_PAN = 0.2f;
	private static float MIN_PITCH = 0.6f;
	private static Vector2 TEMP = new Vector2();
	
	private static float getAdditionalDst(){
		float add = 10 * Day100.camera.zoom;
		if(add < 0)
			add = 0;
		return add;
	}

	
	public static float getVolume(Vector2 position, float maxDst){
		TEMP.set(Day100.camera.position.x, Day100.camera.position.y);
		
		float dst = TEMP.dst(position) + getAdditionalDst();
		return MathUtils.clamp(1 - interpolation.apply(dst / maxDst), 0, 1);
	}

	public float getVolume(Entity e, float maxDst){
		return getVolume(e.getBody().getPosition(), maxDst);
	}

	public static float getPan(float x, float maxDst){
		TEMP.set(Day100.camera.position.x, Day100.camera.position.y);
		
		float dst = TEMP.x - x + getAdditionalDst();
		boolean toRight = TEMP.x < x;
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
		return getPan(e.getBody().getPosition(), maxDst);
	}

	public static float getPitch(Vector2 position, float maxDst){
		TEMP.set(Day100.camera.position.x, Day100.camera.position.y);
		float d = TEMP.dst(position) + getAdditionalDst();
		float p = 1 - interpolation.apply(d / maxDst);
		float pitch = p;
		if(pitch < MIN_PITCH)
			pitch = MIN_PITCH;
		return pitch * Day100.timeScale;
	}

	public static float getPitch(Entity e, float maxDst){
		return getPitch(e.getBody().getPosition(), maxDst);
	}

	public static void playSound(Vector2 position, Sound sound, float baseVolume, float basePitch, float maxDst){
		if(sound == null)
			return;
		sound.play(getVolume(position, maxDst) * baseVolume, getPitch(position, maxDst) * basePitch, getPan(position, maxDst));
	}

	public static void playSound(Entity e, Sound sound, float baseVolume, float basePitch, float maxDst){
		if(e.getBody() == null)
			return;
		playSound(e.getBody().getPosition(), sound, baseVolume, basePitch, maxDst);
	}

	public static void loadAllSounds(){
		if(Day100.DEVELOPER_MODE){
			// Cache
			ArrayList<String> paths = AssetHelper.getAllFiles(Gdx.files.internal("Audio/"), "mp3");
			StringBuilder stringBuilder = new StringBuilder(300);
			for(String path : paths){
				stringBuilder.append(path + "\n");
			}
			Log.info(TAG, "Writing to " + Gdx.files.getLocalStoragePath() + "bin\\Cache\\Audio.txt");
			FileHandle destination = new FileHandle(Gdx.files.getLocalStoragePath() + "bin\\Cache\\Audio.txt");
			destination.writeString(stringBuilder.toString(), false);
			Log.info(TAG, "Cached audio files to bin/Cache");

			// Load from desktop bin.
			String[] audioFiles = new FileHandle(Gdx.files.getLocalStoragePath() + "bin\\Cache\\Audio.txt").readString().split("\n");
			for(String path : audioFiles){
				FileHandle file = Gdx.files.internal(path);
				if(file.exists()){
					if(AssetDataTag.MUSIC.isDataTag(path)){
						// Music
						Day100.assets.load(path, Music.class);
						Log.info(TAG, "Loading " + path + " as MUSIC.");
					}else{
						// Not music (SFX)
						Day100.assets.load(path, Sound.class);
						Log.info(TAG, "Loading " + path + " as SOUND.");
					}
				}
			}			
		}else{
			// Load from cache bin, copied by Loading screen.
			String[] audioFiles = Gdx.files.internal("Cache/Audio.txt").readString().split("\n");
			for(String path : audioFiles){
				FileHandle file = Gdx.files.internal(path);
				if(file.exists()){
					if(AssetDataTag.MUSIC.isDataTag(path)){
						// Music
						Day100.assets.load(path, Music.class);
						Log.info(TAG, "Loading " + path + " as MUSIC.");
					}else{
						// Not music (SFX)
						Day100.assets.load(path, Sound.class);
						Log.info(TAG, "Loading " + path + " as SOUND.");
					}
				}
			}	
		}
	}

}
