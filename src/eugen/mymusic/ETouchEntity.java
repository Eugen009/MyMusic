package eugen.mymusic;

import android.content.res.Resources;

public class ETouchEntity extends EEntity{
	public ETouchEntity( String name, Resources res ){
		super( name );

		float[] vexes = {
			1.0f, 	1.0f, .0f, 1.0f, 0.0f,  1.f, 1.f, 1.f,
			-1.0f, 	1.0f, .0f, .0f,  .0f,   1.f, 1.f, 1.f,
			-1.0f, -1.0f, .0f, .0f,  1.0f,  1.f, 1.f, 1.f,
			1.0f,  -1.0f, .0f, 1.0f, 1.0f,  1.f, 1.f, 1.f
		};
		short drawOrder[] = {  0, 1, 3, 1, 2, 3 };
		EMeshBuilder builder = new EMeshBuilder();
		builder.addVertexes( vexes );
		builder.addFaces( drawOrder );
		builder.addSurface( res, R.drawable.eight );
		this.mMesh = builder.finish(); 
	}
}
