package eugen.mymusic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Triangle {
	private FloatBuffer vertexBuffer;
	public final float poses[] = {
			.0f, .6f, .0f,
			-.5f, -.3f, .0f,
			.5f, -0.3f, .0f
		};
	public Triangle(){
		ByteBuffer bb =  ByteBuffer.allocateDirect(
				poses.length * 4
				);
		bb.order( ByteOrder.nativeOrder() );
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put( poses );
		vertexBuffer.position( 0 );
		

	}
	
	public void draw(){

	}
	
	protected float color[] = { .6f, .7f, .2f, 1.0f };
	protected int vertexCount = 3;
	protected int mProgram = 0;
	protected int mPositionHandle = 0;
	protected int mColorHandle = 0;
	
	
}
