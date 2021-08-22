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
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import beleg.core.graphics.Model;
import beleg.core.graphics.ModelFactory;
import beleg.core.light.DirectionalLight;
import beleg.core.light.PointLight;

public class Main {

	private long m_Window;
	private DeferredRenderer m_Renderer;
	
	private GeometryRenderer m_GeometryRenderer;
	private Scene m_Scene;
	
	private Vector2i m_WindowSize = new Vector2i(960, 540);
	
	private Matrix4f m_Projection;
	private Matrix4f m_View;
	private Matrix4f m_Model;
	private Matrix4f m_ModelTerrain;
	
	public Texture m_Texture;
	
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
		m_Renderer.setup(m_WindowSize.x, m_WindowSize.y);
		
		m_GeometryRenderer = new GeometryRenderer();
		
		m_Projection = new Matrix4f().identity();
		m_View = new Matrix4f().identity();
		
		m_Model = new Matrix4f().identity();
		m_ModelTerrain = new Matrix4f().identity();
		
		
		m_Scene = new Scene();
		m_Scene.load();
		
		
		float ratio = (float) m_Renderer.getProjection().x / m_Renderer.getProjection().y;
		m_Projection.perspective((float) Math.toRadians(75), ratio, 0.1f, 50.0f);
		
		m_View.rotate((float)Math.toRadians(30), 1, 0, 0);
		m_View.translate(0, -2, -4);
		
		Vector3f rotation = new Vector3f(0, 1, 0).normalize();
		
		
		Grid grid = new Grid(1, 1);
		Mesh mesh = MeshGenerator.generateGridMesh(grid); 
		
		
		int projLoc = m_Scene.getCurrentShader().getUniformLocation("u_Projection");
		int viewLoc = m_Scene.getCurrentShader().getUniformLocation("u_View");
		int modelLoc = m_Scene.getCurrentShader().getUniformLocation("u_Model");
		
		m_Scene.getCurrentShader().setMat4(projLoc, m_Projection);
		m_Scene.getCurrentShader().setMat4(viewLoc, m_View);
		
		Model cubeModel = ModelFactory.storeMesh(new Mesh(MeshGenerator.m_CubeData, MeshGenerator.m_CubeIndex));
		Model terrainModel = ModelFactory.storeMesh(MeshGenerator.generateGridMesh(new Grid(16, 16)));
		
		Actor cube = new Actor(m_Model, cubeModel);
		Actor terrain = new Actor(m_ModelTerrain, terrainModel);
		
		m_Scene.addActor(cube);
		m_Scene.addActor(terrain);
		
		m_Texture = new Texture();
		m_Texture.bind();
		m_Texture.setFilteringAndWrapping(GL11.GL_NEAREST, GL11.GL_REPEAT);
		m_Texture.image2D(16, 16, TextureGenerator.genTexture(16, 16, 3));
		m_Texture.unbind();
		
		float lightXDir, lightYDir;
		
		
		while(! GLFW.glfwWindowShouldClose(m_Window)) {
		
			m_ModelTerrain.identity();
			m_ModelTerrain.translate(-8, 0, -8);
			
			m_Model.identity();
			m_Model.rotate((float) Math.toRadians(-GLFW.glfwGetTime() * 25), new Vector3f(0, 1, 0));
			m_Model.translate(-0.5f, 0, -0.5f);
			
			
			lightXDir = (float) Math.cos(GLFW.glfwGetTime() * 0.1);
			lightYDir = (float) Math.sin(GLFW.glfwGetTime() * 0.1);
			
			/*
			 * geometry render pass
			 */
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, m_Renderer.getRenderFBO());
			
			m_Renderer.setViewport();
			GL11.glClearColor(1.0f, 0.5f, 0.25f, 1.0f);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);	
			//GL11.glLineWidth(5.0f);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			//m_Scene.render();
			
			m_GeometryRenderer.useShader(m_Scene.getCurrentShader());
			GL20.glActiveTexture(GL20.GL_TEXTURE0);
			m_Texture.bind();
			for(Actor m : m_Scene.getModels()) {
			
				m_Scene.getCurrentShader().setMat4(modelLoc, m.m_Transform);
				m_GeometryRenderer.render(m.m_Model);
			}
			
			
			/*
			 * deferred render pass
			 */
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	
			GL11.glViewport(0, 0, m_WindowSize.x, m_WindowSize.y);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			
			
			float dtAng = (float) GLFW.glfwGetTime();
			float rAng = dtAng;
			float gAng = (float)((1.0f / 3.0f) * Math.PI * 2.0f) + dtAng;
			float bAng = (float)((2.0f / 3.0f) * Math.PI * 2.0f) + dtAng;
			
			m_Renderer.setLight(0, new DirectionalLight(new Vector3f(lightXDir,  lightYDir, 0), new Vector3f(1)));
			m_Renderer.setLight(0, 
					new PointLight(	new Vector3f(	3 * (float)Math.sin(rAng), 
													3, 
													3 * (float)Math.cos(rAng)), 
									new Vector3f(1, 0, 0)));
			m_Renderer.setLight(1, 
					new PointLight(	new Vector3f(	3 * (float)Math.sin(gAng), 
													3, 
													3 * (float)Math.cos(gAng)), 
									new Vector3f(0, 1, 0)));
			m_Renderer.setLight(2, 
					new PointLight(	new Vector3f(	3 * (float)Math.sin(bAng), 
													3, 
													3 * (float)Math.cos(bAng)), 
									new Vector3f(0, 0, 1)));
			m_Renderer.setAmbient(new Vector3f(0.2f));
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

