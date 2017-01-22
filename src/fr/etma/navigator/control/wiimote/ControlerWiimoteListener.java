package fr.etma.navigator.control.wiimote;

import java.util.LinkedList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import fr.etma.navigator.control.Navigator;
import wiiusej.values.GForce;
import wiiusej.values.Orientation;
import wiiusej.values.RawAcceleration;
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

	protected boolean accelerationActivated = false;
	protected boolean zModeAndNotYMode = true;
	protected boolean translationModeAndNotRotationMode = true;
	protected boolean zModeAndNotYModeNunchuck = true;
	protected boolean rotationModeNunchuck = false;

	public ControlerWiimoteListener(final Navigator navigator,
			PilotageWiimoteWiiuseJ pilot) {

		this.navigator = navigator;
		wiiInterface = pilot;
	}

	/*
	 * Nunchuk event management
	 * 
	 * @see
	 * wiiusej.wiiusejevents.utils.WiimoteListener#onExpansionEvent(wiiusej.
	 * wiiusejevents.physicalevents.ExpansionEvent)
	 */
	@Override
	public void onExpansionEvent(ExpansionEvent ee) {
		if (ee instanceof NunchukEvent) {
			NunchukEvent nunchuk = (NunchukEvent) ee;

			NunchukButtonsEvent buttons = nunchuk.getButtonsEvent();

			if (buttons.isButtonCJustPressed()) {
			}

			if (buttons.isButtonZJustPressed()) {
			}

			if (buttons.isButtonCJustReleased()) {
				if (rotationModeNunchuck)
					System.out
							.println("C released - rotation mode desactivated.");
				else
					System.out.println("C released - rotation mode activated.");
				rotationModeNunchuck = !rotationModeNunchuck;
			}

			if (buttons.isButtonZJustReleased()) {
				if (zModeAndNotYModeNunchuck)
					System.out.println("Z released - z distance activated.");
				else
					System.out.println("Z released - z distance desactivated.");
				zModeAndNotYModeNunchuck = !zModeAndNotYModeNunchuck;
			}

			double x = nunchuk.getNunchukJoystickEvent().getAngle();
			double y = nunchuk.getNunchukJoystickEvent().getMagnitude();

			/* convert 0 to 360 value to -180 to +180 */
			if (x > 180)
				x = x - 360;

			/* test if reversing */
			if (Math.abs(x) > 90)
				y = -y;

			/*
			 * manage the Y axis rotation and moving forward from the nunchuk
			 * joystick only move if joystick is not centered
			 */

			if (Math.abs(y) > wiiInterface.getSeuilSensibiliteTranslation()) {

				Quat4d rotation = new Quat4d();
				rotation.set(new AxisAngle4d(0, 1, 0, -x
						* wiiInterface.getGainRotation()));
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
						rotation.z, rotation.w);

				if (zModeAndNotYModeNunchuck) {
					navigator.supportTranslateInHeadFrame(0, 0, -y
							* wiiInterface.getGainTranslation());
				} else {
					navigator.supportTranslateInHeadFrame(0,
							y * wiiInterface.getGainTranslation(), 0);
				}

			}

			if (rotationModeNunchuck) {
				Quat4d rotation = new Quat4d();
				float aex = nunchuk.getNunchukMotionSensingEvent()
						.getOrientation().getRoll();
				float aey = nunchuk.getNunchukMotionSensingEvent()
						.getOrientation().getPitch();

				if (zModeAndNotYModeNunchuck) {
					rotation.set(new AxisAngle4d(1, 0, 0, -aey
							* wiiInterface.getGainRotation()));
				} else {
					rotation.set(new AxisAngle4d(0, 1, 0, -aey
							* wiiInterface.getGainRotation()));
				}

				navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
						rotation.z, rotation.w);

				rotation.set(new AxisAngle4d(0, 0, 1, aex
						* wiiInterface.getGainRotation()));
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
						rotation.z, rotation.w);

			}

		}
	}

	@Override
	public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent gi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent ge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onIrEvent(IREvent ie) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMotionSensingEvent(MotionSensingEvent ee) {
		// TODO Auto-generated method stub
		accelerationInputReceived(ee);
	}

	@Override
	public void onNunchukInsertedEvent(NunchukInsertedEvent nun) {

	}

	@Override
	public void onNunchukRemovedEvent(NunchukRemovedEvent nre) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusEvent(StatusEvent se) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onButtonsEvent(WiimoteButtonsEvent be) {
		buttonInputReceived(be);

	}

	@Override
	public void onClassicControllerInsertedEvent(
			ClassicControllerInsertedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClassicControllerRemovedEvent(
			ClassicControllerRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnectionEvent(DisconnectionEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	double rotX = 0;
	
	public void accelerationInputReceived(MotionSensingEvent ee) {

		double gainTranslation = wiiInterface.getGainTranslation() ;
		double gainRotation = wiiInterface.getGainRotation();
		
		Orientation ae = ee.getOrientation();
		//RawAcceleration ac = ee.getRawAcceleration();
		GForce ac = ee.getGforce();
		
		/*
		System.out.println("wiimotePitch : " + ae.getPitch()); //
		System.out.println("wiimoteRoll : " + ae.getRoll()); //
		System.out.println("wiimote x acceleration : " + ac.getX());
		System.out.println("wiimote y acceleration : " + ac.getY());
		System.out.println("wiimote z acceleration : " + ac.getZ());
		*/
		
		
		if (accelerationActivated) {
			if (translationModeAndNotRotationMode) { //A
				if (zModeAndNotYMode) { //1
					Quat4d rotation = new Quat4d();
					rotX += ac.getX();
					rotation.set(new AxisAngle4d(0, 1, 0, rotX * gainRotation));
					
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
				//	navigator.supportTranslateInHeadFrame(0.0, 0.0, -ac.getY()
				//			* gainTranslation);
				} else { //2
					Quat4d rotation = new Quat4d();
					rotation.set(new AxisAngle4d(0, 1, 0, -ac.getX()
							* gainRotation));
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
					navigator.supportTranslateInHeadFrame(0.0, -ac.getY()
							* gainTranslation, 0.0);
				}
				
			} else { //B
				if (zModeAndNotYMode) { //1
					Quat4d rotation = new Quat4d();
					rotation.set(new AxisAngle4d(1, 0, 0, -ac.getY()
							* gainRotation));
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
					rotation.set(new AxisAngle4d(0, 1, 0, -ac.getX()
							* gainRotation));
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
				} else { //2
					Quat4d rotation = new Quat4d();
					rotation.set(new AxisAngle4d(1, 0, 0, -ac.getY()
							* gainRotation));
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
					rotation.set(new AxisAngle4d(0, 0, 1, -ac.getX()
							* gainRotation));
					navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
							rotation.z, rotation.w);
				}
			}
		}
	}

	RotationThread rotationThread;
	TranslationThread translationThread;

	public void buttonInputReceived(WiimoteButtonsEvent be) {
		
		double gainTranslation = wiiInterface.getGainTranslation();
		double gainRotation = wiiInterface.getGainRotation();
		
		if (be.isButtonHomeJustPressed()) {
			navigator.goThereAndLookThatWay(0,0, 0, 0, 0, 1, 90);
			System.out.println("Home pressed");
		}
		
		if (be.isButtonOneJustPressed()) {
			System.out.println("1 pressed - Z mode");
			zModeAndNotYMode = true;
		}
		
		if (be.isButtonTwoJustPressed() ) {
			System.out.println("2 pressed - Y mode");
			zModeAndNotYMode = false;
		}
		
		if (be.isButtonAJustPressed()) {
			System.out.println("A pressed - translation mode");
			accelerationActivated = true;
			translationModeAndNotRotationMode = true;
		}
		
		if (be.isButtonBJustPressed()) {
			System.out.println("B pressed - Rotation mode");
			accelerationActivated = true;
			translationModeAndNotRotationMode = false;
		}
		
		if (be.isButtonMinusJustPressed())
			System.out.println("Minus pressed");
		
		if (be.isButtonLeftJustPressed()) {
			System.out.println("Left pressed");
			Quat4d rotation = new Quat4d();
			rotation.set(new AxisAngle4d(0, 1, 0, gainRotation));
			rotationThread = new RotationThread(navigator, rotation);
			rotationThread.start();
		}
		
		if (be.isButtonRightJustPressed()) {
			System.out.println("Right pressed");
			Quat4d rotation = new Quat4d();
			rotation.set(new AxisAngle4d(0, 1, 0, -gainRotation));
			rotationThread = new RotationThread(navigator, rotation);
			rotationThread.start();
		}
		
		if (be.isButtonDownJustPressed()) {
			System.out.println("Down pressed");
			translationThread = new TranslationThread(navigator, new Vector3d(
					0, 0, gainTranslation));
			translationThread.start();
		}
		if (be.isButtonUpJustPressed()) {
			System.out.println("Up pressed");
			translationThread = new TranslationThread(navigator, new Vector3d(
					0, 0, -gainTranslation));
			translationThread.start();
		}
		if (be.isButtonPlusJustPressed())
			System.out.println("Plus pressed");
	
		if (be.isButtonAJustReleased()) {
			System.out.println("A released");
			accelerationActivated = false;
		}

		if (be.isButtonBJustReleased()) {
			System.out.println("Home released");
			accelerationActivated = false;
		}
		
		if (be.isButtonMinusJustReleased())
			System.out.println("Minus released");
		
		
		if (be.isButtonLeftJustReleased()) {
			System.out.println("Left released");
			rotationThread.finish();
		}
		if (be.isButtonRightJustReleased()) {
			System.out.println("Right released");
			rotationThread.finish();
		}
		if (be.isButtonDownJustReleased()) {
			System.out.println("Down released");
			translationThread.finish();
		}
		if (be.isButtonUpJustReleased()) {
			System.out.println("Up released");
			translationThread.finish();
		}
		;
		if (be.isButtonPlusJustReleased())
			System.out.println("Plus released");
	}

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
				navigator.supportTranslateInHeadFrame(translation.x,
						translation.y, translation.z);
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
				navigator.supportRotateInHeadFrame(rotation.x, rotation.y,
						rotation.z, rotation.w);
				try {
					sleep(20);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
