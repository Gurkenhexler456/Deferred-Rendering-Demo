package beleg.core.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class FloatTexture extends Texture{

	@Override
	public void image2D(int _width, int _height, ByteBuffer _data) {
		
		GL11.glTexImage2D(	GL11.GL_TEXTURE_2D, 
							0, 
							GL33.GL_RGB32F, 
							_width, _height, 
							0, 
							GL33.GL_RGB, 
							GL11.GL_FLOAT, 
							_data);
	}
	
}
