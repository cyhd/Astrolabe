package fr.etma.navigator.timeRecorder;

public class StartTimeCountDetector extends Detector {

   public StartTimeCountDetector (Supervisor s) {
      super (s) ;
   }

   @Override
   public void doit(double distance) {
      System.out.println ("supervisor.startTimeCount ()") ;
      supervisor.startTimeCount (this, distance) ;
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
