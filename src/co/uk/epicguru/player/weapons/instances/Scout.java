package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.Exclude;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunManager;

public class Scout extends GunDefinition {

	public static Sound bolt = GunManager.getGunSound("ScoutBolt.mp3");
	@Exclude
	public static TextureRegion[] boltAnim;
	@Exclude
	public static TextureRegion defaultTexture = Day100.gunsAtlas.findRegion("Scout");
	@Exclude
	public float timer;
	@Exclude
	public int frameNumber;
	
	public Scout() {
		super("Scout");
		shotSounds = new Sound[]{
				GunManager.getGunSound("Scout.mp3")
		};
		if(boltAnim == null)
			boltAnim = GunManager.getAnim("Scout", "Bolt", 12);
		frameNumber = -1;
	}
	
	@Override
	public void equipped(GunManager gunManager, Entity e) {
		frameNumber = 0;
		timer = 0;
	}

	public void update(Entity e, GunManager gun, float delta){
		
		if(frameNumber == -1){
			texture = defaultTexture;
			return;
		}
		texture = boltAnim[frameNumber];
		
		timer += delta;
		float interval = 0.05f;
		while(timer >= interval){
			timer -= interval;
			frameNumber++;
			if(frameNumber > boltAnim.length - 1){
				frameNumber = -1;
			}
		}
	}
	
	public void render(Entity e, GunManager g, Batch batch){
		texture = defaultTexture;
	}
	
	public void shot(GunManager gun, Entity shooter){
		bolt.play(0.7f);
		timer = 0;
		frameNumber = 0;
	}

}
