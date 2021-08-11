package beleg.core;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class DepthTexture extends Texture {

	@Override
	public void image2D(int _width, int _height, ByteBuffer _data) {
		
		GL11.glTexImage2D(	GL11.GL_TEXTURE_2D, 
							0, 
							GL33.GL_DEPTH_COMPONENT32F, 
							_width, _height, 
							0, 
							GL33.GL_DEPTH_COMPONENT, 
							GL11.GL_FLOAT, 
							_data);
	}
	
}
