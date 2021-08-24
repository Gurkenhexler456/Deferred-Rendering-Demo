package beleg.core.graphics.lighting;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

	public DirectionalLight(Vector3f _direction, Vector3f _color) {
		
		super(_direction, _color, Light.LIGHT_DIRECTIONAL);
	}
	
}
