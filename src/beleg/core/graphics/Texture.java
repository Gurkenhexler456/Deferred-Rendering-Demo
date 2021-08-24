package beleg.core.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture {

	private int m_TextureID;
	
	public Texture() {
		
		m_TextureID = GL11.glGenTextures();
	}
	
	public void setFilteringAndWrapping(int _filtermode, int _wrapmode) {
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, _wrapmode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, _wrapmode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, _filtermode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, _filtermode);
	}
	
	public void setParameter(int _parameter, int _value) {
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, _parameter, _value);
	}
	
	public void image2D(int _width, int _height, ByteBuffer _data) {
		
		GL11.glTexImage2D(	GL11.GL_TEXTURE_2D, 
							0, 
							GL11.GL_RGB, 
							_width, _height, 
							0, 
							GL11.GL_RGB, 
							GL11.GL_UNSIGNED_BYTE, 
							_data);
	}
	
	public void bind() {
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_TextureID);
	}
	
	public void unbind() {
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void delete() {
		
		GL11.glDeleteTextures(m_TextureID);
	}
	
	public int getID() {
		
		return m_TextureID;
	}
}
