package eugen.mymusic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EMusicCtrl extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {	
//    	return null;
        return inflater.inflate(R.layout.music_ctrl, container, false);
    }

}
