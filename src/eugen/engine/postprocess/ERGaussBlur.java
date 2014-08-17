package eugen.engine.postprocess;

import android.content.Context;
import android.opengl.GLES20;
import eugen.engine.EMesh;
import eugen.engine.EScreen;
import eugen.engine.EShaderManager;
import eugen.mymusic.MyGLRenderer;

public class ERGaussBlur extends IEPostProcess{

	// 高斯模糊需要渲染两次
	public ERGaussBlur(MyGLRenderer render, EMesh mesh ) {
		super(render, mesh );
		String d1[] = { "PROCESS_POST_X", "" };
		this.setDefinesForShader( d1 );
		String shaderName = "gauss";
		this.setShaderName( shaderName );
		ERGaussBlur next = new ERGaussBlur( this );
		next.setShaderName( "gauss" );
		this.mNextPost = next;
		// TODO Auto-generated constructor stub
	}
	
	public ERGaussBlur( ERGaussBlur pre  ) {
		super( pre.mRender, pre.mMesh );
//		String d1[] = { "PROCESS_POST_X", "" };
//		this.setDefinesForShader( d1 );
		String shaderName = "gauss";
		this.setShaderName( shaderName );

		// TODO Auto-generated constructor stub
	}
	
	public void initShader(){
		super.initShader();
		if( mShader != -1 )
			mTexSizeHandler = GLES20.glGetUniformLocation(mShader, "u_TexPixelSize" );

	}
	
	protected void onDrawPost(){
		float size[] = {
//				.0f, .0f
				2.0f / (float)EScreen.mWidth,
				2.0f / (float)EScreen.mHeight
			};
		GLES20.glUniform2fv( mTexSizeHandler, 1, size, 0 );
	}
	
	protected int mTexSizeHandler = -1;
}
