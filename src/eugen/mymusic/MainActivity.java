package eugen.mymusic;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( !EMusicManager.isInit() ){
        	EMusicManager.init();
        }
    	//setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	EMusicManager.Deinit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
//        this.getLayoutInflater().
        
        inflater.inflate(R.menu.main, menu);
        this.getActionBar().hide();
        return super.onCreateOptionsMenu( menu );
    }
    
    @Override 
    public boolean onOptionsItemSelected( MenuItem item ){
    	switch( item.getItemId() ){
    	case R.id.action_search:
    		openSearch();
    		return true;
    	default: 
    		return super.onOptionsItemSelected( item );
    	}
    }
    
    public void openSearch()
    {
    	//do nothing now
    	Intent intent= new Intent( this, PlayCtrl.class );

    	startActivity( intent );
    }
    
    public void onPlayCtrlBtnClicked( View view )
    {
    	Intent intent= new Intent( this, PlayCtrl.class );
    	intent.putExtra( "MusicName", "current song name");
    	startActivity( intent );
    }
    
    public void onSelectMusicBtnClicked( View view )
    {
    	Intent intent= new Intent( this, PlayCtrl.class );
    	
    	startActivity( intent );
    }
    

    
}
