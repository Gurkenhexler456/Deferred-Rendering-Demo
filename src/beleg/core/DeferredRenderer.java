package beleg.core;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class DeferredRenderer {
	
	private float[] m_Positions = {
		
		-1.0f,	-1.0f,	0.0f,		0.0f,	0.0f,
		 1.0f,	-1.0f,	0.0f,		1.0f,	0.0f,
		 1.0f,	 1.0f,	0.0f,		1.0f,	1.0f,
		-1.0f,	 1.0f,	0.0f,		0.0f,	1.0f
	};
	
	private int[] m_Indices = {
			
		0, 1, 2,
		2, 3, 0
	};
	
	private int m_VAO;	
	private int m_VBO;
	private int m_EBO;
	
	private int m_PositionLocation	= 0;
	private int m_UVLocation		= 1;
	
	private Texture 		m_Albedo;
	private Texture 		m_Position;
	private Texture 		m_Normal;
	private DepthTexture 	m_Depth;
	
	private int m_FBO;
	private int[] m_DrawBuffer = {
		GL33.GL_COLOR_ATTACHMENT0,
		GL33.GL_COLOR_ATTACHMENT1,
		GL33.GL_COLOR_ATTACHMENT2,
	};
	
	private Shader m_DeferredShader;
	
	private Vector2i m_Resolution;
	
	
	public DeferredRenderer() {
		
		m_VAO = GL30.glGenVertexArrays();
		m_VBO = GL15.glGenBuffers();
		m_EBO = GL15.glGenBuffers();
		
		m_FBO = GL33.glGenFramebuffers();
		
		m_Albedo 	= new Texture();
		m_Position 	= new Texture();
		m_Normal 	= new Texture();
		m_Depth 	= new DepthTexture();
		
		m_DeferredShader = new Shader();
		
		m_Resolution = new Vector2i();
	}
	
	/**
	 * sets up the shader, the buffer objects, the textures and the fbo to render the scene to
	 * @param _width the vertical resolution of the textures
	 * @param _height the horizontal resolution of the texture
	 */
	public void setup(int _width, int _height) {
		
		setupTextures(_width, _height);
		setupFramebuffer();
		setupShader();
		setupBuffers();
	}
	
	private void setupTextures(int _width, int _height) {
		
		ByteBuffer data = null;
		
		m_Resolution.set(_width, _height);
		
		m_Position.bind();
		m_Position.image2D(m_Resolution.x, m_Resolution.y, data);
		m_Position.setFilteringAndWrapping(GL11.GL_LINEAR, GL11.GL_REPEAT);
		m_Position.unbind();
		
		m_Albedo.bind();
		m_Albedo.image2D(m_Resolution.x, m_Resolution.y, data);
		m_Albedo.setFilteringAndWrapping(GL11.GL_LINEAR, GL11.GL_REPEAT);
		m_Albedo.unbind();
		
		m_Normal.bind();
		m_Normal.image2D(m_Resolution.x, m_Resolution.y, data);
		m_Normal.setFilteringAndWrapping(GL11.GL_LINEAR, GL11.GL_REPEAT);
		m_Normal.unbind();
		
		m_Depth.bind();
		m_Depth.image2D(m_Resolution.x, m_Resolution.y, data);
		m_Depth.setFilteringAndWrapping(GL11.GL_LINEAR, GL11.GL_REPEAT);
		m_Depth.unbind();
	}
	
	private void setupFramebuffer() {
	
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, m_FBO);
		GL33.glDrawBuffers(m_DrawBuffer);
		GL33.glFramebufferTexture2D(	GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, 
										GL11.GL_TEXTURE_2D, m_Position.getID(), 0);
		GL33.glFramebufferTexture2D(	GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT1, 
										GL11.GL_TEXTURE_2D, m_Albedo.getID(), 0);
		GL33.glFramebufferTexture2D(	GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT2, 
										GL11.GL_TEXTURE_2D, m_Normal.getID(), 0);
		GL33.glFramebufferTexture2D(	GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, 
										GL11.GL_TEXTURE_2D, m_Depth.getID(), 0);
		
	
		int comp = GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER);
		if(comp == GL33.GL_FRAMEBUFFER_COMPLETE) {
			
			System.out.println("Framebuffer complete");
		}
		else {
			
			System.out.println("Framebuffer incomplete");
		}
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER,  0);
	}
	
	private void setupShader() {
		
		String vert_source = Resources.loadFileToString("res/shaders/deferred.vert");
		String frag_source = Resources.loadFileToString("res/shaders/deferred.frag");
		
		int vert_id = m_DeferredShader.addShader(GL20.GL_VERTEX_SHADER, vert_source);
		int frag_id = m_DeferredShader.addShader(GL20.GL_FRAGMENT_SHADER, frag_source);
		
		m_DeferredShader.bindAttribLocation(m_PositionLocation, "in_Position");
		m_DeferredShader.bindAttribLocation(m_UVLocation, "in_UV");
		
		m_DeferredShader.link();
		
		m_DeferredShader.removeShader(vert_id);
		m_DeferredShader.removeShader(frag_id);
		
		// setup texture units
		m_DeferredShader.bind();
		GL20.glUniform1i(m_DeferredShader.getUniformLocation("u_Albedo"), 0);
		GL20.glUniform1i(m_DeferredShader.getUniformLocation("u_Position"), 1);
		GL20.glUniform1i(m_DeferredShader.getUniformLocation("u_Normal"), 2);
		GL20.glUniform1i(m_DeferredShader.getUniformLocation("u_Depth"), 3);
		m_DeferredShader.unbind();
	}
	
	public void setupBuffers() {
	
		GL30.glBindVertexArray(m_VAO);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, m_Positions, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(m_PositionLocation, 3, GL11.GL_FLOAT, false, 20, 0);
		GL20.glVertexAttribPointer(m_UVLocation, 2, GL11.GL_FLOAT, false, 20, 12);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, m_Indices, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
	}
	
	
	public int getRenderFBO() {
		
		return m_FBO;
	}
	
	public Vector2i getProjection() {
		
		return m_Resolution;
	}
	
	public void setViewport() {
		
		GL11.glViewport(0, 0, m_Resolution.x, m_Resolution.y);
	}
	
	public int[] getDrawBuffers() {
		
		return m_DrawBuffer;
	}
	
	public void render() {
		
		
		GL11.glClearColor(0.0f, 0.5f, 0.75f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		m_DeferredShader.bind();
		GL30.glBindVertexArray(m_VAO);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_EBO);
		
		GL20.glEnableVertexAttribArray(m_PositionLocation);
		GL20.glEnableVertexAttribArray(m_UVLocation);
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Albedo.bind();
		GL20.glActiveTexture(GL20.GL_TEXTURE1);
		m_Position.bind();
		GL20.glActiveTexture(GL20.GL_TEXTURE2);
		m_Normal.bind();
		GL20.glActiveTexture(GL20.GL_TEXTURE3);
		m_Depth.bind();
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		m_DeferredShader.unbind();
	}
	
	
	public void delete() {
	
		
		m_DeferredShader.delete();
		m_Albedo.delete();
		m_Position.delete();
		m_Normal.delete();
		GL33.glDeleteFramebuffers(m_FBO);
		GL30.glDeleteVertexArrays(m_VAO);
		GL15.glDeleteBuffers(m_VBO);
		GL15.glDeleteBuffers(m_EBO);
	}

}
