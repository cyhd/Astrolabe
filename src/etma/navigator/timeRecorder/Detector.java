package etma.navigator.timeRecorder;

public abstract class Detector {

   protected Supervisor supervisor ;
   protected boolean fired ;
   

   public Detector (Supervisor s) {
      supervisor = s ;
      fired = false ;
   }
   
   public void rearm () {
      fired = false ;
   }
   public abstract void begin (double distance) ;

   public abstract void end () ;
   
}
