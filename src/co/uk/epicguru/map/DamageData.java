package co.uk.epicguru.map;

import com.badlogic.gdx.math.Vector2;

public final class DamageData {

	public Entity dealer;
	public float angle;
	public Vector2 originPoint;
	
	public DamageData(Entity dealer, float angle, Vector2 origin){
		this.dealer = dealer;
		this.angle = angle;
		this.originPoint = origin;
	}
	
}
