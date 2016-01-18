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
   }

   @Override
   public void end () {
   }

}
