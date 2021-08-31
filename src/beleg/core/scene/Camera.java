package beleg.core.scene;

import org.joml.Matrix4f;

import beleg.core.scene.ecs.Actor;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends Actor{

	private Vector3f m_Position;
	private Vector3f m_LookDirection;
	
	private Matrix4f m_Projection;



	public Camera() {

		this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
	}

	public Camera(Vector3f position, Vector3f lookDirection) {
		
		m_Position = new Vector3f(position);
		m_LookDirection = new Vector3f(lookDirection);
		m_Projection = new Matrix4f();
		m_Projection.identity();
	}
	
	public Matrix4f getViewMatrix() {
		
		Matrix4f result = new Matrix4f();
		
		Vector3f UP = new Vector3f(0, 1, 0);
		Vector3f center = new Vector3f(m_Position);
		center.add(m_LookDirection);
		result.lookAt(m_Position, center, UP);


		return result;
	}
	
	
	public void setPerspective(float _fov, float _aspect, float _near, float _far) {
		
		m_Projection.perspective(_fov, _aspect, _near, _far);
	}
	
	public void setOrtho(float _left, float _right, float _bottom, float _top, float _near, float _far) {
		
		m_Projection.ortho(_left, _right, _bottom, _top, _near, _far);
	}
	
	
	public Matrix4f getProjection() {

		return m_Projection;
	}
	
	public void move(float dx, float dy, float dz) {
		
		m_Position.add(dx, dy, dz);
	}
	
	public void move(Vector3f offset) {
		
		m_Position.add(offset);
	}
	
	
	
	public Vector3f getPosition() {
		return m_Position;
	}
	
	public void setPosition(Vector3f _position) {
	
		m_Position = _position;
	}
	
	public Vector3f getLookDirection() {
		return m_LookDirection;
	}
	
	public void setLookDirection(Vector3f direction) {
		
		m_LookDirection.set(direction);
	}
}
