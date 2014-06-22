package eugen.engine;

import android.content.Context;

public interface EIRender {
	public void onSurfaceCreate();
	public void onDrawFrame();
	public void onSurfaceChanged( int w, int h );
	public void clear();
//	public Context getContext();
//	public MyGLRenderer getGLRender();
}
