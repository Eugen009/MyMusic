package eugen.engine.postprocess;

import eugen.engine.EMesh;
import eugen.engine.EMeshBuilder;
import eugen.engine.EShaderManager;
import eugen.mymusic.MyGLRenderer;
import android.content.Context;
import android.opengl.GLES20;

public class IEPostProcess {
	public IEPostProcess( MyGLRenderer render, EMesh mesh ){
		mRender = render;
		this.mMesh = mesh;
//		createMesh();
	}
	
	public void init(){
		if( mMesh == null )
			this.createMesh();
		this.initShader();
		if( this.mNextPost != null )
			mNextPost.init();
	}
	
	public void clear(){
		if( mShader != -1 ){
			GLES20.glDeleteShader( mShader );
		}
		this.destroyMesh();
		if( this.mNextPost != null ){
			mNextPost.clear();
		}
	}
	
	public void setMesh( EMesh mesh ){
		if( mesh == mMesh )return;
		this.destroyMesh();
		this.mMesh = mesh;
	}
	
	public EMesh getMesh() {
		return mMesh;
	}
	
	public void setMeshWithPost( EMesh mesh ){
		this.setMesh( mesh );
		if( this.mNextPost != null )
			mNextPost.setMeshWithPost( mesh );
	}
	
	public void setShaderName( String name ){
		this.mShaderName = name;
	}
	
	protected void destroyMesh(){
		if( this.mCreateBySelf ){
			mMesh.clear();
			mMesh = null;
			this.mCreateBySelf = false;
		}
	}
	
	public void setRender( MyGLRenderer render ){
		this.mRender = render;
	}
	
	public void initShader(){
		if( mRender == null )
			return;
		Context context = this.mRender.getContext();
		EShaderManager.getInst().clearDefines();
		EShaderManager.getInst().setDefines( mShaderDefines );
		int program = EShaderManager.getInst().createShader( context.getResources(), mShaderName );
		mPositionHandle = GLES20.glGetAttribLocation( program, "a_Position" );
		mUVHandle = GLES20.glGetAttribLocation( program, "a_Texcoord" );
		mShader = program;
	}
	
	boolean isActive(){
		return mIsActived;
	}
	
	void setActive( boolean flag ){
		mIsActived = flag;
	}
	
	void setDefinesForShader( String names[] ){
		mShaderDefines = names;
	}
	
	protected void onDrawPost(){
	}
	
	public void draw( int preTex ){
		if( !mIsActived )
			return;
		EMesh mesh = mMesh;
		if( mShader == -1 || mesh == null || !mesh.isValid() || preTex == -1 )
			return;
		GLES20.glUseProgram( mShader );
		onDrawPost();
		GLES20.glEnable( GLES20.GL_TEXTURE_2D );
		GLES20.glEnableVertexAttribArray( mPositionHandle );
		GLES20.glEnableVertexAttribArray( mUVHandle );	
		GLES20.glVertexAttribPointer( 
				mPositionHandle, 3, GLES20.GL_FLOAT, false, 
				mesh.getVertexStride(), mesh.mVertexBuffer );
		GLES20.glVertexAttribPointer(
				mUVHandle, 2, GLES20.GL_FLOAT, false,
				2*4, mesh.mUVBuffer );
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, preTex );
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getFaceVexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.mIndexBuffer );
		GLES20.glDisableVertexAttribArray( mPositionHandle );
		GLES20.glDisableVertexAttribArray( mUVHandle );
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );//unbind texture
		if( mNextPost != null )
			mNextPost.draw( preTex );
	}
	
	void createMesh(){
		this.mCreateBySelf = true;
		EMeshBuilder builder = new EMeshBuilder();
		//不考虑对齐问题，一定是居中的
		float[] pos2d = 
			{ 	-1.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, -1.0f,
				-1.0f, -1.0f
			};
		float pos[] = {
			pos2d[0], pos2d[1], .0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			pos2d[2], pos2d[3], .0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			pos2d[4], pos2d[5], .0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
			pos2d[6], pos2d[7], .0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
		};
		builder.addVertexes(pos);
		short indexes[] = {
			2, 1, 0, 3, 2, 0
		};
		builder.addFaces( indexes );
		mMesh = builder.finish();
	}
	
	protected MyGLRenderer mRender;
	protected int mPositionHandle = -1;
	protected int mUVHandle = -1;
	protected int mShader = -1;
	protected boolean mIsActived = true;
	protected EMesh mMesh;
	protected boolean mCreateBySelf = false;
	protected String mShaderName = "screenrender";
	protected String[] mShaderDefines = null;
	protected IEPostProcess mNextPost = null;
}
