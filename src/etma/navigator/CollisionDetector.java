package etma.navigator;
import javax.media.j3d.* ;
import javax.vecmath.* ;

import etma.navigator.timeRecorder.Detector;

import java.awt.AWTEvent ;
import java.awt.event.KeyEvent ;
import java.util.Enumeration ;

public class CollisionDetector extends Behavior {

	private ColoringAttributes highlight ;
	private boolean inCollision = false ;
	private Shape3D shape ;
	private ColoringAttributes shapeColoring ;
	private Appearance shapeAppearance ;
	private Vector3d colliderPosition = new Vector3d () ;
   private Vector3d targetPosition = new Vector3d () ;
   private Vector3d deltaPosition = new Vector3d () ;
	private TransformGroup targetTG ;
   private Transform3D colliderT3D = new Transform3D () ;
   private Transform3D targetT3D = new Transform3D () ;
   private Transform3D colliderT3DInverse = new Transform3D () ;
   
	private WakeupOnCollisionEntry wEnter ;
	private WakeupOnCollisionExit wExit ;

	private Detector detector = null ;
 
   public CollisionDetector (Shape3D s, Color3f c) {
      this (s, c, null) ;
   }
   
   public CollisionDetector (Shape3D s, Color3f c, Detector d) {
      detector = d ;
		shape = s ;
		highlight = new ColoringAttributes (c, ColoringAttributes.SHADE_GOURAUD) ;
		shapeAppearance = shape.getAppearance () ;
		shapeColoring = shapeAppearance.getColoringAttributes () ;
		inCollision = false ;
		targetTG = (TransformGroup)shape.getParent () ;
	}

	public void initialize () {
		wEnter = new WakeupOnCollisionEntry (shape, WakeupOnCollisionEntry.USE_GEOMETRY) ;
		wExit = new WakeupOnCollisionExit (shape, WakeupOnCollisionEntry.USE_GEOMETRY) ;
		wakeupOn (wEnter) ;
	}

	@SuppressWarnings ("rawtypes")
   public void processStimulus (Enumeration criteria) {
		inCollision = !inCollision ;
		if (inCollision) {
	      while (criteria.hasMoreElements ()) {
	         WakeupOnCollisionEntry w = (WakeupOnCollisionEntry)criteria.nextElement () ;
	         colliderT3D = w.getTriggeringPath ().getTransform () ;
	         colliderT3D.get (colliderPosition) ;
//	         System.out.println ("colliderPosition = " + colliderPosition) ;
	         targetTG.getTransform (targetT3D) ;
	         targetT3D.get (targetPosition) ;
//	         System.out.println ("targetPosition = " + targetPosition) ;
	         // calcul de la position du collider dans le repère de la cible, pour supprimer la distance en Z local
	         colliderT3DInverse.invert (colliderT3D) ;
	         colliderT3D.mul (colliderT3DInverse, targetT3D) ;
	         colliderT3D.get (deltaPosition) ;
	         System.out.println ("deltaPosition = " + deltaPosition + " ; length = " + deltaPosition.length ()) ;
	         deltaPosition.setZ (0.0) ;
	         System.out.println ("deltaPosition = " + deltaPosition + " ; length = " + deltaPosition.length ()) ;
	      }
			shapeAppearance.setColoringAttributes (highlight) ;
			if (detector != null) {
			   detector.begin (deltaPosition.length ()) ;
			}
			wakeupOn (wExit) ;
		} else {
			shapeAppearance.setColoringAttributes (shapeColoring) ;
         if (detector != null) {
            detector.end () ;
         }
			wakeupOn (wEnter) ;
		}
	}

}
