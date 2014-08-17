package eugen.engine;

import eugen.mymusic.MyGLRenderer;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class EMeshRender implements EIRender{

	public EMeshRender( MyGLRenderer render ){
//		this.mContext = context;
		this.mRender = render;
		mMVPMat = new EMatrix();
	}
	
	@Override
	public void onSurfaceCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		drawEntities();
	}

	@Override
	public void onSurfaceChanged( int w, int h ) {
		// TODO Auto-generated method stub
	}
	
	public MyGLRenderer getGLRender() {
		return mRender;
	}
	
	protected void drawEntities(){
		int count = EEntity.getEntityCount();
		if( count == 0)
			return;
		if( mMeshShader == -1 )
			createMeshShader();
		GLES20.glUseProgram( mMeshShader );
		mMVPMat.setMul(mRender.getProjMat(), this.getGLRender().getCamera().mMat );
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMat.m, 0);
		for( int i = 0; i<count; i++ ){
			EEntity ent = EEntity.getEntityAt(i);
			if( ent != null )
				this.drawEntity( ent );
		}
	}
	
	protected void createMeshShader(){
		Context context = this.mRender.getContext();
		int program = MyShader.createShader( context.getResources(), "mesh" );
		muMVPMatrixHandle = GLES20.glGetUniformLocation( program, "uMVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation( program, "vPosition" );
		mUVHandle = GLES20.glGetAttribLocation( program, "a_texcoord" );
		mColorHandle = GLES20.glGetUniformLocation( program, "vColor" );
		mMeshShader = program;
	}
	
	protected void drawEntity( EEntity ent ){
		EMesh mesh = ent.getMesh();
		if( mesh == null || !mesh.isValid() )
			return;
		
		GLES20.glEnable( GLES20.GL_TEXTURE_2D );
		mesh.prepareTex();
		//render mesh
		GLES20.glEnableVertexAttribArray( mPositionHandle );
		GLES20.glEnableVertexAttribArray( mUVHandle );
		GLES20.glVertexAttribPointer( 
				mPositionHandle, 3, GLES20.GL_FLOAT, false, 
				mesh.getVertexStride(), mesh.mVertexBuffer );
		GLES20.glVertexAttribPointer(
				mUVHandle, 2, GLES20.GL_FLOAT, false,
				2*4, mesh.mUVBuffer );
		GLES20.glUniform4fv( mColorHandle, 1, color, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if( mesh.mTex != null )
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.mTex.mTexId );
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getFaceVexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.mIndexBuffer );
		GLES20.glDisableVertexAttribArray( mPositionHandle );
		GLES20.glDisableVertexAttribArray( mUVHandle );
	}
	
	public void clear(){}
	
	protected float color[] = { 1.f, 1.f, 1.f, 1.0f };
	
	protected int mMeshShader = -1;
	protected int mPositionHandle = 0;
	protected int mUVHandle = 0;
	protected int mColorHandle = 0;
	protected int muMVPMatrixHandle = 0;
//	protected ECamera mCamera;
	protected EMatrix mMVPMat;
	
	protected Context mContext;
	protected MyGLRenderer mRender;



}
