package beleg.core;


import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGammaRamp;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

public class Main {

	private long m_Window;
	private DeferredRenderer m_Renderer;
	
	private GeometryRenderer m_GeometryRenderer;
	private Scene m_Scene;
	
	private Vector2i m_WindowSize = new Vector2i(960, 540);
	
	private Matrix4f m_Projection;
	private Matrix4f m_View;
	private Matrix4f m_Model;
	
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
		
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		
		m_Window = GLFW.glfwCreateWindow(m_WindowSize.x, m_WindowSize.y, "Deferred Rendering Demo", 0, 0);
		
		if(m_Window == 0) {
			
			throw new RuntimeException("Window creation failed");
		}
		
		GLFW.glfwMakeContextCurrent(m_Window);
		
		GL.createCapabilities();
		
		Callback debugProc = GLUtil.setupDebugMessageCallback();
		
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
			
			GLFWGammaRamp gamma = GLFW.glfwGetGammaRamp(pointer);
			
			//System.out.printf("");
			
			GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(pointer);
			
			for(int v = 0; v < buffer.remaining(); v++) {
				
				GLFWVidMode vidmode = buffer.get(v);
				
				System.out.printf("Vidmode %d:\n", v);
				System.out.printf("\tResolution: %d, %d\n", buffer.width(), buffer.height());
				System.out.printf("\tRefresh Rate: %d\n", buffer.refreshRate());
				System.out.printf("\tRGB: %d %d %d\n", buffer.redBits(), buffer.greenBits(), buffer.blueBits());
			}
		}
	}
	
	public void loop() {
		
		m_Renderer = new DeferredRenderer();
		m_Renderer.setup(1280, 720);
		
		m_GeometryRenderer = new GeometryRenderer();
		m_GeometryRenderer.setup();
		
		m_Projection = new Matrix4f().identity();
		m_View = new Matrix4f().identity();
		m_Model = new Matrix4f().identity();
		
		m_GeometryRenderer.setProjection(m_Projection);
		m_GeometryRenderer.setView(m_View);
		m_GeometryRenderer.setModel(m_Model);
		
		m_Scene = new Scene();
		m_Scene.load();
		
		
		float ratio = (float) m_Renderer.getProjection().x / m_Renderer.getProjection().y;
		m_Projection.perspective((float) Math.toRadians(75), ratio, 0.1f, 50.0f);
		
		m_View.rotate((float)Math.toRadians(30), 1, 0, 0);
		m_View.translate(0, -2, -3f);
		
		Vector3f rotation = new Vector3f(0, 1, 0).normalize();
		
		
		Grid grid = new Grid(1, 1);
		Mesh mesh = MeshGenerator.generateGridMesh(grid); 
		
		
		while(! GLFW.glfwWindowShouldClose(m_Window)) {
			
			m_Model.identity();
			m_Model.rotate((float)Math.toRadians(GLFW.glfwGetTime() * 50), rotation);
			m_Model.translate(-0.5f, -0.5f, -0.5f);
			
			
			
			/*
			 * geometry render pass
			 */
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, m_Renderer.getRenderFBO());
			
			m_Renderer.setViewport();
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);	
			//GL11.glLineWidth(5.0f);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			m_GeometryRenderer.use();
			m_Scene.render();
			
			
			/*
			 * deferred render pass
			 */
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	
			GL11.glViewport(0, 0, m_WindowSize.x, m_WindowSize.y);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			
			m_Renderer.render();
			
				
			GLFW.glfwSwapBuffers(m_Window);
			
			GLFW.glfwPollEvents();
		}
		
		m_Renderer.delete();
	}
	
	public void terminate() {
		
		GLFW.glfwDestroyWindow(m_Window);
		
		GLFW.glfwTerminate();
	}

}

