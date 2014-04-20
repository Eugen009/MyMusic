package eugen.mymusic;

import android.util.Log;

public class EMusicData {
	public String path;
	public String name;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String ST_VOLUME = "volume";
	public static final String ST_MUSIC_PATH = "music_path";
	public static final String ST_CUR_MUSIC_NAME = "cur_music_name";
	public static final String ST_CUR_MUSIC_INDEX = "cur_music_index";
	public static final String[] MusicTypes = { ".mp3", ".wma", ".wav" };
	
	public static boolean isMusicFileByExt( String filename ){
//		String filename = curFile.getName();
		int dotIdx = filename.lastIndexOf( '.' );
		if( dotIdx > 1 ){
			String ext = filename.substring( dotIdx, filename.length() );
			if( !ext.isEmpty() ){
				for( int i = 0; i< MusicTypes.length;i ++){
					if( ext.compareToIgnoreCase(MusicTypes[i]) == 0  )
						return true;
				}
			}
		}
		return false;
	}
	
	public static void log(String msg){
		Log.d("MyLog", msg);
	}
}
