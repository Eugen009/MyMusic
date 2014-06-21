package eugen.mymusic;

public class EVector4 {
	public EVector4(){
		
	}
	public EVector4( float r, float g, float b, float a ){
		v[0] = r;
		v[1] = g;
		v[2] = b;
		v[3] = a;
	}
	
	float v[] = {1.0f, 1.0f, 1.0f, 1.0f };
	
	public final static EVector4 RED = new EVector4( 1.0f, .0f, .0f, 1.0f ); 
	public final static EVector4 WHITE = new EVector4( 1.0f, 1.0f, 1.0f, 1.0f ); 

}
