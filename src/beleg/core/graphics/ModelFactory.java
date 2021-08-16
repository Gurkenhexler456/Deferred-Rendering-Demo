package beleg.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL20;

import beleg.core.Mesh;

public class ModelFactory {

	public static final int ATTRIB_LOCATION_POSITION 	= 0;
	public static final int ATTRIB_LOCATION_UV 			= 1;
	public static final int ATTRIB_LOCATION_NORMAL 		= 2;
	
	
	public static Model storeMesh(Mesh _mesh) {
		
		int vao = GL30.glGenVertexArrays();
		int vbo = GL15.glGenBuffers();
		int ebo = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, _mesh.getData(), GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(ATTRIB_LOCATION_POSITION, 	3, GL11.GL_FLOAT, false, 32, 0);
		GL20.glVertexAttribPointer(ATTRIB_LOCATION_UV, 			2, GL11.GL_FLOAT, false, 32, 12);
		GL20.glVertexAttribPointer(ATTRIB_LOCATION_NORMAL, 		3, GL11.GL_FLOAT, false, 32, 20);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, _mesh.getIndices(), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		
		
		Model result = new Model(vao, vbo, ebo);
		result.setVertexCount(_mesh.getIndices().length);
		
		return result;
	}
	
}
