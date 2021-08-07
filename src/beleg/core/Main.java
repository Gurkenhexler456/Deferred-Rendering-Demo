package beleg.core;

import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class Main {

	private long m_Window;
	
	public static void main(String[] args) {
		
		
		Main main = new Main();
		
		main.init();
		main.loop();
		main.terminate();
	}
	
	
	public void init() {
		
		if(! GLFW.glfwInit()) {
			
			throw new RuntimeException("Couldn't initialize GLFW");
		}
		
		
		printMonitorInfo();
		
		GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MINOR, 3);
		//GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		m_Window = GLFW.glfwCreateWindow(960, 540, "Deferred Rendering Demo", 0, 0);
		
		if(m_Window == 0) {
			
			throw new RuntimeException("Window creation failed");
		}
		
		GLFW.glfwMakeContextCurrent(m_Window);
		
		GL.createCapabilities();
		
		String version = GL11.glGetString(GL11.GL_VERSION);
		System.out.printf("Version: %s\n", version);
		
	}
	
	public void printMonitorInfo() {
		
		PointerBuffer monitors = GLFW.glfwGetMonitors();
		long pointer;
		float[] x = new float[1];
		float[] y = new float[1];
		int[] xMM = new int[1];
		int[] yMM = new int[1];
		
		for(int i = 0; i < monitors.remaining(); i++) {
			
			pointer = monitors.get(i);
			String name = GLFW.glfwGetMonitorName(pointer);
			
			System.out.printf("Monitor #%d:\n", i);
			System.out.printf("Name:  %s\n", name);
			
			GLFW.glfwGetMonitorContentScale(pointer, x, y);
			System.out.printf("Content Scale: %f, %f\n", x[0], y[0]);
			
			GLFW.glfwGetMonitorPhysicalSize(pointer, xMM, yMM);
			System.out.printf("Physical Size: %d, %d\n", xMM[0], yMM[0]);
			
			GLFW.glfwGetMonitorPos(pointer, xMM, yMM);
			System.out.printf("Position: %d, %d\n", xMM[0], yMM[0]);
		}
	}
	
	public void loop() {
		
		ByteBuffer data = null;
		
		int image = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, image);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 960, 560, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		
		int fbo = GL33.glGenFramebuffers();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, fbo);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, image, 0);
		
		
		int comp = GL33.glCheckFramebufferStatus(fbo);
		if(comp == GL33.GL_FRAMEBUFFER_COMPLETE) {
			
			System.out.println("Framebuffer complete");
		}
		else {
			
			System.out.println("Framebuffer incomplete");
		}
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		
		while(! GLFW.glfwWindowShouldClose(m_Window)) {
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			GLFW.glfwSwapBuffers(m_Window);
			
			GLFW.glfwPollEvents();
		}
		
		GL33.glDeleteFramebuffers(fbo);
	}
	
	public void terminate() {
		
		GLFW.glfwDestroyWindow(m_Window);
		
		GLFW.glfwTerminate();
	}

}

