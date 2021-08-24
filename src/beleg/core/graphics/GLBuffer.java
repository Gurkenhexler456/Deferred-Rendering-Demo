package beleg.core.graphics;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class GLBuffer {

	private int m_ID;
	private int m_Target;
	private int m_Usage;
	
	
	public GLBuffer(int _target, int _usage) {
		
		m_Target 	= _target;
		m_Usage 	= _usage;
		m_ID 		= GL15.glGenBuffers();
	}
	
	
	
	public void createFromInts(int[] _data) {
		
		GL15.glBufferData(m_Target, _data, m_Usage);
	}
	
	public void createFromFloats(float[] _data) {
		
		GL15.glBufferData(m_Target, _data, m_Usage);
	}
	
	
	
	public void setInts(int _offset, int[] _data) {
		
		GL15.glBufferSubData(m_Target, _offset, _data);
	}
	
	public void setFloats(int _offset, float[] _data) {

		GL15.glBufferSubData(m_Target, _offset, _data);
	}
	
	
	
	public void bind() {
		
		GL15.glBindBuffer(m_Target, m_ID);
	}
	
	public void unbind() {
		
		GL15.glBindBuffer(m_Target, 0);
	}
}
