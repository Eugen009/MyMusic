package eugen.engine;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

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
	}

	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		if( this.mShader == -1 ){
			this.createMeshShader();
			if( mShader == -1) return;
		}
//		mMVPMat.setMul(mRender.getProjMat(), this.getGLRender().getCamera().mMat );
	
//		ESpriteManager mgr = ESpriteManager.getInstance();
//		int count = mgr.getSpriteSize();
		this.collectVisibilty();
		int count = this.mVisibilityCollection.size();
		
		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, this.mFrameBuffer );
		GLES20.glUseProgram( mShader );
		
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		
        if( true )
		for( int i = 0; i<count; i++ ){
//			EEntity ent = EEntity.getEntityAt(i);
			ESprite sprite= this.mVisibilityCollection.get( i );
//			mTemp.setMul( this.m2DMVPMat, sprite.mMat );

			if( sprite != null )
				this.drawSprite( sprite );
//			sprite.nextFrame( ETimer.getInst().getTimeDifference() );
		}
		
		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
		
		GLES20.glUseProgram( mShader );
		
		//ºó´¦Àí
		if( mFullSprite != null ){
			this.drawSprite(mFullSprite);
		}
	}
	
	public void drawSprite(ESprite sprite){
//		if( !sprite.isVisible() ) return;

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
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.mTexId );
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
		int backTexId = this.createRenderTex();
		if( this.mFullSprite == null ){
			mFullSprite = new EFullSprite();
			mFullSprite.setTex( backTexId );
		}
		if( true ) return;
		int[] buffers= new int[1];
		GLES20.glGenRenderbuffers( 1, buffers, 0 );
		this.mFrameBuffer = buffers[0];
		if( mFrameBuffer > 0){
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, mFrameBuffer );
			// then create render buffer
//			GLES20.glGenRenderbuffers( 1, buffers, 0 );
//			if( buffers[0] > 0){
//				mRenderBuffer = buffers[0];
				// create the depth buffer
//				GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, mRenderBuffer );
//				GLES20.glRenderbufferStorage( 
//						GLES20.GL_RENDERBUFFER, 
//						GLES20.GL_DEPTH_COMPONENT, 
//						EScreen.mWidth, EScreen.mHeight );
//				GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0);
		        // attach a texture to FBO color attachement point
				GLES20.glFramebufferTexture2D(
						GLES20.GL_FRAMEBUFFER, 
						GLES20.GL_COLOR_ATTACHMENT0, 
						GLES20.GL_TEXTURE_2D, 
						backTexId, 0);
		        // attach a renderbuffer to depth attachment point
//				GLES20.glFramebufferRenderbuffer(
//						GLES20.GL_FRAMEBUFFER, 
//						GLES20.GL_DEPTH_ATTACHMENT, 
//						GLES20.GL_RENDERBUFFER, mRenderBuffer );
//			}
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
			int res = GLES20.glCheckFramebufferStatus( GLES20.GL_FRAMEBUFFER);
			if( res != GLES20.GL_FRAMEBUFFER_COMPLETE ){
				int k= 34;
				k++;
			}
		}	
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
	    
		ByteBuffer bb =  ByteBuffer.allocateDirect(
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
		if( this.mFullSprite != null ){
			this.mFullSprite.onRemove();
			mFullSprite = null;
		}
		if( this.mRenderBuffer > 0 ){
			buf[0] = this.mRenderBuffer;
			GLES20.glDeleteRenderbuffers( 1, buf, 1);
		}
		if( mFrameBuffer > 0 ){
			buf[0] = this.mFrameBuffer;
			GLES20.glDeleteFramebuffers( 1, buf, 1 );
		}
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
	
	protected int mFrameBuffer = 0;
	protected int mRenderBuffer = 0;
//	protected int mBackTexId = 0;
	protected EFullSprite mFullSprite = null;
//	protected long mPreSysTime = 0;
}
