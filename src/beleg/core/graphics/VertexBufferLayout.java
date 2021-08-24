package beleg.core.graphics;

import org.lwjgl.opengl.GL11;

/**
 * 
 * @author tobias
 */
public class VertexBufferLayout {

	
	private int m_Count;
	private int m_Type;
	private boolean m_Normalized;
	private int m_Stride;
	
	public VertexBufferLayout(int _count, int _type, boolean _normalized, int _stride) {
		
		m_Count = _count;
		m_Type = _type;
		m_Normalized = _normalized;
		m_Stride = _stride;
	}
	
	public int getCount() {
		
		return m_Count;
	}
	
	public int getType() {
		
		return m_Type;
	}

	public boolean getNormalized() {
	
		return m_Normalized;
	}
	
	public int getStride() {
	
		return m_Stride;
	}
	
	// returns the size of one element in bytes
	public int getElementSize() {
		
		switch(m_Type) {
			case	GL11.GL_FLOAT			:	return 4;
			case	GL11.GL_UNSIGNED_INT	:	return 4;
			case	GL11.GL_UNSIGNED_BYTE	:	return 1;
		}
		
		return 0;
	}
	
}
