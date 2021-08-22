package beleg.core.light;

import org.joml.Vector3f;

public class PointLight extends Light {

	public PointLight(Vector3f _position, Vector3f _color) {
		super(_position, _color, Light.LIGHT_POINT);
		
	}

}
