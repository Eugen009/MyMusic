package game;

import android.content.Context;
import eugen.engine.ECallbackData;
import eugen.engine.ECallbackHandler;
import eugen.engine.EEntity2D;
import eugen.engine.EInput;
import eugen.engine.EMatrix;
import eugen.engine.EScreen;
import eugen.engine.ESprite;
import eugen.engine.ESpriteManager;
import eugen.engine.ETimer;
import eugen.engine.ETouchCallbackData;
import eugen.engine.EVector3;
import eugen.engine.EVector4;
import eugen.engine.physics.ECollisionData;
import eugen.mymusic.R;

public class EGPlayer extends EEntity2D implements ECallbackHandler{
	
	enum PlayerState{
		NONE,NORMAL,HIT,DEAD,RESTARTING
	}
	
	public EGPlayer( Context context ){
		EInput.onTouch.addHandler( this );
		mSprite = new ESprite();
		mSprite.setTex( context.getResources(), R.drawable.plane );
		ESpriteManager.getInstance().addSprite( mSprite );
		mBulletPool = new EGBulletPool();
		this.mBulletPool.initPool( 10, context );
//		this.setOrientation( .0f, .0f, 180.0f);
		setCollisionBitmask( EGMessageInfo.PlayerLayer );
	}
	
	public void onTouch( float x, float y ){

	}
	
	public void onThink( float timeDiff ){
		if( this.mSprite == null ) return;
		super.onThink( timeDiff );
		mUpdate = true;
		switch( this.mPlayerState ){
		case NORMAL:
//			this.updateMove(timeDiff);
			break;
		case HIT:
			this.updateHit(timeDiff);
			break;
		default:
			
		}	
		onNormalUpdate( timeDiff );
		this.updateMove(timeDiff);
		mUpdate = false;
	}
	
	protected void updateMove( float timeDiff ){
		if( mDir == null ) return;
		float next = this.mStep * timeDiff;
		if( mCur + next > 1.0f ){ 
			next = 1.0f - mCur; 
			mCur = 1.0f;
		}else
			mCur += next;
		EVector3 inc = mDir.mul( next );
		this.incPosition( inc );
	}
	
	protected void resetMoveData(){
		mStep = .0f;
		mCur = 1.0f;
		this.mDir = null;
	}
	
	@Override
	public void onCallbackHandle(ECallbackData data) {
		// TODO Auto-generated method stub
//		if( this.mPlayerState != PlayerState.NORMAL )
//			return;
		if( mSprite == null ) return;
		while( mUpdate );
		if( data.mSender == EInput.onTouch ){
			ETouchCallbackData tData = (ETouchCallbackData)data;
			tData.y = -tData.y;
			EMatrix tMat = mSprite.getMat();
			EVector3 v = tMat.getTranslate();
			EVector3 touchPos = new EVector3( tData.x, tData.y, .0f );
			if( !mSprite.getBoundingBox().isInside(touchPos)){
				return;
			}
			mStep = .0f;
			mCur = 1.0f;
//			this.mDir.set( tData.x, tData.y, v.z() );
			mDir.setSub( touchPos, v );
			float dis = mDir.getLength();
			if( dis > .000001f ){
				mStep = mSpeed / dis;
				mCur = .0f;
			}else{
				mDir.set( .0f, .0f, .0f );
			}
		}
	}
	
	@Override 
	public void onMessageFunction( int msgId, Object obj ){
		if( msgId == EGMessageInfo.CollisionMsgId ){
			ECollisionData data = (ECollisionData)obj;
			if( data != null && data.other.getCollisionBitmask() == EGMessageInfo.EnemyLayer ){
				if( this.mPlayerState == PlayerState.NORMAL ){
					mPlayerState = PlayerState.HIT;
					this.onHit();
				}
			}
		}
	}
	
	protected void onHit(){
		mHitTime = 3.0f;
	}
	
	protected void onNormalUpdate( float timeDiff ){
		if( mBulletPool == null ) return;
		if( this.mCurLT > this.mLaunchTime ){
			mCurLT = .0f;
			EGBullet bullet = this.mBulletPool.getFreeBullet();
			EGScene scene = EGSceneManager.getInst().getCurScene();
			if( bullet != null && scene != null ){
				bullet.setPosition( this.getPosition() );
			}
		}else{
			mCurLT += timeDiff;
		}
	}
	
	protected void updateHit( float timeDiff ){
		if( mHitTime > .0f ){
			mHitTime -= timeDiff;
			this.mSprite.setColor( EVector4.RED );//new EVector4( 1.0f, .0f, .0f, 1.0f ) );
		}else{
			this.mSprite.setColor( EVector4.WHITE );//new EVector4( 1.0f, 1.0f, 1.0f, 1.0f ) );
			this.mPlayerState = PlayerState.NORMAL;
		}
	}
	
	protected void onDead(){
		
	}
	
	protected float mSpeed = 5000.0f;
	protected float mCur = .0f;
	protected float mStep = .0f;
	protected EVector3 mDir = new EVector3();
	protected PlayerState mPlayerState = PlayerState.NORMAL;
	protected boolean mUpdate = false;
	protected float mLaunchTime = 0.1f;
	protected float mCurLT = .0f;
	protected float mHitTime = 3.0f;
	protected EGBulletPool mBulletPool;// = new EGBulletPool();
}
