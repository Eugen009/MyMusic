package eugen.engine.physics;

public class EPPhysicsWorld {
	public EPPhysicsWorld(){
		
	}
	
	public void setCollisionBetween( int i, int j , boolean flag ){
		int bitmask = 1 << (i * 4 + j);
		if( !flag ){
			bitmask = ~bitmask;
			mCollisionBitmask &= bitmask;
		}else{
			mCollisionBitmask |= bitmask;
		}
	}
	
	public boolean isCollisionBetween( int i, int j ){
		int bitmask = 1 <<( i*4 + j );
		return (bitmask & mCollisionBitmask) != 0;
	}
	
	public void setCollisionBitmask( int bitmask ){
		this.mCollisionBitmask = bitmask;
	}
	
	protected int mCollisionBitmask = 0xffffffff;
	
	public static EPPhysicsWorld getInst(){
		if( gWorld == null )
			gWorld = new EPPhysicsWorld();
		return gWorld;
	}
	
	public static EPPhysicsWorld gWorld;
	
}
