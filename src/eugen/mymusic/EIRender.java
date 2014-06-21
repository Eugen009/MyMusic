package eugen.mymusic;

import android.content.Context;

public interface EIRender {
	public void onSurfaceCreate();
	public void onDrawFrame();
	public void onSurfaceChanged( int w, int h );
//	public Context getContext();
//	public MyGLRenderer getGLRender();
}
