package eugen.engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class MyShader {
	public static int loadShader( int type, String shaderCode ){
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode );
		GLES20.glCompileShader( shader );
		
	  int[] compiled = new int[1];
	  //获取Shader的编译情况
	  GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
	  if (compiled[0] == 0) 
	  {//若编译失败则显示错误日志并删除此shader
	      Log.e("ES20_ERROR", "Could not compile shader " + type + ":");
	      Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
	      GLES20.glDeleteShader(shader);
	      shader = 0;      
	  }  
		
	  return shader;
	}
	
	public static int loadShader( int type, Resources r, String filename ){
		String buff = loadStrFile( filename, r );
		return loadShader( type, buff );
	}
	
	public static String loadStrFile( String fname, Resources r ){
	   	String result=null;    	
	   	try
	   	{
	   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
	   		result=new String(buff,"UTF-8"); 
	   		result=result.replaceAll("\\r\\n","\n");
	   	}
	   	catch(Exception e)
	   	{
	   		e.printStackTrace();
	   	}    	
	   	return result;
		
	}
	
	public static final int createShader( Resources res, String shaderName ){
		int vertexShader = MyShader.loadShader( GLES20.GL_VERTEX_SHADER, res, shaderName + ".vs" );
		int fragmentShader = MyShader.loadShader( GLES20.GL_FRAGMENT_SHADER, res, shaderName + ".ps" );
		int iProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader( iProgram, vertexShader );
		GLES20.glAttachShader( iProgram, fragmentShader );
		GLES20.glLinkProgram( iProgram );
		return iProgram;
	}
	
	public static final String vertexShaderCode =
		"uniform mat4 uMVPMatrix;		\n"+
	    "attribute vec4 vPosition;		\n" +
		"attribute vec2 a_texcoord; \n" +
	    "varying vec2 v_texcoord; \n" +
	    "void main() {					\n" +
	    "  	gl_Position = uMVPMatrix *vPosition;\n" +
	    "	v_texcoord = a_texcoord; \n" +
	    "}\n";

	public static final String fragmentShaderCode =
	    "precision mediump float;\n" +
	    "uniform vec4 vColor;\n" +
	    "uniform sampler2D s_Tex;\n" +
	    "varying vec2 v_texcoord;\n" +
	    "void main() {\n" +
		"	vec4 tmpColor = texture2D( s_Tex, v_texcoord ); \n"+
	    "  gl_FragColor = tmpColor * vColor;\n" +
		"  //gl_FragColor = gl_FragColor + vec4( 1.0, 1.0, 1.0, 1.0 );\n" +
		"  //gl_FragColor.xy = gl_FragColor.xy * v_texcoord;//  + vec4( 1.0, 1.0, 1.0, 1.0 );\n" +
	    "}";
}
