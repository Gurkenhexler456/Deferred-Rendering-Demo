package beleg.demo;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import beleg.core.scene.Behaviour;
import beleg.core.scene.ecs.Actor;

public class RotationBehaviour extends Behaviour {

	protected Matrix4f m_Model;
	protected Vector3f m_RotationAxis;
	
	public RotationBehaviour(Actor _actor, Vector3f _rotationAxis) {
		super(_actor);
		
		m_Model 		= m_Actor.getTransform();
		m_RotationAxis 	= _rotationAxis;
	}

	@Override
	public void update() {
		
		Vector3f translation = new Vector3f(m_RotationAxis);
		translation.mul(2);
		translation.add(new Vector3f(-0.5f));
		
		m_Model.identity();
		m_Model.rotate((float) GLFW.glfwGetTime(), m_RotationAxis);
		m_Model.translate(translation);
	}

}
