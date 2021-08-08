package beleg.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL20;

public class Scene {

	public int m_VAO;
	public int m_VBO;
	public int m_EBO;
	
	public void load() {
	
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);
		
		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MeshGenerator.m_CubeData, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(GeometryRenderer.m_PositionLocation, 3, GL11.GL_FLOAT, false, 32, 0);
		GL20.glVertexAttribPointer(GeometryRenderer.m_UVLocation, 2, GL11.GL_FLOAT, false, 32, 12);
		GL20.glVertexAttribPointer(GeometryRenderer.m_NormalLocation, 3, GL11.GL_FLOAT, false, 32, 20);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		m_EBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, MeshGenerator.m_CubeIndex, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
	}
	
	public void render() {
		
		GL30.glBindVertexArray(m_VAO);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_EBO);
		
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_PositionLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_UVLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_NormalLocation);

		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
	}
	
}
