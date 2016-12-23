package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;

import co.uk.epicguru.player.weapons.GunDefinition;

public abstract class ReflectionActor {

	public Actor[] actors;
	public String fieldName;
	public String type;
	
	public ReflectionActor(String name, String type, Actor... actors){
		this.fieldName = name;
		this.type = type;
		this.actors = actors;
	}
	
	public abstract void apply(GunDefinition gun) throws Exception;
	
}
