package eugen.mymusic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
//import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
//import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import org.fmod.FMODAudioDevice;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayCtrl extends Activity 
		implements 	OnSeekBarChangeListener, 
					OnItemClickListener,
					MediaPlayer.OnCompletionListener,
					Handler.Callback,
					Runnable{ 
	
	public enum ABState{
		NONE, A, B, PLAY
	};
	
	protected class ABInfo{
		public int startTime = 0;
		public int endTime = 0;
		public ABState state = ABState.A;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EMusicData.log( "on create start" );
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_ctrl);
		// Show the Up button in the action bar.
		setupActionBar();

		
		SharedPreferences settings = getSharedPreferences( EMusicData.PREFS_NAME, 0 );
		float curVolume = settings.getFloat( EMusicData.ST_VOLUME, 1.0f );
		this.m_curPath = settings.getString( EMusicData.ST_MUSIC_PATH, null );
		this.m_curMusicName = settings.getString( EMusicData.ST_CUR_MUSIC_NAME, null );
		this.mCurMusicIndex = settings.getInt( EMusicData.ST_CUR_MUSIC_INDEX, -1 );
		
		mPlayBtn = (ImageButton)findViewById( R.id.PlayBtn );
		SeekBar progressBar = (SeekBar)this.findViewById( R.id.progressBar1);
		if( progressBar != null){
//			progressBar
			progressBar.setProgress( 0 );
			progressBar.setOnSeekBarChangeListener( this );
			m_ProgressBar = progressBar;
		}
		SeekBar speedBar = (SeekBar)this.findViewById(R.id.spdBar);
		if( speedBar != null ){
			speedBar.setProgress(50);
			speedBar.setOnSeekBarChangeListener( this );
		}
		SeekBar volBar = (SeekBar)this.findViewById(R.id.volBar );
		if( volBar != null ){
			m_VolumeBar = volBar;
			volBar.setProgress( (int)(curVolume * 100.0f) );
			volBar.setOnSeekBarChangeListener( this );
		}
		//set the list view
		ListView listView = (ListView)this.findViewById( R.id.lstMusicList );
		mMusicList = listView;
		showPathFile();

		listView.setOnItemClickListener( this );
		// set the music
		setVolumeControlStream( AudioManager.STREAM_MUSIC );
		TextView curMusicName = (TextView)this.findViewById(R.id.curMusicName);
		if( m_curMusicName != null && !m_curMusicName.isEmpty() ){
			curMusicName.setText( m_curMusicName );
		}else{
			curMusicName.setText( "Waiting for music...." );
		}	
		
		FMODAudioDevice.init( this );
		
		setStateStart();
		
		m_Handler = new Handler( this );
		this.m_bUpdated = true;
		this.m_UpdateThread = new Thread( this );
		m_UpdateThread.start();
		
		EMusicData.log( "on create end" );
	}
	
	@Override
	protected void onStop(){
		this.stopMusic();
		SharedPreferences settings = getSharedPreferences( EMusicData.PREFS_NAME, 0 );
		SharedPreferences.Editor editor = settings.edit();
		float vol = (float)m_VolumeBar.getProgress() / 100.0f;
		editor.putFloat( EMusicData.ST_VOLUME, vol );
		editor.putString( EMusicData.ST_MUSIC_PATH, this.m_curPath );
		editor.putString( EMusicData.ST_CUR_MUSIC_NAME, this.m_curMusicName );
		editor.putInt( EMusicData.ST_CUR_MUSIC_INDEX, this.mCurMusicIndex );
		editor.commit();
		setStateStop();
		super.onStop();
	} 
	
	@Override
	protected void onDestroy(){
		EMusicData.log( "on destroy start" );
		m_bUpdated = false;
		try
    	{
			if( m_UpdateThread != null ){
				m_UpdateThread.join();
			}
    	}
    	catch (InterruptedException e) { }	

		FMODAudioDevice.close();
		super.onDestroy();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );//R.menu.play_ctrl, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void OnMusicStopped(){
		mPlayBtn.setImageResource( R.drawable.play );
		SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		bar.setProgress(0);
	}
	
	@Override
	public void run() {
		// refresh ui
//		static long curTime = SystemClock.uptimeMillis();
		
		while( m_bUpdated ){
//			if( SystemClock.uptimeMillis() -curTime > 100 ){
				m_Handler.sendMessage( new Message() );
//				curTime = SystemClock.uptimeMillis();
//			}
			try {
				Thread.sleep( 100 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			this.m_UpdateThread.sleep( 100 );
		}
	}
	
	public boolean playMusic(){
		if( this.m_curPath == null || this.m_curPath.isEmpty() ||
			this.m_curMusicName == null || this.m_curMusicName.isEmpty() ){
			return false;
		}
		String filename = m_curPath + "/" + m_curMusicName;
		this.stopMusic();
		int soundId = this.playSound( filename );
		switch( soundId ){
			case -1:{
				String msg = "The system is not initialized";
				new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			}break;
			case -2:{
				String msg = "Fail to create the sound:";
				msg += filename;
				new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			}break;
			case -3:{
				String msg = "Fail to create channels ";
				new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			}break;
		}
		m_iCurSoundId = soundId;
		mPlayBtn.setImageResource( R.drawable.pause );
		TextView curPathText = (TextView)this.findViewById(R.id.curMusicName);
		curPathText.setText( m_curMusicName );
		return true;
	}
	
	public void stopMusic(){
		this.stopSound( 0 );
		m_iCurSoundId = -1;
		mPlayBtn.setImageResource( R.drawable.play );//( "Play" );
		SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		bar.setProgress( 0 );
	}
	
    public void onPlayBtnClicked( View view ){
    	if( m_iCurSoundId >= 0 ){
    		boolean nextState = !this.isSoundPaused( m_iCurSoundId );
    		this.pauseSound( m_iCurSoundId, nextState );
    		mPlayBtn.setImageResource( nextState? R.drawable.play: R.drawable.pause );
//    		btn.setText( nextState? "Play" : "Pause" );
    	}else
    		this.playMusic();
    }
    
    public void onStopBtnClicked( View view )
    {
    	this.stopMusic();
    	mPlayBtn.setImageResource( R.drawable.play );
    }
    
    public void onSelectMusicBtnClicked( View view )
    {
    	Intent intent= new Intent( this, PlayCtrl.class );
    	startActivity( intent );
    }
    
    public void setCurSelectedMusic( String str )
    {
    	this.curSelectedMusic = str;
    }
    
    public String getCurSelectedMusic()
    {
    	return this.curSelectedMusic;
    }
    
    private String curSelectedMusic;

	@Override
	public boolean handleMessage(Message msg) {
		// updat the progress
		if( m_bUpdateProgress ){
			if( m_iCurSoundId >=0 ){
		    	SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		    	float cur = (float)getSoundPos(m_iCurSoundId) / (float)getSoundDur(m_iCurSoundId);
				if( !getSoundLooped(m_iCurSoundId) && 1.0f - cur < 0.00001f ){
					this.stopMusic();
					cur = 0.0f;
				}else{
			    	cur *= 100.0f;
					bar.setProgress( (int)cur );
				}
			}else{
				
			}
		}
		updateSoundSystem();
		return true;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser ) {
		if( !fromUser )
			return;
		if( this.findViewById(R.id.spdBar) == seekBar ){
			if( m_iCurSoundId >= 0 ){
				float cur = (float)progress;
				cur *= 0.01f;
				cur += 0.5f;
				this.setSoundSpd( m_iCurSoundId, cur ); 
			}
			TextView speedText = (TextView)findViewById( R.id.speedText );
			String spdStr = "spd:";
			spdStr += progress;
			speedText.setText( spdStr );
		}else if( this.findViewById(R.id.volBar) == seekBar ){
			if( m_iCurSoundId >= 0 ){
				float cur = (float)progress;
				cur /= 100.0f;
				this.setSoundVol( m_iCurSoundId, cur ); 
			}
			TextView volText = (TextView)findViewById( R.id.volText );
			String volStr = "vol:";
			volStr += progress;
			volText.setText( volStr );
		}else if( seekBar == m_ProgressBar ){
			if( m_iCurSoundId >= 0 ){
				float cur = (float)progress;
				cur *= 0.01f;
				cur *= getSoundDur( m_iCurSoundId );
				this.setSoundPos( m_iCurSoundId, (int)cur );
			}

		}
	}
	
	@Override
	public void onCompletion(MediaPlayer player) {
		this.stopMusic();

	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {
		if( bar == m_ProgressBar ){
			m_bUpdateProgress = false;
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		if( bar == m_ProgressBar ){
			m_bUpdateProgress = true;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if( parent == mMusicList ){//this.findViewById( R.id.lstMusicList ) ){
			TextView curMusicName = (TextView)findViewById( R.id.curMusicName );
			if( curMusicName != null ){
				curMusicName.setText( m_curMusicList.get(position) );
				File nextFile = new File( m_curPath, m_curMusicList.get(position) );
				if( nextFile.isDirectory() ){
					m_curPath += "/";
					m_curPath += m_curMusicList.get(position);
					mMusicList.clearChoices();
					this.showPathFile();
					mCurMusicIndex = -1;
				}else{
					m_curMusicName = m_curMusicList.get( position );
					this.mCurMusicIndex = position;
					this.playMusic();
				}
			}
		}	
	}
	
	protected int playMusicAtList( int pos ){
		File nextFile = new File( m_curPath, m_curMusicList.get(pos) );
		if( !nextFile.isDirectory() ){
			m_curMusicName = m_curMusicList.get( pos );
			this.mCurMusicIndex = pos;
			this.playMusic();
			return pos;
		}
		return -1;
	}
	
	public boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		if( Environment.MEDIA_MOUNTED.equals( state )||
			Environment.MEDIA_MOUNTED_READ_ONLY.equals( state)){
			return true;
		}
		return false;	
	}
	
	public void onFileUpBtnClicked( View view ){
		if( m_curPath != null && !m_curPath.isEmpty() ){
			int slash = m_curPath.lastIndexOf( File.separator );
			if( slash > 1 ){
				m_curPath = m_curPath.substring( 0, slash );
			}
		}
		this.showPathFile();
	}
	
	public void showPathFile(){
		if( m_curMusicList == null )
			m_curMusicList = new ArrayList<String>();
		m_curMusicList.clear();
		File path = null;
		String msg;
		if( m_curPath == null || m_curPath.isEmpty() ){
			path = Environment.getExternalStorageDirectory();
		}else{
			path = new File( m_curPath );
			if( !path.isDirectory() ){
				path = Environment.getExternalStorageDirectory();
			}
		}		
		if( path != null ){
			File[] files = path.listFiles();
			int count = files.length;
			for( int i =0; i< count; i++ ){
				File curFile = files[i];
				if( curFile.isDirectory() ){
					m_curMusicList.add( curFile.getName() );
				}else if( curFile.isFile() ){
					String filename = curFile.getName();
					if( EMusicData.isMusicFileByExt( filename )){
						m_curMusicList.add( filename );
					}
				}
			}
			this.m_curPath = path.getAbsolutePath();
		}
		msg = m_curPath;
		msg += " : "; 
		msg += m_curMusicList.size();
		// refresh the list view
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        R.layout.my_music_lst_item,  m_curMusicList );
		mMusicList.setAdapter( adapter );
		// refresh the show of current path
		TextView curPathText = (TextView)this.findViewById(R.id.curPath);
		curPathText.setText( msg );
	} 
	
	public void onPreBtnClicked( View view ){
		if( m_curPath == null || m_curPath.isEmpty() || m_curMusicList.isEmpty() || mCurMusicIndex < 0 ||
			mCurMusicIndex >= m_curMusicList.size() )
			return;
		for( int i = mCurMusicIndex - 1; i >= 0; i -- ){
			int res = this.playMusicAtList( i );
			if( res != - 1 ){
				mCurMusicIndex = res;
				ListView listView = (ListView)this.findViewById( R.id.lstMusicList );
				listView.setSelectionFromTop(res, 0);
				return;
			}
		}
		this.playMusic();
	}

	public void onNextBtnClicked( View view ){
		if( m_curPath == null || m_curPath.isEmpty() || m_curMusicList.isEmpty() || mCurMusicIndex < 0 ||
			mCurMusicIndex >= m_curMusicList.size() )
			return;
		for( int i = mCurMusicIndex + 1; i < m_curMusicList.size(); i ++ ){
			int res = this.playMusicAtList( i );
			if( res != - 1 ){
				mCurMusicIndex = res;
				ListView listView = (ListView)this.findViewById( R.id.lstMusicList );
				listView.setSelectionFromTop(res, 0);
				return;
			}
		}
		this.playMusic();
	}
	
	public void onABBtnClicked( View view ){
		if( this.m_iCurSoundId >= 0 ){
			ImageButton btn = (ImageButton)this.findViewById( R.id.ABBtn );
			if( btn != null ){
				switch( mAbInfo.state ){
				case A:
					mAbInfo.state = ABState.B;
					mAbInfo.startTime = this.getSoundPos( this.m_iCurSoundId );
					break;
				case B:
					mAbInfo.state = ABState.PLAY;
					mAbInfo.endTime = this.getSoundPos( this.m_iCurSoundId );
					if( mAbInfo.endTime < mAbInfo.startTime ){
						int tmp = mAbInfo.endTime;
						mAbInfo.endTime = mAbInfo.startTime;
						mAbInfo.startTime = tmp;
					}else if( mAbInfo.endTime == mAbInfo.startTime ){
						mAbInfo.state = ABState.A;
					}
					if( mAbInfo.state == ABState.PLAY ){
						setSoundLooped( m_iCurSoundId, true );
						setSoundLoopPoint( m_iCurSoundId, mAbInfo.startTime, mAbInfo.endTime );
					}
					break;
				case PLAY:
					mAbInfo.state = ABState.A;
					setSoundLooped( m_iCurSoundId, false );
					break;
				default:
					break;
				}
				switch( mAbInfo.state ){
				case A:
					btn.setImageResource( R.drawable.a );
					break;
				case B:
					btn.setImageResource( R.drawable.b );
					break;
				case PLAY:
					btn.setImageResource( R.drawable.c );
					break;
				default: break;
				}
			}
		}
	}
	
	static {
    	// Try debug libraries...
    	try { System.loadLibrary("fmodD");
    		  System.loadLibrary("fmodstudioD"); }
    	catch (UnsatisfiedLinkError e) { }
    	// Try logging libraries...
    	try { System.loadLibrary("fmodL");
    		  System.loadLibrary("fmodstudioL"); }
    	catch (UnsatisfiedLinkError e) { }
		// Try release libraries...
		try { System.loadLibrary("fmod");
		      System.loadLibrary("fmodstudio"); }
		catch (UnsatisfiedLinkError e) { }
		System.loadLibrary("stlport_shared");
		System.loadLibrary("playCtrl");
	}
	
	private native void setStateStart();
	private native void setStateStop();
	private native void setStateDestroy();
	private native void main();
	private native void updateSoundSystem();
	private native int playSound( String filepath );
	private native boolean stopSound( int id );
	private native void pauseSound( int id, boolean flag );
	private native boolean isSoundPaused( int id );
	private native int getSoundPos( int id );
	private native int getSoundDur( int id );
	private native void setSoundPos( int id, int pos );
	private native void setSoundVol( int id, float vol );
	private native void setSoundSpd( int id, float spd );
	private native void setSoundLoopPoint( int id, int start, int end );
	private native void setSoundLoopCount( int id, int count );
	private native void setSoundLooped( int id, boolean flag );
	private native boolean getSoundLooped( int id );
	
//	protected MediaPlayer m_Sound;
	// view
	protected ListView mMusicList = null;
	protected ProgressBar m_VolumeBar = null;
	protected SeekBar m_ProgressBar = null;
	protected ImageButton mPlayBtn = null;
	// properties
	protected boolean m_bChanged = false;
	protected boolean m_bFinished = true;
	protected List<String> m_curMusicList = null;
	protected String m_curPath;
	protected String m_curMusicName;
	protected int mCurMusicIndex = -1;
	protected Handler m_Handler = null;
	protected boolean m_bUpdateProgress = true;
	protected Thread m_UpdateThread = null;
	protected Thread m_FmodThread = null;
	protected static boolean m_bUpdated = false;
	protected int m_iCurSoundId = -1;
	protected ABInfo mAbInfo = new ABInfo();

}

