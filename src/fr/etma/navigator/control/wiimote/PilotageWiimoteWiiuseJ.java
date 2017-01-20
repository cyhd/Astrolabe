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

	double seuilSensibiliteRotation = 0.02;
	double seuilSensibiliteTranslation = 5.0;
	double gainRotation = 0.001;
	double gainTranslation = 0.2;

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
		Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, true); // ,
																	// WiiUseApiManager.WIIUSE_STACK_MS);
		if (wiimotes.length > 0) {
			wiimote = wiimotes[0];
			//wiimote.activateContinuous();
			//wiimote.activateSmoothing();
			wiimote.addWiiMoteEventListeners( controlerWiimote);
			connectDisconnectButton.removeActionListener(connectionListener);			
			connectDisconnectButton.addActionListener(disconnectionListener);
			connectDisconnectButton.setText("Disconnect");
			status.setText(wiimotes[0] + " connected.");
		} else {
			status.setText("No wiimotes found !!!");
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
		JSlider sliderSeuilRotation;
		JSlider sliderGainTranslation;
		JSlider sliderGainRotation;
		
		
		connectDisconnectButton = new JButton("Connect");
		connectionListener = new ActionListener() {public void actionPerformed(ActionEvent e) {	connect();	}};
		disconnectionListener = new ActionListener() {public void actionPerformed(ActionEvent e) {  disconnect();	}};
		connectDisconnectButton.addActionListener(connectionListener);
		status = new JLabel("unknown");

		final float seuilTranslationFactor = 1.0f;
		final String labelTZstring = "Sensibility TZ:  ";
		JLabel labelTZ = new JLabel(labelTZstring + seuilSensibiliteTranslation);

		final float seuilRotationFactor = 100.0f;
		final String labelRYstring = "Sensibility RY:  ";
		JLabel labelRY = new JLabel(labelRYstring + seuilSensibiliteRotation);
		
		final double gainTranslationFactor = 100.0;
		String gainTZstring = "Gain TZ:  ";
		JLabel gainTZ = new JLabel(gainTZstring + gainTranslation );
		
		final double gainRotationFactor = 1000.0;
		String gainRYstring = "Gain RY:  ";
		JLabel gainRY = new JLabel(gainRYstring + gainRotation);
		
		// orientation, val de l unite minimale, val unite max, val courante
		sliderSeuilTranslation = new JSlider(SwingConstants.HORIZONTAL, 0, 180, (int) (seuilSensibiliteTranslation * seuilTranslationFactor));
		sliderSeuilTranslation.setMajorTickSpacing(100); 
		sliderSeuilTranslation.setMinorTickSpacing(10); 
		sliderSeuilTranslation.setPaintTicks(true); 
		sliderSeuilTranslation.setPaintLabels(true); 
		sliderSeuilTranslation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				seuilSensibiliteTranslation = sliderSeuilTranslation.getValue() / seuilTranslationFactor;
				labelTZ.setText(labelTZstring + seuilSensibiliteTranslation);
			}
		});

		sliderSeuilRotation = new JSlider(SwingConstants.HORIZONTAL, 0, 180,
				(int) (seuilSensibiliteRotation * seuilRotationFactor));
		sliderSeuilRotation.setMajorTickSpacing(100);
		sliderSeuilRotation.setMinorTickSpacing(10);
		sliderSeuilRotation.setPaintTicks(true);
		sliderSeuilRotation.setPaintLabels(true);
		sliderSeuilRotation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				seuilSensibiliteRotation = sliderSeuilRotation.getValue() / seuilRotationFactor;
				labelRY.setText(labelRYstring + seuilSensibiliteRotation);
			}
		});

		sliderGainTranslation = new JSlider(SwingConstants.HORIZONTAL, 0, 180, (int) (gainTranslation * gainTranslationFactor));
		sliderGainTranslation.setMajorTickSpacing(100); 
		sliderGainTranslation.setMinorTickSpacing(10); 
		sliderGainTranslation.setPaintTicks(true); 
		sliderGainTranslation.setPaintLabels(true); 
		sliderGainTranslation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				gainTranslation = sliderGainTranslation.getValue() / gainTranslationFactor;
				gainTZ.setText(gainTZstring + gainTranslation );
			}
		});

		sliderGainRotation = new JSlider(SwingConstants.HORIZONTAL, 0, 180, (int) (gainRotation * gainRotationFactor));
		sliderGainRotation.setMajorTickSpacing(100); 
		sliderGainRotation.setMinorTickSpacing(10); 
		sliderGainRotation.setPaintTicks(true); 
		sliderGainRotation.setPaintLabels(true); 
		sliderGainRotation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				gainRotation = sliderGainRotation.getValue() / gainRotationFactor;
				gainRY.setText(gainRYstring + gainRotation);
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(8, 2));
		mainPanel.add(new JLabel("Connection to Wiimote"));
		mainPanel.add(connectDisconnectButton);

		mainPanel.add(new JLabel("Connected:"));
		mainPanel.add(status);
		mainPanel.add(labelTZ);
		mainPanel.add(sliderSeuilTranslation);
		mainPanel.add(labelRY);
		mainPanel.add(sliderSeuilRotation);
		mainPanel.add(gainTZ);
		mainPanel.add(sliderGainTranslation);
		mainPanel.add(gainRY);
		mainPanel.add(sliderGainRotation);
		getContentPane().add(mainPanel);
		pack();
		setVisible(true);
	}

	public double getSeuilSensibiliteRotation() {
		return seuilSensibiliteRotation;
	}

	public double getGainRotation() {
		return gainRotation;
	}

	public double getSeuilSensibiliteTranslation() {
		return seuilSensibiliteTranslation;
	}

	public double getGainTranslation() {
		return gainTranslation;
	}
}
