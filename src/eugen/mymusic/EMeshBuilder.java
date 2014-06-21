package eugen.mymusic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

public class EMeshBuilder {
	
	final static int FLOAT_SIZE = 4;
	final static int SHORT_SIZE = 2;

	public void addVertex( float x, float y, float z, float u, float v, float r,float g,float b ){
		mVexes.add(x);
		mVexes.add(y);
		mVexes.add(z);	
		mUVs.add(u);
		mUVs.add(v);
		mColors.add(r);
		mColors.add(g);
		mColors.add(b);
	}
	
	public void addVertex( EVector3 vec, EVector2 uv ){
		for( int i = 0; i<3; i++ )
			mVexes.add( vec.v[i]);
			mUVs.add( uv.x );
			mUVs.add( uv.y );
	}
	
	public void addVertexes( float[] vexes ){
		if( vexes.length < 8 ) return;
		for( int i = 0; i< vexes.length; i+= 8){
			addVertex( vexes[i], vexes[i+1], vexes[i+2], vexes[i+3], vexes[i+4] 
					, vexes[i+5], vexes[i+6], vexes[i+7]
					);
		}
	}
	
	public void addVertexes( EVector3[] vexes, EVector2[] uvs ){
		for( int i = 0; i< vexes.length; i++)
			addVertex( vexes[i], uvs[i] );
	}
	
	public void addFaces( short[] indexes ){
		if( indexes.length < 3 )
			return;
		for( int i = 0; i< indexes.length; i+=3 ){
			this.addFace(indexes[i], indexes[i+1], indexes[i+2]);
		}
	}
	
	public void addFace( short a, short b, short c ){
		mFaces.add(a);
		mFaces.add(b);
		mFaces.add(c);
	}
	
	protected FloatBuffer createFloatBuf( List<Float> content )
	{
		ByteBuffer bb =  ByteBuffer.allocateDirect(
				content.size() * FLOAT_SIZE );
		bb.order( ByteOrder.nativeOrder() );
		FloatBuffer buf = bb.asFloatBuffer();
		float[] data = new float[content.size()];
		for( int i = 0; i< content.size();i++){
			data[i] = content.get(i);
		}
		buf.put( data );
		buf.position( 0 );
		return buf;
	}
	
	protected void addSurface( Resources res, int resId ){
		if( mSurface == null )
			mSurface = new ESurface();
		mSurface.mRes = res;
		mSurface.mResId = resId;
	}
	
	protected ShortBuffer createShortBuf( List<Short> content )
	{
		ByteBuffer bb =  ByteBuffer.allocateDirect(
				content.size() * SHORT_SIZE );
		bb.order( ByteOrder.nativeOrder() );
		ShortBuffer buf = bb.asShortBuffer();
		short[] data = new short[content.size()];
		for( int i = 0; i< content.size();i++){
			data[i] = content.get(i);
		}
		buf.put( data );
		buf.position( 0 );
		return buf;
	}
	
	public EMesh finish(){
		if(mVexes.isEmpty()|| mVexes.size() %3 != 0)
			return null;
		EMesh mesh = new EMesh(); 
		mesh.mVertexBuffer = createFloatBuf( mVexes );
		mesh.mVertexCount = mVexes.size() / 3;
		mesh.mUVBuffer = createFloatBuf( mUVs );
		mesh.mColorBuffer = createFloatBuf( mColors );
		mesh.mIndexBuffer = createShortBuf( mFaces );
		mesh.mFaceCount = mFaces.size() / 3;
		mesh.mSurface = mSurface;
//		if( mSurface != null ){
//			mesh.mTexId = EHelper.createTexture( mSurface.mRes, mSurface.mResId );
//		}
		return mesh;
	}
	
	protected float[] getVexes(){
		float[] vexes = new float[mVexes.size()];
		for( int i = 0; i< mVexes.size();i++){
			vexes[i] = mVexes.get(i);
		}
		return vexes;
	}
	
	protected List<Float> mVexes = new ArrayList<Float>();
	protected List<Float> mUVs = new ArrayList<Float>();
	protected List<Short> mFaces = new ArrayList<Short>();
	protected List<Float> mColors = new ArrayList<Float>();
	protected ESurface mSurface;
	protected int mMaxSize;
	protected int mCurSize;
}
