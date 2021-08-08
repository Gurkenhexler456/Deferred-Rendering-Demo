package beleg.core;

import org.lwjgl.opengl.GL20;

public class GeometryRenderer {

	private Shader m_DefaultShader;
	public static final int m_PositionLocation = 0;
	public static final int m_UVLocation = 1;
	public static final int m_NormalLocation = 2;
	
	public GeometryRenderer() {
		
		m_DefaultShader = new Shader();
	}
	
	public void setup() {
	
		String vert_source = Resources.loadFileToString("res/shaders/default.vert");
		String frag_source = Resources.loadFileToString("res/shaders/default.frag");
		
		int vert_id = m_DefaultShader.addShader(GL20.GL_VERTEX_SHADER, vert_source);
		int frag_id = m_DefaultShader.addShader(GL20.GL_FRAGMENT_SHADER, frag_source);
		
		m_DefaultShader.bindAttribLocation(m_PositionLocation, "in_Position");
		m_DefaultShader.bindAttribLocation(m_UVLocation, "in_UV");
		m_DefaultShader.bindAttribLocation(m_NormalLocation, "in_Normal");
		
		m_DefaultShader.bindFragDataLocation(0, "out_Position");
		m_DefaultShader.bindFragDataLocation(1, "out_Albedo");
		m_DefaultShader.bindFragDataLocation(2, "out_Normal");
		
		m_DefaultShader.link();
		
		m_DefaultShader.removeShader(vert_id);
		m_DefaultShader.removeShader(frag_id);
	}
	
	public void use() {
		
		m_DefaultShader.bind();
	}
}
