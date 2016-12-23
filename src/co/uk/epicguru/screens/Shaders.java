package co.uk.epicguru.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;

public final class Shaders {

	public static ArrayList<ShaderProgram> compliedShaders = new ArrayList<ShaderProgram>();
	public static ArrayList<String> compliedShadersNames = new ArrayList<String>();
	public static ArrayList<String> found = new ArrayList<String>();
	
	public static ArrayList<ShaderProgram> getAllShaders(){
		if(Day100.DEVELOPER_MODE){
			cacheShaders();
			return getShadersDevMode();
		}
		else{
			return getShadersChacheMode();			
		}
		
	}
	
	private static ArrayList<ShaderProgram> getShadersChacheMode() {
		
		Log.info("Shaders", "Getting shaders chache (Export) mode. DEV MODE = false");
		
		FileHandle source = null;
		String[] names = null;
		
		try{
			source = Gdx.files.internal("Cache/Shaders.txt");	
			
			compliedShaders.clear();
			found.clear();
			
			names = source.readString().split("\n");
		}catch(Exception e){
			Log.info("Shaders", "Error in shader load : " + e.getMessage());
			if(e.getMessage().contains("File not found")){
				Log.error("Shaders", "COPY THE CACHE, COPY THE CACHE, COPY THE CACHE...");
			}
			return null;
		}
		
		Log.info("Shaders", "Found " + names.length / 2 + " shaders.");
		for(String name : names){
			FileHandle handle = Gdx.files.internal("Shaders/" + name);
			if(found.contains(handle.nameWithoutExtension())){
				continue;
			}else{
				found.add(handle.nameWithoutExtension());
				compliedShaders.add(getShader(handle.nameWithoutExtension()));
			}
		}
		
		compliedShadersNames.clear();
		compliedShadersNames.addAll(found);
		
		return compliedShaders;
	}
	
	public static void cacheShaders(){
		FileHandle source = Gdx.files.internal("Shaders/");
		Log.info("Shaders", "Writing to " + Gdx.files.getLocalStoragePath() + "bin\\Cache\\Shaders.txt");
		FileHandle destination = new FileHandle(Gdx.files.getLocalStoragePath() + "bin\\Cache\\Shaders.txt");
		destination.writeString(source.readString(), false);
		Log.info("Shaders", "Cached shaders to bin/Cache");
	}

	private static ArrayList<ShaderProgram> getShadersDevMode(){
		Log.info("Shaders", "Getting all shaders... DEV MODE = true");
		
		compliedShaders.clear();
		found.clear();
		
		
		FileHandle folder = Gdx.files.internal("Shaders/");
		Log.info("Shaders", "Loading from " + folder.path());
		String[] names = folder.readString().split("\n");
		
		Log.info("Shaders", "Found " + names.length / 2 + " shaders.");
		for(String name : names){
			FileHandle handle = Gdx.files.internal("Shaders/" + name);
			if(found.contains(handle.nameWithoutExtension())){
				continue;
			}else{
				found.add(handle.nameWithoutExtension());
				compliedShaders.add(getShader(handle.nameWithoutExtension()));
			}
		}
		
		compliedShadersNames.clear();
		compliedShadersNames.addAll(found);
		
		return compliedShaders;
	}
	
	public static ShaderProgram getShader(String name) {
		FileHandle vertex;
		FileHandle fragment;

		try {
			try{
				vertex = Gdx.files.internal("Shaders/" + name + ".vsh");
				fragment = Gdx.files.internal("Shaders/" + name + ".fsh");
				if(vertex == null){
					throw new Exception("Vertex shader not found for shader " + name);					
				}
				if(fragment == null){
					throw new Exception("Fragment shader not found for shader " + name);
				}
			}catch(Exception e){
				Log.error("Shaders", "A file for a shader was not found : " + e.getMessage());
				throw e;
			}
			Log.info("Shaders", "Loaded shader : " + name);
			ShaderProgram shader = new ShaderProgram(vertex, fragment);

			if (shader.isCompiled()) {
				Log.info("Shaders", "The shader is healthy!");
			} else {
				Log.error("Shaders", "The shader did not compile!");
				Log.error("Shaders", shader.getLog());
			}

			return shader;
		} catch (Exception e) {
			Log.error("Shaders", "Failed to load shader : " + name);
			return null;
		}

	}

	public static Vector2 vector = new Vector2();
	public static Vector2 vector2 = new Vector2();
	public static Vector2 getTextureAtlasShaderMulti(TextureRegion region){
		
		float regionWidth = (float)(region.getRegionWidth() / Constants.PPM);
		float regionHeight = (float)(region.getRegionHeight() / Constants.PPM);
		
		float totalWidth = (float)(region.getTexture().getWidth() / Constants.PPM);
		float totalHeight = (float)(region.getTexture().getHeight() / Constants.PPM);
		
		float percentageX = regionWidth / totalWidth;
		float percentageY = regionHeight / totalHeight;
		
		float multiX = 1f / percentageX;
		float multiY = 1f / percentageY;
		
		vector.set(multiX, multiY);
		
		return vector;
	}
	
	public static Vector2 getTextureAtlasShaderSubtract(TextureRegion region){
		
		float regionX = (float)(region.getRegionX() / Constants.PPM);
		float regionY = (float)(region.getRegionY() / Constants.PPM);
		float totalWidth = (float)(region.getTexture().getWidth() / Constants.PPM);
		float totalHeight = (float)(region.getTexture().getHeight() / Constants.PPM);
		regionX /= totalWidth;
		regionY /= totalHeight;
		
		vector2.set(regionX, regionY);
		
		return vector2;
	}
	
}
