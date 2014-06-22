package eugen.engine;

import java.util.ArrayList;
import java.util.List;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

public class ESprite {
	
	public enum AlignType{
		LEFT_TOP,
		CENTER;
	}
	final static float gMeshBaseVal = 0.5f;
	
	public ESprite(){
		
	}
	
	public ESprite( Resources res, int id ){
		this.setTex( res, id );
	}
	
	public void setTex( Resources res, int id ){
		if( this.mMesh == null ){
			this.createMesh();
		}
		ESurface surface = new ESurface();
		Bitmap bitmap = BitmapFactory.decodeResource( res, id );
		if( bitmap != null ){
			float w = bitmap.getWidth() * this.mUVInfo[2]; //this.mColumn;
			float h = bitmap.getHeight() * this.mUVInfo[3];//this.mRow;
			this.mMat.scale( w, h, 1.0f );
		}
		surface.mBitmap = bitmap;
		surface.mRes = res;
		surface.mResId = id;
		mMesh.mSurface = surface;//new ESurface();
	}
	
	public EMesh createMesh(){
		EMeshBuilder builder = new EMeshBuilder();
//		float b = .5f;
//		EVector3 offset = this.getAlignOffset( mAlignType );
		float[] pos2d = getAlignPoses( mAlignType );
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
	
	protected EVector3 getAlignOffset( AlignType type ){
		EVector3 res = new EVector3();
		switch( type ){
		case LEFT_TOP:res.set( 1.0f, -1.0f, .0f );
		break;
		case CENTER: res.set( .0f, .0f, .0f );
		}
		return res;
	}
	
	protected float[] getAlignPoses( AlignType type ){
		
		float[] poses = {
				-1.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, -1.0f,
				-1.0f, -1.0f
		};
		EVector3 offset = this.getAlignOffset( type );
		for( int i =0; i< 8; i+= 2){
			poses[i] += offset.x();
			poses[i+1] += offset.y();
		}
		for( int i = 0; i< 8; i++ ) poses[i] *= gMeshBaseVal;
		
		return poses;
		
	}
	
	public EMesh getMesh(){
		return mMesh;
	}
	
	public EMatrix getMat(){
		return mMat;
	}
	
	public void setPosition( EVector3 pos ){
		this.mMat.setTranslate( pos );
	}
	
	public void setPosition( float x, float y, float z ){
		this.mMat.setTranslate( x, y, z );
	}
	
	public void setScale( float x, float y ){
		this.mMat.scale( x, y, 1 );
	}
	
	public EVector4 getColor() {
		return this.mColor;
	}
	
	public void setColor( EVector4 color ){
		this.mColor = color;
	}
	
	public EBoundingBox getBoundingBox(){
		if( mBBox == null ){
			EVector3 min = mMat.getTranslate();
			EVector3 max = mMat.mul( new EVector3(1.0f, 1.0f, .0f) );//new EVector3( 1.0f, 1.0f, .0f );
			mBBox = new EBoundingBox( min, max );
		}
		return mBBox;
	}
	
	public void setBoundingBox( EBoundingBox bbox ){
		this.mBBox = bbox.clone();
	}
	
	public void setAlign( AlignType type ){
		this.mAlignType = type;
		if( this.mMesh != null ){
			ESurface tmp = this.mMesh.mSurface;
			this.createMesh();
			mMesh.mSurface = tmp;
		}
	}
	
	public void setUVInfo( int column, int row ){
		if( column > 0 && row > 0 ){
			this.mUVInfo[2] = 1.0f/ column;
			this.mUVInfo[3] = 1.0f/ row;
			this.mColumn = column;
			this.mRow = row;
		}
		
	}
	
	public void onTouch( float x, float y ){
		// do something
	}
	
	protected float[] calcUV( int row, int column ){
		if( row < mRow && column < mColumn ){
			float width = 1.0f/ (float)mColumn;
			float height = 1.0f/ (float)mRow;
			float[] uv = { column * width, row* height, (column+1)* width, (row+1)* height };
			return uv;
		}else{
			float[] uv = { .0f, .0f, 1.0f, 1.0f };
			return uv;
		}
	}
	
	public float[] getUVInfo(){
//		mUVOffset[]
		return mUVInfo;
	}
	
	public void getFinalMatrix( EMatrix res, EMatrix tmat ){
		res.setMul( tmat, mMat );
	}
	
	public void setRollInfo( float umax, float vmax, float utime, float vtime ){
		this.mUVOffsetInfo[0] = umax;
		this.mUVOffsetInfo[1] = vmax;
		if( utime > 0.000001f )
			this.mUVOffsetInfo[2] = 1.0f /utime;
		if( vtime > 0.000001f )
			this.mUVOffsetInfo[3] = 1.0f /vtime;
	}
	
	public float[] getUVOffset(){
		return mUVOffset;
	}
	
	public void nextFrame( float timeDiff ){
		if( mPlay ){
			mUVInfo[0] += 1.0f;
			if( mUVInfo[0] >= mColumn ){
				if( mUVInfo[1] < (float)(mRow -1) ){
					mUVInfo[1] += 1.0f;
					mUVInfo[0] = .0f;
				}else{
					if( mLoop ){
						mUVInfo[0] = .0f;
						mUVInfo[1] = .0f;
					}else{
						mUVInfo[0] = (float)(mColumn -1);
					}
				}
			}
		}
		mUVOffset[0] += mUVOffsetInfo[0] * mUVOffsetInfo[2] * timeDiff;
		if( mUVOffset[0] > mUVOffsetInfo[0] ){ 
			if( mLoop )
				mUVOffset[0] = .0f;
			else
			mUVOffset[0] = mUVOffsetInfo[0];
		
		}
		mUVOffset[1] += mUVOffsetInfo[1] * mUVOffsetInfo[3] * timeDiff;
		if( mUVOffset[1] > mUVOffsetInfo[1] ){ 
			if( mLoop )
				mUVOffset[1] = .0f;
			else
				mUVOffset[1] = mUVOffsetInfo[1];
		}
	}
	
	public void setVisible( boolean flag ){
		this.mVisible = flag;
	}
	
	public boolean isVisible(){
		return mVisible;
	}
	
	public void onRemove(){
		if( this.mMesh != null && mMesh.mTexId > 0 ){
			if( mMesh.mTexId > 0){
				int buffers[] = new int[1];
				buffers[0] = mMesh.mTexId;
				GLES20.glDeleteTextures( 1, buffers, 0);
				mMesh.mTexId = 0;
			}
			if( mMesh.mSurface != null && mMesh.mSurface.mBitmap != null ){
				mMesh.mSurface.mBitmap.recycle();
			}
		}
	}
	
	
	public boolean mVisible = true;
	public EMesh mMesh;
//	public EVector3 mPos =;
	public EMatrix mMat = new EMatrix();
	public EBoundingBox mBBox;
	protected AlignType mAlignType = AlignType.CENTER;
	protected EVector4 mColor = new EVector4();
	// uv
	protected boolean mLoop = true;
	protected boolean mPlay = false;
	protected int mColumn = 1;
	protected int mRow = 1;
	protected float[] mUVInfo= { 0.0f, 0.0f, 1.0f, 1.0f };
	protected float[] mUVOffset = { .0f, .0f, .0f, .0f };
	protected float[] mUVOffsetInfo ={ .0f, .0f, .0f, .0f };// umax, vmax, utime, vtime
	
}
