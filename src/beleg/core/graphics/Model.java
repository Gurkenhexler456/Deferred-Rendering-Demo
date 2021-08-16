package beleg.core.graphics;

public class Model {

	private int m_VAO;
	private int m_VBO;
	private int m_EBO;
	
	private int m_VertexCount;
	
	public Model(int _vao, int _vbo, int _ebo) {
		
		m_VAO 			= _vao;
		m_VBO 			= _vbo;
		m_EBO 			= _ebo;
		m_VertexCount 	= 0;
	}
	
	
	public int getVAO() {
		
		return m_VAO;
	}
	
	public int getVBO() {
		
		return m_VBO;
	}
	
	public int getEBO() {
		
		return m_EBO;
	}
	
	public int getVertexCount() {
		
		return m_VertexCount;
	}
	
	public void setVertexCount(int _vertexCount) {
		
		m_VertexCount = _vertexCount;
	}
}
