package eugen.mymusic;

import android.opengl.Matrix;

public class ECamera {
	public ECamera(){
		mMat = new EMatrix();
		mMat.lookAt( 
			new EVector3(.0f,.0f, -6.0f), 
			new EVector3() );
	}
	
	public void setPosition( EVector3 pos ){
		mMat.m[12] = pos.x();
		mMat.m[13] = pos.y();
		mMat.m[14] = pos.z();	
//		Matrix.translateM(mMat.m, 0, pos.x(), pos.y(), pos.z() );
	}
	
	public EVector3 getPosition(){
		EVector3 vec = new EVector3(
			mMat.m[12], mMat.m[13], mMat.m[14]);
		return vec;
	}
	
	public EMatrix mMat;
	public float fov[]= { 90.0f, 90.0f };
}
