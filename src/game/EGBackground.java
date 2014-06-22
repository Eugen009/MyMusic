package game;

import android.content.Context;
import eugen.engine.EEntity2D;
import eugen.engine.EFullSprite;
import eugen.engine.ESpriteManager;
import eugen.mymusic.R;

public class EGBackground extends EEntity2D{
	public EGBackground( Context context ){
		mSprite = new EFullSprite();
		mSprite.setTex( context.getResources(), R.drawable.dusk );
		mSprite.setRollInfo( .0f, 1.0f, .0f, 10.0f );
		ESpriteManager.getInstance().addSprite( mSprite );
		this.incOrientation(.0f, .0f, 180.0f );
	}
	
//	protected EFullSprite mSprite = null; 
}
