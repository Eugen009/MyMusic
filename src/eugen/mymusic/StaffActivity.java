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
//		setContentView(R.layout.activity_staff);
		mGLView = new MyGLSurfaceView(this);
		setContentView( mGLView );
		
		// test
		
//		mTest = new ETouchEntity("touch", this.getResources() );
//		mTest = null;
//		mTestSprite = new EGPlayer();//new EFullSprite();//ESpriteManager.getInstance().createSprite();
//		mTestSprite.setUVInfo( 1, 4 );
//		mTestSprite.setTex( this.getResources(), 
//				R.drawable.eight );
//				R.drawable.star );
//		mTestSprite.setRollInfo(.0f, 1.0f, .0f, 5.0f );
//		ESpriteManager.getInstance().addSprite( mTestSprite );
//		mTestSprite.setScale( 100.0f, 100.0f );
//		mTestSprite = new ESprite( this.getResources(), R.drawable.eight );
		
		this.mUpdateThread = new Thread( this );
//		mScene = new EGScene();
//		mScene = 
		EGScene scene = EGSceneManager.getInst().createScene( this );
		scene.init( this );
		scene.setUpdate( true );
//		mScene.init( this );
//		mScene.setUpdate( true );
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
//			EGSceneManager.getInst()
//			mScene.update();
		}
	}
	
	protected void onDestroy(){
		if( this.mUpdateThread != null ){
//			if( mScene != null )
//				mScene.setUpdate( false );
			mUpdate = false;
			try {
				mUpdateThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mUpdateThread = null;
		}
//		if( mTest!= null)
//			mTest.remove();
//		EEntity.removeAll();
		super.onDestroy();
	}
	
	private GLSurfaceView mGLView;
//	protected ETouchEntity mTest;
//	protected ESprite mTestSprite;
	protected Thread mUpdateThread;
	protected boolean mUpdate = true;
//	protected EGScene mScene;
}
