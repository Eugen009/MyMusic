package game;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;

public class EGBulletPool {
	public EGBulletPool(){
		
	}
	
	public void initPool( int num, Context context ){
		for( int i = 0; i< num; i++ ){
			EGBullet bullet = new EGBullet( context, this );
			mFreeBullets.addFirst( bullet );
			EGSceneManager.getInst().getCurScene().addEntity( bullet );
		}
	}
	
	public EGBullet getFreeBullet(){
		if( mFreeBullets.size() == 0 ) return null;
		EGBullet bullet = mFreeBullets.pop();
		mUsedBullets.push( bullet );
		bullet.setUpdate( true );
		bullet.setVisible( true );
		return bullet;
	}
	
	public void freeBullet( EGBullet bullet ){
		int index = mUsedBullets.indexOf( bullet );
		if( index != -1 ){
			bullet.setUpdate( false );
			bullet.setVisible( false );
			mUsedBullets.remove( index );
			mFreeBullets.push( bullet );
		}
	}
	
	protected LinkedList<EGBullet> mUsedBullets = new LinkedList<EGBullet>();
	protected LinkedList<EGBullet> mFreeBullets = new LinkedList<EGBullet>();
}
