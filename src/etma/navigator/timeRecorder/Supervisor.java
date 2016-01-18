package etma.navigator.timeRecorder;

import etma.navigator.control.Navigator;

public class Supervisor {

   protected Measurer measurer ;
   protected int numberOfImtermediates ;
   protected int currentCount ;
   protected boolean activated ;
   
   public Supervisor (Navigator navigator, int noi) {
      measurer = new Measurer (navigator) ;
      numberOfImtermediates = noi ;
      currentCount = 0 ;
      activated= false ;
   }
   
   public void startTimeCount (Detector detector, double distance) {
      if (! activated) {
         System.out.println ("startTimeCount () : activation") ;
         measurer.start () ;
         activated = true ;
         currentCount = 0 ;
         measurer.addDifference (distance) ;
         
      } else {
         System.out.println ("startTimeCount () : already activated") ;
      }
   }

   public void stopTimeCount (Detector detector, double distance) {
      if (activated && (currentCount == numberOfImtermediates)) {
         System.out.println ("stopTimeCount () : acknowledge") ;
         measurer.addDifference (distance) ;
         measurer.setFinished (true) ;
         measurer.record(Integer.toString(currentCount));
      } else {
         System.out.println ("stopTimeCount () : not yet authorized") ;
         detector.rearm () ;
      }
   }

   public void intermediateTimeCount (Detector detector, double distance) {
      if (activated) {
         System.out.println ("intermediateTimeCount () : OK") ;
         measurer.addDifference (distance) ;
         currentCount ++ ;
         measurer.record(Integer.toString(currentCount));
         
      } else {
         System.out.println ("intermediateTimeCount () : not yet authorized") ;
         detector.rearm () ;
      }
   }

}
