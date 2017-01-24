package fr.etma.navigator.timeRecorder;

public class IntermediateTimeCountDetector extends Detector {

   public IntermediateTimeCountDetector (Supervisor s) {
      super (s) ;
   }

   @Override
   public void doit (double distance) {
      System.out.println ("supervisor.intermediateTimeCount ()") ;
      supervisor.intermediateTimeCount (this, distance) ;
      target.select();
      if (nextTarget != null) {
    	  nextTarget.setAware () ;
    	  System.out.println ("setAware") ;
      }
   }

   @Override
   public void end () {
   }

}
