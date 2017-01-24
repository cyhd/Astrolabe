package fr.etma.navigator.timeRecorder;

import java.io.File;

import javax.media.j3d.TransformGroup;

import fr.etma.navigator.shape.TargetShape;
import fr.etma.navigator.sound.PlayerWav;

public abstract class Detector {

	protected Supervisor supervisor;
	protected boolean fired;
	protected TargetShape target;

	public Detector(Supervisor s) {
		supervisor = s;
		fired = false;
	}
	
	public void rearm() {
		fired = false;
	}

	public abstract void doit(double distance);
	
	public void begin(double distance) {
		  // check step passed through is the right one
	      if  ((supervisor.isCurrent(getId()))&&(! fired)) {
	    	  fired = true ;
	          doit(distance);
	       }
	      else {
	    	  if  (fired)
	    		  	  {System.out.println( "Go to step " + supervisor.getCurrent() + " now, already passed through " + getId()  );
	    	  			new PlayerWav("sounds"+File.separator+"no.wav", false).start();}
	    	  else if (!supervisor.isCurrent(getId()))
	    			  {System.out.println( "Go to step " + supervisor.getCurrent() + " before step " + getId() + "!"  );
	    	  			new PlayerWav("sounds"+File.separator+"no.wav", false).start();}
	      }
	    }


	public abstract void end();

	public void add(TargetShape s) {
		target = s;
	}
	
	public int getId() {
		return target.getId();
	}

}
