package game;

import java.util.Random;

import eugen.engine.EEntity2D;
import eugen.engine.EScreen;
import eugen.engine.ESpriteManager;
import eugen.engine.physics.ECollisionData;
import eugen.mymusic.ESprite;
import eugen.mymusic.EVector3;
import eugen.mymusic.R;
import android.content.Context;

public class EGEnemy extends EEntity2D{
	EGEnemy( Context context ){
		mSprite = new ESprite();
		mSprite.setTex( context.getResources(), R.drawable.plane );
		ESpriteManager.getInstance().addSprite( mSprite );
		this.setCollisionBitmask( EGMessageInfo.EnemyLayer );
//		this.setByRandomData();
	}
	
	@Override
	public void onThink( float timeDiff ){
		if( mSprite == null ) return;
		super.onThink(timeDiff);
		EVector3 curPos = this.getPosition();
		if( curPos == null ) return;
		if( this.getBoundingBox().getMin().y() > -EScreen.mHeight ){
			this.incPosition( mDir.mul( mSpeed* timeDiff ) );			
		}else{
			setByRandomData();
		}
	}
	
	public void onMessageFunction( int id, Object obj ){
		if( EGMessageInfo.CollisionMsgId == id ){	
			ECollisionData data = (ECollisionData)obj;
			if( data.other != null && 
				( data.other.getCollisionBitmask() == EGMessageInfo.BulletLayer || 
				  data.other.getCollisionBitmask() == EGMessageInfo.PlayerLayer )){
				this.setByRandomData();
			}
		}
	}
	
	public void setByRandomData(){
		float x = gRandom.nextFloat();
		x *= EScreen.mWidth;
		this.setPosition( new EVector3( x, -50.0f, .0f ));
		mSpeed = gRandom.nextFloat() * 250.0f + 250.0f;
		this.updateBoundingBox();
	}
	
	public void dispose(){
		
	}
	protected float mSpeed = 500.0f;
	protected EVector3 mDir = new EVector3( .0f, -1.0f, 0.0f );
	protected static Random gRandom = new Random();
}
