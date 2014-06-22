package eugen.engine;

import java.util.ArrayList;


public class EEntity {
	
	public EEntity( String name ){
		mName = name;
		gEntities.add( this );	
	}
	
	public void remove(){
		gEntities.remove(this);
	}
	
	public void setMesh(EMesh mesh){ mMesh = mesh; }
	public EMesh getMesh(){return mMesh;}
	
	protected EMesh mMesh = null;
	protected EVector3 mPosition;// ={.0f, .0f, .0f };
	protected EVector3 mOrientation;// = {.0f, .0f, .0f };
	protected EMatrix mMat;
	protected String mName = "";
	
	protected static ArrayList<EEntity> gEntities = new ArrayList<EEntity>();
	public static int getEntityCount(){
		return gEntities.size();
	}
	public static EEntity searchEntity( String name ){
		int count = gEntities.size();
		for( int i = 0; i < count; i++ ){
			EEntity ent = gEntities.get(i);
			if( gEntities.get(i).mName == name )
				return ent;
		}
		return null;
	}
	public static void removeAll(){
		gEntities.clear();
	}
	public static EEntity getEntityAt( int i ){
		if( i < 0 || i >= gEntities.size() )
			return null;
		return gEntities.get(i);
	}
	
}
