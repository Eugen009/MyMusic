package eugen.engine;

public class ETimer {
	public float getTimeDifference(){
		return mTimeDiff;
	}
	public void update(){
		if( this.mPreSysTime == 0  ){
			this.mPreSysTime = System.currentTimeMillis();
		}
		this.mTimeDiff = (float)(System.currentTimeMillis() - mPreSysTime)*.001f;
		this.mPreSysTime = System.currentTimeMillis();
	} 
	public static ETimer getInst(){
		return gTimer;
	}
	protected static ETimer gTimer = new ETimer();
	protected float mTimeDiff = .0f;
	protected long mPreSysTime = 0;
	
}
