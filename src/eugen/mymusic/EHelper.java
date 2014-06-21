package eugen.mymusic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class EHelper {
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
	
	static int createTexture( Resources res, int resId ){
		Bitmap bitmap = BitmapFactory.decodeResource( res, resId );
//		int height = bitmap.getHeight();
//		int widht = bitmap.getWidth();
		int texId = createTexture( bitmap );
		bitmap.recycle();
		return texId;
	}
}
