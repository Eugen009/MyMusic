package eugen.mymusic;

import eugen.engine.EFullSprite;
import eugen.engine.ESpriteManager;
import game.EGBackground;
import game.EGPlayer;
import game.EGScene;
import game.EGSceneManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class StaffActivity extends Activity implements Runnable{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLView = new MyGLSurfaceView(this);
		setContentView( mGLView );
		
		this.mUpdateThread = new Thread( this );
		EGScene scene = EGSceneManager.getInst().createScene( this );
		scene.init( this );
		scene.setUpdate( true );
		mUpdateThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.staff, menu);
		return true;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		long preTime = System.currentTimeMillis();

		while( mUpdate ){
			EGScene cur = EGSceneManager.getInst().getCurScene();
			if( cur != null )
				cur.update();
		}
	}
	
	protected void onDestroy(){
		if( this.mUpdateThread != null ){
			mUpdate = false;
			try {
				mUpdateThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mUpdateThread = null;
		}
		EGSceneManager.getInst().clear();
		this.mGLView.onDestroy();
		super.onDestroy();
	}
	
	private MyGLSurfaceView mGLView;
//	protected ETouchEntity mTest;
//	protected ESprite mTestSprite;
	protected Thread mUpdateThread;
	protected boolean mUpdate = true;
//	protected EGScene mScene;
}
