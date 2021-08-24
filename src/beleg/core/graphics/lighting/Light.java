package beleg.core.graphics.lighting;

import org.joml.Vector3f;

public abstract class Light {

	public static final int LIGHT_DIRECTIONAL = 0;
	public static final int LIGHT_POINT = 1;
	
	private Vector3f m_Position;
	private Vector3f m_Color;
	private int m_Type;
	
	public Light(Vector3f _position, Vector3f _color, int _type) {
		
		m_Position 	= _position;
		m_Color 	= _color;
		m_Type 		= _type;
	}
	
	public Vector3f getPosition() {
		
		return m_Position;
	}
	
	public Vector3f getColor() {
		
		return m_Color;
	}
	
	public int getType() {
		
		return m_Type;
	}
}
