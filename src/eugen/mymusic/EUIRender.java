package eugen.mymusic;

import eugen.engine.EScreen;
import eugen.engine.ESpriteManager;
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
		GLES20.glUseProgram( mShader );
//		mMVPMat.setMul(mRender.getProjMat(), this.getGLRender().getCamera().mMat );
	
		ESpriteManager mgr = ESpriteManager.getInstance();
		int count = mgr.getSpriteSize();
		
		for( int i = 0; i<count; i++ ){
//			EEntity ent = EEntity.getEntityAt(i);
			ESprite sprite= mgr.getSpriteAt(i);
//			mTemp.setMul( this.m2DMVPMat, sprite.mMat );
			sprite.getFinalMatrix( mTemp, this.m2DMVPMat );
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mTemp.m, 0);
			GLES20.glUniform4fv( muUVInfo, 1, sprite.getUVInfo(), 0);
			GLES20.glUniform4fv( muUVOffsetHandle, 1, sprite.getUVOffset(), 0 );
			GLES20.glUniform4fv( muColorHandle, 1, sprite.getColor().v, 0 );
			if( sprite != null )
				this.drawSprite( sprite );
//			sprite.nextFrame( ETimer.getInst().getTimeDifference() );
		}
	}
	
	public void drawSprite(ESprite sprite){
		if( !sprite.isVisible() ) return;
		EMesh mesh = sprite.getMesh();
		if( mesh == null || !mesh.isValid() )
			return;
		
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
//		GLES20.glUniform4fv( mColorHandle, 1, color, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.mTexId );
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getFaceVexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.mIndexBuffer );
		GLES20.glDisableVertexAttribArray( mPositionHandle );
		GLES20.glDisableVertexAttribArray( mColorHandle );
		GLES20.glDisableVertexAttribArray( mUVHandle );
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
		EScreen.mScreenBBox.mMax.set( w, 0, .0f );
		EScreen.mScreenBBox.mMin.set( 0, -h, .0f);
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
//	protected long mPreSysTime = 0;
}
