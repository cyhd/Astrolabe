package etma.navigator.shape;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

class CubeGeometry extends QuadArray {

	// point front high right
	public static final Point3d pfhr = new Point3d (1.0, 1.0, 1.0) ;
	// point front high left
	public static final Point3d pfhl = new Point3d (-1.0, 1.0, 1.0) ;
	// point front low right
	public static final Point3d pflr = new Point3d (1.0, -1.0, 1.0) ;
	// point front low left
	public static final Point3d pfll = new Point3d (-1.0, -1.0, 1.0) ;
	// point back high right
	public static final Point3d pbhr = new Point3d (1.0, 1.0, -1.0) ;
	// point back high left
	public static final Point3d pbhl = new Point3d (-1.0, 1.0, -1.0) ;
	// point back low right
	public static final Point3d pblr = new Point3d (1.0, -1.0, -1.0) ;
	// point back low left
	public static final Point3d pbll = new Point3d (-1.0, -1.0, -1.0) ;

	final static Point3d [] points = { pfhr, pfhl, pflr, pfll, pbhr, pbhl, pblr,
	      pbll } ;

	Point3d [] faces = {
	// front face
	      points [0], points [1], points [3], points [2],
	      // back face
	      points [5], points [4], points [6], points [7],
	      // right face
	      points [0], points [2], points [6], points [4],
	      // left face
	      points [1], points [5], points [7], points [3],
	      // top face
	      points [1], points [0], points [4], points [5],
	      // bottom face
	      points [3], points [7], points [6], points [2] } ;

	public CubeGeometry () {
		super (24, GeometryArray.COORDINATES) ;
		setCoordinates (0, faces) ;
	}

}

public class Cube extends Shape3D {

	public Cube () {
		super (new CubeGeometry ()) ;
		setAppearance (new Appearance ()) ;
	}

}
