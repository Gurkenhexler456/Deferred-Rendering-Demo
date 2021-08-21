package beleg.core;

public class MeshGenerator {

	public static final float[] m_CubeData = {
			
		// FRONT
		0.0f,	0.0f,	0.0f,		0.0f,	0.0f,		0.0f,	0.0f,	-1.0f,
		1.0f,	0.0f,	0.0f,		1.0f,	0.0f,		0.0f,	0.0f,	-1.0f,
		1.0f,	1.0f,	0.0f,		1.0f,	1.0f,		0.0f,	0.0f,	-1.0f,
		0.0f,	1.0f,	0.0f,		0.0f,	1.0f,		0.0f,	0.0f,	-1.0f,
		
		// BACK
		1.0f,	0.0f,	1.0f,		0.0f,	0.0f,		0.0f,	0.0f,	1.0f,
		0.0f,	0.0f,	1.0f,		1.0f,	0.0f,		0.0f,	0.0f,	1.0f,
		0.0f,	1.0f,	1.0f,		1.0f,	1.0f,		0.0f,	0.0f,	1.0f,
		1.0f,	1.0f,	1.0f,		0.0f,	1.0f,		0.0f,	0.0f,	1.0f,
		
		// BOTTOM
		0.0f,	0.0f,	1.0f,		0.0f,	0.0f,		0.0f,	-1.0f,	0.0f,
		1.0f,	0.0f,	1.0f,		1.0f,	0.0f,		0.0f,	-1.0f,	0.0f,
		1.0f,	0.0f,	0.0f,		1.0f,	1.0f,		0.0f,	-1.0f,	0.0f,
		0.0f,	0.0f,	0.0f,		0.0f,	1.0f,		0.0f,	-1.0f,	0.0f,
		
		// TOP
		0.0f,	1.0f,	0.0f,		0.0f,	0.0f,		0.0f,	1.0f,	0.0f,
		1.0f,	1.0f,	0.0f,		1.0f,	0.0f,		0.0f,	1.0f,	0.0f,
		1.0f,	1.0f,	1.0f,		1.0f,	1.0f,		0.0f,	1.0f,	0.0f,
		0.0f,	1.0f,	1.0f,		0.0f,	1.0f,		0.0f,	1.0f,	0.0f,
		
		// LEFT
		0.0f,	0.0f,	1.0f,		0.0f,	0.0f,		-1.0f,	0.0f,	0.0f,
		0.0f,	0.0f,	0.0f,		1.0f,	0.0f,		-1.0f,	0.0f,	0.0f,
		0.0f,	1.0f,	0.0f,		1.0f,	1.0f,		-1.0f,	0.0f,	0.0f,
		0.0f,	1.0f,	1.0f,		0.0f,	1.0f,		-1.0f,	0.0f,	0.0f,
		
		// RIGHT
		1.0f,	0.0f,	0.0f,		0.0f,	0.0f,		1.0f,	0.0f,	0.0f,
		1.0f,	0.0f,	1.0f,		1.0f,	0.0f,		1.0f,	0.0f,	0.0f,
		1.0f,	1.0f,	1.0f,		1.0f,	1.0f,		1.0f,	0.0f,	0.0f,
		1.0f,	1.0f,	0.0f,		0.0f,	1.0f,		1.0f,	0.0f,	0.0f
	};
	
	public static final int[] m_CubeIndex = {
		
		0, 1, 2,
		2, 3, 0,
		
		4, 5, 6,
		6, 7, 4,
		
		8, 9, 10,
		10, 11, 8,
		
		12, 13, 14,
		14, 15, 12,
		
		16, 17, 18,
		18, 19, 16,
		
		20, 21, 22,
		22, 23, 20
	};

	
	public static final float[] m_PlaneData = {
		
		0.0f,	0.0f,	0.0f,		0.0f,	0.0f,		0.0f,	1.0f,	0.0f,
		0.0f,	1.0f,	0.0f,		1.0f,	0.0f,		0.0f,	1.0f,	0.0f,
		0.0f,	1.0f,	1.0f,		1.0f,	1.0f,		0.0f,	1.0f,	0.0f,
		0.0f,	0.0f,	1.0f,		0.0f,	1.0f,		0.0f,	1.0f,	0.0f
	};
	
	public static final int[] m_PlaneIndices = {
			
		0, 1, 2,
		2, 3, 0
	};
	
	
	
	
	
	
	public static Mesh generateGridMesh(Grid _grid) {
		
		int width = _grid.getWidth();
		int height = _grid.getHeight();
		
		int vertCount_x = width + 1;
		int vertCount_z = height + 1;
		
		
		float[] 	data 		= new float	[vertCount_x * vertCount_z * 8];
		int[] 		indices		= new int	[width * height * 6];
		
		int index;
		int indexPN;
		
		
		for(int z = 0; z < vertCount_z; z++) {
			
			for(int x = 0; x < vertCount_x; x++) {
				
				index 	= 8 * (x + z * vertCount_x);
				
				data[index]   		= x;
				data[index + 1] 	= 0;
				data[index + 2] 	= z;
				
				data[index + 3]   	= (float)(x) / width;
				data[index + 4] 	= (float)(z) / height;
				
				data[index + 5]   	= 0;
				data[index + 6] 	= 1;
				data[index + 7] 	= 0;
			}
		}
		
		
		for(int z = 0; z < height; z++) {
			
			for(int x = 0; x < width; x++) {
				
				index 	= 6 * (x + z * width);
				
				indices[index] 		= x 		+ z 		* vertCount_x;
				indices[index + 1] 	= (x + 1) 	+ z 		* vertCount_x;
				indices[index + 2] 	= (x + 1) 	+ (z + 1) 	* vertCount_x;
				
				indices[index + 3] 	= (x + 1) 	+ (z + 1) 	* vertCount_x;
				indices[index + 4] 	= x 		+ (z + 1) 	* vertCount_x;
				indices[index + 5] 	= x 		+ z 		* vertCount_x;
			}
		}
		
		return new Mesh(data, indices);
	}
}
