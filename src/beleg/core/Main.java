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

import beleg.core.graphics.Material;
import beleg.core.graphics.Model;
import beleg.core.graphics.ModelFactory;
import beleg.core.graphics.Shader;
import beleg.core.graphics.Texture;
import beleg.core.graphics.TextureGenerator;
import beleg.core.graphics.lighting.DirectionalLight;
import beleg.core.graphics.lighting.PointLight;
import beleg.core.scene.Behaviour;
import beleg.core.scene.Scene;
import beleg.core.scene.ecs.Actor;
import beleg.demo.RotationBehaviour;

public class Main {

	private long m_Window;
	private DeferredRenderer m_Renderer;
	
	private GeometryRenderer m_GeometryRenderer;
	private Scene m_Scene;
	
	private Vector2i m_WindowSize = new Vector2i(960, 540);
	private Vector2i m_Resolution = new Vector2i();
	
	private Matrix4f m_Projection;
	private Matrix4f m_View;
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
	
	public void setRenderResolution(float _percentage) {
		
		m_Resolution.x = (int) (m_Resolution.x * _percentage);
		m_Resolution.y = (int) (m_Resolution.y * _percentage);
	}
	
	public void loop() {
		
		m_Resolution.set(m_WindowSize);
		setRenderResolution(1);
		
		
		m_Renderer = new DeferredRenderer();
		m_Renderer.setup(m_Resolution.x, m_Resolution.y);
		
		m_GeometryRenderer = new GeometryRenderer();
		
		m_Projection = new Matrix4f().identity();
		m_View = new Matrix4f().identity();
		
		m_ModelTerrain = new Matrix4f().identity();
		
		
		
		float ratio = (float) m_Renderer.getProjection().x / m_Renderer.getProjection().y;
		m_Projection.perspective((float) Math.toRadians(75), ratio, 0.1f, 50.0f);
		
		m_View.rotate((float)Math.toRadians(30), 1, 0, 0);
		m_View.translate(0, -4, -8);
		
		Vector3f rotation = new Vector3f(0, 1, 0).normalize();
		
		
		m_Scene = loadTestScene();
		m_Scene.load();
		
		
		m_Texture = new Texture();
		m_Texture.bind();
		m_Texture.setFilteringAndWrapping(GL11.GL_NEAREST, GL11.GL_REPEAT);
		m_Texture.image2D(16, 16, TextureGenerator.genTexture(16, 16, 3));
		m_Texture.unbind();
		
		float lightXDir, lightYDir;
		
		
		while(! GLFW.glfwWindowShouldClose(m_Window)) {
		
			for(Actor actor : m_Scene.getActors()) {
				
				Behaviour b = actor.getComponent(Behaviour.class);
				
				if(b != null) {
					
					b.update();
				}
			}
			
			
			m_ModelTerrain.identity();
			m_ModelTerrain.translate(-8, -2, -8);
			
			float pos = (float) Math.sin(Math.toRadians(-GLFW.glfwGetTime() * 25)) + 3;
			float angle = (float) Math.toRadians(-GLFW.glfwGetTime() * 25);
			
			
			
			lightXDir = (float) Math.cos(GLFW.glfwGetTime() * 0.1);
			lightYDir = (float) Math.sin(GLFW.glfwGetTime() * 0.1);
			
			/*
			 * geometry render pass
			 */
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, m_Renderer.getRenderFBO());
			
			m_Renderer.setViewport();
			GL11.glClearColor(0, 0, 0, 1.0f);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);	
			//GL11.glLineWidth(5.0f);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			//m_Scene.render();
			
			for(Actor actor : m_Scene.getActors()) {
			
				Material material = actor.getComponent(Material.class);
				Model model = actor.getComponent(Model.class);
				material.bind();
				
				GL20.glActiveTexture(GL20.GL_TEXTURE0);
				m_Texture.bind();
				
				material.getShader().setMat4("u_Projection", m_Projection);
				material.getShader().setMat4("u_View", m_View);
				material.getShader().setMat4("u_Model", actor.getTransform());
				
				
				m_GeometryRenderer.render(model);
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
													(float)Math.sin(rAng), 
													3 * (float)Math.cos(rAng)), 
									new Vector3f(1, 0, 0)));
			m_Renderer.setLight(1, 
					new PointLight(	new Vector3f(	3 * (float)Math.sin(gAng), 
													(float)Math.sin(gAng),
													3 * (float)Math.cos(gAng)), 
									new Vector3f(0, 1, 0)));
			m_Renderer.setLight(2, 
					new PointLight(	new Vector3f(	3 * (float)Math.sin(bAng), 
													(float)Math.sin(bAng),
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
	
	
	
	
	public Scene loadTestScene() {
		
		Material defaultMaterial = new Material(buildDefaultShader());
		
		Actor cube = new Actor();
		cube.addComponent(defaultMaterial);
		cube.addComponent(
				ModelFactory.buildDefaultModel(
						new Mesh(MeshGenerator.m_CubeData, MeshGenerator.m_CubeIndex)
				));
		cube.addComponent(new RotationBehaviour(cube, new Vector3f(1, 0, 0)));
		
		Actor cube2 = new Actor();
		cube2.addComponent(defaultMaterial);
		cube2.addComponent(
				ModelFactory.buildDefaultModel(
						new Mesh(MeshGenerator.m_CubeData, MeshGenerator.m_CubeIndex)
				));
		cube2.addComponent(new RotationBehaviour(cube2, new Vector3f(0, 1, 0)));
		
		Actor cube3 = new Actor();
		cube3.addComponent(defaultMaterial);
		cube3.addComponent(
				ModelFactory.buildDefaultModel(
						new Mesh(MeshGenerator.m_CubeData, MeshGenerator.m_CubeIndex)
				));
		cube3.addComponent(new RotationBehaviour(cube3, new Vector3f(0, 0, 1)));
		
		
		Actor terrain = new Actor();
		terrain.addComponent(defaultMaterial);
		terrain.addComponent(
				ModelFactory.buildDefaultModel(
						MeshGenerator.generateGridMesh(new Grid(16, 16))
				));
		m_ModelTerrain = terrain.getTransform();
		
		Scene result = new Scene();
		
		result.addActor(cube);
		result.addActor(terrain);
		result.addActor(cube2);
		result.addActor(cube3);
		
		
		return result;
	}
	
	public Shader buildDefaultShader() {
		
		Shader shader = new Shader();
		
		String vert_source = Resources.loadFileToString("res/shaders/default.vert");
		String frag_source = Resources.loadFileToString("res/shaders/default.frag");
		
		int vert_id = shader.addShader(GL20.GL_VERTEX_SHADER, vert_source);
		int frag_id = shader.addShader(GL20.GL_FRAGMENT_SHADER, frag_source);
		
		shader.bindAttribLocation(GeometryRenderer.m_PositionLocation, "in_Position");
		shader.bindAttribLocation(GeometryRenderer.m_UVLocation, "in_UV");
		shader.bindAttribLocation(GeometryRenderer.m_NormalLocation, "in_Normal");
		
		shader.bindFragDataLocation(0, "out_Position");
		shader.bindFragDataLocation(1, "out_Albedo");
		shader.bindFragDataLocation(2, "out_Normal");
		
		shader.link();
		
		shader.removeShader(vert_id);
		shader.removeShader(frag_id);
		
		return shader;
	}
	
}

