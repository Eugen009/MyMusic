package eugen.mymusic;

import java.io.File;
import java.io.IOException;
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
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayCtrl extends Activity 
		implements 	OnSeekBarChangeListener, 
					OnItemClickListener,
					MediaPlayer.OnCompletionListener,
					Handler.Callback,
					Runnable{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_ctrl);
		// Show the Up button in the action bar.
		setupActionBar();
		
		SharedPreferences settings = getSharedPreferences( EMusicData.PREFS_NAME, 0 );
		float curVolume = settings.getFloat( EMusicData.ST_VOLUME, 1.0f );
		this.m_curPath = settings.getString( EMusicData.ST_MUSIC_PATH, null );
		this.m_curMusicName = settings.getString( EMusicData.ST_CUR_MUSIC_NAME, null );
		this.mCurMusicIndex = settings.getInt( EMusicData.ST_CUR_MUSIC_INDEX, -1 );
		
		
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
		showPathFile();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        R.layout.my_music_lst_item,  m_curMusicList );
		listView.setAdapter( adapter );
		listView.setOnItemClickListener( this );
		// set the music
		setVolumeControlStream( AudioManager.STREAM_MUSIC );
		TextView curMusicName = (TextView)this.findViewById(R.id.curMusicName);
		if( m_curMusicName != null && !m_curMusicName.isEmpty() ){
			curMusicName.setText( m_curMusicName );
		}else{
			curMusicName.setText( "Waiting for music...." );
		}	
		m_Handler = new Handler( this );
		this.m_UpdateThread = new Thread( this );
		m_UpdateThread.start();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		this.stopMusic();
		SharedPreferences settings = getSharedPreferences( EMusicData.PREFS_NAME, 0 );
		SharedPreferences.Editor editor = settings.edit();
		float vol = (float)m_VolumeBar.getProgress() / 100.0f;
		editor.putFloat( EMusicData.ST_VOLUME, vol );
		editor.putString( EMusicData.ST_MUSIC_PATH, this.m_curPath );
		editor.putString( EMusicData.ST_CUR_MUSIC_NAME, this.m_curMusicName );
		editor.putInt( EMusicData.ST_CUR_MUSIC_INDEX, this.mCurMusicIndex );
		editor.commit();
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void OnMusicStopped(){
		Button btn = (Button)findViewById(R.id.PlayBtn);
		if( btn != null ) 
			btn.setText( "Play" );
		SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		bar.setProgress(0);
	}
	
	@Override
	public void run() {
		// refresh ui
		long curTime = SystemClock.uptimeMillis();
		while( true ){
			if( SystemClock.uptimeMillis() -curTime > 100 ){
				m_Handler.sendMessage( new Message() );
				curTime = SystemClock.uptimeMillis();
			}
		}
	}
	
	public boolean playMusic(){
		if( this.m_curPath == null || this.m_curPath.isEmpty() ||
			this.m_curMusicName == null || this.m_curMusicName.isEmpty() ){
			return false;
		}
		String filename = m_curPath + "/" + m_curMusicName;
		this.stopMusic();
		m_Sound = new MediaPlayer();
		m_Sound.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			m_Sound.setDataSource( filename );
		}catch( IOException e ){
			e.printStackTrace();
			String msg = "Fail to load file:";
			msg += filename;
			new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			this.stopMusic();
			return false;
		}
		try {
			m_Sound.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			String msg = "Fail to prepare the music:";
			msg += filename;
			new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			this.stopMusic();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "Fail to prepare the music:";
			msg += filename;
			new AlertDialog.Builder(this) .setTitle("Warning") .setMessage(msg) .show();
			this.stopMusic();
		}
		float curVol = (float)m_VolumeBar.getProgress() / 100.0f ;
		m_Sound.setVolume( curVol, curVol );
		m_Sound.setOnCompletionListener( this );
		m_Sound.start();
		Button btn = (Button)findViewById(R.id.PlayBtn);
    	if( btn != null ){
    		btn.setText( "Pause" );
    	}
		TextView curPathText = (TextView)this.findViewById(R.id.curMusicName);
		curPathText.setText( m_curMusicName );
		return true;
	}
	
	public void stopMusic(){
		if( m_Sound != null ){
			if( m_Sound.isPlaying() ){
				m_Sound.stop();
			}
			m_Sound.release();
			m_Sound = null;
		}
		Button btn = (Button)findViewById(R.id.PlayBtn);
    	if( btn == null )
    		return;
		btn.setText( "Play" );
		SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		bar.setProgress( 0 );
	}
	
    public void onPlayBtnClicked( View view ){
		Button btn = (Button)findViewById(R.id.PlayBtn);
    	if( btn == null )
    		return;
    	if( m_Sound != null ){
    		if( m_Sound.isPlaying() ){
    			m_Sound.pause();
    			btn.setText( "Play" );
    		}else{ 
    			m_Sound.start();
    			btn.setText( "Pause" );
    		}
    	}else{
    		this.playMusic();
    	}
    }
    
    public void onStopBtnClicked( View view )
    {
    	if( m_Sound == null )
    		return;
    	this.stopMusic();
    	Button btn = (Button)findViewById( R.id.PlayBtn );
    	btn.setText( "Play" );
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
			if( m_Sound != null && m_Sound.isPlaying() ){
		    	SeekBar bar = (SeekBar)findViewById(R.id.progressBar1);
		    	float cur = (float)m_Sound.getCurrentPosition() / (float)m_Sound.getDuration();
				cur *= 100.0f;
				bar.setProgress( (int)cur );
				return true;
			}else{
				
			}
		}
		return false;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser ) {
		if( !fromUser )
			return;
		if( this.findViewById(R.id.spdBar) == seekBar ){
			if( m_Sound != null ){
				
			}
		}else if( this.findViewById(R.id.volBar) == seekBar ){
			if( m_Sound != null ){
				float cur = (float)progress;
				cur /= 100.0f;
				m_Sound.setVolume( cur, cur );
				TextView volText = (TextView)findViewById( R.id.volText );
				String volStr = "vol:";
				volStr += progress;
				volText.setText( volStr );
			}
		}else if( seekBar == m_ProgressBar ){
			if( m_Sound != null ){
				float cur = (float)progress;
				cur *= 0.01f;
				cur *= m_Sound.getDuration();
				m_Sound.seekTo( (int)cur );
				TextView speedText = (TextView)findViewById( R.id.speedText );
				String spdStr = "spd:";
				spdStr += progress;
				speedText.setText( spdStr );
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
		if( parent == this.findViewById( R.id.lstMusicList ) ){
			TextView curMusicName = (TextView)findViewById( R.id.curMusicName );
			if( curMusicName != null ){
				curMusicName.setText( m_curMusicList.get(position) );
				File nextFile = new File( m_curPath, m_curMusicList.get(position) );
				if( nextFile.isDirectory() ){
					m_curPath += "/";
					m_curPath += m_curMusicList.get(position);
					this.showPathFile();
					mCurMusicIndex = -1;
				}else{
					m_curMusicName = m_curMusicList.get( position );
					this.mCurMusicIndex = position;
//					m_curMusicName = m_curPath + "/" + m_curMusicList.get( position );
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
					int dotIdx = filename.indexOf( '.' );
					if( dotIdx > 1 ){
						String ext = filename.substring( dotIdx, filename.length() );
						if( !ext.isEmpty() && ext.compareToIgnoreCase(".mp3") == 0  ){
							m_curMusicList.add( filename );
						}
					}
				}
			}
			this.m_curPath = path.getAbsolutePath();
		}
		msg = m_curPath;
		msg += " : ";
		msg += m_curMusicList.size();
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
	
	protected MediaPlayer m_Sound;
	protected boolean m_bChanged = false;
	protected boolean m_bFinished = true;
	protected ProgressBar m_VolumeBar = null;
	protected List<String> m_curMusicList = null;
	protected String m_curPath;
	protected String m_curMusicName;
	protected int mCurMusicIndex = -1;
	protected Handler m_Handler = null;
	protected boolean m_bUpdateProgress = true;
	protected Thread m_UpdateThread = null;
	protected SeekBar m_ProgressBar;



}
