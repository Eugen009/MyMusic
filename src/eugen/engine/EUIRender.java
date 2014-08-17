package eugen.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import eugen.engine.postprocess.ERGaussBlur;
import eugen.engine.postprocess.IEPostProcess;
import eugen.mymusic.MyGLRenderer;
import android.content.Context;
import android.opengl.GLES20;

public class EUIRender implements EIRender {

	public EUIRender( MyGLRenderer render ){
		mRender = render;		
	}
	
	@Override
	public void onSurfaceCreate() {
		// TODO Auto-generated method stub
		mLastPp = 
				new IEPostProcess( this.mRender, null );
//				new ERGaussBlur( this.mRender, null );
		mLastPp.init();
		this.mGaussPost = 
//				new IEPostProcess( mRender, null );
				new ERGaussBlur( this.mRender, mLastPp.getMesh());
		mGaussPost.init();
		
	}

	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		if( this.mShader == -1 ){
			this.createMeshShader();
			if( mShader == -1) return;
		}
		this.collectVisibilty();
		int count = this.mVisibilityCollection.size();
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		GLES20.glClearColor(0, 0, 0, 0);
		GLES20.glUseProgram( mShader );
		mFrameBuffer.bind();
//		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, mFrameBuffer.getFrameBufferId() );
		
		
//        GLES20.glClearColor(1, 1, 1, 1);
//        GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		
		for( int i = 0; i < count; i++ ){
			ESprite sprite= this.mVisibilityCollection.get( i );
			if( sprite != null )
				this.drawSprite( sprite );
		}
		
		//post process	
//        if( this.mGaussPost != null ){
//        	ERTexture tex = mFrameBuffer.getRenderTarget();
//    		mFrameBuffer.setRenderTarget( this.mPPTargetNext );
//    		mPPTargetNext = tex;
//    		mFrameBuffer.bind();
//        	mGaussPost.draw( mPPTargetNext.mTexId );
//        }
//		mFrameBuffer.unBind();
		//mGaussBuffer.bind();
		//if( this.mGaussPost != null ){
		//	mGaussPost.draw( mFrameBuffer.getRenderTargetId() );
		//}
		
        // last process 
        mFrameBuffer.unBind();
//		mGaussBuffer.unBind();
//		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );	
		if( mLastPp != null )
			mLastPp.draw( mFrameBuffer.getRenderTargetId() );
	}
	
	public void drawSprite(ESprite sprite){
		EMesh mesh = sprite.getMesh();
		if( mesh == null || !mesh.isValid() )
			return;
		sprite.getFinalMatrix( mTemp, this.m2DMVPMat );
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mTemp.m, 0);
		GLES20.glUniform4fv( muUVInfo, 1, sprite.getUVInfo(), 0);
		GLES20.glUniform4fv( muUVOffsetHandle, 1, sprite.getUVOffset(), 0 );
		GLES20.glUniform4fv( muColorHandle, 1, sprite.getColor().getData(), 0 );
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		GLES20.glEnable( GLES20.GL_TEXTURE_2D );
		mesh.prepareTex();
		//render mesh
		GLES20.glEnableVertexAttribArray( mPositionHandle );
		GLES20.glEnableVertexAttribArray( mUVHandle );
		GLES20.glEnableVertexAttribArray( this.mColorHandle );	
		GLES20.glVertexAttribPointer( 
				mPositionHandle, 3, GLES20.GL_FLOAT, false, 
				mesh.getVertexStride(), mesh.mVertexBuffer );
		GLES20.glVertexAttribPointer(
				mUVHandle, 2, GLES20.GL_FLOAT, false,
				2*4, mesh.mUVBuffer );
		GLES20.glVertexAttribPointer(
				mColorHandle, 3, GLES20.GL_FLOAT, false,
				3*4, mesh.mColorBuffer );
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if( mesh.mTex != null )
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.mTex.mTexId );
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getFaceVexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.mIndexBuffer );
		GLES20.glDisableVertexAttribArray( mPositionHandle );
		GLES20.glDisableVertexAttribArray( mColorHandle );
		GLES20.glDisableVertexAttribArray( mUVHandle );
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );//unbind texture
	}

	@Override
	public void onSurfaceChanged(int w, int h) {
		float halfw = (float)w * .5f;
		float halfh = (float)h * .5f;
		halfw = 1.0f/halfw;
		halfh = 1.0f/halfh;
		m2DMVPMat = new EMatrix(
			halfw, 0.f, .0f,  0.0f,
			.0f, halfh, .0f, 0.0f,
			0.0f, .0f, 1.f, .0f,
			-1.0f, 1.0f, .0f, 1.0f
		);
		mTemp = new EMatrix();
		EScreen.mWidth = w;
		EScreen.mHeight = h;
		EScreen.mScreenBBox.set( new EVector3( .0f, -h, .0f), new EVector3( w, .0f, .0f));
	}
	
	protected void createMeshShader(){
		Context context = this.mRender.getContext();
		int program = MyShader.createShader( context.getResources(), "screen" );
		muMVPMatrixHandle = GLES20.glGetUniformLocation( program, "uMVPMatrix");
		muUVInfo = GLES20.glGetUniformLocation( program, "uUVInfo");
		muUVOffsetHandle = GLES20.glGetUniformLocation( program, "uUVOffset" ); 
		muColorHandle = GLES20.glGetUniformLocation( program, "uColor" ); 
		mPositionHandle = GLES20.glGetAttribLocation( program, "a_Position" );
		mUVHandle = GLES20.glGetAttribLocation( program, "a_Texcoord" );
		mColorHandle = GLES20.glGetAttribLocation( program, "a_Color" );
		mShader = program;
		
//		if( true ) return;
		// create fbo
//		mPPTarget = this.createRenderTex();
//		mPPTargetNext.clear();
//		mPPTargetNext = ERTextureManager.createRenderTargetTex();
		mFrameBuffer.create();
		mGaussBuffer.create();
	}
	
	protected void collectVisibilty(){
		mVisibilityCollection.clear();
		ESpriteManager mgr = ESpriteManager.getInstance();
		int count = mgr.getSpriteSize();
		
		for( int i = 0; i<count; i++ ){
//			EEntity ent = EEntity.getEntityAt(i);
			ESprite sprite= mgr.getSpriteAt(i);
			if( sprite.isVisible()
				&& EScreen.mScreenBBox.intersectAABB( sprite.getBoundingBox() )){
				this.mVisibilityCollection.add( sprite );
			}
		}
	}
	
	int createRenderTex(){
		int[] buf = new int[1];
	    GLES20.glGenTextures(1, buf, 0 );
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buf[0]);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT );
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT );
//	    GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_GENERATE_MIPMAP, GLES20.GL_TRUE); // automatic mipmap generation included in OpenGL v1.4
//	    Buffer texBuf = null;//new IntBuffer();
	    
		ByteBuffer bb = ByteBuffer.allocateDirect(
				EScreen.mWidth * EScreen.mHeight * 2 );
		bb.order( ByteOrder.nativeOrder() );
		ShortBuffer texBuf = bb.asShortBuffer();
	    GLES20.glTexImage2D(
	    		GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 
	    		EScreen.mWidth, EScreen.mHeight, 
	    		0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_SHORT_4_4_4_4, texBuf );
	    GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );
		return buf[0];
	}
	
	public void clear(){
		int[] buf = new int[1];
		if( this.mLastPp != null ){
			mLastPp.clear();
		}
		if( this.mGaussPost != null ){
			mGaussPost.clear();
		}
//		if( this.mPPTarget != -1 ){
//			int buffers[] = new int[1];
//			buffers[0] = mPPTarget;
//			GLES20.glDeleteTextures( 1, buffers, 0);
//		}
		if( this.mRenderBuffer > 0 ){
			buf[0] = this.mRenderBuffer;
			GLES20.glDeleteRenderbuffers( 1, buf, 1);
		}
		mFrameBuffer.clear();
		mGaussBuffer.clear();
//		mPPTargetNext.clear();
	}
	
	protected EMatrix m2DMVPMat;
	protected EMatrix mTemp;
	protected MyGLRenderer mRender;
	protected int muMVPMatrixHandle = -1;
	protected int muUVInfo = -1;
	protected int muUVOffsetHandle = -1;
	protected int mPositionHandle = -1;
	protected int mUVHandle = -1;
	protected int mColorHandle = -1;
	protected int mShader = -1;
	protected int muColorHandle = -1;
	protected ArrayList<ESprite> mVisibilityCollection = new ArrayList<ESprite>();
	protected boolean mUpdateVisiblilty = false;
	
//	protected int mFrameBuffer = 0;
	protected int mRenderBuffer = 0;
	protected IEPostProcess mLastPp;
	protected IEPostProcess mGaussPost;
	protected ERFrameBuffer mFrameBuffer = new ERFrameBuffer();
	protected ERFrameBuffer mGaussBuffer = new ERFrameBuffer();
//	protected ArrayList<IEPostProcess> mPostProcesses = new ArrayList<IEPostProcess>();
//	protected int mPPTarget = -1;//后处理的渲染目标
//	protected ERTexture mPPTargetNext = new ERTexture();
}
