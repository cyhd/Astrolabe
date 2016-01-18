package etma.navigator.timeRecorder;

public class StartTimeCountDetector extends Detector {

   public StartTimeCountDetector (Supervisor s) {
      super (s) ;
   }

   @Override
   public void begin (double distance) {
      if (! fired) {
         fired = true ;
         System.out.println ("supervisor.startTimeCount ()") ;
         supervisor.startTimeCount (this, distance) ;
      }
   }

   @Override
   public void end () {
   }

}
