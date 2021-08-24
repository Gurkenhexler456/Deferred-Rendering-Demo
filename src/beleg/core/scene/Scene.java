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
	
	
	
	public Shader buildDefaultShader() {
		
		Shader shader = new Shader();
		
		String vert_source = Resources.loadFileToString("res/shaders/default.vert");
		String frag_source = Resources.loadFileToString("res/shaders/default.frag");
		
		int vert_id = shader.addShader(GL20.GL_VERTEX_SHADER, vert_source);
		int frag_id = shader.addShader(GL20.GL_FRAGMENT_SHADER, frag_source);
		
		shader.bindAttribLocation(GeometryRenderer.m_PositionLocation, "in_Position");
		shader.bindAttribLocation(GeometryRenderer.m_UVLocation, "in_UV");
		shader.bindAttribLocation(GeometryRenderer.m_NormalLocation, "in_Normal");
		
		shader.bindFragDataLocation(0, "out_Position");
		shader.bindFragDataLocation(1, "out_Albedo");
		shader.bindFragDataLocation(2, "out_Normal");
		
		shader.link();
		
		shader.removeShader(vert_id);
		shader.removeShader(frag_id);
		
		return shader;
	}
}
