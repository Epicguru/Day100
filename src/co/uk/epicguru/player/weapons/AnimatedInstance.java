package co.uk.epicguru.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.map.Entity;

public class AnimatedInstance extends GunInstance {

	public ArrayList<GunAnimation> loadedAnimations = new ArrayList<GunAnimation>();
	public TextureRegion defaultTexture;
	public GunAnimation currentAnimation;
	private int currentFrame;
	private float FPS = 10;
	private float timer = 0;
	
	public AnimatedInstance(GunManager gunManager, Entity entity, TextureRegion defaultTexture) {
		super(gunManager, entity);
		this.defaultTexture = defaultTexture;
	}
	
	public float getTimer(){
		return timer;
	}
	
	/**
	 * Gets the Frames Per Second that the animations play at.
	 */
	public float getFPS(){
		return FPS;
	}
	
	/**
	 * Sets the Frames Per Second that the animations play at.
	 */
	public void setFPS(float fps){
		this.FPS = fps;
	}
	
	/**
	 * Gets the current frame number in the current animation.
	 * @return
	 */
	public int getCurrentFrame(){
		return currentFrame;
	}
	
	/**
	 * Gets the currently playing animation. MAY BE NULL IF NONE PLAYING!
	 * @return
	 */
	public GunAnimation getCurrentAnimation(){
		return currentAnimation;
	}
	
	/**
	 * Sets the current animation, starting at the given frame number.
	 */
	public void setAnimation(GunAnimation animation, int startFrame){
		if(animation == null){
			currentAnimation = null;
			currentFrame = 0;
		}
		this.currentAnimation = animation;
		this.currentFrame = startFrame;
		this.timer = 0;
	}
	
	/**
	 * Sets the animation given a name. The animation must be in the loaded pool using the method {@link #addAnimation(String, int)}.
	 */
	public GunAnimation setAnimation(String animation, int startFrame){
		GunAnimation anim = findAnimation(animation);
		setAnimation(anim, startFrame);
		return anim;
	}
	
	/**
	 * Sets the animation given a name. The animation must be in the loaded pool using the method {@link #addAnimation(String, int)}.
	 */
	public GunAnimation setAnimation(String animation){
		return setAnimation(animation, 0);
	}
	
	/**
	 * Finds a loaded animation from its name.
	 */
	public GunAnimation findAnimation(String name){
		for(GunAnimation animation : loadedAnimations){
			if(animation.name.equals(name))
				return animation;
		}
		//Log.error("Animated Gun Instance", "Failed to find animation '" + name + "'");
		return null;
	}
	
	/**
	 * Loads and adds an animation to the loaded pool.
	 * @param name The name of the animation to load from disk.
	 * @param frames THe number of frames that the animation has.
	 */
	public void addAnimation(String name, int frames){
		loadedAnimations.add(new GunAnimation(gun.name, name, frames));
	}
	
	/**
	 * Gets the texture for whatever animation that is playing. If there is no animation playing, returns the default texture
	 * specified in the constructor.
	 */
	public TextureRegion getCurrentFrameTexture(){
		if(currentAnimation == null){
			return defaultTexture;
		}
		return currentAnimation.frames[currentFrame];
	}
	
	public void update(float delta){
		if(getCurrentAnimation() == null)
			return;
		timer += delta;
		while(timer >= 1f / FPS){
			timer -= 1f / FPS;
			currentFrame++;
			if(currentFrame > currentAnimation.frames.length - 1){
				currentAnimation = null;
				currentFrame = 0;
				break;
			}
		}
	}
}
