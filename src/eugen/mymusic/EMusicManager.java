package eugen.mymusic;

public class EMusicManager {
	
	public EMusicData getCurMusic(){ 
		return m_curMusic;
	}
	
	public float getVolume(){
		return m_fVolume;
	}
	
	public float getSpeed(){
		return m_fSpeed;
	}
	
	public void setSpeed( float speed ){
		m_fSpeed = speed;
	}
	
	public void setVolume( float volume ){
		m_fVolume = volume;
	}
	
	public static EMusicManager getManager(){
		return g_Manager;
	}
	
	public static void init(){
		if( g_Manager == null ){
			g_Manager = new EMusicManager();
		}
	}
	
	public static void Deinit(){
		g_Manager = null;
	}
	
	public static boolean isInit(){
		return g_Manager != null;
	}
	
	public static EMusicManager g_Manager = null;
	
	protected EMusicData m_curMusic;
	protected float m_fVolume;
	protected float m_fSpeed;
	protected float m_fProcess;
}
