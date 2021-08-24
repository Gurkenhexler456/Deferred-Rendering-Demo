package beleg.core.graphics;

import beleg.core.scene.ecs.Component;

public class Material extends Component {
	
	private Shader m_Shader;
	
	public Material(Shader _shader) {
		
		m_Shader = _shader;
	}
	
	public void bind() {
		
		m_Shader.bind();
	}

	public Shader getShader() {
		
		return m_Shader;
	}
	
}
