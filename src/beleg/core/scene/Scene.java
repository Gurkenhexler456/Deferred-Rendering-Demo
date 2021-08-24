package beleg.core.scene;


import java.util.ArrayList;

import org.lwjgl.opengl.GL20;

import beleg.core.GeometryRenderer;
import beleg.core.Resources;
import beleg.core.graphics.Shader;
import beleg.core.scene.ecs.Actor;


/**
 * This class implements a Scene used by the application.
 * It stores all it's components
 * @author Tobias Hofmann
 */
public class Scene {
	
	
	
	private ArrayList<Actor> m_Actor = new ArrayList<Actor>();
	
	
	
	public void load() {
		
		
	}
	
	public ArrayList<Actor> getActors() {
		
		return m_Actor;
	}
	
	public void addActor(Actor _actor) {
	
		m_Actor.add(_actor);
	}
}
