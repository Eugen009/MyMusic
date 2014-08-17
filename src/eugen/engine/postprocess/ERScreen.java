package eugen.engine.postprocess;

import eugen.engine.EMesh;
import eugen.mymusic.MyGLRenderer;

public class ERScreen extends IEPostProcess {

	public ERScreen(MyGLRenderer render) {
		super(render, null );
		this.mShaderName = "gauss";
		
		// TODO Auto-generated constructor stub
	}
	
	protected EMesh mMesh;
	
}
