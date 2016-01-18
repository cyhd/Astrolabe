package fr.etma.navigator.timeRecorder;

import javax.media.j3d.TransformGroup;

import fr.etma.navigator.shape.TargetShape;

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
	      if  ((supervisor.isCurrentStep(getId()))&&(! fired)) {
	          fired = true ;
	          doit(distance);
	       }
	      else System.out.println( "Go to step " + supervisor.getCurrentStep() + " before step " + getId() + "!"  );
	    }


	public abstract void end();

	public void add(TargetShape s) {
		target = s;
	}
	
	public int getId() {
		return target.getId();
	}

}
