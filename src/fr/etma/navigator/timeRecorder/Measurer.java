package fr.etma.navigator.timeRecorder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import fr.etma.navigator.control.Navigator;
import fr.etma.navigator.shape.TubeShape;

public class Measurer extends Thread {

	protected Navigator navigator;
	TubeShape[] tubeShapes;
	protected boolean finished = false;
	protected double length = 0.0;
	protected double rotation = 0.0;
	protected double duration = 0.0;
	protected double precision;

	protected double time = 0;
	private int current = 0;

	public void setFinished(boolean finished) {
		this.finished = finished;
		Logger.getInstance().close();
	}

	public Measurer(Navigator navigator) {
		this.navigator = navigator;
		this.tubeShapes = null;
	}

	synchronized public void addDifference(double distance) {
		precision = precision + distance;
	}
	
	/**
	 * record a random step with a name
	 * @param pname step name given
	 */
	synchronized public void record(String pname) {
		Logger.getInstance().recordXML(pname, duration,length,rotation, precision);
	}
	
	/**
	 * record the crossing of a target
	 * @param id  id of the target
	 * @param yes true it was the right sequential target (in order), false if the target crossed was not the next expected one (for example, a user crosses target 0 and the target 3 instead of target 1
	 */
	synchronized public void record(int id, boolean yes) {
		Logger.getInstance().recordXML(id,yes, duration,length,rotation, precision);
	}
	
	@Override
	public void run() {
		finished = false;
		length = 0.0;
		precision = 0.0;
		
		time = System.currentTimeMillis();
		Quat4d previousOrientation = new Quat4d();
		Quat4d currentOrientation = new Quat4d();
		Quat4d deltaOrientation = new Quat4d();
		Vector3d previousPosition = new Vector3d();
		Vector3d currentPosition = new Vector3d();
		Vector3d deltaPosition = new Vector3d();
		previousOrientation = navigator.getHeadOrientationInGlobalFrame();
		previousPosition = navigator.getSupportPositionInGlobalFrame();
		while (!finished) {
			// calcul camera TARGET
			currentOrientation = navigator.getHeadOrientationInGlobalFrame();
			currentPosition = navigator.getSupportPositionInGlobalFrame();
			deltaPosition.negate(previousPosition);
			deltaPosition.add(currentPosition);
			deltaOrientation.inverse(previousOrientation);
			deltaOrientation.mul(currentOrientation);
			deltaOrientation.normalize();
			rotation = rotation + Math.acos(deltaOrientation.getW()); // devrait
																		// pourtant
																		// être
																		// le
																		// double...
			// System.out.println ("duration = " + duration / 1000) ;
			// System.out.println ("length = " + length) ;
			// System.out.println ("rotation = " + rotation) ;
			length = length + deltaPosition.length();
			previousPosition.set(currentPosition);
			previousOrientation.set(currentOrientation);
			
			/*
			// distance CAMERA to CYLINDER
			double distance = tubeShapes[Math.max(current-1,0)].distanceToPoint(navigator.getHeadPositionInGlobalFrame());
			if (distance > 0.0) {
				System.out.println(" sortie de: " + distance + " du tube " + current);
			}
			*/
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		duration = System.currentTimeMillis() - time;
		System.out.println("duration = " + duration / 1000);
		System.out.println("length = " + length);
		System.out.println("rotation = " + rotation);
		System.out.println("precision = " + precision);
		
		record("final");
	}

	public void setCurrent(int c) {
		this.current  = c;		
	}

	public void setTubeShapes(TubeShape[] tubeShapes) {
		this.tubeShapes = tubeShapes;
	}

}
