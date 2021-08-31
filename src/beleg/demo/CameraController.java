package beleg.demo;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import beleg.core.scene.Camera;


public class CameraController {


    private float m_CameraSpeed = 5.0f;

    private Vector3f m_X_Axis;
    private Vector3f m_Y_Axis;
    private Vector3f m_Z_Axis;

    private float m_Yaw;
    private float m_Pitch;
    private final float MAX_PITCH = (float) Math.toRadians(89.0f);
    private float m_Mouse_Sensitivity = 1.0f;
    
    
    private Camera m_Camera;
    
    private boolean[] m_Keys;
    private Vector2f m_MousePosition;
    private Vector2i m_WindowSize;

    public CameraController(Camera _camera, boolean[] _keys, Vector2f _mousePosition, Vector2i _windowSize) {

    	m_Camera = _camera;
    	
    	m_Keys = _keys;
    	m_MousePosition = _mousePosition;
    	m_WindowSize = _windowSize;
    	
        m_X_Axis = new Vector3f(0, 0, 0);
        m_Y_Axis = new Vector3f(0, 0, 0);
        m_Z_Axis = new Vector3f(0, 0, 0);

        m_Yaw = 0;
        m_Pitch = 0;
    }


    public void updatePosition(float _deltatime) {

        float speed = m_CameraSpeed * _deltatime;
        Vector3f move_direction = new Vector3f(0, 0, 0);

        m_Y_Axis.set(new Vector3f(0, 1, 0));
        
        m_Z_Axis.set(m_Camera.getLookDirection());
        m_Z_Axis.cross(m_Y_Axis, m_X_Axis);

        m_Y_Axis.cross(m_X_Axis, m_Z_Axis);
        
        m_Y_Axis.normalize();
        m_Z_Axis.normalize();
        m_X_Axis.normalize();

        //forward / backward
        if(m_Keys[GLFW.GLFW_KEY_W]) {

            move_direction.add(m_Z_Axis);
        }

        if(m_Keys[GLFW.GLFW_KEY_S]) {

            move_direction.sub(m_Z_Axis);
        }

        // left / right
        if(m_Keys[GLFW.GLFW_KEY_A]) {

            move_direction.sub(m_X_Axis);
        }

        if(m_Keys[GLFW.GLFW_KEY_D]) {

            move_direction.add(m_X_Axis);
        }

        // move
        if(move_direction.length() > 0){

            move_direction.normalize();
            move_direction.mul(speed);
            m_Camera.move(move_direction);
        }
    }

    public void updateRotation() {

        Vector3f look_direction = new Vector3f();

        Vector2f center = new Vector2f(m_WindowSize);
        Vector2f offset = new Vector2f();
        center.mul(0.5f);
        center.sub(m_MousePosition, offset);
        offset.x /= center.x;
        offset.y /= center.y;
        
        m_Yaw -= offset.x * m_Mouse_Sensitivity;
        m_Pitch += offset.y * m_Mouse_Sensitivity;
             
        if(m_Pitch > MAX_PITCH) {
        	
        	m_Pitch = MAX_PITCH;
        }
        else if(m_Pitch < -MAX_PITCH) {
        	
        	m_Pitch = -MAX_PITCH;
        }
        
        look_direction.x = (float) (Math.cos(m_Pitch) * Math.cos(m_Yaw));
        look_direction.y = (float) (Math.sin(m_Pitch));
        look_direction.z = (float) (Math.cos(m_Pitch) * Math.sin(m_Yaw));
        
        look_direction.normalize();

        m_MousePosition.set(center);
        
        m_Camera.setLookDirection(look_direction);
    }

    public void update(float _delta) {

        updatePosition(_delta);
        updateRotation();
    }
    
    public void setMouseSensitivity(float mouseSensitivity) {
    	
    	m_Mouse_Sensitivity = mouseSensitivity;
    }
    
    public float getMouseSensitivity() {
    	
    	return m_Mouse_Sensitivity;
    }
}

