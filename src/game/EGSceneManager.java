package game;

import android.content.Context;

public class EGSceneManager {
	public EGSceneManager(){
		
	}
	
	public static EGSceneManager getInst(){
		if( gMgr == null )
			gMgr = new EGSceneManager();
		return gMgr;
	}
	
	public EGScene createScene( Context context ){
		EGScene scene = new EGScene();
//		scene.init(context);
//		scene.setUpdate( true );
		mCurScene = scene;
		return scene;
	}
	
	public EGScene getCurScene(){
		return this.mCurScene;
	}
	
	protected static EGSceneManager gMgr;
	protected EGScene mCurScene;
}
