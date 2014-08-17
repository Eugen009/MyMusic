package eugen.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class ERTextureManager {

	
	static int createTexture( Bitmap bitmap ){
		if( bitmap == null )
			return 0;
		int texes[] ={ 0 };
		GLES20.glGenTextures( 1, texes, 0 );
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, texes[0] );
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, 
				GLES20.GL_TEXTURE_MAG_FILTER, 
				GLES20.GL_LINEAR );
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, 
				GLES20.GL_TEXTURE_MIN_FILTER, 
				GLES20.GL_LINEAR );
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, 
				GLES20.GL_TEXTURE_WRAP_T, 
				GLES20.GL_REPEAT );
//				GLES20.GL_CLAMP_TO_EDGE );
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, 
				GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT );
//				GLES20.GL_CLAMP_TO_EDGE );
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0 );
		
//		bitmap.recycle();
		
		return texes[0];
		
	}
	
	ERTexture createRenderTargetTex(){
	    mCurRenderTarget ++;
		int[] buf = new int[1];
	    GLES20.glGenTextures(1, buf, 0 );
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buf[0]);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
		ByteBuffer bb =  ByteBuffer.allocateDirect(
				EScreen.mWidth * EScreen.mHeight * 2 );
		bb.order( ByteOrder.nativeOrder() );
		ShortBuffer texBuf = bb.asShortBuffer();
	    GLES20.glTexImage2D(
	    		GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 
	    		EScreen.mWidth, EScreen.mHeight, 
	    		0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_SHORT_4_4_4_4, texBuf );
	    GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );
	    ERTexture tex = new ERTexture( buf[0] );
	    tex.mResId = mCurRenderTarget;
	    tex.mMgr = this;
	    this.mResources.put( mCurRenderTarget, tex );
		return tex;
	}
	
	ERTexture createTexture( Resources res, int resId ){
		if( mResources.containsKey(resId) )
			return (ERTexture)mResources.get(resId);
		Bitmap bitmap = BitmapFactory.decodeResource( res, resId );
		if( bitmap == null) return null;
		int texId = createTexture( bitmap );
		bitmap.recycle();
		ERTexture tex = new ERTexture();
		tex.mTexId = texId;
		tex.mResId = resId;
		tex.mMgr = this;
		mResources.put( resId, tex );
		return tex;
	}
	
	void removeTexture( ERTexture tex ){
		if( tex == null ) return;
		if( mResources.containsKey(tex.mResId) ){
			int tmp[]= { tex.mTexId };
			GLES20.glDeleteTextures( GLES20.GL_TEXTURE, tmp, 0 );
			tex.mTexId = -1;
			mResources.remove( tex.mResId );
		}
	}
	
	static ERTextureManager getInst(){ return gInst; }
	
	@SuppressLint("UseSparseArrays")
	protected HashMap < Integer, ERTexture> mResources = new HashMap<Integer,ERTexture>();
	static ERTextureManager gInst = new ERTextureManager();
	static int mCurRenderTarget = 10000;
	
	
}
