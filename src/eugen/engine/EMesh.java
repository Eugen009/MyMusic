package eugen.engine;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


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
		if( mTexId == -1 && mSurface != null ){
			if( mSurface.mBitmap != null ){
				mTexId = EHelper.createTexture( mSurface.mBitmap );
			}else{	
				mTexId = EHelper.createTexture( mSurface.mRes, mSurface.mResId );
			}
		}
	}

	public FloatBuffer mVertexBuffer = null;
	public FloatBuffer mUVBuffer = null;
	public ShortBuffer mIndexBuffer = null;
	public FloatBuffer mColorBuffer = null;
	public int mTexId = -1;
	public int mVertexCount = 0;
	public int mFaceCount = 0;
	public ESurface mSurface = null;

}
