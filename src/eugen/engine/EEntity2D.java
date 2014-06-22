package eugen.engine;


public class EEntity2D {
	
	public EVector3 getPosition(){
		if( mSprite == null ) return null;
		return mSprite.mMat.getTranslate();
	}
	
	public void setPosition( EVector3 pos ){
		if( mSprite != null ){
			mSprite.mMat.setTranslate( pos );
			mChanged = true;
		}
	}
	
	public void setPosition( float x, float y, float z ){
		if( mSprite != null ){
			mSprite.mMat.setTranslate(x, y, z);
			mChanged = true;
		}
	}
	
	public void incOrientation( float roll, float pitch, float yaw ){
		if( mSprite !=  null ){
			mSprite.mMat.rotate( roll, pitch, yaw );
			mChanged = true;
		}
	}
	
	public EBoundingBox getBoundingBox(){
		if( this.mSprite != null ){
			return mSprite.getBoundingBox();
		}
		return null;
	}
	
	public void incPosition( EVector3 inc ){
		if( mSprite == null ) return;
		EMatrix tMat = this.mSprite.getMat();
		EVector3 pos = tMat.getTranslate();
		pos.add( inc );
		tMat.setTranslate( pos );
//		EVector3 tpos = tMat.getTranslate();
		mChanged = true;
	}
	
	public boolean isCollided( EEntity2D ent ){
		if( this.mSprite != null && ent.mSprite != null ){
			return mSprite.getBoundingBox().intersectAABB( ent.mSprite.getBoundingBox() );
		}
		return false;
	}
	
	public boolean isChanged(){
		return mChanged;
	}
	
	public void setChanged( boolean flag ){
		this.mChanged = flag;
	}
	
	public void onMessageFunction( int MsgId, Object obj ){
		// implements 
	}
	
	public void onThink( float timeDiff ){
		if( this.mSprite != null ){
			mSprite.nextFrame(timeDiff);
		}
	}
	
	public void updateBoundingBox(){
		if( this.mSprite == null )
			return;
		if( mChanged ){
			EVector3 min = new EVector3( -.5f, -.5f, .0f );
			EVector3 max = new EVector3( .5f, .5f, .0f );
			EBoundingBox bbox = new EBoundingBox( min, max );
			bbox.mulSelfMatrix( mSprite.mMat );
//			min = mSprite.mMat.mul( min );
//			max = mSprite.mMat.mul( max );
//			EBoundingBox bbox = new EBoundingBox( min, max );
			mSprite.setBoundingBox( bbox );
		}
	}
	
	public void remove(){
		this.mRemoved = true;
	}
	
	public boolean isRemoved(){
		return this.mRemoved;
	}
	
	public void setUpdate( boolean bUpdate ){
		this.mUpdate = bUpdate;
	}
	
	public boolean isUpdate(){
		return mUpdate;
	}
	
	public void setVisible( boolean flag ){
		if( mSprite != null ){
			mSprite.setVisible( flag );
		}
	}
	
	public void setCollisionBitmask( int bitmask ){
		mCollisionBitmask = bitmask;
	}
	
	public int getCollisionBitmask(){
		return mCollisionBitmask;
	}
	
	protected ESprite mSprite = null;
	protected boolean mChanged = true;
	protected boolean mRemoved = false;
	protected boolean mUpdate = true;
	protected int mCollisionBitmask = 0;
}
