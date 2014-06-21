package game;

import android.content.Context;
import eugen.engine.EEntity2D;
import eugen.engine.EScreen;
import eugen.engine.ESpriteManager;
import eugen.engine.physics.ECollisionData;
import eugen.mymusic.ESprite;
import eugen.mymusic.EVector3;
import eugen.mymusic.R;

public class EGBullet extends EEntity2D {
	
	public EGBullet( Context context, EGBulletPool pool ){
		mSprite = new ESprite();
		mSprite.setTex( context.getResources(), R.drawable.bullet );
		ESpriteManager.getInstance().addSprite( mSprite );
		mPool = pool;
		setCollisionBitmask( EGMessageInfo.BulletLayer );
	}
	
	public void onThink( float timeDiff ){
		if( mSprite == null ) return;
		super.onThink(timeDiff);
		if( EScreen.mScreenBBox.intersectAABB( this.getBoundingBox()) ){
			EVector3 inc = mDir.mul( this.mSpeed * timeDiff );
			this.incPosition( inc );
		}else{
			this.setUpdate( false );
			this.mPool.freeBullet( this );
		}
	}
	
	public void onMessageFunction( int id, Object obj ){
		if( id == EGMessageInfo.CollisionMsgId ){
			ECollisionData data = (ECollisionData)obj;
			if( data.other != null && data.other.getCollisionBitmask() == EGMessageInfo.EnemyLayer ){
				mPool.freeBullet( this );
			}
		}
	}
	
	public void remove(){
		super.remove();
		if( mPool != null ){
			this.mPool.freeBullet( this );
		}
	}
	
	protected float mSpeed = 500.0f;
	protected EVector3 mDir = new EVector3( .0f, 1.0f, .0f );
	protected EGBulletPool mPool = null;
}
