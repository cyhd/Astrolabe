package fr.etma.navigator.shape;

import java.io.FileNotFoundException;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

import fr.etma.navigator.CollisionDetector;
import fr.etma.navigator.timeRecorder.Detector;

public class ShapeFactory {

	static public TransformGroup createCube (Vector3d v3d, Color3f color, Color3f collisionColor) {
    Transform3D translation = new Transform3D () ;
    translation.setTranslation (v3d) ;
    translation.setScale (0.3d) ;
    TransformGroup objTrans = new TransformGroup (translation) ;
    objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
    objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
    objTrans.setCapability (Node.ENABLE_PICK_REPORTING) ;
    Cube box = new Cube () ;
    box.getGeometry ().setCapability (Geometry.ALLOW_INTERSECT) ;
    Appearance app = new Appearance () ;
    box.setAppearance (app) ;
    objTrans.addChild (box) ;
    ColoringAttributes ca = new ColoringAttributes () ;
    ca.setColor (color) ;
    app.setCapability (Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE) ;
    app.setColoringAttributes (ca) ;
    TransparencyAttributes ta = new TransparencyAttributes () ;
    ta.setTransparency (0.5f) ;
    ta.setTransparencyMode (TransparencyAttributes.FASTEST) ;
    app.setTransparencyAttributes (ta);
    PolygonAttributes pa = new PolygonAttributes () ;
    pa.setCullFace (PolygonAttributes.CULL_NONE) ;
    app.setPolygonAttributes (pa);
    CollisionDetector cd = new CollisionDetector (box, collisionColor, null) ;
    BoundingSphere bounds = new BoundingSphere (new Point3d (0.0, 0.0, 0.0), 1.0) ;
    cd.setSchedulingBounds (bounds) ;

    // Add the behavior to the scene graph
    objTrans.addChild (cd) ;
    return (objTrans) ;
 }

	static public TransformGroup createTarget (Vector3d v3d, Vector3d nextV3d, Color3f color, Color3f collisionColor, Detector detector) {
	      Transform3D translation = new Transform3D () ;
	      
	      Vector3d y = new Vector3d (0, 1, 0) ;
	      Vector3d length = new Vector3d () ;
	      length.sub (nextV3d, v3d) ;
	      Vector3d rotationAxis = new Vector3d () ;
	      rotationAxis.cross (y, length) ;
	      Quat4d rotation = new Quat4d () ;
	      rotation.set (new AxisAngle4d (rotationAxis.getX (), rotationAxis.getY (), rotationAxis.getZ (), y.angle (length))) ;      
	      translation.setRotation (rotation) ;

	      translation.setTranslation (v3d) ;
	      translation.setScale (new Vector3d (1.3, 0.05, 1.3)) ;
	      TransformGroup objTrans = new TransformGroup (translation) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
	      objTrans.setCapability (Node.ENABLE_PICK_REPORTING) ;
	      Cube box = new Cube () ;
	      box.getGeometry ().setCapability (Geometry.ALLOW_INTERSECT) ;
	      Appearance app = new Appearance () ;
	      box.setAppearance (app) ;
	      objTrans.addChild (box) ;
	      ColoringAttributes ca = new ColoringAttributes () ;
	      ca.setColor (color) ;
	      app.setCapability (Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE) ;
	      app.setColoringAttributes (ca) ;
	      TransparencyAttributes ta = new TransparencyAttributes () ;
	      ta.setTransparency (0.5f) ;
	      ta.setTransparencyMode (TransparencyAttributes.FASTEST) ;
	      app.setTransparencyAttributes (ta);
	      PolygonAttributes pa = new PolygonAttributes () ;
	      pa.setCullFace (PolygonAttributes.CULL_NONE) ;
	      app.setPolygonAttributes (pa);
	      CollisionDetector cd = new CollisionDetector (box, collisionColor, detector) ;
	      BoundingSphere bounds = new BoundingSphere (new Point3d (0.0, 0.0, 0.0), 1.0) ;
	      cd.setSchedulingBounds (bounds) ;
	      Appearance smallApp = new Appearance () ;
	      ColoringAttributes smallca = new ColoringAttributes () ;
	      smallca.setColor (collisionColor) ;
	      Box smallBox = new Box (0.2f, 0.1f, 0.2f, smallApp) ;
	      smallApp.setColoringAttributes (smallca) ;
	      objTrans.addChild (smallBox) ;
	      // Add the behavior to the scene graph
	      objTrans.addChild (cd) ;
	      return (objTrans) ;
	   }
	      
	  static public TransformGroup createTarget (Vector3d previousV3d, Vector3d v3d, Vector3d nextV3d, Color3f color, Color3f collisionColor, Detector detector) {
	      Transform3D translation = new Transform3D () ;

	      Vector3d y = new Vector3d (0, 1, 0) ;
	      Vector3d length = new Vector3d () ;
	      length.sub (nextV3d, v3d) ;
	      Vector3d rotationAxis = new Vector3d () ;
	      rotationAxis.cross (y, length) ;
	      Quat4d rotation1 = new Quat4d () ;
	      rotation1.set (new AxisAngle4d (rotationAxis.getX (), rotationAxis.getY (), rotationAxis.getZ (), y.angle (length))) ;   
	      length.sub (v3d, previousV3d) ;
	      rotationAxis = new Vector3d () ;
	      rotationAxis.cross (y, length) ;
	      Quat4d rotation2 = new Quat4d () ;
	      rotation2.set (new AxisAngle4d (rotationAxis.getX (), rotationAxis.getY (), rotationAxis.getZ (), y.angle (length))) ;   
	      Quat4d rotation = new Quat4d (rotation1) ;
	      rotation.interpolate (rotation2, 0.5) ;
	      
	      translation.setRotation (rotation) ;

	      translation.setTranslation (v3d) ;
	      translation.setScale (new Vector3d (1, 0.05, 1)) ;
	      TransformGroup objTrans = new TransformGroup (translation) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
	      objTrans.setCapability (Node.ENABLE_PICK_REPORTING) ;
	      Cube box = new Cube () ;
	      box.getGeometry ().setCapability (Geometry.ALLOW_INTERSECT) ;
	      Appearance app = new Appearance () ;
	      box.setAppearance (app) ;
	      objTrans.addChild (box) ;
	      ColoringAttributes ca = new ColoringAttributes () ;
	      ca.setColor (color) ;
	      app.setCapability (Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE) ;
	      app.setColoringAttributes (ca) ;
	      TransparencyAttributes ta = new TransparencyAttributes () ;
	      ta.setTransparency (0.5f) ;
	      ta.setTransparencyMode (TransparencyAttributes.FASTEST) ;
	      app.setTransparencyAttributes (ta);
	      PolygonAttributes pa = new PolygonAttributes () ;
	      pa.setCullFace (PolygonAttributes.CULL_NONE) ;
	      app.setPolygonAttributes (pa);
	      Appearance smallApp = new Appearance () ;
	      ColoringAttributes smallca = new ColoringAttributes () ;
	      smallca.setColor (collisionColor) ;
	      Box smallBox = new Box (0.2f, 0.1f, 0.2f, smallApp) ;
	      smallApp.setColoringAttributes (smallca) ;
	      objTrans.addChild (smallBox) ;

	      CollisionDetector cd = new CollisionDetector (box, collisionColor, detector) ;
	      BoundingSphere bounds = new BoundingSphere (new Point3d (0.0, 0.0, 0.0), 1.0) ;
	      cd.setSchedulingBounds (bounds) ;

	      // Add the behavior to the scene graph
	      objTrans.addChild (cd) ;
	      return (objTrans) ;
	   }
	     
	   static public TransformGroup createLine (Vector3d from, Vector3d to, Color3f color) {
		      Transform3D translation = new Transform3D () ;
		      Vector3d middle = new Vector3d (from) ;
		      middle.add (to) ;
		      middle.set (middle.getX () / 2, middle.getY () / 2, middle.getZ () / 2) ;
		      Vector3d y = new Vector3d (0, 1, 0) ;
		      Vector3d length = new Vector3d () ;
		      length.sub (to, from) ;
		      Vector3d rotationAxis = new Vector3d () ;
		      rotationAxis.cross (y, length) ;
		      Quat4d rotation = new Quat4d () ;
		      rotation.set (new AxisAngle4d (rotationAxis.getX (), rotationAxis.getY (), rotationAxis.getZ (), y.angle (length))) ;      
		      translation.setRotation (rotation) ;
		      translation.setTranslation (middle) ;
		      //translation.setScale (new Vector3d (1, length.length (), 1)) ;
		      TransformGroup objTrans = new TransformGroup (translation) ;
		      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
		      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
		      //objTrans.setCapability (TransformGroup.ENABLE_PICK_REPORTING) ;
		      Cylinder cylinder = new Cylinder (2.0f, (float)length.length ()) ;
		      //cylinder.getShape (Cylinder.BODY).getGeometry ().setCapability (Geometry.ALLOW_INTERSECT) ;
		      cylinder.getShape (Cylinder.BOTTOM).removeAllGeometries () ;
		      cylinder.getShape (Cylinder.TOP).removeAllGeometries () ;
		      Appearance app = new Appearance () ;
		      ColoringAttributes ca = new ColoringAttributes () ;
		      ca.setColor (color) ;
		      ColoringAttributes ca2 = new ColoringAttributes () ;
		      ca2.setColor(new Color3f (1.0f, 0.0f, 0.0f));
		      app.setCapability (Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE) ;
		      app.setColoringAttributes (ca) ;
		      TransparencyAttributes ta = new TransparencyAttributes () ;
		      ta.setTransparency (0.8f) ;
		      ta.setTransparencyMode (TransparencyAttributes.FASTEST) ;
		      app.setTransparencyAttributes (ta);
		      PolygonAttributes pa = new PolygonAttributes () ;
		      pa.setCullFace (PolygonAttributes.CULL_NONE) ;
		      app.setPolygonAttributes (pa);
		      cylinder.setAppearance (app) ;
		      objTrans.addChild (cylinder) ;
		      Appearance app2 = new Appearance () ;
		      app2.setColoringAttributes (ca2); 
		      app2.setTransparencyAttributes (ta);
		      app2.setPolygonAttributes (pa);
		      Box box = new Box (0.025f, (float)length.length () / 2.0f - 0.6f, 0.025f, app2) ;
		      objTrans.addChild (box) ;
		      return (objTrans) ;
		   }
	   
	  static public TransformGroup createLine2 (Vector3d from, Vector3d to, Color3f color) {
	      Transform3D translation = new Transform3D () ;
	      Vector3d middle = new Vector3d (from) ;
	      middle.add (to) ;
	      middle.set (middle.getX () / 2, middle.getY () / 2, middle.getZ () / 2) ;
	      Vector3d y = new Vector3d (0, 1, 0) ;
	      Vector3d length = new Vector3d () ;
	      length.sub (to, from) ;
	      Vector3d rotationAxis = new Vector3d () ;
	      rotationAxis.cross (y, length) ;
	      Quat4d rotation = new Quat4d () ;
	      rotation.set (new AxisAngle4d (rotationAxis.getX (), rotationAxis.getY (), rotationAxis.getZ (), y.angle (length))) ;      
	      translation.setRotation (rotation) ;
	      translation.setTranslation (middle) ;
	      //translation.setScale (new Vector3d (1, length.length (), 1)) ;
	      TransformGroup objTrans = new TransformGroup (translation) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
	      objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
	      //objTrans.setCapability (TransformGroup.ENABLE_PICK_REPORTING) ;
	      Cylinder cylinder = new Cylinder (2.0f, (float)length.length ()) ;
	      //cylinder.getShape (Cylinder.BODY).getGeometry ().setCapability (Geometry.ALLOW_INTERSECT) ;
	      cylinder.getShape (Cylinder.BOTTOM).removeAllGeometries () ;
	      cylinder.getShape (Cylinder.TOP).removeAllGeometries () ;
	      Appearance app = new Appearance () ;
	      ColoringAttributes ca = new ColoringAttributes () ;
	      ca.setColor (color) ;
	      app.setCapability (Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE) ;
	      app.setColoringAttributes (ca) ;
	      TransparencyAttributes ta = new TransparencyAttributes () ;
	      ta.setTransparency (0.8f) ;
	      ta.setTransparencyMode (TransparencyAttributes.FASTEST) ;
	      app.setTransparencyAttributes (ta);
	      PolygonAttributes pa = new PolygonAttributes () ;
	      pa.setCullFace (PolygonAttributes.CULL_NONE) ;
	      app.setPolygonAttributes (pa);
	      cylinder.setAppearance (app) ;
	      objTrans.addChild (cylinder) ;
	      Box box = new Box (0.025f, (float)length.length () / 2.0f - 0.6f, 0.025f, app) ;
	      objTrans.addChild (box) ;
	      return (objTrans) ;
	   }

	static public BranchGroup loadFile (String filename, Vector3d v3d) {
	      VrmlLoader loader = new VrmlLoader () ;
	      BranchGroup objRoot = new BranchGroup () ;
	      try {
	         Scene scene = loader.load (filename) ;
	         TransformGroup objTrans = new TransformGroup () ;
	         Transform3D t3d = new Transform3D () ;
	         t3d.setTranslation (v3d) ;
	         objTrans.setTransform (t3d) ;
	         //objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_READ) ;
	         //objTrans.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
	         //objTrans.setCapability (TransformGroup.ENABLE_PICK_REPORTING) ;
	         objTrans.addChild (scene.getSceneGroup ()) ;
	         objRoot.addChild (objTrans) ;
	         
//	         Vector3d v3d2 = new Vector3d (v3d) ;
//	         TransformGroup objTrans2 = new TransformGroup () ;
//	         Transform3D t3d2 = new Transform3D () ;
//	         t3d2.setTranslation (v3d2) ;
//	         objTrans2.setTransform (t3d2) ;
//	         objTrans2.setCapability (TransformGroup.ALLOW_TRANSFORM_WRITE) ;
//	         objTrans2.setCapability (TransformGroup.ENABLE_PICK_REPORTING) ;
//	         Shape3D s3d = new Shape3D (recursiveFindGeometry(objTrans)) ;
//	         Appearance app = new Appearance();
//	         app.setCapability (Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE) ;
//	         Material material = new Material () ;
//	         material.setCapability (Material.ALLOW_COMPONENT_WRITE) ;
//	         material.setDiffuseColor (new Color3f (1,0,0)) ;
//	         app.setMaterial (material) ;
//	         s3d.setAppearance (app);
	//
//	         objTrans2.addChild (s3d) ;
	        
	      } catch (FileNotFoundException e) {
	         e.printStackTrace () ;
	      }
	      return objRoot ;
	   }

	public static Node createSmallBox() {
		 Appearance smallApp = new Appearance () ;
	      Box smallBox = new Box (0.05f, 0.05f, 0.05f, smallApp) ;
	      smallBox.getShape (Box.FRONT).setCapability (Geometry.ALLOW_INTERSECT) ;
	      smallBox.getShape (Box.BACK).setCapability (Geometry.ALLOW_INTERSECT) ;
	      smallBox.getShape (Box.TOP).setCapability (Geometry.ALLOW_INTERSECT) ;
	      smallBox.getShape (Box.BOTTOM).setCapability (Geometry.ALLOW_INTERSECT) ;
	      smallBox.getShape (Box.LEFT).setCapability (Geometry.ALLOW_INTERSECT) ;
	      smallBox.getShape (Box.RIGHT).setCapability (Geometry.ALLOW_INTERSECT) ;
	      
		return smallBox;
	}

}
