package fr.etma.navigator.control.keyboard;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import fr.etma.navigator.control.Navigator;

/**
 * NavigatorBehavior manages the camera movements (Navigator class) through Keyboard and Mouse
 * inherits from Java3D Behavior class
 * @author Thierry Duval
 * @see Navigator
 */
public class NavigatorBehavior  extends Behavior {

   protected Vector3d deltaT = new Vector3d () ;
   protected Quat4d deltaR = new Quat4d () ;
   protected Quat4d absoluteR = new Quat4d () ;
   protected Quat4d initHeadR = new Quat4d () ;
   protected Quat4d initHeadRInv = new Quat4d () ;
   protected WakeupOr wEvents ;
   protected boolean pressed = false ;
   protected int xInit  ;
   protected int yInit  ;
   
   protected Navigator navigator ;
   
   public NavigatorBehavior (Navigator navigator) {
      this.navigator = navigator ;
      WakeupOnAWTEvent keyPressed = new WakeupOnAWTEvent (KeyEvent.KEY_PRESSED) ;
      WakeupOnAWTEvent keyReleased = new WakeupOnAWTEvent (KeyEvent.KEY_RELEASED) ;
      WakeupOnAWTEvent mouseWheeled = new WakeupOnAWTEvent (MouseEvent.MOUSE_WHEEL) ;
      WakeupOnAWTEvent mouseDragged = new WakeupOnAWTEvent (MouseEvent.MOUSE_DRAGGED) ;
      WakeupOnAWTEvent mousePressed = new WakeupOnAWTEvent (MouseEvent.MOUSE_PRESSED) ;
      WakeupOnAWTEvent mouseReleased = new WakeupOnAWTEvent (MouseEvent.MOUSE_RELEASED) ;
      WakeupCriterion [] conditions = { keyPressed, keyReleased, mouseWheeled, mouseDragged, mousePressed, mouseReleased } ;
      wEvents = new WakeupOr (conditions) ;

      MoveThread mt = new MoveThread () ;
      mt.start () ;
   }

   @Override
public void initialize () {
      wakeupOn (wEvents) ;
   }

   @Override
@SuppressWarnings ("rawtypes")
   public void processStimulus (Enumeration criteria) { // examiner criteria �
      while (criteria.hasMoreElements ()) {
         WakeupOnAWTEvent w = (WakeupOnAWTEvent)criteria.nextElement () ;
         AWTEvent events [] = w.getAWTEvent () ;  
         synchronized (deltaT) {
            synchronized (deltaR) {
               for (int i = 0 ; i < events.length ; i++) {
                  if (events [i].getID () == KeyEvent.KEY_PRESSED) {
                     int k  =  ((KeyEvent)events [i]).getKeyCode() ;
                     if (k == KeyEvent.VK_UP) {
                        deltaT.z = -0.1 ;
                     } else if (k == KeyEvent.VK_DOWN) {
                        deltaT.z = 0.1 ;
                     } else if (k == KeyEvent.VK_PAGE_UP) {
                        deltaT.y = 0.1 ;
                     } else if (k == KeyEvent.VK_PAGE_DOWN) {
                        deltaT.y = -0.1 ;
                     } else if (k == KeyEvent.VK_LEFT) {
                        deltaR.set (new AxisAngle4d (new Vector3d (0, 1, 0), 0.02)) ;
                     } else if (k == KeyEvent.VK_RIGHT) {
                        deltaR.set (new AxisAngle4d (new Vector3d (0, 1, 0), -0.02)) ;
                     }
                  } else if (events [i].getID () == KeyEvent.KEY_RELEASED) {
                     int k  =  ((KeyEvent)events [i]).getKeyCode() ;
                     if (k == KeyEvent.VK_UP) {
                        deltaT.z = 0 ;
                     } else if (k == KeyEvent.VK_DOWN) {
                        deltaT.z = 0 ;
                     } else if (k == KeyEvent.VK_PAGE_UP) {
                        deltaT.y = 0 ;
                     } else if (k == KeyEvent.VK_PAGE_DOWN) {
                        deltaT.y = 0 ;
                     } else if (k == KeyEvent.VK_LEFT) {
                        deltaR.set (new AxisAngle4d (new Vector3d (0, 1, 0), 0.0)) ;
                     } else if (k == KeyEvent.VK_RIGHT) {
                        deltaR.set (new AxisAngle4d (new Vector3d (0, 1, 0), 0.0)) ;
                     }
                  } else if (events [i].getID () == MouseEvent.MOUSE_WHEEL) {
                     MouseWheelEvent mwe  =  ((MouseWheelEvent)events [i]) ;
                     //deltaT.y = deltaT.y + mwe.getPreciseWheelRotation () / 10 ;
//                  } else if ((events [i].getID () == MouseEvent.MOUSE_DRAGGED) ||
//                             (events [i].getID () == MouseEvent.MOUSE_PRESSED)) {
//                     MouseEvent me  =  ((MouseEvent)events [i]) ;
//                     int width = me.getComponent ().getWidth () ;
//                     int height = me.getComponent ().getHeight () ;
//                     int x = me.getX () ;
//                     int y = me.getY () ;
//                     double azimuth = Math.atan2 (width / 2.0 - x, 500.0) ;
//                     double elevation = Math.atan2 (height / 2.0 - y, 500.0) ;
//                     Quat4d azimuthR = new Quat4d () ;
//                     azimuthR.set (new AxisAngle4d (0, 1, 0, azimuth)) ;
//                     Quat4d elevationR = new Quat4d () ;
//                     elevationR.set (new AxisAngle4d (1, 0, 0, elevation)) ;
//                     absoluteR.mul (azimuthR, elevationR) ;
//                     navigator.setHeadOrientationInSupportFrame (absoluteR.x, absoluteR.y, absoluteR.z, absoluteR.w);
                  } else if (events [i].getID () == MouseEvent.MOUSE_PRESSED) {
                     initHeadR = navigator.getHeadRotationInSupportFrame () ;
                     MouseEvent me  =  ((MouseEvent)events [i]) ;
                     xInit = me.getX () ;
                     yInit = me.getY () ;
                  } else if (events [i].getID () == MouseEvent.MOUSE_DRAGGED) {
                     MouseEvent me  =  ((MouseEvent)events [i]) ;
                     int dx = me.getX () - xInit;
                     int dy = me.getY () - yInit ;
                     double azimuth = Math.atan2 (-dx, 500.0) ;
                     double elevation = Math.atan2 (-dy, 500.0) ;
                     Quat4d azimuthR = new Quat4d () ;
                     azimuthR.set (new AxisAngle4d (0, 1, 0, azimuth)) ;
                     Quat4d elevationR = new Quat4d () ;
                     elevationR.set (new AxisAngle4d (1, 0, 0, elevation)) ;
                     Quat4d relativeR = new Quat4d () ;
                     relativeR.mul (azimuthR, elevationR) ;
                     absoluteR.mul (initHeadR, relativeR);
                     navigator.setHeadOrientationInSupportFrame (absoluteR.x, absoluteR.y, absoluteR.z, absoluteR.w);
                  }
               }
            }       
         }
         wakeupOn (wEvents) ;
      }
   }

   class MoveThread extends Thread {

      protected boolean finished ;

      public void finish () {
         finished = true ;
      }

      @Override
	public void run () {
         while (! finished) {
            synchronized (deltaT) {
               synchronized (deltaR) {
                  navigator.supportRotateInHeadFrame (deltaR.x, deltaR.y, deltaR.z, deltaR.w) ;
                  navigator.supportTranslateInHeadFrame (deltaT.x, deltaT.y, deltaT.z) ; 
               }
            }
            try {
               sleep (20) ;
            } catch (InterruptedException e) {
            }
         }
      }

   }

}

