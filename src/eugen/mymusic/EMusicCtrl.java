package eugen.mymusic;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class EMusicCtrl extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
        View res = inflater.inflate(R.layout.music_ctrl, container, false);
        return res;
    }
    
    protected void InitUiToProp( View view ){
    	mMusicProgress = (SeekBar)view.findViewById( R.id.MusicProgress );
    	mPlayBtn = (ImageButton)view.findViewById( R.id.MusicPlayBtn );
    	mStopBtn = (ImageButton)view.findViewById( R.id.MusicStopBtn );
    	
//		SharedPreferences settings = getSharedPreferences( EMusicData.PREFS_NAME, 0 );
//		float curVolume = settings.getFloat( EMusicData.ST_VOLUME, 1.0f );
//		this.mCurPath = settings.getString( EMusicData.ST_MUSIC_PATH, null );
//		this.mCurMusicName = settings.getString( EMusicData.ST_CUR_MUSIC_NAME, null );
    }
      
    protected SeekBar mMusicProgress;
    protected ImageButton mPlayBtn;
    protected ImageButton mStopBtn;
    protected MediaPlayer mMusic;
    
    protected String mCurPath;
    protected String mCurMusicName;
}
