package eugen.mymusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyMusicServer 
	extends Service 
	implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
	public static final String ACTION_PLAY = "Play";
	MediaPlayer m_Music = null;
	
	public int onStartCommand( Intent intent, int flags, int startId ){
		if( intent.getAction().equals(ACTION_PLAY) ){

		}
		return 0;
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		if( player != null )
			player.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean onError( MediaPlayer mp, int what, int extra ){
		// do something
		return true;
	}
}
