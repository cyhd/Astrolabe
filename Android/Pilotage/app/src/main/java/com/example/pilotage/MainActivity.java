package com.example.pilotage;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;

public class MainActivity extends Activity {

	public static InetAddress address ;
	public static Socket socket ;
	public static BluetoothSocket btSocket ;
	public static int port = 1234 ;
	public static ObjectOutputStream out ;
	public static Quat4d leftRotation = new Quat4d () ;
	public static Quat4d rightRotation = new Quat4d () ;
	public static Vector3d frontTranslation = new Vector3d () ;
	public static Vector3d backTranslation = new Vector3d () ;
	public static SensorManager sensorManager ;
	public static Sensor accelerometer ;
	public static Sensor gyroscope ;
	public static Sensor orientation ;
	public static Sensor magnetometer ;
	public static AccelerometerListener accelerometerListener ;
	public static GyroscopeListener gyroscopeListener ;
	public static OrientationListener orientationListener ;
	public static MagnometerListener magnetometerListener ;
	public static Compute3DOFThread threeDOFComputation ;
	public static Acquire3DOFThread threeDOFAcquisition ;
	public static ThreeDOFThread threeDOF ;

	public static float [] accelerometerValues = new float [3] ;
	public static float [] gyroscopeValues = new float [3] ;
	public static float [] orientationValues = new float [3] ;
	public static float [] magnetometerValues = new float [3] ;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PlaceholderFragment phf = new PlaceholderFragment () ;
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, phf)
			.commit();
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build () ;
		StrictMode.setThreadPolicy (policy) ;

		leftRotation.set (new AxisAngle4d (0, 1, 0, 0.1)) ;
		leftRotation.normalize () ;
		rightRotation.set (new AxisAngle4d (0, 1, 0, -0.1)) ;
		rightRotation.normalize () ;
		frontTranslation.set (0, 0, -0.1) ;
		backTranslation.set (0, 0, 0.1) ;
		
		sensorManager = (SensorManager)getSystemService (Context.SENSOR_SERVICE) ;
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//orientation = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				
	}

	@Override
	protected void onResume () {
		super.onResume () ;
		sensorManager.registerListener (accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL) ;
		sensorManager.registerListener (gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL) ;
		sensorManager.registerListener (orientationListener, orientation, SensorManager.SENSOR_DELAY_NORMAL) ;
		sensorManager.registerListener (magnetometerListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL) ;
	}

	@Override
	protected void onPause () {
		super.onPause () ;
		sensorManager.unregisterListener (accelerometerListener) ;
		sensorManager.unregisterListener (gyroscopeListener) ;
		sensorManager.unregisterListener (orientationListener) ;
		sensorManager.unregisterListener (magnetometerListener) ;
	}
	
	private static void actionConnection () {
		EditText textAddress = (EditText)(rootView.findViewById (R.id.editTextIPConnection)) ;
		try {
			address = InetAddress.getByName (textAddress.getText().toString()) ;
	        socket = new Socket (address, port) ;
	        out = new ObjectOutputStream (socket.getOutputStream ()) ;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		at = new AcceptThread ();
//		at.start () ;
	}

	private static void actionDisconnection () {	
		diffuseMessage ("server", "disconnect", null) ;
	}

	private static void actionGo (double i, double j, double k) {
		HashMap<String, Object> hm = new HashMap<String, Object> () ;
		hm.put ("x", i) ;
		hm.put ("y", j) ;
		hm.put ("z", k) ;
		diffuseMessage ("viewpoint", "translate", hm) ;
	}

	private static void actionRotate (double x, double y, double z, double w) {
		HashMap<String, Object> hm = new HashMap<String, Object> () ;
		hm.put ("x", x) ;
		hm.put ("y", y) ;
		hm.put ("z", z) ;
		hm.put ("w", w) ;
		diffuseMessage ("viewpoint", "rotate", hm) ;
	}

	private static void actionOrientate (double x, double y, double z, double w) {
		HashMap<String, Object> hm = new HashMap<String, Object> () ;
		hm.put ("x", x) ;
		hm.put ("y", y) ;
		hm.put ("z", z) ;
		hm.put ("w", w) ;
		diffuseMessage ("head", "orientate", hm) ;
	}

	private static void diffuseMessage (String name, String command, HashMap<String, Object> hm) {
		if (out != null) {
			synchronized (out) {
				try {
					out.writeObject (name) ;
					out.writeObject (command) ;
					out.writeObject (hm) ;
					out.flush () ;
				} catch (IOException e) {
					e.printStackTrace();
				}catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId () ;
		if (id == R.id.action_settings) {
			return true ;
		}
		return super.onOptionsItemSelected (item) ;
	}

	
	protected static View rootView ;
//	protected static AcceptThread at;
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		protected GoThread goThread = new GoThread (0, 0, 0) ;
		protected RotateThread rotateThread = new RotateThread (0, 0, 0, 0) ;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container, false);
			Button bFront = (Button)(rootView.findViewById (R.id.buttonFront)) ;
			bFront.setOnClickListener(new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionGo (frontTranslation.x, frontTranslation.y, frontTranslation.z) ;
				}}) ;
			Button bBack = (Button)(rootView.findViewById (R.id.buttonBack)) ;
			bBack.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionGo (backTranslation.x, backTranslation.y, backTranslation.z) ;
				}}) ;
			Button bLeft = (Button)(rootView.findViewById (R.id.buttonLeft)) ;
			bLeft.setOnClickListener(new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionRotate (leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w) ;
				}}) ;
			Button bRight = (Button)(rootView.findViewById (R.id.buttonRight)) ;
			bRight.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionRotate (rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w) ;
				}
			}) ;
			Button bConnection = (Button)(rootView.findViewById (R.id.buttonConnection)) ;
			bConnection.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionConnection () ;
				}
			}) ;
			Button bDisconnection = (Button)(rootView.findViewById (R.id.buttonDisconnection)) ;
			bDisconnection.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					System.out.println ("OnClick") ;
					actionDisconnection () ;
				}
			}) ;
			Button bLeftRight = (Button)(rootView.findViewById (R.id.buttonLeftRight)) ;
			bLeftRight.setOnDragListener (new OnDragListener () {
				@Override
				public boolean onDrag (View view, DragEvent event) {
					double x = event.getX () / view.getWidth () - 0.5 ;
					double y = event.getY () / view.getHeight () - 0.5 ;
					Quat4d rotation = new Quat4d () ;
					rotation.set (new AxisAngle4d (0, 1, 0, 0.1 * x)) ;
					actionRotate (rotation.x, rotation.y, rotation.z, rotation.w) ;
					return true ;
				}
			});
			bLeftRight.setOnTouchListener(new OnTouchListener () {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					double x = event.getX () / view.getWidth () - 0.5 ;
					double y = event.getY () / view.getHeight () - 0.5 ;
					Quat4d rotation = new Quat4d () ;
					rotation.set (new AxisAngle4d (0, 1, 0, -0.01 * x)) ;
					actionRotate (rotation.x, rotation.y, rotation.z, rotation.w) ;
					if (rotateThread != null) {
						rotateThread.finish () ;
					}
					rotateThread = new RotateThread (rotation.x, rotation.y, rotation.z, rotation.w) ;
					rotateThread.start () ;
					return false ;
				}
			}) ;
			bLeftRight.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					if (rotateThread != null) {
						rotateThread.finish () ;
					}
				}
			}) ;
			Button bFrontBack = (Button)(rootView.findViewById (R.id.buttonFrontBack)) ;
			bFrontBack.setOnTouchListener(new OnTouchListener () {
				@Override
				public boolean onTouch (View view, MotionEvent event) {
					double x = event.getX () / view.getWidth () - 0.5 ;
					double y = event.getY () / view.getHeight () - 0.5 ;
					if (goThread != null) {
						goThread.finish () ;
					}
					goThread = new GoThread (0, 0, 0.5 * y) ;
					goThread.start () ;
					return false ;
				}
			}) ;
			bFrontBack.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View view) {
					if (goThread != null) {
						goThread.finish () ;
					}
				}
			}) ;
			CheckBox checkBoxAccelerometer = (CheckBox)(rootView.findViewById (R.id.checkBoxAccelerometer)) ;
			if (accelerometer != null) {
				checkBoxAccelerometer.setChecked (true) ;
				TextView textViewAccelerometer = (TextView)(rootView.findViewById (R.id.textViewAccelerometer)) ;
				accelerometerListener = new AccelerometerListener (textViewAccelerometer) ;
			} else {
				checkBoxAccelerometer.setChecked (false) ;
			}
			CheckBox checkBoxGyroscope = (CheckBox)(rootView.findViewById (R.id.checkBoxGyroscope)) ;
			if (gyroscope != null) {
				checkBoxGyroscope.setChecked (true) ;
				TextView textViewGyroscope = (TextView)(rootView.findViewById (R.id.textViewGyroscope)) ;
				gyroscopeListener = new GyroscopeListener (textViewGyroscope) ;
			} else {
				checkBoxGyroscope.setChecked (false) ;
			}
			CheckBox checkBoxOrientation = (CheckBox)(rootView.findViewById (R.id.checkBoxOrientation)) ;
			if (orientation != null) {
				checkBoxOrientation.setChecked (true) ;
				TextView textViewOrientation = (TextView)(rootView.findViewById (R.id.textViewOrientation)) ;
				orientationListener = new OrientationListener (textViewOrientation) ;
			} else {
				checkBoxOrientation.setChecked (false) ;
			}
			CheckBox checkBoxMagnetometer = (CheckBox)(rootView.findViewById (R.id.checkBoxMagnetometer)) ;
			if (magnetometer != null) {
				checkBoxMagnetometer.setChecked (true) ;
				TextView textViewMagnetometer = (TextView)(rootView.findViewById (R.id.textViewMagnetometer)) ;
				magnetometerListener = new MagnometerListener (textViewMagnetometer) ;
			} else {
				checkBoxMagnetometer.setChecked (false) ;
			}
			final CheckBox checkBoxActivation = (CheckBox)(rootView.findViewById (R.id.checkBoxActivate)) ;
			final RadioButton radioButtonAcquisition = (RadioButton)(rootView.findViewById (R.id.radioButtonAcquisition)) ;
			final RadioButton radioButtonComputation = (RadioButton)(rootView.findViewById (R.id.radioButtonComputation)) ;
			checkBoxActivation.setOnCheckedChangeListener (new OnCheckedChangeListener () {
				@Override
				public void onCheckedChanged (CompoundButton checkBox, boolean isChecked) {
					if (isChecked) {
						if (threeDOF != null) {
							threeDOF.finish () ;
						}
						if (radioButtonAcquisition.isChecked ()) {
							threeDOF = new Acquire3DOFThread () ;
							threeDOF.start ();
						} else if (radioButtonComputation.isChecked ()) {
							threeDOF = new Compute3DOFThread () ;
							threeDOF.start ();
						}
					} else {
						if (threeDOF != null) {
							threeDOF.finish () ;
						}
					}
				}
			}) ;
			radioButtonAcquisition.setOnCheckedChangeListener (new OnCheckedChangeListener () {
				@Override
				public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {					
					if (isChecked) {
						if (threeDOF != null) {
							threeDOF.finish () ;
						}
						threeDOFAcquisition = new Acquire3DOFThread () ;
						threeDOF = threeDOFAcquisition ;
						if (checkBoxActivation.isChecked ()) {
							threeDOFAcquisition.start () ;
						}
					} else {
						threeDOFAcquisition.finish () ;
					}
				}
			}) ;
			radioButtonComputation.setOnCheckedChangeListener (new OnCheckedChangeListener () {
				@Override
				public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {					
					if (isChecked) {
						if (threeDOF != null) {
							threeDOF.finish () ;
						}
						threeDOFComputation = new Compute3DOFThread () ;
						threeDOF = threeDOFComputation ;
						if (checkBoxActivation.isChecked ()) { 
							threeDOFComputation.start () ;
						}
						
					} else {
						threeDOFComputation.finish () ;
					}
				}
			}) ;
			return rootView;
		}

		class GoThread extends Thread {
			protected double x ;
			protected double y ;
			protected double z ;
			protected boolean finished = false ;

			public GoThread (double x, double y, double z) {
				this.x = x ;
				this.y = y ;
				this.z = z ;
			}
			public void finish() {
				finished = true ;
			}
			@Override
			public void run () {
				while (! finished) {
					actionGo (x, y, z) ;
					try {
						sleep (20) ;
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		class RotateThread extends Thread {
			protected double x ;
			protected double y ;
			protected double z ;
			protected double w ;
			protected boolean finished = false ;

			public RotateThread (double x, double y, double z, double w) {
				this.x = x ;
				this.y = y ;
				this.z = z ;
				this.w = w ;
			}
			public void finish() {
				finished = true ;
			}
			@Override
			public void run () {
				while (! finished) {
					actionRotate (x, y, z, w) ;
					try {
						sleep (50) ;
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	static class MagnometerListener implements SensorEventListener {

		TextView view ;

	    public MagnometerListener (TextView view) {
	        this.view = view ;
	    }

		@Override
		public void onAccuracyChanged (Sensor s, int a) {

		}

		@Override
		public void onSensorChanged (SensorEvent event) {
			synchronized (magnetometerValues) {
				magnetometerValues [0] = event.values [0] ;
				magnetometerValues [1] = event.values [1] ;
				magnetometerValues [2] = event.values [2] ;
			}
			view.setText (magnetometerValues [0] + " " + magnetometerValues [1] + " " + magnetometerValues [2]) ;
		}

	}

	static class OrientationListener implements SensorEventListener {

		TextView view ;

	    public OrientationListener (TextView view) {
	        this.view = view ;
	    }

		@Override
		public void onAccuracyChanged (Sensor s, int a) {

		}

		@Override
		public void onSensorChanged (SensorEvent event) {
			synchronized (orientationValues) {
				orientationValues [0] = event.values [0] ;
				orientationValues [1] = event.values [1] ;
				orientationValues [2] = event.values [2] ;
			}
			view.setText (orientationValues [0] + " " + orientationValues [1] + " " + orientationValues [2]) ;
		}

	}

	static class AccelerometerListener implements SensorEventListener {

		TextView view ;

	    public AccelerometerListener (TextView view) {
	        this.view = view ;
	    }

		@Override
		public void onAccuracyChanged (Sensor s, int a) {

		}

		@Override
		public void onSensorChanged (SensorEvent event) {
			synchronized (accelerometerValues) {
				accelerometerValues [0] = event.values [0] ;
				accelerometerValues [1] = event.values [1] ;
				accelerometerValues [2] = event.values [2] ;
			}
			view.setText (accelerometerValues [0] + " " + accelerometerValues [1] + " " + accelerometerValues [2]) ;
		}

	}

	static class GyroscopeListener implements SensorEventListener {

		TextView view ;

	    public GyroscopeListener (TextView view) {
	        this.view = view ;
	    }

		@Override
		public void onAccuracyChanged (Sensor s, int a) {

		}

		@Override
		public void onSensorChanged (SensorEvent event) {
			gyroscopeValues [0] = event.values [0] ;
			gyroscopeValues [1] = event.values [1] ;
			gyroscopeValues [2] = event.values [2] ;
			view.setText (event.values [0] + " " + event.values [1] + " " + event.values [2]) ;
		}

	}

	static abstract class ThreeDOFThread extends Thread {

		protected boolean finished = false ;

		public void finish () {
			finished = true ;
		}

		public void restart() {
			finished = false ;
		}
		
	}

	static class Acquire3DOFThread extends ThreeDOFThread {

		protected float [] values = new float [3] ;
		protected Quat4f initialOrientation = new Quat4f () ;
		protected Quat4f initialOrientationInv = new Quat4f () ;
		protected LinkedList<Quat4f> orientations = new LinkedList<Quat4f> () ;

		@Override
		public void start () {
			super.start () ;
			Quat4f orientationX = new Quat4f () ;
			orientationX.set (new AxisAngle4d (1, 0, 0, Math.toRadians (-orientationValues [1]))) ;
			Quat4f orientationY = new Quat4f () ;
			orientationY.set (new AxisAngle4d (0, 1, 0, Math.toRadians (-orientationValues [0]))) ;
			Quat4f orientationZ = new Quat4f () ;
			orientationZ.set (new AxisAngle4d (0, 0, 1, Math.toRadians (orientationValues [2]))) ;
			initialOrientation.mul (orientationY, orientationX) ;
			initialOrientation.mul (initialOrientation, orientationZ) ;
			initialOrientation.normalize () ;
			initialOrientation.normalize () ;
			initialOrientationInv.inverse (initialOrientation) ;
			initialOrientationInv.normalize () ;
		}
		
		@Override
		public void run () {
			while (! finished) {
				synchronized (orientationValues) {
					double azimuth = Math.toRadians (-orientationValues [0]) ;
					double pitch = Math.toRadians (-orientationValues [1]) ;
					double elevation = Math.toRadians (orientationValues [2]) ;
					Quat4f orientationF = new Quat4f () ;
					Quat4f orientationX = new Quat4f () ;
					//orientationX.set (new AxisAngle4d (1, 0, 0, Math.toRadians (90 -orientationValues [1]))) ;
					orientationX.set (new AxisAngle4d (1, 0, 0, pitch)) ;
					Quat4f orientationY = new Quat4f () ;
					orientationY.set (new AxisAngle4d (0, 1, 0, azimuth)) ;
					Quat4f orientationZ = new Quat4f () ;
					orientationZ.set (new AxisAngle4d (0, 0, 1, elevation)) ;
					orientationF.mul (orientationY, orientationX) ;
					orientationF.mul (orientationF, orientationZ) ;
					orientationF.normalize () ;
					orientations.addFirst (orientationF) ;
					if (orientations.size () > 1) {
						orientations.removeLast () ;
					}
					Quat4f cumulQuat = new Quat4f (orientations.getFirst()) ; 
					for (int i = 1 ; i < orientations.size () ; i++) {
						cumulQuat.interpolate (orientations.get (i), 0.9f * (1 - i * 1.0f / (i + 1))) ;
					}
					cumulQuat.normalize () ;
					actionOrientate (cumulQuat.x, cumulQuat.y, cumulQuat.z, cumulQuat.w) ;
				}
				try {
					sleep (20) ;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	static class Compute3DOFThread extends ThreeDOFThread {

		protected float [] values = new float [3] ;
		protected float [] R = new float [9] ;
		protected Quat4f initialOrientation = new Quat4f () ;
		protected Quat4f initialOrientationInv = new Quat4f () ;
		protected LinkedList<Quat4f> orientations = new LinkedList<Quat4f> () ;

		@Override
		public void start () {
			super.start () ;
			SensorManager.getRotationMatrix (R, null, accelerometerValues, magnetometerValues) ;
			Matrix3f rm3 = new Matrix3f (R) ;
			Matrix4f rm4 = new Matrix4f () ;
			rm4.set (rm3) ;
			initialOrientation = new Quat4f () ;
			rm4.get (initialOrientation) ;
			initialOrientation.normalize () ;
			Quat4f changement = new Quat4f () ;
			changement.set (new AxisAngle4d (1, 0, 0, Math.PI / 2)) ;
			initialOrientation.mul (changement) ;
			initialOrientation.normalize () ;						
			initialOrientationInv.inverse (initialOrientation) ;
			initialOrientationInv.normalize () ;
		}
		
		@Override
		public void run () {
			//float [] badValues = new float [3] ;
			while (! finished) {
				synchronized (accelerometerValues) {
					synchronized (magnetometerValues) {
						SensorManager.getRotationMatrix (R, null, accelerometerValues, magnetometerValues) ;
						//SensorManager.remapCoordinateSystem (R, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, newR);
						Matrix3f rm3 = new Matrix3f (R) ;
						Matrix4f rm4 = new Matrix4f () ;
						rm4.set (rm3) ;
						Quat4f orientationf = new Quat4f () ;
						rm4.get (orientationf) ;
						orientationf.normalize () ;
						Quat4f changement = new Quat4f () ;
						changement.set (new AxisAngle4d (1, 0, 0, Math.PI / 2)) ;
						orientationf.mul (changement) ;
						orientationf.normalize () ;						
						orientationf.mul (initialOrientationInv, orientationf) ;
//						orientationf.mul (orientationf, initialOrientation) ;
						orientationf.normalize () ;
						orientations.addFirst (orientationf) ;
						if (orientations.size () > 1) {
							orientations.removeLast () ;
						}
						Quat4f cumulQuat = new Quat4f (orientations.getFirst ()) ; 
						for (int i = 1 ; i < orientations.size () ; i++) {
							cumulQuat.interpolate (orientations.get (i), 0.9f * (1 - i * 1.0f / (i + 1))) ;
						}
						cumulQuat.normalize () ;
						actionOrientate (cumulQuat.x, cumulQuat.y, cumulQuat.z, cumulQuat.w) ;
					}
				}
				try {
					sleep (20) ;
				} catch (InterruptedException e) {
				}
			}
		}
	}

//	protected static class AcceptThread extends Thread {
//		// The local server socket
//		private BluetoothServerSocket mmServerSocket;
//		private String NAME ;
//		private UUID MY_UUID ;
//		BluetoothSocket socket ;
//		BluetoothAdapter mAdapter ;
//		
//		public AcceptThread() {
//			socket = null;
//			mAdapter = BluetoothAdapter.getDefaultAdapter();
//			NAME = new String ("A700");
//			MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66") ;
//		}
//
//		public void run () {         
//			// Listen to the server socket if we're not connected
//			while (true) {
//				try {
//					// Create a new listening server socket
//					// MY_UUID is the UUID you want to use for communication
//					mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord (NAME, MY_UUID);                    
//					System.out.println(" mmServerSocket.accept () ::::::::::::::::::");
//					//mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);  you can also try using In Secure connection...
//					// This is a blocking call and will only return on a
//					// successful connection or an exception      
//					System.out.println(" mmServerSocket.accept () ?????????????????");
//					socket = mmServerSocket.accept () ;                   
//					System.out.println(" mmServerSocket.accept () !!!!!!!!!!!!!!!!!");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				try {
//					mmServerSocket.close();
//					InputStream tmpIn = null;
//					OutputStream tmpOut = null;
//					// Get the BluetoothSocket input and output streams
//					tmpIn = socket.getInputStream();
//					tmpOut = socket.getOutputStream();
//					DataInputStream mmInStream = new DataInputStream(tmpIn);
//					DataOutputStream mmOutStream = new DataOutputStream(tmpOut); 
//					// here you can use the Input Stream to take the string from the client whoever is connecting
//					//similarly use the output stream to send the data to the client
//				} catch (Exception e) {
//					//catch your exception here
//				}
//
//			}
//		}
//	}
}
