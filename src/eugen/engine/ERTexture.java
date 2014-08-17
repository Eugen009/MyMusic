package eugen.engine;

public class ERTexture {
	public ERTexture(){
		mTexId = -1;
	}
	public ERTexture( int texId ){
		this.mTexId = texId;
	}
	public void dispose(){
		mReference --;
		if( mReference <= 0 && mMgr != null ){
			mMgr.removeTexture( this );
		}
	}
	public void addRef(){
		mReference ++;
	}
	protected int mTexId = -1;
	protected int mResId = -1;
	protected ERTextureManager mMgr = null;
	protected int mReference = 0;
}
