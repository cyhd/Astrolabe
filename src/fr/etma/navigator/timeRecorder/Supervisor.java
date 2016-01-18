package fr.etma.navigator.timeRecorder;

import fr.etma.navigator.control.Navigator;

public class Supervisor {

	protected Measurer measurer;
	protected int numberOfImtermediates;
	protected int currentCount;
	protected boolean activated;

	public Supervisor(Navigator navigator, int noi) {
		measurer = new Measurer(navigator);
		numberOfImtermediates = noi;
		currentCount = 0;

	}

	public void startTimeCount(Detector detector, double distance) {
			System.out.println("startTimeCount (0) : activation");
			measurer.start();
			measurer.addDifference(distance);
			measurer.record("GOOD STEP 0");
			currentCount++;
	}

	public void stopTimeCount(Detector detector, double distance) {
			System.out.println("stopTimeCount ("+currentCount+") : acknowledge");
			measurer.addDifference(distance);
			measurer.record("GOOD STEP" + currentCount);
			measurer.setFinished(true);
			
	}

	public void intermediateTimeCount(Detector detector, double distance) {
			System.out.println("intermediateTimeCount (" + currentCount + ") : OK");
			measurer.addDifference(distance);
			measurer.record("GOOD STEP" + currentCount);
			currentCount++;
			

	}

	public int getCurrentStep() {
		return currentCount;
	}

	public boolean isCurrentStep(int id) {
		if (currentCount == id) {
			return true;
		} 
		measurer.record("WRONG STEP " + id);
		return false;
	}


}
