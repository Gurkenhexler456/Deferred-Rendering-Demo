package beleg.core;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class GeometryRenderer {

	private Shader m_DefaultShader;
	public static final int m_PositionLocation = 0;
	public static final int m_UVLocation = 1;
	public static final int m_NormalLocation = 2;
	
	public int m_u_Projection;
	public int m_u_View;
	public int m_u_Model;
	
	public Matrix4f m_Projection;
	public Matrix4f m_View;
	public Matrix4f m_Model;
	
	public GeometryRenderer() {
		
		m_DefaultShader = new Shader();
		
		m_Projection 	= new Matrix4f().identity();
		m_View 			= new Matrix4f().identity();
		m_Model			= new Matrix4f().identity();
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
		
		m_DefaultShader.bind();
		m_u_Model 		= m_DefaultShader.getUniformLocation("u_Model");
		m_u_View 		= m_DefaultShader.getUniformLocation("u_View");
		m_u_Projection 	= m_DefaultShader.getUniformLocation("u_Projection");
		m_DefaultShader.unbind();
	}
	
	public void use() {
		
		m_DefaultShader.bind();
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		
		buffer = m_Projection.get(buffer);
		GL20.glUniformMatrix4fv(m_u_Projection, false, buffer);
		
		buffer = m_View.get(buffer);
		GL20.glUniformMatrix4fv(m_u_View, false, buffer);
		
		buffer = m_Model.get(buffer);
		GL20.glUniformMatrix4fv(m_u_Model, false, buffer);
	}
	
	public void setProjection(Matrix4f _projection) {
		m_Projection = _projection;
	}
	
	public void setView(Matrix4f _view) {
		m_View = _view;
	}
	
	public void setModel(Matrix4f _model) {
		m_Model = _model;
	}
}
