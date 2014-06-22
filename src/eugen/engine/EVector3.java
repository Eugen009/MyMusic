package eugen.engine;

public class EVector3 {
	public EVector3(){
		v = new float[]{.0f, .0f, .0f};
	}
	public EVector3( float x, float y, float z){
		v = new float[]{x,y,z};
	}
	public EVector3( float a ){
		v = new float[3];
		for( int i = 0; i< 3; i++ ) v[i] =a;
	}
	public void add( float x, float y, float z ){
		v[0]+= x;
		v[1]+= y;
		v[2]+=z;
	}
	public void add( EVector3 n ){
		for( int i = 0; i<3; i++ ){
			v[i] += n.v[i];
		}
	}
	public void setSub( EVector3 a, EVector3 b ){
		for( int i = 0; i< 3;i++)
			v[i] = a.v[i] - b.v[i];
	}
	
	public void set( float x, float y, float z ){
		v[0]= x;
		v[1]= y;
		v[2]= z;
	}
	public float[] getData(){
		return v;
	}
	public void normalize(){
		float t = this.getLength();
		if( t > 0.000000001f ){
			t = 1/ t;
			for( int i =0; i<3; i++ ) v[i]*= t;
		}
	}
	public float getLength(){
		return (float) Math.sqrt( getLengthSquare() );
	}
	public float getLengthSquare(){
		float t = 0.0f;
		for( int i = 0; i< 3; i++){
			t += ( v[i] * v[i] );
		}
		return t;
	}
	public void setMul( EVector3 o, float n ){
		for( int i =0; i< 3;i++) v[i]= o.v[i] * n;
	}
	public EVector3 mul( float n ){
		EVector3 temp = new EVector3();
		temp.setMul( this, n );
		return temp;
	}
	public EVector3 clone()
	{
		return new EVector3( v[0], v[1], v[2] );
	}
	public float x(){ return v[0];}
	public float y(){ return v[1];}
	public float z(){ return v[2];}
	public float[] v;// = {.0f, .0f, .0f};
}
