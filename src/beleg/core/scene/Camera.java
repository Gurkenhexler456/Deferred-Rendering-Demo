package beleg.core.scene;

import org.joml.Matrix4f;

import beleg.core.scene.ecs.Actor;

public class Camera extends Actor {

	protected Matrix4f m_Projection;
	
	public Camera() {
		
		super();
		m_Projection = new Matrix4f().identity();
	}
	
	public void setOrtho(float _left, float _right, float _bottom, float _top, float _near, float _far) {
		
		m_Projection.ortho(_left, _right, _bottom, _top, _near, _far);
	}
	
	public void setPerspective(float _fov, float _aspect, float _near, float _far) {
		
		m_Projection.perspective(_fov, _aspect, _near, _far);
	}
	
	public Matrix4f getProjection() {
		
		return m_Projection;
	}
}
