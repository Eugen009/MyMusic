package eugen.engine;

import java.util.ArrayList;
import java.util.List;

public class ECallback {
	public void addHandler( ECallbackHandler handler ){
		this.mHandlers.add( handler );
	}
	public void triggerAll( ECallbackData data ){
		if( data == null ){
			data = new ECallbackData();
		}
		data.mSender = this;
		int count = mHandlers.size();
		for( int i = 0; i< count; i++ ){
			ECallbackHandler handler = mHandlers.get(i);
			handler.onCallbackHandle( data );
		}
	}
	List<ECallbackHandler> mHandlers = new ArrayList<ECallbackHandler>();
}
