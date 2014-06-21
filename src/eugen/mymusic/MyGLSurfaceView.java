package eugen.mymusic;

import javax.microedition.khronos.opengles.GL10;

import eugen.engine.EInput;
import eugen.engine.ESpriteManager;
import eugen.engine.ETouchCallbackData;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView{
	public MyGLSurfaceView( Context context ){
		super( context );
		
		//
//		String ext = javax.microedition.khronos.opengles.GL10.glGetString( GL10.GL_EXTENSIONS );
		//
		
		this.setEGLContextClientVersion( 2 );
//		this.setRenderMode( GLSurfaceView.RENDERMODE_WHEN_DIRTY );
		mRenderer = new MyGLRenderer( context );
		setRenderer( mRenderer );
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent e){
	    float x = e.getX();
	    float y = e.getY();
	    
    	

	    switch (e.getAction()) {
	    case MotionEvent.ACTION_DOWN:

	    	break;
	    case MotionEvent.ACTION_MOVE:

	            float dx = x - mPreviousX;
	            float dy = -y + mPreviousY;
	            
	            

	            // reverse direction of rotation above the mid-line
//	            if (y > getHeight() / 2) {
//	              dx = dx * -1 ;
//	            }

	            // reverse direction of rotation to left of the mid-line
//	            if (x < getWidth() / 2) {
//	              dy = dy * -1 ;
//	            }
		    	ETouchCallbackData data = new ETouchCallbackData();
		        data.x = x;
		        data.y = y;
		        EInput.onTouch.triggerAll( data );
	            
	            
//	            ESpriteManager.getInstance().checkTouch( x, y );
//	            if( sprite != null ){
//	            	mSelectedSprite = sprite;
//	            }
//	            if( mSelectedSprite != null ){
//	            	mSelectedSprite.setPosition( x, -y, .0f );
//	            }
	            
	            ECamera cam = mRenderer.getCamera();
	            EVector3 curPos = cam.getPosition();
	            curPos.add(dx*.01f,dy*.01f,.0f);
	            cam.setPosition( curPos );
//	            mRenderer.setAngle(
//	                    mRenderer.getAngle() +
//	                    ((dx + dy) * TOUCH_SCALE_FACTOR);  // = 180.0f / 320
	            requestRender();
	    }

	    mPreviousX = x;
	    mPreviousY = y;
	    return true;
	}
	
	MyGLRenderer mRenderer;
	float mPreviousX = .0f;
	float mPreviousY = .0f;
	
	ESprite mSelectedSprite;

}
