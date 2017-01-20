package fr.etma.navigator.control.wiimote;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import fr.etma.navigator.control.Navigator;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukButtonsEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;

public class ControlerWiimoteListener implements WiimoteListener {

	Navigator navigator;
	PilotageWiimoteWiiuseJ wiiInterface;
	
	protected boolean wiimoteDirectionActivated = false;
	protected boolean accelerationActivated = false;
	protected boolean zModeAndNotYMode = true;
	protected boolean translationModeAndNotRotationMode = true;
	protected boolean zModeAndNotYModeNunchuck = true;
	protected boolean rotationModeNunchuck = false;
	
	public ControlerWiimoteListener(final Navigator navigator, PilotageWiimoteWiiuseJ pilot) {
	
		this.navigator = navigator;
		wiiInterface = pilot;
	}
	
	@Override
	public void onButtonsEvent(WiimoteButtonsEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnectionEvent(DisconnectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExpansionEvent(ExpansionEvent arg0) {
		if (arg0 instanceof NunchukEvent) {
			NunchukEvent nunchuk = (NunchukEvent) arg0;
			NunchukButtonsEvent buttons = nunchuk.getButtonsEvent();

			if (buttons.isButtonCJustPressed()) {
				System.out.println("C pressed");
				rotationModeNunchuck = true;
			}

			if (buttons.isButtonZJustPressed()) {
				System.out.println("Z pressed");
				zModeAndNotYModeNunchuck = false;
			}
			if (buttons.isButtonCJustReleased()) {
				System.out.println("C released");
				rotationModeNunchuck = false;
			}
			if (buttons.isButtonZJustReleased()) {
				System.out.println("Z released");
				zModeAndNotYModeNunchuck = true;
			}

			double x = nunchuk.getNunchukJoystickEvent().getAngle() - 90;
			double y = nunchuk.getNunchukJoystickEvent().getMagnitude() - 90;

			System.out.println("stick x = " + x + " ; y = " + y);

			if (Math.abs(x) > wiiInterface.getSeuilSensibiliteRotation()) {
				// System.out.println ("stick x = " + x + " ; y = " + y + " ;
				// angle = " + x) ;
				Quat4d rotation = new Quat4d();
				rotation.set(new AxisAngle4d(0, 1, 0, -x * wiiInterface.getGainRotation()));
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y, rotation.z, rotation.w);
			}
			if (Math.abs(y) > wiiInterface.getSeuilSensibiliteTranslation()) {
				// System.out.println ("stick x = " + x + " ; y = " + y + " ;
				// angle = " + y) ;
				if (zModeAndNotYModeNunchuck) {
					navigator.supportTranslateInHeadFrame(0, 0, -y * wiiInterface.getGainTranslation());
				} else {
					navigator.supportTranslateInHeadFrame(0, y * wiiInterface.getGainTranslation(), 0);
				}
			}
			if (rotationModeNunchuck) {
				Quat4d rotation = new Quat4d();
				float aex = nunchuk.getNunchukMotionSensingEvent().getRawAcceleration().getX();
				float aey = nunchuk.getNunchukMotionSensingEvent().getRawAcceleration().getY();
				if (zModeAndNotYModeNunchuck) {
					rotation.set(new AxisAngle4d(1, 0, 0, -aey * wiiInterface.getGainRotation()));
				} else {
					rotation.set(new AxisAngle4d(0, 1, 0, -aey * wiiInterface.getGainRotation()));
				}
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y, rotation.z, rotation.w);
				rotation.set(new AxisAngle4d(0, 0, 1, -aex * wiiInterface.getGainRotation()));
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y, rotation.z, rotation.w);
			}

		}
	}

	@Override
	public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onIrEvent(IREvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMotionSensingEvent(MotionSensingEvent ee) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNunchukInsertedEvent(NunchukInsertedEvent nun) {

	}

	@Override
	public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusEvent(StatusEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * protected LinkedList<Double> azimuths = new LinkedList<Double> () ;
	 * protected LinkedList<Double> pitchs = new LinkedList<Double> () ;
	 * protected LinkedList<Double> rolls = new LinkedList<Double> () ;
	 * protected final int historyLenght = 20 ;
	 * 
	 * protected final int xWiimoteResolution = 1024;
	protected final int yWiimoteResolution = 768;
	protected final int xMax = 1920;
	protected final int yMax = 1080;
	protected final int xMin = 0;
	protected final int yMin = 0;
	protected double wiimotePitch;
	protected double wiimoteRoll;
	protected double azimuth;
 
	 * 
	 * 
	 * public void accelerationInputReceived (WRAccelerationEvent ae) {
	 * wiimotePitch = ae.getPitch () ; wiimoteRoll = ae.getRoll () ; if
	 * (wiimoteRoll > Math.PI) { wiimoteRoll = wiimoteRoll - 2 * Math.PI ; } //
	 * System.out.println ("wiimotePitch : " + wiimotePitch) ; //
	 * System.out.println ("wiimoteRoll : " + wiimoteRoll) ; //
	 * System.out.println ("wiimote x acceleration : " + ae.getXAcceleration ())
	 * ; // System.out.println ("wiimote y acceleration : " +
	 * ae.getYAcceleration ()) ; // System.out.println (
	 * "wiimote z acceleration : " + ae.getZAcceleration ()) ; if
	 * (accelerationActivated) { if (translationModeAndNotRotationMode) { if
	 * (zModeAndNotYMode) { Quat4d rotation = new Quat4d () ; rotation.set (new
	 * AxisAngle4d (0, 1, 0, -ae.getXAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; navigator.supportTranslateInHeadFrame (0.0, 0.0,
	 * -ae.getYAcceleration () * gainTranslation) ; } else { Quat4d rotation =
	 * new Quat4d () ; rotation.set (new AxisAngle4d (0, 1, 0,
	 * -ae.getXAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; navigator.supportTranslateInHeadFrame (0.0,
	 * -ae.getYAcceleration () * gainTranslation, 0.0) ; } } else { if
	 * (zModeAndNotYMode) { Quat4d rotation = new Quat4d () ; rotation.set (new
	 * AxisAngle4d (1, 0, 0, -ae.getYAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; rotation.set (new AxisAngle4d (0, 1, 0,
	 * -ae.getXAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; } else { Quat4d rotation = new Quat4d () ; rotation.set
	 * (new AxisAngle4d (1, 0, 0, -ae.getYAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; rotation.set (new AxisAngle4d (0, 0, 1,
	 * -ae.getXAcceleration () * gainRotation)) ;
	 * navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z,
	 * rotation.w) ; } } } }
	 * 
	 * RotationThread rotationThread ; TranslationThread translationThread ;
	 * 
	 * 
	 * public void buttonInputReceived (WRButtonEvent be) { if (be.wasPressed
	 * (WRButtonEvent.TWO)) System.out.println ("2 pressed") ; if (be.wasPressed
	 * (WRButtonEvent.ONE)) { wiimoteDirectionActivated = true ;
	 * System.out.println ("1 pressed") ; } if (be.wasPressed (WRButtonEvent.B))
	 * { System.out.println ("B pressed") ; zModeAndNotYMode = false ; } if
	 * (be.wasPressed (WRButtonEvent.A)) { System.out.println ("A pressed") ;
	 * accelerationActivated = true ; translationModeAndNotRotationMode = true ;
	 * } if (be.wasPressed (WRButtonEvent.MINUS)) System.out.println (
	 * "Minus pressed") ; if (be.wasPressed (WRButtonEvent.HOME)) {
	 * System.out.println ("Home pressed") ; accelerationActivated = true ;
	 * translationModeAndNotRotationMode = false ; } if (be.wasPressed
	 * (WRButtonEvent.LEFT)) { System.out.println ("Left pressed") ; Quat4d
	 * rotation = new Quat4d () ; rotation.set (new AxisAngle4d (0, 1, 0,
	 * gainRotation)) ; rotationThread = new RotationThread (navigator,
	 * rotation) ; rotationThread.start () ; } if (be.wasPressed
	 * (WRButtonEvent.RIGHT)) { System.out.println ("Right pressed") ; Quat4d
	 * rotation = new Quat4d () ; rotation.set (new AxisAngle4d (0, 1, 0,
	 * -gainRotation)) ; rotationThread = new RotationThread (navigator,
	 * rotation) ; rotationThread.start () ; } if (be.wasPressed
	 * (WRButtonEvent.DOWN)) { System.out.println ("Down pressed") ;
	 * translationThread = new TranslationThread (navigator, new Vector3d (0, 0,
	 * gainTranslation)) ; translationThread.start () ; } if (be.wasPressed
	 * (WRButtonEvent.UP)) { System.out.println ("Up pressed") ;
	 * translationThread = new TranslationThread (navigator, new Vector3d (0, 0,
	 * -gainTranslation)) ; translationThread.start () ; } if (be.wasPressed
	 * (WRButtonEvent.PLUS)) System.out.println ("Plus pressed") ; if
	 * (be.wasReleased (WRButtonEvent.TWO)) System.out.println ("2 released") ;
	 * if (be.wasReleased (WRButtonEvent.ONE)) { wiimoteDirectionActivated =
	 * false ; System.out.println ("1 released") ; } if (be.wasReleased
	 * (WRButtonEvent.B)) { System.out.println ("B released") ; zModeAndNotYMode
	 * = true ; } if (be.wasReleased (WRButtonEvent.A)) { System.out.println (
	 * "A released") ; accelerationActivated = false ; } if (be.wasReleased
	 * (WRButtonEvent.MINUS)) System.out.println ("Minus released") ; if
	 * (be.wasReleased (WRButtonEvent.HOME)) { System.out.println (
	 * "Home released") ; translationModeAndNotRotationMode = true ;
	 * accelerationActivated = false ; } if (be.wasReleased
	 * (WRButtonEvent.LEFT)) { System.out.println ("Left released") ;
	 * rotationThread.finish () ; } if (be.wasReleased (WRButtonEvent.RIGHT)) {
	 * System.out.println ("Right released") ; rotationThread.finish () ; } if
	 * (be.wasReleased (WRButtonEvent.DOWN)) { System.out.println (
	 * "Down released") ; translationThread.finish () ; } if (be.wasReleased
	 * (WRButtonEvent.UP)) { System.out.println ("Up released") ;
	 * translationThread.finish () ; }; if (be.wasReleased (WRButtonEvent.PLUS))
	 * System.out.println ("Plus released") ; }
	 * 
	 * 
	 * 
	 */
	
	class TranslationThread extends Thread {

		protected Navigator navigator;
		protected boolean finished = false;
		protected Vector3d translation;

		public TranslationThread(Navigator navigator, Vector3d translation) {
			this.translation = translation;
			this.navigator = navigator;
		}

		public void finish() {
			finished = true;
		}

		@Override
		public void run() {
			while (!finished) {
				navigator.supportTranslateInHeadFrame(translation.x, translation.y, translation.z);
				try {
					sleep(20);
				} catch (InterruptedException e) {
				}
			}
		}

	}

	class RotationThread extends Thread {

		protected Navigator navigator;
		protected boolean finished = false;
		protected Quat4d rotation;

		public RotationThread(Navigator navigator, Quat4d rotation) {
			this.rotation = rotation;
			this.navigator = navigator;
		}

		public void finish() {
			finished = true;
		}

		@Override
		public void run() {
			while (!finished) {
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y, rotation.z, rotation.w);
				try {
					sleep(20);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	
}
