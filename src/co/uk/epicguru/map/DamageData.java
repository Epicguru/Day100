package co.uk.epicguru.map;

import com.badlogic.gdx.math.Vector2;

public final class DamageData {

	public Entity dealer;
	public float angle;
	public Vector2 originPoint;
	public Vector2 hitPoint;
	
	public DamageData(Entity dealer, float angle, Vector2 origin, Vector2 hitPoint){
		this.dealer = dealer;
		this.angle = angle;
		this.originPoint = origin;
		this.hitPoint = hitPoint;
	}
	
}
