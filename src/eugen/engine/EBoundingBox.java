package eugen.engine;


public class EBoundingBox {
	EVector3 mMin;
	EVector3 mMax;
	
	public EBoundingBox( EVector3 min, EVector3 max ){
		mMin = min.clone();
		mMax = max.clone();
	}
	
	public EBoundingBox(){
		this.empty();
	}
	
	public void set( EVector3 min, EVector3 max ){
		mMin = min.clone();
		mMax = max.clone();
	}
	
	public boolean isInside( EVector3 pos ){
		return pos.x() >= mMin.x() && pos.y() >= mMin.y() && pos.z() >= mMin.z() 
				&& pos.x() <= mMax.x() && pos.y() <= mMax.y() && pos.z() <= mMax.z();
		
	}
	
	public EVector3 getMin(){ return mMin; }
	public EVector3 getMax(){ return mMax; }
	
	public EBoundingBox clone(){
		EBoundingBox n = new EBoundingBox( mMin, mMax );
		return n;
	}
	
	public boolean intersectAABB( EBoundingBox bbox ){
		if( mMin.x() > bbox.mMax.x() ) return false; 
		if( mMin.y() > bbox.mMax.y() ) return false; 
		if( mMax.x() < bbox.mMin.x() ) return false; 
		if( mMax.y() < bbox.mMin.y() ) return false;
		return true;
	}
	
	public void empty(){
		float maxNum = 1e32f;
		this.mMax = new EVector3( maxNum );
		this.mMin = new EVector3( -maxNum );
	}
	
	public EVector3 getCenter(){
		EVector3 center = new EVector3();
		center.setSub(mMax, mMin);
		return center;
	}
	
	public void add( EVector3 v ){
		for( int i = 0; i< 3;i++ ){
			if( mMin.v[i] > v.v[i] ){
				mMin.v[i] = v.v[i];
			}
		}
		for( int i = 0; i< 3;i++ ){
			if( mMax.v[i] < v.v[i] ){
				mMax.v[i] = v.v[i];
			}
		}
	}
	
	public void mulSelfMatrix( EMatrix mat ){
		EVector3 tMin = new EVector3();
		EVector3 tMax = new EVector3();
		for( int j = 0; j< 3; j++ ){
			for( int i = 0; i < 3; i++ ){
				int matId = i * 4 + j;
				if( mat.m[matId] > 0){
					tMin.v[j] += mMin.v[j] * mat.m[matId];
					tMax.v[j] += mMax.v[j] * mat.m[matId]; 
				}else{
					tMin.v[j] += mMax.v[j] * mat.m[matId];
					tMax.v[j] += mMin.v[j] * mat.m[matId];
				}
			}
			//w 
			tMin.v[j] += mat.m[ 12 + j ]; 
			tMax.v[j] += mat.m[ 12 + j ]; 
		}
		this.mMax = tMax;
		this.mMin = tMin;
	}
}
