package beleg.core;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Shader {

	
	public int m_ProgramID;
	
	
	public Shader() {
		
		this(GL20.glCreateProgram());
	}
	
	public Shader(int programID) {
		
		m_ProgramID = programID;
	}
	
	
	
	public int addShader(int type, String source) {
		
		int shader = GL20.glCreateShader(type);
	
		GL20.glShaderSource(shader, source);
		
		compile(shader);
		
		GL20.glAttachShader(m_ProgramID, shader);
		
		return shader;
	}
	
	private void compile(int shader) {
		
		GL20.glCompileShader(shader);
		
		if(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader));
		}
	}
	
	public void link() {
		
		GL20.glLinkProgram(m_ProgramID);
		
		if(GL20.glGetProgrami(m_ProgramID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			
			throw new RuntimeException(GL20.glGetProgramInfoLog(m_ProgramID));
		}
	}
	
	public void bindAttribLocation(int _index, String _name) {
		
		GL20.glBindAttribLocation(m_ProgramID, _index, _name);
	}
	
	public void bindFragDataLocation(int _index, String _name) {
		
		GL30.glBindFragDataLocation(m_ProgramID, _index, _name);
	}
	
	public void removeShader(int shader) {
		
		GL20.glDetachShader(m_ProgramID, shader);
		GL20.glDeleteShader(shader);
	}
	
	public void delete() {
		
		GL20.glDeleteProgram(m_ProgramID);
	}
	
	
	public void bind() {
		
		GL20.glUseProgram(m_ProgramID);
	}
	
	public void unbind() {
		
		GL20.glUseProgram(0);
	}
	
	
	public int getAttributeLocation(String _name) {
		
		return GL20.glGetAttribLocation(m_ProgramID, _name);
	}
	
	public int getUniformLocation(String _name) {
		
		return GL20.glGetUniformLocation(m_ProgramID, _name);
	}
	
	
	public int getProgram() {
		return m_ProgramID;
	}
	
	
	public void setMat4(int _location, Matrix4f _matrix) {
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer = _matrix.get(buffer);
		
		this.bind();
		GL20.glUniformMatrix4fv(_location, false, buffer);
	}
	
}
