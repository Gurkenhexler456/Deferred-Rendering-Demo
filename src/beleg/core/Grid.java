package beleg.core;

/**
 * 
 * defines a grid, where the width defines the size on the x axis and 
 * the height defines the size on the z axis
 *
 * 	width: 2
 *   +---+
 * | |   |
 * |
 * | 6-7-8 -+
 * | |/|/|  |
 * | 3-4-5  | height: 2
 * | |/|/|  |
 * | 0-1-2 -+
 * |
 * +--------->
 * 
 * to get the number of vertices per side add one to the size
 * 
 */
public class Grid {

	
	private int m_Width;
	private int m_Height;
	
	public Grid(int _width, int _height) {
		
		m_Width 	= _width;
		m_Height 	= _height;
	}
	
	
	
	public int getWidth() {
		
		return m_Width;
	}
	
	public int getHeight() {
		
		return m_Height;
	}
	
}
