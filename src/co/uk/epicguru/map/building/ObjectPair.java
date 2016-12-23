package co.uk.epicguru.map.building;

public class ObjectPair<A, B> {

	public A a;
	public B b;
	
	public ObjectPair(A a, B b){
		this.a = a;
		this.b = b;
	}
	
	public ObjectPair<A, B> set(A a, B b){
		this.a = a;
		this.b = b;
		return this;
	}
	
}
