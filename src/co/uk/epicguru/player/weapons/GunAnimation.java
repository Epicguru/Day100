package co.uk.epicguru.player.weapons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class GunAnimation {

	public TextureRegion[] frames;
	public String name;
	
	public GunAnimation(String gunName, String animName, int frames){
		this.name = animName;
		this.frames = GunManager.getAnim(gunName, animName, frames);
	}
	
}
