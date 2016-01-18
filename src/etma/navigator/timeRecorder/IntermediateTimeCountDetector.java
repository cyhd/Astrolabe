package etma.navigator.timeRecorder;

public class IntermediateTimeCountDetector extends Detector {

   public IntermediateTimeCountDetector (Supervisor s) {
      super (s) ;
   }

   @Override
   public void begin (double distance) {
      if (! fired) {
         fired = true ;
         System.out.println ("supervisor.intermediateTimeCount ()") ;
         supervisor.intermediateTimeCount (this, distance) ;
      }
   }

   @Override
   public void end () {
   }

}
