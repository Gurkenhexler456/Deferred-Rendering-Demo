package beleg.core;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import beleg.core.graphics.Material;
import beleg.core.graphics.Model;
import beleg.core.graphics.ModelFactory;
import beleg.core.graphics.Shader;
import beleg.core.graphics.Texture;
import beleg.core.graphics.TextureGenerator;
import beleg.core.graphics.lighting.DirectionalLight;
import beleg.core.graphics.lighting.PointLight;
import beleg.core.scene.Behaviour;
import beleg.core.scene.Camera;
import beleg.core.scene.Scene;
import beleg.core.scene.ecs.Actor;
import beleg.demo.CameraController;
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
	

	public Matrix4f m_WallTransform;
	public Model 	m_WallModel;
	public Shader 	m_WallShader;
	
	public Camera m_Camera;
	public CameraController m_CameraController;
	
	
	public Vector2f m_MousePosition = new Vector2f();
	
	public boolean m_Keys[] = new boolean[512];
	
	public boolean m_MouseLocked = false;
	public boolean m_DeferredWireframe = false;
	public boolean m_ForwardWireframe = false;
	
	public float m_DeltaLock = 0.0f;
	public float m_DeltaDeferredWire = 0.0f;
	public float m_DeltaForwardWire = 0.0f;
	
	
	public float m_MaxKlickTime = 0.2f;
	
	
	
	
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
		
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		
		m_Window = GLFW.glfwCreateWindow(m_WindowSize.x, m_WindowSize.y, "Deferred Rendering Demo", 0, 0);
		
		GLFW.glfwSetKeyCallback(m_Window, this::handleKey);
		GLFW.glfwSetCursorPosCallback(m_Window, this::handleMouse);
		
		if(m_Window == 0) {
			
			throw new RuntimeException("Window creation failed");
		}
		
		GLFW.glfwMakeContextCurrent(m_Window);
		
		GL.createCapabilities();
		
		String version = GL11.glGetString(GL11.GL_VERSION);
		System.out.printf("Version: %s\n", version);
		
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
		
		m_WallShader = buildDefaultShader();
		m_WallTransform = new Matrix4f();
		m_WallModel = 	ModelFactory.buildDefaultModel(
							MeshGenerator.generateGridMesh(new Grid(16, 9)
						));
		
		
		
		float ratio = (float) m_Renderer.getProjection().x / m_Renderer.getProjection().y;
		m_Projection.perspective((float) Math.toRadians(75), ratio, 0.1f, 50.0f);
		
		m_View.translate(0, 1, -8);
		
		
		
		m_Scene = loadTestScene();
		m_Scene.load();
		
		
		m_Texture = new Texture();
		m_Texture.bind();
		m_Texture.setFilteringAndWrapping(GL11.GL_NEAREST, GL11.GL_REPEAT);
		m_Texture.image2D(8, 8, TextureGenerator.genTexture(8, 8, 3));
		m_Texture.unbind();
		

		m_Camera = new Camera();
		m_CameraController = new CameraController(m_Camera, m_Keys, m_MousePosition, m_WindowSize);
		m_CameraController.update(0.0f);
		
		
		long lastFrame, currentFrame;
		float delta;
		
		lastFrame = System.currentTimeMillis();
		
		while(! GLFW.glfwWindowShouldClose(m_Window)) {
		
			
			currentFrame = System.currentTimeMillis();
			delta = (currentFrame - lastFrame) / 1000f;
			lastFrame = currentFrame;
			
			m_DeltaLock += delta;
			m_DeltaDeferredWire += delta;
			m_DeltaForwardWire += delta;
			
			

			checkFlags();
			
			if(m_MouseLocked) {
			
				m_CameraController.update(delta);
			}
			m_View = m_Camera.getViewMatrix();
			
			
			for(Actor actor : m_Scene.getActors()) {
				
				Behaviour b = actor.getComponent(Behaviour.class);
				
				if(b != null) {
					
					b.update();
				}
			}
			
			
			geometryRenderPass();
			
			deferredRenderPass();
			
			
			// updating depth texture
			GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, m_Renderer.getRenderFBO());
			GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0);
			GL33.glBlitFramebuffer(	0, 0, 
									m_Resolution.x, m_Resolution.y, 
									0, 0, 
									m_WindowSize.x, m_WindowSize.y, 
									GL11.GL_DEPTH_BUFFER_BIT, 
									GL11.GL_NEAREST);
			
			
			renderWalls();
			
			
				
			GLFW.glfwSwapBuffers(m_Window);
			
			GLFW.glfwPollEvents();
		}
		
		m_Renderer.delete();
	}
	
	
	public void terminate() {
		
		GLFW.glfwDestroyWindow(m_Window);
		
		GLFW.glfwTerminate();
	}
	
	
	
	public void checkFlags() {
		
		if(m_Keys[GLFW.GLFW_KEY_L] && m_DeltaLock >= m_MaxKlickTime) {
			
			m_DeltaLock = 0;
			m_MouseLocked = !m_MouseLocked;
		}
		
		if(m_Keys[GLFW.GLFW_KEY_1] && m_DeltaDeferredWire >= m_MaxKlickTime) {
						
			m_DeltaDeferredWire = 0;
			m_DeferredWireframe = !m_DeferredWireframe;
		}
		
		if(m_Keys[GLFW.GLFW_KEY_2] && m_DeltaForwardWire >= m_MaxKlickTime) {
			
			m_DeltaForwardWire = 0;
			m_ForwardWireframe = !m_ForwardWireframe;
		}
	}
	
	public void geometryRenderPass() {
		
		
		m_ModelTerrain.identity();
		m_ModelTerrain.translate(-8, -2, -8);
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, m_Renderer.getRenderFBO());
		
		m_Renderer.setViewport();
		GL11.glClearColor(0, 0, 0, 1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		if(m_DeferredWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
		else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
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
	}
	
	public void deferredRenderPass() {
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);

		GL11.glViewport(0, 0, m_WindowSize.x, m_WindowSize.y);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		
		float lightXDir = (float) Math.cos(GLFW.glfwGetTime() * 0.1);
		float lightYDir = (float) Math.sin(GLFW.glfwGetTime() * 0.1);
		
		float dtAng = (float) GLFW.glfwGetTime();
		float rAng = dtAng;
		float gAng = (float)((1.0f / 3.0f) * Math.PI * 2.0f) + dtAng;
		float bAng = (float)((2.0f / 3.0f) * Math.PI * 2.0f) + dtAng;
		
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
		m_Renderer.setLight(3, new DirectionalLight(new Vector3f(lightXDir,  lightYDir, 0), new Vector3f(1)));
		
		m_Renderer.render();
	}
	
	public void renderWalls() {
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		if(m_ForwardWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
		else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
		m_GeometryRenderer.useShader(m_WallShader);
		
		
		// position wall
		m_WallTransform.identity();
		m_WallTransform.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
		m_WallTransform.translate(-8, 0, -4.5f);
		m_WallTransform.translate(0, 8, 2.5f);
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Renderer.getPositionTexture().bind();
		
		m_WallShader.setMat4("u_Projection", m_Projection);
		m_WallShader.setMat4("u_View", m_View);
		m_WallShader.setMat4("u_Model", m_WallTransform);
		
		m_GeometryRenderer.render(m_WallModel);
		
		// color wall
		m_WallTransform.identity();
		m_WallTransform.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
		m_WallTransform.rotate((float) Math.toRadians(-90), new Vector3f(0, 0, 1));
		m_WallTransform.translate(-8, 0, -4.5f);
		m_WallTransform.translate(0, 8, 2.5f);
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Renderer.getAlbedoTexture().bind();
		
		m_WallShader.setMat4("u_Model", m_WallTransform);
		
		m_GeometryRenderer.render(m_WallModel);
		
		// normal wall
		m_WallTransform.identity();
		m_WallTransform.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
		m_WallTransform.rotate((float) Math.toRadians(90), new Vector3f(0, 0, 1));
		m_WallTransform.translate(-8, 0, -4.5f);
		m_WallTransform.translate(0, 8, 2.5f);
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Renderer.getNormalTexture().bind();
		
		m_WallShader.setMat4("u_Model", m_WallTransform);
		
		m_GeometryRenderer.render(m_WallModel);
		
		// depth wall
		m_WallTransform.identity();
		m_WallTransform.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
		m_WallTransform.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1));
		m_WallTransform.translate(-8, 0, -4.5f);
		m_WallTransform.translate(0, 8, 2.5f);
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		m_Renderer.getDepthTexture().bind();
		
		m_WallShader.setMat4("u_Model", m_WallTransform);
		
		m_GeometryRenderer.render(m_WallModel);
	}
	
	
	
	
	public Scene loadTestScene() {
		
		Material defaultMaterial = new Material(buildDeferredShader());
		
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
	
	public Shader buildDeferredShader() {
		
		Shader shader = new Shader();
		
		String vert_source = Resources.loadFileToString("res/shaders/default_deferred.vert");
		String frag_source = Resources.loadFileToString("res/shaders/default_deferred.frag");
		
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
	
	public Shader buildDefaultShader() {
		
		Shader shader = new Shader();
		
		String vert_source = Resources.loadFileToString("res/shaders/default.vert");
		String frag_source = Resources.loadFileToString("res/shaders/default.frag");
		
		int vert_id = shader.addShader(GL20.GL_VERTEX_SHADER, vert_source);
		int frag_id = shader.addShader(GL20.GL_FRAGMENT_SHADER, frag_source);
		
		shader.bindAttribLocation(0, "in_Position");
		shader.bindAttribLocation(1, "in_UV");
		shader.bindAttribLocation(2, "in_Normal");
		
		shader.link();
		
		shader.removeShader(vert_id);
		shader.removeShader(frag_id);
		
		return shader;
	}



	public void handleKey(long window, int key, int scancode, int action, int mods) {
		
		if(action == GLFW.GLFW_PRESS) {
			
			m_Keys[key] = true;
		}
		else if(action == GLFW.GLFW_RELEASE) {
			
			m_Keys[key] = false;
		}
		
	}


	public void handleMouse(long window, double xpos, double ypos) {
		
		m_MousePosition.set(xpos, ypos);
		
		if(m_MouseLocked) {
			
			GLFW.glfwSetCursorPos(m_Window, m_WindowSize.x >> 1, m_WindowSize.y >> 1);
		}
	}

	
	
	
}

