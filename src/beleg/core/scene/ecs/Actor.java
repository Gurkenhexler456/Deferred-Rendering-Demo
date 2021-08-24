package beleg.core.scene.ecs;


import java.util.ArrayList;

import org.joml.Matrix4f;

import beleg.core.graphics.Model;


public class Actor {

	public Matrix4f m_Transform;
	public Model m_Model;
	private ArrayList<Component> m_Components;
	
	public Actor(Matrix4f _transform, Model _model) {
		
		m_Transform 	= _transform;
		m_Model 		= _model;
		m_Components 	= new ArrayList<>();
	}
	
	public void addComponent(Component _component) {
		
		if(m_Components.contains(_component)) {
			
			throw new RuntimeException("Component already added");
		}
		
		m_Components.add(_component);
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
