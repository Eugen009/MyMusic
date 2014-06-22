package eugen.engine;

import java.util.ArrayList;
import java.util.List;


public class ESpriteManager {
	protected static ESpriteManager gInstance = null;
	public static ESpriteManager getInstance(){
		if( gInstance == null )
			gInstance = new ESpriteManager();
		return gInstance;
	}
	
	public List<ESprite> gSprites = new ArrayList<ESprite>();
	public ESprite createSprite(){
		ESprite sprite = new ESprite();
		gSprites.add(sprite);
		return sprite;
	}
	public void removeSprite( ESprite obj ){
		gSprites.remove(obj);
//		gSprites.indexOf(obj);
	}
	public void addSprite( ESprite obj ){
		gSprites.add( obj );
	}
	public int getSpriteSize(){
		return gSprites.size();
	}
	public ESprite getSpriteAt( int i ){
		return gSprites.get(i);
	}
	public void checkTouch( float x, float y ){
		int count = gSprites.size();
		EVector3 vec = new EVector3( x, y, 0.0f );
		for( int i = 0; i< count; i++ ){
			ESprite sprite = gSprites.get( i );
			EBoundingBox bbox = sprite.getBoundingBox();
			if( bbox.isInside( vec )) {
				sprite.onTouch( x, y );
//				return sprite;
			}
		}
	}
	public void clear(){
		int count = gSprites.size();
		for( int i = 0; i< count; i++ ){
			ESprite sprite = gSprites.get(i);
			sprite.onRemove();
		}
		this.gSprites.clear();
	}
	
}
