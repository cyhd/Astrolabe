package fr.etma.navigator.timeRecorder;

import fr.etma.navigator.control.Navigator;
import fr.etma.navigator.shape.TubeShape;

public class Supervisor {

	protected Measurer measurer;
	protected int numberOfImtermediates;
	protected int current;
	protected boolean activated;

	void setCurrent(int c) { 
		current = c;
		measurer.setCurrent(c);
		}
	
	int getCurrent() { return current; }
	
	public Supervisor(Measurer measurer, int noi) {
		this.measurer = measurer;
		numberOfImtermediates = noi;
		setCurrent(0);
	}

	public void startTimeCount(Detector detector, double distance) {
			System.out.println("startTimeCount (0) : activation");
			measurer.start();
			measurer.addDifference(distance);
			measurer.record(0, true);
			setCurrent(getCurrent()+1);
	}

	public void stopTimeCount(Detector detector, double distance) {
			System.out.println("stopTimeCount ("+getCurrent()+") : acknowledge");
			measurer.addDifference(distance);
			measurer.record(getCurrent(), true);
			measurer.setFinished(true);
		
	}

	public void intermediateTimeCount(Detector detector, double distance) {
			System.out.println("intermediateTimeCount (" + getCurrent() + ") : OK");
			measurer.addDifference(distance);
			measurer.record(getCurrent(), true);
			setCurrent(getCurrent() + 1);

	}

	public boolean isCurrent(int id) {
		if (getCurrent() == id) {
			return true;
		} 
		measurer.record(id,false);
		return false;
	}


}
