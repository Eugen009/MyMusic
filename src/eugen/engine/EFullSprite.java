package eugen.engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import eugen.mymusic.EMatrix;
import eugen.mymusic.EMesh;
import eugen.mymusic.EMeshBuilder;
import eugen.mymusic.ESprite;
import eugen.mymusic.ESurface;

// fill all the screen
public class EFullSprite extends ESprite{
	public void getFinalMatrix( EMatrix res, EMatrix tmat ){
		res.set( this.mMat );
	}
	
	public void setTex( Resources res, int id ){
		if( this.mMesh == null ){
			this.createMesh();
		}
		ESurface surface = new ESurface();
		Bitmap bitmap = BitmapFactory.decodeResource( res, id );
		surface.mBitmap = bitmap;
		surface.mRes = res;
		surface.mResId = id;
		mMesh.mSurface = surface;//new ESurface();
	}
	
	public EMesh createMesh(){
		EMeshBuilder builder = new EMeshBuilder();
		//不考虑对齐问题，一定是居中的
		float[] pos2d = 
			{ 	-1.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, -1.0f,
				-1.0f, -1.0f
			};
		float pos[] = {
			pos2d[0], pos2d[1], .0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
			pos2d[2], pos2d[3], .0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
			pos2d[4], pos2d[5], .0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			pos2d[6], pos2d[7], .0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f
		};
		builder.addVertexes(pos);
		short indexes[] = {
			2, 1, 0, 3, 2, 0
		};
		builder.addFaces( indexes );
		mMesh = builder.finish();
		return mMesh;
	}
}
