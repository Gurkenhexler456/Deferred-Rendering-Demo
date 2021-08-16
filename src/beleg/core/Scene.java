package beleg.core;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import beleg.core.graphics.Model;
import beleg.core.graphics.ModelFactory;

import org.lwjgl.opengl.GL20;

public class Scene {

	public int m_VAO;
	public int m_VBO;
	public int m_EBO;
	public Texture m_Texture;
	
	public Model m_Model;
	
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
		
		m_Texture = new Texture();
		m_Texture.bind();
		m_Texture.setFilteringAndWrapping(GL11.GL_NEAREST, GL11.GL_REPEAT);
		m_Texture.image2D(8, 8, genTexture(8, 8, 3));
		m_Texture.unbind();
		
		Grid grid = new Grid(30, 30);
		m_Model = ModelFactory.storeMesh(MeshGenerator.generateGridMesh(grid));
	}
	
	public void render() {
		
		GL30.glBindVertexArray(m_VAO);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_EBO);
		
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_PositionLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_UVLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_NormalLocation);

		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Texture.bind();
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
		
		
		
		GL30.glBindVertexArray(m_Model.getVAO());
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_Model.getEBO());
		
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_PositionLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_UVLocation);
		GL20.glEnableVertexAttribArray(GeometryRenderer.m_NormalLocation);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, m_Model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
	}
	
	
	public ByteBuffer genTexture(int _w, int _h, int _c) {
		
		byte[] data = new byte[_w * _h * _c];
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		
		int index;
		
		for(int y = 0; y < _h; y++) {
			
			for(int x = 0; x < _w; x++) {
				
				index = _c * (y * _w + x);
				
				for(int i = 0; i < _c; i++) {
					
					data[index + i] = (byte) (((x + y) % 2) * 0xff);
				}
			}
		}
		
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	
}
