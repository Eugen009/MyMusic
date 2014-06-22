package eugen.engine;


public class EAnimSprite extends ESprite
{
	protected boolean mLoop = true;
	protected boolean mPlay = false;
	protected int mColumn = 1;
	protected int mRow = 1;
	protected float[] mUVInfo= { 0.0f, 0.0f, 1.0f, 1.0f };
	protected float[] mUVOffset = { .0f, .0f, .0f, .0f };
	protected float[] mUVOffsetInfo ={ .0f, .0f, .0f, .0f };// umax, vmax, utime, vtime
}
