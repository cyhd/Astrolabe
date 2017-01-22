package fr.etma.navigator.control.wiimote;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.awvalenti.wiiusej.WiiusejNativeLibraryLoadingException;

import fr.etma.navigator.control.Navigator;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;

public class PilotageWiimoteWiiuseJ extends JFrame {

	private static final long serialVersionUID = 1L;

	private Wiimote wiimote;
	private ControlerWiimoteListener controlerWiimote;

	private JLabel status;
	private JButton connectDisconnectButton;
	private ActionListener connectionListener;
	private ActionListener disconnectionListener;

	//double seuilSensibiliteRotation = 0.02;
	double seuilSensibiliteTranslation = 0.1;
	double gainRotation = 0.01; 
	double gainTranslation = 0.1;

	public PilotageWiimoteWiiuseJ(final Navigator navigator) {

		this.setTitle("Wii Control");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				PilotageWiimoteWiiuseJ.this.disconnect();
				PilotageWiimoteWiiuseJ.this.dispose();
				System.exit(333);
			}
		});

		buildInterface();

		controlerWiimote = new ControlerWiimoteListener(navigator, this);

	}

	public boolean connect() {
		WiiUseApiManager wm;
		try {
			wm = new WiiUseApiManager();

			Wiimote[] wiimotes = wm.getWiimotes(1); // , true); // ,
													// WiiUseApiManager.WIIUSE_STACK_MS);
			if (wiimotes.length > 0) {
				wiimote = wiimotes[0];
				wiimote.activateContinuous();
				//wiimote.activateSmoothing();
				wiimote.activateMotionSensing();
				wiimote.addWiiMoteEventListeners(controlerWiimote);
				connectDisconnectButton
						.removeActionListener(connectionListener);
				connectDisconnectButton
						.addActionListener(disconnectionListener);
				connectDisconnectButton.setText("Disconnect");
				status.setText(wiimotes[0] + " connected.");
			} else {
				status.setText("No wiimotes found !!!");
				return false;
			}
		} catch (WiiusejNativeLibraryLoadingException e) {
			System.out.println("cannot load wiiuse dll.");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public void disconnect() {
		if (wiimote != null) {
			wiimote.disconnect();
			connectDisconnectButton.removeActionListener(disconnectionListener);
			connectDisconnectButton.addActionListener(connectionListener);
			connectDisconnectButton.setText("Connect");
			status.setText("unconnected");
			System.out.println("WiiMote disconnected.");
		}
	}

	private void buildInterface() {
		JSlider sliderSeuilTranslation;
		JSlider sliderGainTranslation;
		JSlider sliderGainRotation;

		connectDisconnectButton = new JButton("Connect");
		connectionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		};
		disconnectionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		};
		connectDisconnectButton.addActionListener(connectionListener);
		status = new JLabel("unknown");

		final float seuilTranslationFactor = 100.0f;
		final String labelTZstring = "Sensibility TZ:  ";
		JLabel labelTZ = new JLabel(labelTZstring + seuilSensibiliteTranslation);

		final double gainTranslationFactor = 100.0;
		String gainTZstring = "Gain TZ:  ";
		JLabel gainTZ = new JLabel(gainTZstring + gainTranslation);
		
		final double gainRotationFactor = 250.0;
		String gainRYstring = "Gain RY:  ";
		JLabel gainRY = new JLabel(gainRYstring + gainRotation);

		// orientation, val de l unite minimale, val unite max, val courante
		sliderSeuilTranslation = new JSlider(SwingConstants.HORIZONTAL, 0, (int) seuilTranslationFactor,
				(int) (seuilSensibiliteTranslation * seuilTranslationFactor));
		sliderSeuilTranslation.setMajorTickSpacing((int)seuilTranslationFactor / 2);
		sliderSeuilTranslation.setMinorTickSpacing((int)seuilTranslationFactor / 10);
		sliderSeuilTranslation.setPaintTicks(true);
		sliderSeuilTranslation.setPaintLabels(true);
		sliderSeuilTranslation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				seuilSensibiliteTranslation =  (sliderSeuilTranslation.getValue()
						/ seuilTranslationFactor);
				labelTZ.setText(labelTZstring + seuilSensibiliteTranslation);
			}
		});

		
		/* gainTranslation is varying between 0 and 1 
		 * 
		 */
		sliderGainTranslation = new JSlider(SwingConstants.HORIZONTAL, 0, (int) gainTranslationFactor,
				(int) (gainTranslation * gainTranslationFactor));
		sliderGainTranslation.setMajorTickSpacing((int) gainTranslationFactor / 2);
		sliderGainTranslation.setMinorTickSpacing((int) gainTranslationFactor / 10);
		sliderGainTranslation.setPaintTicks(true);
		sliderGainTranslation.setPaintLabels(true);
		sliderGainTranslation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				gainTranslation = sliderGainTranslation.getValue() / gainTranslationFactor;
				gainTZ.setText(gainTZstring + gainTranslation);
			}
		});
		
		sliderGainRotation = new JSlider(SwingConstants.HORIZONTAL, 1, (int)gainRotationFactor,
				(int) (1.0 / gainRotation));
		sliderGainRotation.setMajorTickSpacing((int) (gainRotationFactor / 2.0));
		sliderGainRotation.setMinorTickSpacing((int) (gainRotationFactor / 10.0));
		sliderGainRotation.setPaintTicks(true);
		sliderGainRotation.setPaintLabels(true);
		sliderGainRotation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				gainRotation = 1.0f / sliderGainRotation.getValue();
				gainRY.setText(gainRYstring + gainRotation);
			}
		});
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6, 2));
		mainPanel.add(new JLabel("Connection to Wiimote"));
		mainPanel.add(connectDisconnectButton);

		mainPanel.add(new JLabel("Connected:"));
		mainPanel.add(status);
		mainPanel.add(labelTZ);
		mainPanel.add(sliderSeuilTranslation);
		mainPanel.add(gainTZ);
		mainPanel.add(sliderGainTranslation);
		mainPanel.add(gainRY);
		mainPanel.add(sliderGainRotation);
		
		getContentPane().add(mainPanel);
		pack();
		setVisible(true);
	}

	public double getGainRotation() {
		return gainRotation * Math.PI / 180.0;
	}

	public double getSeuilSensibiliteTranslation() {
		return seuilSensibiliteTranslation ;
	}

	public double getGainTranslation() {
		return gainTranslation;
	}
}
