package eugen.engine;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;


public class EMesh {
	
	public EMesh(){
	}
	
	public int getVertexStride(){
		return 3 * 4;
	}
	
	public int getUVStride(){
		return 2 *4;
	}
	public int getFaceVexCount(){
		return mFaceCount * 3;
	}
	
	public boolean isValid(){
		return mVertexCount > 0&& mFaceCount> 0;
	}
	
	public void prepareTex(){
//		if( mTexId == -1 && mSurface != null ){
//			if( mSurface.mBitmap != null ){
//				mTexId = EHelper.createTexture( mSurface.mBitmap );
//			}else{	
//				mTexId = EHelper.createTexture( mSurface.mRes, mSurface.mResId );
//			}
//		}
		if( mTex == null ){
			mTex = ERTextureManager.getInst().createTexture(mSurface.mRes, mSurface.mResId );
		}
	}
	
	public void clear(){
//		if( mTexId != -1 ){
//			int texes[] ={ 0 };
//			texes[0] = mTexId;
//			GLES20.glDeleteTextures( 1, texes, 1 );
//		}
		if( mTex != null ) mTex.dispose();
		if( mVertexBuffer != null) mVertexBuffer.clear();
		if( mUVBuffer != null ) mUVBuffer.clear();
		if( mIndexBuffer != null ) mIndexBuffer.clear();
		if( mColorBuffer != null ) mColorBuffer.clear();
	}

	public FloatBuffer mVertexBuffer = null;
	public FloatBuffer mUVBuffer = null;
	public ShortBuffer mIndexBuffer = null;
	public FloatBuffer mColorBuffer = null;
//	public int mTexId = -1;
	public ERTexture mTex;
	public int mVertexCount = 0;
	public int mFaceCount = 0;
	public ESurface mSurface = null;

}
