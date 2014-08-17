package eugen.engine;

import android.opengl.GLES20;

public class ERFrameBuffer {
	public int getRenderTargetId(){
		return mRenderTarget.mTexId;
	}
	
	public ERTexture getRenderTarget(){
		return mRenderTarget;
	}
	
	public int getFrameBufferId(){
		return mFrameBuffer;
	}
	
	public int createRenderBuffer(){
		int buf[]= { -1 };
		GLES20.glGenRenderbuffers( 1, buf, 0 );
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, buf[0] );
		GLES20.glRenderbufferStorage( GLES20.GL_RENDERBUFFER, 
				GLES20.GL_DEPTH_COMPONENT, EScreen.mWidth, EScreen.mHeight );
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0 );
		return buf[0];
	}
	
	public int create(){
		this.clear();
		int res = -1;
//		if( true ) return;
		// create fbo
		mRenderTarget.dispose();
		mRenderTarget = ERTextureManager.getInst().createRenderTargetTex();
		mRenderTarget.addRef();
//		if( true ) return;
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
						mRenderTarget.mTexId, 0);
		        // attach a renderbuffer to depth attachment point
//				mDepthBuffer = this.createRenderBuffer();
//				GLES20.glFramebufferRenderbuffer(
//						GLES20.GL_FRAMEBUFFER, 
//						GLES20.GL_DEPTH_ATTACHMENT, 
//						GLES20.GL_RENDERBUFFER, mDepthBuffer );
//			}
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
			res = GLES20.glCheckFramebufferStatus( GLES20.GL_FRAMEBUFFER);
		}
		return res;	
	}
	
	void bind(){
		if( mFrameBuffer != -1){
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, mFrameBuffer );
			GLES20.glClearColor( 0, 0, 0, 0 );
			GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );// | GLES20.GL_DEPTH_BUFFER_BIT );
			
		}
	}
	
	void unBind(){
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0 );
	}
	
	void setRenderTarget( ERTexture tex ){
		this.mRenderTarget = tex;
		if( mFrameBuffer > 0){
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, mFrameBuffer );
				GLES20.glFramebufferTexture2D(
						GLES20.GL_FRAMEBUFFER, 
						GLES20.GL_COLOR_ATTACHMENT0, 
						GLES20.GL_TEXTURE_2D, 
						mRenderTarget.mTexId, 0);
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
		}
	}
	
	void clear(){
		if( mFrameBuffer != -1 ){
			int tmp[] = { mFrameBuffer };
			GLES20.glDeleteFramebuffers( GLES20.GL_FRAMEBUFFER, tmp, 0 );
			mFrameBuffer = -1;
		}
		if( this.mDepthBuffer != -1 ){
			int tmp[] = { mDepthBuffer };
			GLES20.glDeleteRenderbuffers( GLES20.GL_RENDERBUFFER, tmp, 0 );
		}
		mRenderTarget.dispose();
	}
	
	protected ERTexture mRenderTarget= new ERTexture( -1 );
	protected int mFrameBuffer = -1;
	protected int mDepthBuffer = -1;
}
