package beleg.core;

import org.joml.Matrix4f;

import beleg.core.graphics.Model;

public class Actor {

	public Matrix4f m_Transform;
	public Model m_Model;
	
	public Actor(Matrix4f _transform, Model _model) {
		
		m_Transform = _transform;
		m_Model = _model;
	}
	
}
