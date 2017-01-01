package co.uk.epicguru.map;

import com.badlogic.gdx.math.Vector2;

public final class DamageData {

	public Entity dealer;
	public float angle;
	public Vector2 originPoint;
	public Vector2 hitPoint;
	
	public DamageData(){}
	
	public DamageData(Entity dealer, float angle, Vector2 origin, Vector2 hitPoint){
		this.dealer = dealer;
		this.angle = angle;
		this.originPoint = origin;
		this.hitPoint = hitPoint;
	}
	
	public DamageData(DamageData other){
		this.dealer = other.dealer;
		this.angle = other.angle;
		this.originPoint = other.originPoint;
		this.hitPoint = other.hitPoint;
	}
	
	public void reset(){
		this.dealer = null;
		this.angle = 0;
		this.originPoint = null;
		this.hitPoint = null;
	}
	
	public DamageData set(Entity dealer, float angle, Vector2 origin, Vector2 hitPoint){
		this.dealer = dealer;
		this.angle = angle;
		this.originPoint = origin;
		this.hitPoint = hitPoint;
		return this;
	}	
}
