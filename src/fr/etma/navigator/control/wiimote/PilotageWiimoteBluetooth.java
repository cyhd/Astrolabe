package fr.etma.navigator.control.wiimote;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteListener;

import com.intel.bluetooth.BlueCoveConfigProperties;

import fr.etma.navigator.control.Navigator;

public class PilotageWiimoteBluetooth extends JFrame {

   private static final long serialVersionUID = 1L ;
   protected WiiRemote wiimote = null ;
   protected Navigator navigator ;
   protected JLabel status ;
   protected JButton connectDisconnectButton ;
   protected JTextField bluetoothField ;
   protected ConnectionListener connectionListener ;
   protected DisconnectionListener disconnectionListener ;
   protected JSlider sliderSeuilTranslation ;
   protected JSlider sliderSeuilRotation ;
   protected JSlider sliderGainTranslation ;
   protected JSlider sliderGainRotation ;
   protected double seuilSensibiliteRotation = 0.02 ;
   protected double seuilSensibiliteTranslation = 0.02 ;
   protected double gainRotation = 0.01 ;
   protected double gainTranslation = 0.2 ;
   protected JButton goButton ;
   protected JTextField xTF ;
   protected JTextField yTF ;
   protected JTextField zTF ;
   protected boolean wiimoteDirectionActivated = false ;
   protected boolean accelerationActivated = false ;
   protected boolean zModeAndNotYMode = true ;
   protected boolean translationModeAndNotRotationMode = true ;
   protected boolean zModeAndNotYModeNunchuck = true ;
   protected boolean rotationModeNunchuck = false ;
   protected final int xWiimoteResolution = 1024 ;
   protected final int yWiimoteResolution = 768 ;
   protected final int xMax = 1920 ;
   protected final int yMax = 1080 ;
   protected final int xMin = 0 ;
   protected final int yMin = 0 ;
   protected double wiimotePitch ;
   protected double wiimoteRoll ;
   protected double azimuth ;
   

   public PilotageWiimoteBluetooth (final Navigator navigator) {
      System.setProperty (BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
      this.navigator = navigator ;
      bluetoothField = new JTextField ("0017AB2AB4A3") ;
      connectDisconnectButton = new JButton ("Connect") ;
      connectionListener = new ConnectionListener () ;
      disconnectionListener = new DisconnectionListener () ;
      connectDisconnectButton.addActionListener (connectionListener) ;
      status = new JLabel ("unknown") ;
      sliderSeuilTranslation = new JSlider (SwingConstants.HORIZONTAL, 0, 100, (int)(seuilSensibiliteTranslation * 250)) ;
      sliderSeuilRotation = new JSlider (SwingConstants.HORIZONTAL, 0, 100, (int)(seuilSensibiliteRotation * 250)) ;
      sliderGainTranslation = new JSlider (SwingConstants.HORIZONTAL, 0, 100, (int)(gainTranslation * 100)) ;
      sliderGainRotation = new JSlider (SwingConstants.HORIZONTAL, 0, 100, (int)(gainRotation * 1000)) ;
      sliderSeuilTranslation.addChangeListener (new ChangeListener () {
         @Override
         public void stateChanged (ChangeEvent e) {
            seuilSensibiliteTranslation = sliderSeuilTranslation.getValue () / 250.0 ;
         }});
      sliderSeuilRotation.addChangeListener (new ChangeListener () {
         @Override
         public void stateChanged (ChangeEvent e) {
            seuilSensibiliteRotation = sliderSeuilRotation.getValue () / 250.0 ;
         }});
      sliderGainTranslation.addChangeListener (new ChangeListener () {
         @Override
         public void stateChanged (ChangeEvent e) {
            gainTranslation = sliderGainTranslation.getValue () / 100.0 ;
         }});
      sliderGainRotation.addChangeListener (new ChangeListener () {
         @Override
         public void stateChanged (ChangeEvent e) {
            gainRotation = sliderGainRotation.getValue () / 1000.0 ;
         }});
      JPanel mainPanel = new JPanel () ;
      mainPanel.setLayout (new GridLayout (8, 2)) ;
      mainPanel.add (new JLabel ("Connection to Wiimote")) ;
      mainPanel.add (connectDisconnectButton) ;
      mainPanel.add (new JLabel ("Bluetooth address:")) ;
      mainPanel.add (bluetoothField) ;
      mainPanel.add (new JLabel ("Connected:")) ;
      mainPanel.add (status) ;
      mainPanel.add (new JLabel ("Sensibility TZ:")) ;
      mainPanel.add (sliderSeuilTranslation) ;
      mainPanel.add (new JLabel ("Sensibility RY:")) ;
      mainPanel.add (sliderSeuilRotation) ;
      mainPanel.add (new JLabel ("Gain TZ:")) ;
      mainPanel.add (sliderGainTranslation) ;
      mainPanel.add (new JLabel ("Gain RY:")) ;
      mainPanel.add (sliderGainRotation) ;
      goButton = new JButton ("Go") ;
      JPanel goPanel = new JPanel () ;
      goPanel.setLayout (new GridLayout (1,3)) ;
      xTF = new JTextField ("0") ;
      yTF = new JTextField ("0") ;
      zTF = new JTextField ("0") ;
      goPanel.add (xTF) ;
      goPanel.add (yTF) ;
      goPanel.add (zTF) ;
      goButton.addActionListener (new ActionListener () {
         @Override
         public void actionPerformed (ActionEvent e) {
            try {
               navigator.setSupportPositionInGlobalFrame (new Double (xTF.getText ()).doubleValue (), new Double (yTF.getText ()).doubleValue (), new Double (zTF.getText ()).doubleValue ()) ;
            } catch (java.lang.NumberFormatException ex) {}
            }});
      mainPanel.add (goButton) ;
      mainPanel.add (goPanel) ;
      getContentPane ().add (mainPanel) ;
      pack () ;
      setVisible (true) ;
   }

   protected class ConnectionListener implements ActionListener {
      @Override
      public void actionPerformed (ActionEvent e) {
         connect (bluetoothField.getText ()) ;
      }
   }

   protected class DisconnectionListener implements ActionListener {
      @Override
      public void actionPerformed (ActionEvent e) {
         disconnect () ;
      }
   }

   public void connect (String bluetoothAddress) {
      wiimote = null ;
      
      while (wiimote == null) {
          try {
      //  	  wiimote = WiiRemoteJ.connectToRemote (bluetoothAddress) ;
        	  wiimote = WiiRemoteJ.findRemote();
          }
          catch(Exception e) {
        	  wiimote = null;
              e.printStackTrace();
              System.out.println("Failed to connect remote. Trying again.");
              
          }
      }
      
      try {
        // wiimote = WiiRemoteJ.findRemote();
        // wiimote = WiiRemoteJ.connectToRemote (bluetoothAddress) ;
         status.setText ("connected");
         wiimote.addWiiRemoteListener (new WiimoteListener ()) ;
         wiimote.setIRSensorEnabled (true, WRIREvent.BASIC) ;
         wiimote.setAccelerometerEnabled (true) ;
         boolean [] leds = new boolean [] { true, false, false, false } ;
         wiimote.setLEDLights (leds) ;
         wiimote.modulatedVibrateFor (100, 1) ;
         connectDisconnectButton.removeActionListener (connectionListener) ;
         connectDisconnectButton.addActionListener (disconnectionListener);
         connectDisconnectButton.setText ("Disconnect");
      } catch (Exception e) {
         status.setText ("unconnected");
         System.out.print(e.getLocalizedMessage());
         e.printStackTrace();
      }
   }

   public void disconnect () {
      if (wiimote != null) {
         wiimote.disconnect () ;
         connectDisconnectButton.removeActionListener (disconnectionListener) ;
         connectDisconnectButton.addActionListener (connectionListener);
         connectDisconnectButton.setText ("Connect");
         status.setText ("unconnected");
      }
   }

	class WiimoteListener implements WiiRemoteListener {

	   protected LinkedList<Double> azimuths = new LinkedList<Double> () ;
	   protected LinkedList<Double> pitchs = new LinkedList<Double> () ;
	   protected LinkedList<Double> rolls = new LinkedList<Double> () ;
	   protected final int historyLenght = 20 ;

		@Override
		public void IRInputReceived (WRIREvent ire) {
		   if (wiimoteDirectionActivated) {
		      double x, y, deltaX, deltaY, sensorBarLenght ;
		      if ((ire.getIRLights () [0] != null) && (ire.getIRLights () [1] != null)) {
		         // position des diodes dans le repère caméra wiimote, entre 0 et 1
		         x = (ire.getIRLights () [0].getX () + ire.getIRLights () [1].getX ()) / 2.0 ;
		         y = (ire.getIRLights () [0].getY () + ire.getIRLights () [1].getY ()) / 2.0 ;
		         // écart entre les 2 diodes dans le repère caméra wiimote, entre 0 et 1
		         deltaX = Math.abs (ire.getIRLights () [0].getX () - ire.getIRLights () [1].getX ()) ;
		         deltaY = Math.abs (ire.getIRLights () [0].getY () - ire.getIRLights () [1].getY ()) ;
		         sensorBarLenght = Math.sqrt (deltaX * deltaX + deltaY * deltaY) ;
		         // angle d'azimuth estimé
		         azimuth = Math.atan2 (x - 0.5, 0.1 / sensorBarLenght) ; 
		         azimuths.addFirst (azimuth);
		         if (azimuths.size () > historyLenght) {
		            azimuths.removeLast () ;
		         }
//               System.out.println ("x = " + x) ;
//               System.out.println ("sensorBarLenght = " + sensorBarLenght) ;
//               System.out.println ("azimuth = " + azimuth) ;
		      }
            Double Pitch = new Double (wiimotePitch) ;
            Double Roll = new Double (wiimoteRoll) ;
		      if (! Pitch.isNaN () && ! Roll.isNaN ()) {
	            pitchs.addFirst (Pitch) ;
	            rolls.addFirst (Roll) ;
               if (pitchs.size () > historyLenght) {
                  pitchs.removeLast () ;
               }
               if (rolls.size () > historyLenght) {
                  rolls.removeLast () ;
               }
            } else {
               System.out.println ("NaN") ;              
            }
            double averagePitch = 0 ;
            double averageAzimuth = 0 ;
            double averageRoll = 0 ;
            for (int i = 0 ; i < azimuths.size () ; i++) {
               averageAzimuth = averageAzimuth + azimuths.get (i) ;
            }
            if (azimuths.size () > 0) {
               averageAzimuth = averageAzimuth / azimuths.size () ;
            }
            for (int i = 0 ; i < pitchs.size () ; i++) {
               averagePitch = averagePitch + pitchs.get (i) ;
            }
            if (pitchs.size () > 0) {
               averagePitch = averagePitch / pitchs.size () ;
            }
            for (int i = 0 ; i < rolls.size () ; i++) {
               averageRoll = averageRoll + rolls.get (i) ;
            }
            if (rolls.size () > 0) {
               averageRoll = averageRoll / rolls.size () ;
            }
            Quat4d rotX = new Quat4d () ;
            rotX.set (new AxisAngle4d (1, 0, 0, -averagePitch)) ;
            Quat4d rotY = new Quat4d () ;
            rotY.set (new AxisAngle4d (0, 1, 0, averageAzimuth)) ;
            Quat4d rotZ = new Quat4d () ;
            rotZ.set (new AxisAngle4d (0, 0, 1, -averageRoll)) ;
            System.out.println ("averagePitch = " + averagePitch + " averageRoll = " + averageRoll + " averageAzimuth = " + averageAzimuth) ;
            Quat4d rot = new Quat4d () ;
            rot.mul (rotY, rotX) ;
            rot.normalize () ;
            rot.mul (rot, rotZ) ;
            rot.normalize () ;
            navigator.setHeadOrientationInSupportFrame (rot.x, rot.y, rot.z, rot.w) ;
		   }
		}

		@Override
		public void accelerationInputReceived (WRAccelerationEvent ae) {
		   wiimotePitch =  ae.getPitch () ;
		   wiimoteRoll = ae.getRoll () ;
		   if (wiimoteRoll > Math.PI) {
		      wiimoteRoll = wiimoteRoll - 2 * Math.PI ;
		   }
//         System.out.println ("wiimotePitch : " + wiimotePitch) ;
//         System.out.println ("wiimoteRoll : " + wiimoteRoll) ;
//       System.out.println ("wiimote x acceleration : " + ae.getXAcceleration ()) ;
//			System.out.println ("wiimote y acceleration : " + ae.getYAcceleration ()) ;
//			System.out.println ("wiimote z acceleration : " + ae.getZAcceleration ()) ;
		   if (accelerationActivated) {
		      if (translationModeAndNotRotationMode) {
		         if (zModeAndNotYMode) {
		            Quat4d rotation = new Quat4d () ;
		            rotation.set (new AxisAngle4d (0, 1, 0, -ae.getXAcceleration () * gainRotation)) ;
		            navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
		            navigator.supportTranslateInHeadFrame (0.0, 0.0, -ae.getYAcceleration () * gainTranslation) ;
		         } else {
		            Quat4d rotation = new Quat4d () ;
		            rotation.set (new AxisAngle4d (0, 1, 0, -ae.getXAcceleration () * gainRotation)) ;
		            navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
		            navigator.supportTranslateInHeadFrame (0.0, -ae.getYAcceleration () * gainTranslation, 0.0) ;		         
		         }
		      } else {
               if (zModeAndNotYMode) {
                  Quat4d rotation = new Quat4d () ;
                  rotation.set (new AxisAngle4d (1, 0, 0, -ae.getYAcceleration () * gainRotation)) ;
                  navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
                  rotation.set (new AxisAngle4d (0, 1, 0, -ae.getXAcceleration () * gainRotation)) ;
                  navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
                } else {
                  Quat4d rotation = new Quat4d () ;
                  rotation.set (new AxisAngle4d (1, 0, 0, -ae.getYAcceleration () * gainRotation)) ;
                  navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
                  rotation.set (new AxisAngle4d (0, 0, 1, -ae.getXAcceleration () * gainRotation)) ;
                  navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
               }		         
		      }
		   }
		}

		RotationThread rotationThread ;
		TranslationThread translationThread ;
		
		@Override
		public void buttonInputReceived (WRButtonEvent be) {
			if (be.wasPressed (WRButtonEvent.TWO)) System.out.println ("2 pressed") ;
			if (be.wasPressed (WRButtonEvent.ONE)) {
            wiimoteDirectionActivated = true ;
            System.out.println ("1 pressed") ;
			}
			if (be.wasPressed (WRButtonEvent.B)) {
			   System.out.println ("B pressed") ;
            zModeAndNotYMode = false ;
			}
			if (be.wasPressed (WRButtonEvent.A)) {
			   System.out.println ("A pressed") ;
			   accelerationActivated = true ;
            translationModeAndNotRotationMode = true ;
			}
			if (be.wasPressed (WRButtonEvent.MINUS)) System.out.println ("Minus pressed") ;
			if (be.wasPressed (WRButtonEvent.HOME)) {
			   System.out.println ("Home pressed") ;
            accelerationActivated = true ;
			   translationModeAndNotRotationMode = false ;
			}
			if (be.wasPressed (WRButtonEvent.LEFT)) {
			   System.out.println ("Left pressed") ;
			   Quat4d rotation = new Quat4d () ;
			   rotation.set (new AxisAngle4d (0, 1, 0, gainRotation)) ;
			   rotationThread = new RotationThread (navigator, rotation) ;
			   rotationThread.start () ;
			}
			if (be.wasPressed (WRButtonEvent.RIGHT)) {
			   System.out.println ("Right pressed") ;
            Quat4d rotation = new Quat4d () ;
            rotation.set (new AxisAngle4d (0, 1, 0, -gainRotation)) ;
            rotationThread = new RotationThread (navigator, rotation) ;
            rotationThread.start () ;
			}
			if (be.wasPressed (WRButtonEvent.DOWN)) {
            System.out.println ("Down pressed") ;
            translationThread = new TranslationThread (navigator, new Vector3d (0, 0, gainTranslation)) ;
            translationThread.start () ;
			}
			if (be.wasPressed (WRButtonEvent.UP)) {
			   System.out.println ("Up pressed") ;
            translationThread = new TranslationThread (navigator, new Vector3d (0, 0, -gainTranslation)) ;
            translationThread.start () ;
			}
			if (be.wasPressed (WRButtonEvent.PLUS)) System.out.println ("Plus pressed") ;
			if (be.wasReleased (WRButtonEvent.TWO)) System.out.println ("2 released") ;
			if (be.wasReleased (WRButtonEvent.ONE)) {
			   wiimoteDirectionActivated = false ;
			   System.out.println ("1 released") ;
			}
			if (be.wasReleased (WRButtonEvent.B)) {
			   System.out.println ("B released") ;
			   zModeAndNotYMode = true ;
			}
			if (be.wasReleased (WRButtonEvent.A)) {
			   System.out.println ("A released") ;
            accelerationActivated = false ;
			}
			if (be.wasReleased (WRButtonEvent.MINUS)) System.out.println ("Minus released") ;
			if (be.wasReleased (WRButtonEvent.HOME)) {
			   System.out.println ("Home released") ;
            translationModeAndNotRotationMode = true ;
            accelerationActivated = false ;
			}
			if (be.wasReleased (WRButtonEvent.LEFT)) {
			   System.out.println ("Left released") ;
			   rotationThread.finish () ;
			}
			if (be.wasReleased (WRButtonEvent.RIGHT)) {
			   System.out.println ("Right released") ;
			   rotationThread.finish () ;
			}
			if (be.wasReleased (WRButtonEvent.DOWN)) {
			   System.out.println ("Down released") ;
            translationThread.finish () ;
			}
			if (be.wasReleased (WRButtonEvent.UP)) {
			   System.out.println ("Up released") ;
            translationThread.finish () ;
			};
			if (be.wasReleased (WRButtonEvent.PLUS)) System.out.println ("Plus released") ;
		}

		@Override
		public void combinedInputReceived (WRCombinedEvent arg0) {
			//System.out.println ("combinedInputReceived") ;
		}

		@Override
		public void disconnected () {
			System.out.println ("disconnected") ;
		}

		@Override
		public void extensionConnected (WiiRemoteExtension arg0) {
			System.out.println ("extensionConnected") ;
			try {
				wiimote.setExtensionEnabled (true) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void extensionDisconnected (WiiRemoteExtension arg0) {
			System.out.println ("extensionDisconnected") ;
		}

		@Override
		public void extensionInputReceived (WRExtensionEvent ee) {
			if (ee instanceof WRNunchukExtensionEvent) {
				WRNunchukExtensionEvent nee = (WRNunchukExtensionEvent)ee ;
				WRAccelerationEvent ae = nee.getAcceleration () ;
//				System.out.println ("nunchuk x acceleration : " + ae.getXAcceleration ()) ;
//				System.out.println ("nunchuk y acceleration : " + ae.getYAcceleration ()) ;
//				System.out.println ("nunchuk z acceleration : " + ae.getZAcceleration ()) ;
            if (nee.wasPressed (WRNunchukExtensionEvent.C)) {
               System.out.println ("C pressed") ;
               rotationModeNunchuck = true ;
            }
            if (nee.wasPressed (WRNunchukExtensionEvent.Z)) {
               System.out.println ("Z pressed") ;
               zModeAndNotYModeNunchuck = false ;
            }
				if (nee.wasReleased (WRNunchukExtensionEvent.C)) {
				   System.out.println ("C released") ;
				   rotationModeNunchuck = false ;
				}
				if (nee.wasReleased (WRNunchukExtensionEvent.Z)) {
				   System.out.println ("Z released") ;
               zModeAndNotYModeNunchuck = true ;
				}
				double x = nee.getAnalogStickData ().getX () ;
				double y = nee.getAnalogStickData ().getY () ;
				if (Math.abs (x) > seuilSensibiliteRotation) {
				   //System.out.println ("stick x = " + x + " ; y = " + y + " ; angle = " + nee.getAnalogStickData ().getAngle ()) ;
				   Quat4d rotation = new Quat4d () ;
				   rotation.set (new AxisAngle4d (0, 1, 0, -x * gainRotation)) ;
				   navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
				}
				if (Math.abs (y) > seuilSensibiliteTranslation) {
				   //System.out.println ("stick x = " + x + " ; y = " + y + " ; angle = " + nee.getAnalogStickData ().getAngle ()) ;
				   if (zModeAndNotYModeNunchuck) {
				      navigator.supportTranslateInHeadFrame (0, 0, -y * gainTranslation) ;
				   } else {
				      navigator.supportTranslateInHeadFrame (0, y * gainTranslation, 0) ;
				   }
				}
				if (rotationModeNunchuck) {
				   Quat4d rotation = new Quat4d () ;
				   if (zModeAndNotYModeNunchuck) {
				      rotation.set (new AxisAngle4d (1, 0, 0, -ae.getYAcceleration () * gainRotation)) ;
				   } else {
				      rotation.set (new AxisAngle4d (0, 1, 0, -ae.getYAcceleration () * gainRotation)) ;
				   }
               navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
				   rotation.set (new AxisAngle4d (0, 0, 1, -ae.getXAcceleration () * gainRotation)) ;
				   navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
				}	   
			}
			
		}

		@Override
		public void extensionPartiallyInserted () {
			System.out.println ("extensionPartiallyInserted") ;
		}

		@Override
		public void extensionUnknown () {
			System.out.println ("extensionUnknown") ;
		}

		@Override
		public void statusReported (WRStatusEvent arg0) {
			System.out.println ("statusReported") ;
		}

	}
	
   class TranslationThread extends Thread {
      
      protected Navigator navigator ;
      protected boolean finished = false ;
      protected Vector3d translation ;
      
      public TranslationThread (Navigator navigator, Vector3d translation) {
         this.translation = translation ;
         this.navigator = navigator ;
      }
      
      public void finish () {
         finished = true ;
      }

      @Override
	public void run () {
         while (! finished) {
            navigator.supportTranslateInHeadFrame (translation.x, translation.y, translation.z) ;
            try {
               sleep (20) ;
            } catch (InterruptedException e) {
            }
         }
      }
      
   }

   class RotationThread extends Thread {
      
      protected Navigator navigator ;
      protected boolean finished = false ;
      protected Quat4d rotation ;
      
      public RotationThread (Navigator navigator, Quat4d rotation) {
         this.rotation = rotation ;
         this.navigator = navigator ;
      }
      
      public void finish () {
         finished = true ;
      }

      @Override
	public void run () {
         while (! finished) {
            navigator.supportRotateInHeadFrame (rotation.x, rotation.y, rotation.z, rotation.w) ;
            try {
               sleep (20) ;
            } catch (InterruptedException e) {
            }
         }
      }
      
   }

}
