package beleg.core.scene;

import beleg.core.scene.ecs.Actor;
import beleg.core.scene.ecs.Component;

public abstract class Behaviour extends Component {

	protected Actor m_Actor;
	
	public Behaviour(Actor _actor) {
		
		m_Actor = _actor;
	}
	
	public abstract void update();
}
