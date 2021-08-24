package beleg.core.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class TextureGenerator {

	public static ByteBuffer genTexture(int _w, int _h, int _c) {
		
		byte[] data = new byte[_w * _h * _c];
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		
		int index;
		
		for(int y = 0; y < _h; y++) {
			
			for(int x = 0; x < _w; x++) {
				
				index = _c * (y * _w + x);
				
				for(int i = 0; i < _c; i++) {
					
					data[index + i] = (byte) (((x + y) % 2) * 0xff);
				}
			}
		}
		
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	
}
