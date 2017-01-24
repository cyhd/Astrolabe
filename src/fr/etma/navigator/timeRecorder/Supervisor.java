package fr.etma.navigator.timeRecorder;

import java.io.File;

import fr.etma.navigator.control.Navigator;
import fr.etma.navigator.shape.TubeShape;
import fr.etma.navigator.sound.PlayerWav;

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
			new PlayerWav("sounds"+File.separator+"yes.wav", false).start();
			measurer.start();
			measurer.addDifference(distance);
			measurer.record(0, true);
			setCurrent(getCurrent()+1);
	}

	public void stopTimeCount(Detector detector, double distance) {
			System.out.println("stopTimeCount ("+getCurrent()+") : acknowledge");
			new PlayerWav("sounds"+File.separator+"finish.wav", false).start();
			measurer.addDifference(distance);
			measurer.record(getCurrent(), true);
			measurer.setFinished(true);
	}

	public void intermediateTimeCount(Detector detector, double distance) {
			System.out.println("intermediateTimeCount (" + getCurrent() + ") : OK");
			new PlayerWav("sounds"+File.separator+"yes.wav", false).start();
			measurer.addDifference(distance);
			measurer.record(getCurrent(), true);
			setCurrent(getCurrent()+1);
	}

	public boolean isCurrent(int id) {
		if (getCurrent() == id) {
			return true;
		} 
		measurer.record(id,false);
		return false;
	}


}
