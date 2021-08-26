package beleg.core.scene.ecs;


import java.util.ArrayList;

import org.joml.Matrix4f;



public class Actor {

	protected Matrix4f m_Transform;
	protected ArrayList<Component> m_Components;
	
	public Actor() {
		
		m_Transform 	= new Matrix4f().identity();
		m_Components 	= new ArrayList<>();
	}
	
	public void setTransform(Matrix4f _transform) {
		
		m_Transform.set(_transform);
	}
	
	public Matrix4f getTransform() {
		
		return m_Transform;
	}
	
	public void addComponent(Component _component) {
		
		if(m_Components.contains(_component)) {
			
			throw new RuntimeException("Component already added");
		}
		
		m_Components.add(_component);
		_component.setParent(this);
	}
	
	public <T extends Component> T getComponent(Class<T> _class) {
		
		for(Component component : m_Components) {
			
			if(_class.isInstance(component)) {
				
				return _class.cast(component);
			}
		}
		
		return null;
	}
	
}
