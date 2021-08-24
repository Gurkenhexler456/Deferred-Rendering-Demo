package beleg.core.graphics;

public abstract class Material {
	
	private Shader m_Shader;
	
	public Material(Shader _shader) {
		
		m_Shader = _shader;
	}
	
	public abstract void setup();
	
	public void bind() {
		
		m_Shader.bind();
	}

}
