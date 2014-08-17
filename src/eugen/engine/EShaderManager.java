package eugen.engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class EShaderManager {
	
	class DefineInfo{
		String name;
		String value;
		public DefineInfo( String name, String value ){
			this.name = name;
			this.value = value;
		}
	}
	
	public static EShaderManager getInst(){
		return gMgr;
	}
	
	public void setDefine( String name, String value ){
		if( name == null || indexOfDefine(name) != -1 )
			return;
		int id = indexOfDefine(name);
		DefineInfo info = new DefineInfo( name, value );			
		if( id == -1 ){
			mDefines.add(info);
		}else{
			mDefines.set( id, info );
		}
	}
	
	public void setDefines( String names[] ){
		if( names == null )
			return;
		int count = names.length;
		if( count % 2 != 0 )
			return;
		for( int i = 0; i<count; i+= 2){
			this.setDefine( names[i], names[i+1]);
		}
	}
	
	public int indexOfDefine( String name ){
		int count = mDefines.size();
		for( int i =0 ;i<count;i++){
			if( mDefines.get(i).name == name )
				return i;
		}
		return -1;
	}
	
	public void clearDefines(){
		mDefines.clear();
	}
	
	public String createDefineCode(){
		String res = new String();
		int count = mDefines.size();
		for( int i = 0; i< count; i++ ){
			DefineInfo info = mDefines.get(i);
			res += "#define ";
			res += info.name;
			if( info.value != null ){
				res += " ";
				res += info.value;
			}
			res += "\n";
		}
		return res;
	}
	
	public int loadShader( int type, String shaderCode ){
		String defines = this.createDefineCode();
		shaderCode = defines + shaderCode;
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode );
		GLES20.glCompileShader( shader );
		int[] compiled = new int[1];
		//获取Shader的编译情况
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			//若编译失败则显示错误日志并删除此shader
			Log.e("ES20_ERROR", "Could not compile shader " + type + ":");
			Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			shader = 0;      
		}  
		
		return shader;
	}
	
	public int loadShader( int type, Resources r, String filename ){
		String buff = loadStrFile( filename, r );
		return loadShader( type, buff );
	}
	
	public String loadStrFile( String fname, Resources r ){
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
	
	public int createShader( Resources res, String shaderName ){
		int vertexShader = this.loadShader( GLES20.GL_VERTEX_SHADER, res, shaderName + ".vs" );
		int fragmentShader = this.loadShader( GLES20.GL_FRAGMENT_SHADER, res, shaderName + ".ps" );
		int iProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader( iProgram, vertexShader );
		GLES20.glAttachShader( iProgram, fragmentShader );
		GLES20.glLinkProgram( iProgram );
		return iProgram;
	}
	
	protected ArrayList<DefineInfo> mDefines = new ArrayList<DefineInfo>();
	protected static EShaderManager gMgr = new EShaderManager();
}
