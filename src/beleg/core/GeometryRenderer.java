package beleg.core;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import beleg.core.graphics.Model;

public class GeometryRenderer {

	public static final int m_PositionLocation = 0;
	public static final int m_UVLocation = 1;
	public static final int m_NormalLocation = 2;
	
	private int m_u_Projection;
	private int m_u_View;
	private int m_u_Model;
	
	public Shader m_CurrentShader;
	
	
	public GeometryRenderer() {
		
		
	}
	
	public void useShader(Shader _shader) {
	
		m_CurrentShader = _shader;
		m_CurrentShader.bind();
	}
	
	
	public void render(Model _model) {
		
		GL30.glBindVertexArray(_model.getVAO());
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _model.getEBO());
		
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_PositionLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_UVLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_NormalLocation);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, _model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
	}
}
