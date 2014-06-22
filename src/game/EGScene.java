package game;

import java.util.ArrayList;

import android.content.Context;

import eugen.engine.EEntity2D;
import eugen.engine.ESpriteManager;
import eugen.engine.ETimer;
import eugen.engine.physics.ECollisionData;
import eugen.engine.physics.EPPhysicsWorld;

public class EGScene {
	
	public void init( Context context ){
		initPhysics();
		this.createBackground( context );
		this.createPlayer(context);
		int enemyCount = 5;
		for( int i = 0; i < enemyCount; i++ ){
			this.createEnemy( context );
		}
	}
	
	public void initPhysics(){
//		EPPhysicsWorld.getInst().setCollisionBitmask(0);
		EPPhysicsWorld world = EPPhysicsWorld.getInst();
		world.setCollisionBitmask( 0 );
		world.setCollisionBetween( EGMessageInfo.PlayerLayer, EGMessageInfo.EnemyLayer, true );
		world.setCollisionBetween( EGMessageInfo.BulletLayer, EGMessageInfo.EnemyLayer, true );
	}
	
	public void update(){
		ETimer.getInst().update();
		float timeDiff = ETimer.getInst().getTimeDifference();
		purgeRemovedEntities();
		int size = mEntities.size();
		for( int i = 0; i< size; i++ ){
			EEntity2D ent = mEntities.get( i );
			if( ent != null && ent.isUpdate() )
				ent.onThink( timeDiff );
		}
		for( int i = 0; i< size; i++ ){
			EEntity2D ent = mEntities.get( i );
			ent.updateBoundingBox();
		}
		this.updateCollision(timeDiff);
		
		for( int i = 0; i< size; i++ )
			mEntities.get(i).setChanged( false );	
	}
	
	public void updateCollision( float timeDiff ){
//		if( this.mPlayers == null )
//			return;
		
		int size = this.mEntities.size();
		for( int i = 0; i < size; i++ ){
			EEntity2D entA = mEntities.get( i );
			for( int j = i+1; j < size; j++ ){
				EEntity2D entB = mEntities.get( j );
				if( EPPhysicsWorld.getInst().isCollisionBetween( entA.getCollisionBitmask(), entB.getCollisionBitmask() )
					&& entA.isCollided( entB ) ){
					ECollisionData data = new ECollisionData();
					data.other = entB;
					entA.onMessageFunction( EGMessageInfo.CollisionMsgId, data );
					data.other = entA;
					entB.onMessageFunction( EGMessageInfo.CollisionMsgId, data );
				}
			}
		}
		

	}
	
	public void addEntity( EEntity2D ent ){
		this.mEntities.add( ent );
	}
	
	public void createPlayer( Context context ){
		EEntity2D mPlayers = new EGPlayer( context );
		addEntity( mPlayers );
	}
	
	public void createEnemy( Context context ){
		EGEnemy enemy = new EGEnemy( context );
		addEntity( enemy );
//		this.mEnemies.add( enemy );
	}
	
	public void createBackground( Context context ){
		EEntity2D mBackground = new EGBackground( context );
		addEntity( mBackground );
	}
	
	public boolean isUpdate(){
		return mUpdate;
	}
	
	public void setUpdate( boolean flag ){
		this.mUpdate = flag;
	}
	
	public void purgeRemovedEntities(){
		int size = this.mEntities.size();
		boolean flag = true;
		while( flag ){
			flag = false;
			for( int i = 0; i < size; i++ ){
				EEntity2D ent = this.mEntities.get(i);
				if( ent != null && ent.isRemoved() ){
					this.mEntities.remove( i );
					flag = true;
					break;
				}
			}
		}
	}
	
	public void clear(){
		this.mEntities.clear();
		ESpriteManager.getInstance().clear();
	}
	
//	protected EGPlayer mPlayers;
//	protected EGBackground mBackground;
	protected ArrayList<EEntity2D> mEntities = new ArrayList<EEntity2D>();
//	protected ArrayList<EGEnemy> mEnemies = new ArrayList<EGEnemy>();
	protected boolean mUpdate = true;
	
	
}
