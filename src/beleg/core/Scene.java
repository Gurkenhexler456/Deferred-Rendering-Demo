package beleg.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import beleg.core.graphics.Model;
import beleg.core.graphics.ModelFactory;
import beleg.core.scene.Actor;

import org.lwjgl.opengl.GL20;

/**
 * This class implements a Scene used by the application.
 * It stores all components
 * @author Tobias Hofmann
 */
public class Scene {
	
	public Model m_Terrain;
	
	public Shader m_CurrentShader;
	
	public ArrayList<Actor> m_Actor = new ArrayList<Actor>();
	public ArrayList<Shader> m_Shaders;
	
	public void load() {
		
		m_CurrentShader = buildDefaultShader();	
	}
	
	public ArrayList<Actor> getModels() {
		
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
	
	
	
	
	public Shader getCurrentShader() {
		
		return m_CurrentShader;
	}
	
	
}
