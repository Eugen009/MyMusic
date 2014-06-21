package eugen.mymusic;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyGLRenderer implements GLSurfaceView.Renderer{

	public MyGLRenderer( Context context ){
		mCamera = new ECamera();
		mContext = context;
		mRenders = new ArrayList<EIRender>();
		this.addRender( new EMeshRender(this));
		this.addRender( new EUIRender( this ));
	}
	
	public void addRender( EIRender render ){
		mRenders.add(render);
	}
	
	public Context getContext(){return mContext;}
	
	public ECamera getCamera(){ return mCamera; }
	
	public boolean checkSupportExt( GL10 gl, String ext ){
		String extes = GLES20.glGetString( GLES20.GL_EXTENSIONS );
		return extes.indexOf( ext ) >=0 ;
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		// TODO Auto-generated method stub
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
//		drawEntities();
		int size = mRenders.size();
		for( int i = 0; i<size;i ++ ){
			mRenders.get(i).onDrawFrame();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int w, int h) {
		// TODO Auto-generated method stub
		GLES20.glViewport( 0, 0, w, h );
		float ratio = (float) ((float)w/(float)h / Math.tan( Math.toRadians(mCamera.fov[0]*.5f) ));
		float yscale = (float) (1.0f / Math.tan( Math.toRadians( mCamera.fov[1]*.5f) ));
		Matrix.frustumM(mProjMat.m, 0, -ratio, ratio, -yscale, yscale, 3, 7);

		int size = mRenders.size();
		for( int i = 0; i<size;i ++ ){
			mRenders.get(i).onSurfaceChanged(w, h);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor( .0f, .0f, .0f, 1.0f);
		mProjMat = new EMatrix();
//		mMVPMat = new EMatrix();	
		int size = mRenders.size();
		for( int i = 0; i<size;i ++ ){
			mRenders.get(i).onSurfaceCreate();
		}
	}
	
//	protected void createMeshShader(){
//		int program = MyShader.createShader( mContext.getResources(), "mesh" );
//		muMVPMatrixHandle = GLES20.glGetUniformLocation( program, "uMVPMatrix");
//		mPositionHandle = GLES20.glGetAttribLocation( program, "vPosition" );
//		mUVHandle = GLES20.glGetAttribLocation( program, "a_texcoord" );
//		mColorHandle = GLES20.glGetUniformLocation( program, "vColor" );
//		mMeshShader = program;
//	}
	
//	protected void drawEntities(){
//		int count = EEntity.getEntityCount();
//		if( count == 0)
//			return;
//		if( mMeshShader == -1 )
//			createMeshShader();
//		GLES20.glUseProgram( mMeshShader );
//		mMVPMat.setMul(mProjMat, mCamera.mMat );
//		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMat.m, 0);
//		for( int i = 0; i<count; i++ ){
//			EEntity ent = EEntity.getEntityAt(i);
//			if( ent != null )
//				this.drawEntity( ent );
//		}
//	}
	
//	protected void draw2D(){
//		
//	}
	
//	protected void drawEntity( EEntity ent ){
//		EMesh mesh = ent.getMesh();
//		if( mesh == null || !mesh.isValid() )
//			return;
//		
//		GLES20.glEnable( GLES20.GL_TEXTURE_2D );
//		mesh.prepareTex();
//		//render mesh
//		GLES20.glEnableVertexAttribArray( mPositionHandle );
//		GLES20.glEnableVertexAttribArray( mUVHandle );
//		GLES20.glVertexAttribPointer( 
//				mPositionHandle, 3, GLES20.GL_FLOAT, false, 
//				mesh.getVertexStride(), mesh.mVertexBuffer );
//		GLES20.glVertexAttribPointer(
//				mUVHandle, 2, GLES20.GL_FLOAT, false,
//				2*4, mesh.mUVBuffer );
//		GLES20.glUniform4fv( mColorHandle, 1, color, 0);
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.mTexId );
//		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getFaceVexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.mIndexBuffer );
//		GLES20.glDisableVertexAttribArray( mPositionHandle );
//		GLES20.glDisableVertexAttribArray( mUVHandle );
//	}
	
	public EMatrix getProjMat(){
		return mProjMat;
	}
	
	protected float color[] = { 1.f, 1.f, 1.f, 1.0f };
	
//	protected int mMeshShader = -1;
//	protected int m2DShader = -1;
//	protected int mPositionHandle = 0;
//	protected int mUVHandle = 0;
//	protected int mColorHandle = 0;
//	protected int muMVPMatrixHandle = 0;
	protected ECamera mCamera;
	protected EMatrix mProjMat;
//	protected EMatrix mMVPMat;
//	protected EMatrix m2DMVPMat;
	
	protected Context mContext;
	
	List<EIRender> mRenders;

}
