package etma.navigator.timeRecorder;

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
      }
   }

   @Override
   public void end () {
   }

}
