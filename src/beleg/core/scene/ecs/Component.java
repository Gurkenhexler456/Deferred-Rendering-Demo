package beleg.core.scene.ecs;

public abstract class Component {

	private Actor m_Parent;
	
	public Component() {
		
	}
	
	public void setParent(Actor _parent) {
		
		m_Parent = _parent;
	}
	
	public Actor getParent() {
		
		return m_Parent;
	}
}
