package beleg.core;

public class Mesh {

	private float[] m_Data;
	
	private int[] m_Indices;
	
	
	public Mesh(float[] _data, int[] _indices) {
		
		m_Data 		= _data;
		m_Indices 	= _indices;
	}
	
	
	public float[] getData() {
		
		return m_Data;
	}

	public int[] getIndices() {
	
		return m_Indices;
	}
}
