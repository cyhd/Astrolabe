package fr.etma.navigator.timeRecorder;

public class StopTimeCountDetector extends Detector {

   public StopTimeCountDetector (Supervisor s) {
      super (s) ;
   }

   @Override
   public void begin (double distance) {
      if (! fired) {
         fired = true ;
         System.out.println ("supervisor.stopTimeCount ()") ;
         supervisor.stopTimeCount (this, distance) ;
         target.select();
      }
   }

   @Override
   public void end () {
   }

}
