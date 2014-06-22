package eugen.engine;

import android.opengl.Matrix;

public class EMatrix {
	public EMatrix(){
		
	}
	
	public EMatrix(
			float m1,float m2,float m3,float m4,
			float m5,float m6,float m7,float m8,
			float m9,float m10,float m11,float m12,
			float m13,float m14,float m15,float m16 ){
		m[0] = m1;m[1] = m2;m[2] = m3;m[3] = m4;
		m[4] = m5;m[5] = m6;m[6] = m7;m[7] = m8;
		m[8] = m9;m[9] = m10;m[10] = m11;m[11] = m12;
		m[12] = m13;m[13] = m14;m[14] = m15;m[15] = m16;
	}
	
	public void translate( float x, float y, float z ){
		Matrix.translateM(m, 0, x, y, z);
	}
	
	public void set( EMatrix mat ){
		for( int i = 0; i< 16; i++ ){
			m[i] = mat.m[i];
		}
	}
	
	public void setTranslate( float x, float y, float z ){
		m[12] = x;
		m[13] = y;
		m[14] = z;
	}
	
	public void setTranslate( EVector3 vec ){
		m[12] = vec.x();
		m[13] = vec.y();
		m[14] = vec.z();
	}
	
	public EVector3 getTranslate() 
	{
		EVector3 pos = new EVector3( m[12], m[13], m[14] );
		return pos;
	}
	
	public void rotate( float x, float y, float z ){
		float[] tempM = new float[16];
		Matrix.setIdentityM(tempM, 0);
		Matrix.setRotateEulerM(tempM, 0, x, y, z );
		float[] res = new float[16];
		Matrix.multiplyMM( res, 0, this.m, 0, tempM, 0 );
		this.m = res;
	}
	
	public void lookAt( EVector3 eye, EVector3 target, EVector3 up ){
		Matrix.setLookAtM(
			m, 0, eye.x(),eye.y(),eye.z(),
			target.x(),target.y(),target.z(),
			up.x(),up.y(),up.z() );
	}
	
	public void scale( float x, float y, float z ){
		Matrix.scaleM(m, 0, x, y, z);
	}
	
	public void lookAt( EVector3 eye, EVector3 target ){
		Matrix.setLookAtM(
				m, 0, 
				eye.x(),eye.y(),eye.z(),
				target.x(),target.y(),target.z(),
				.0f, 1.0f , .0f );
	}
	
	public void mul( EMatrix mat ){
		float[] res = { 
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f,  1.f, 0.0f, 0.0f,
				0.0f,  0.0f, 1.f, 0.0f,
				1.0f, 1.0f, 1.0f, 1.0f
			};
		Matrix.multiplyMM( res, 0, m, 0, mat.m, 0);
		m = res;
	}
	
	public EVector3 mul( EVector3 vec ){
		float[] tv = { vec.x(), vec.y(), vec.z(), 1.0f };
		float[] rv = { .0f, .0f, .0f, 1.0f };
		Matrix.multiplyMV( rv, 0, this.m, 0, tv, 0 );
		
		EVector3 temp = new EVector3( rv[0], rv[1], rv[2] );
		return temp;
	}
	
	public void setMul( EMatrix m1, EMatrix m2 ){
		Matrix.multiplyMM( m, 0, m1.m, 0, m2.m, 0);
	}
	
	public void identity(){
		Matrix.setIdentityM( m, 0 );
	}
	
	//妹的，老子自己写可以了吧
	public void mulM( EMatrix other ){
		float[] ta = new float[4];
		for( int i = 0; i< 4; i++ ){
			int id = i * 4;
			// copy
			for( int j = 0; j< 4;j++ ){
				ta[j] = m[id+j];
			}
			for( int j = 0; j<4; j++ ){
				m[id+j] = 0;
				int oId = 0;
				oId = j * 4;
				for( int k = 0; k< 4; k++ ){
					m[id+j] +=  ta[k] * other.m[oId + k*4];
				}
			}
		}
	}
	
	public float[] m = { 
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		};
}
