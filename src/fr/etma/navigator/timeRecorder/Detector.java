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

	public abstract void begin(double distance);

	public abstract void end();

	public void add(TargetShape s) {
		target = s;

	}

}
