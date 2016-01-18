package fr.etma.navigator.timeRecorder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import fr.etma.navigator.control.Navigator;

public class Measurer extends Thread {
   
   protected Navigator navigator ;
   protected boolean finished = false ;
   protected double length = 0.0 ;
   protected double rotation = 0.0 ;
   protected double duration = 0.0 ;
   protected FileWriter outFile ;
   protected double precision ;
   
   protected double time = 0;
   
   public void setFinished (boolean finished) {
      this.finished = finished ;
   }

   public Measurer (Navigator n) {
      navigator = n ;
   }

   synchronized public void addDifference (double distance) {
      precision = precision + distance ;
   }
   
   synchronized public void record(String pname) {
	   try {
		   outFile.write ( "step " + pname + '\n');
           outFile.write ("Duration : " + ((System.currentTimeMillis () - time) / 1000) + '\n');
           outFile.write ("Length : " + length + '\n');
           outFile.write ("Rotation : " + rotation + '\n');
           outFile.write ("Precision : " + precision + '\n');
           outFile.flush () ;     
        } catch (IOException e) {
           e.printStackTrace();
        }
   }
   
   @Override
   public void run () {
      finished = false ;
      length = 0.0 ;
      precision = 0.0 ;
      String date = new Date (System.currentTimeMillis ()).toString () ;
      date = date.replace (' ', '_'). replace (':', '_') ;
      String fileName = "Exp_Intersemestre_" + date + ".txt";
      try {
         outFile = new FileWriter (fileName, true) ;
         outFile.write ("started" + '\n');
         outFile.flush () ;     
         
      } catch (IOException e1) {
         e1.printStackTrace();
      }

      time = System.currentTimeMillis () ;
      Quat4d previousOrientation = new Quat4d () ;
      Quat4d currentOrientation = new Quat4d () ;
      Quat4d deltaOrientation = new Quat4d () ;
      Vector3d previousPosition = new Vector3d () ;
      Vector3d currentPosition = new Vector3d () ;
      Vector3d deltaPosition = new Vector3d () ;
      previousOrientation = navigator.getHeadOrientationInGlobalFrame () ;
      previousPosition = navigator.getSupportPositionInGlobalFrame () ;
      while (! finished) {
         currentOrientation = navigator.getHeadOrientationInGlobalFrame () ;
         currentPosition = navigator.getSupportPositionInGlobalFrame () ;
         deltaPosition.negate (previousPosition) ;
         deltaPosition.add (currentPosition) ;
         deltaOrientation.inverse (previousOrientation) ;
         deltaOrientation.mul (currentOrientation) ;
         deltaOrientation.normalize () ;
         rotation = rotation + Math.acos (deltaOrientation.getW ()) ; // devrait pourtant être le double...
//         System.out.println ("duration = " + duration / 1000) ;
//         System.out.println ("length = " + length) ;
//         System.out.println ("rotation = " + rotation) ;
         length = length + deltaPosition.length () ;
         previousPosition.set (currentPosition) ;
         previousOrientation.set (currentOrientation) ;
         try {
            Thread.sleep (20) ;
         } catch (InterruptedException e) {
            e.printStackTrace () ;
         }
      }
      duration = System.currentTimeMillis () - time;
      System.out.println ("duration = " + duration / 1000) ;
      System.out.println ("length = " + length) ;
      System.out.println ("rotation = " + rotation) ;
      System.out.println ("precision = " + precision) ;
      try {
         outFile.write ("Duration : " + (duration / 1000) + '\n');
         outFile.write ("Length : " + length + '\n');
         outFile.write ("Rotation : " + rotation + '\n');
         outFile.write ("Precision : " + precision + '\n');
         outFile.flush () ;         
         outFile.close () ;
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
   
}
