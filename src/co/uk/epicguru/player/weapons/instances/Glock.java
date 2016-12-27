package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.Exclude;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunManager;

public final class Glock extends GunDefinition {

	@Exclude
	public static TextureRegion[] fireAnim;
	@Exclude
	public static TextureRegion defaultTexture = Day100.gunsAtlas.findRegion("Glock");
	@Exclude
	public float timer;
	@Exclude
	public int frameNumber;
	
	public Glock() {
		super("Glock");
		if(fireAnim == null)
			fireAnim = GunManager.getAnim("Glock", "Fire", 6);
		frameNumber = -1;
	}
	
	public void update(Entity e, GunManager gun, float delta){
		
		if(frameNumber == -1){
			texture = defaultTexture;
			return;
		}
		texture = fireAnim[frameNumber];
		
		timer += delta;
		float interval = 0.01f;
		while(timer >= interval){
			timer -= interval;
			frameNumber++;
			if(frameNumber > fireAnim.length - 1){
				frameNumber = -1;
			}
		}
	}
	
	public void shot(GunManager gun, Entity shooter){
		timer = 0;
		frameNumber = 0;
	}

}
