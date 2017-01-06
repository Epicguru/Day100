package co.uk.epicguru.helpers;

import co.uk.epicguru.main.Day100;

public class CamZoom {

	public static float getZoom(){
		if(Day100.player == null || Day100.player.isDead() || Day100.player.getBody() == null)
			return 1;
		
		float base = Day100.player.isMounting() ? 1.5f : 0.5f;
		float shootingAddition = Day100.player.gunManager.getEquipped() == null ? 0 : Day100.player.gunManager.getOffset() > 0 ? Day100.player.gunManager.getOffset() / 1500f : 0;
		
		return base + shootingAddition;
	}
	
}
