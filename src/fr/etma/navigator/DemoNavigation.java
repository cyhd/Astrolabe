package fr.etma.navigator;

import java.awt.GraphicsConfiguration;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import fr.etma.navigator.control.Navigator;
import fr.etma.navigator.control.keyboard.NavigatorBehavior;
import fr.etma.navigator.control.network.PilotageServerSocket;
import fr.etma.navigator.control.wiimote.PilotageWiimoteVRPN;
import fr.etma.navigator.control.wiimote.PilotageWiimoteWiiuseJ;
import fr.etma.navigator.shape.ShapeFactory;
import fr.etma.navigator.shape.TargetShape;
import fr.etma.navigator.shape.TubeShape;
import fr.etma.navigator.timeRecorder.Detector;
import fr.etma.navigator.timeRecorder.IntermediateTimeCountDetector;
import fr.etma.navigator.timeRecorder.Measurer;
import fr.etma.navigator.timeRecorder.StartTimeCountDetector;
import fr.etma.navigator.timeRecorder.StopTimeCountDetector;
import fr.etma.navigator.timeRecorder.Supervisor;

public class DemoNavigation extends JFrame implements WindowListener {

	/**
    * 
    */
	private static final long serialVersionUID = -7195818365236790571L;
	private VirtualUniverse universe = null;
	private Canvas3D canvas3D = null;
	private TransformGroup viewpointTG = new TransformGroup();
	private Supervisor supervisor;
	private TubeShape[] tubeShapes;
	private PilotageWiimoteWiiuseJ pwb; 

	public BranchGroup createSceneGraph(Vector3d[] listePositions) {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		Detector detector = new StartTimeCountDetector(supervisor);
		TargetShape virtualBegin = new TargetShape( 0,
				listePositions[0], listePositions[1], new Color3f(0.0f, 0.0f,
						1.0f), new Color3f(1.0f, 0.0f, 0.0f), detector);
		detector.add(virtualBegin);
		objRoot.addChild(virtualBegin);
		
		for (int i = 1; i < listePositions.length - 1; i++) {
			detector = new IntermediateTimeCountDetector(supervisor);
			TargetShape virtualObject = new TargetShape( i,
					listePositions[i - 1], listePositions[i],
					listePositions[i + 1], new Color3f(0.0f, 0.0f, 1.0f),
					new Color3f(1.0f, 0.0f, 0.0f),
					detector);
			detector.add(virtualObject);
			objRoot.addChild(virtualObject);
		}
		
		detector = new StopTimeCountDetector(supervisor);
		TargetShape virtualEnd = new TargetShape( listePositions.length - 1,
				listePositions[listePositions.length - 1],
				listePositions[listePositions.length - 2], new Color3f(0.0f,
						0.0f, 1.0f), new Color3f(1.0f, 0.0f, 0.0f),
				detector);
		detector.add(virtualEnd);
		objRoot.addChild(virtualEnd);
		
		tubeShapes = new TubeShape[listePositions.length-1];
		for (int i = 1; i < listePositions.length; i++) {
			TubeShape virtualObject = new TubeShape(i,
					listePositions[i - 1], listePositions[i], new Color3f(0.0f,
							1.0f, 0.0f));
			tubeShapes [i-1] = virtualObject;
			objRoot.addChild(virtualObject);
		}

		//objRoot.addChild(ShapeFactory.loadFile("data/niveau1.wrl",
		//		new Vector3d(-2, 0, 0)));
		// objRoot.addChild (loadFile ("data/niveau2.wrl", new Vector3d (-2, 0,
		// 0))) ;
		// objRoot.addChild (loadFile ("data/niveau0_plane.wrl", new Vector3d
		// (-2, 0, 0))) ;
		// objRoot.addChild (loadFile ("data/niveau0_plane1.wrl", new Vector3d
		// (-2, 0, 0))) ;
		// objRoot.addChild (loadFile ("data/niveau1_plane2.wrl", new Vector3d
		// (-2, 0, 0))) ;
		// objRoot.addChild (loadFile ("data/niveau1_plane3.wrl", new Vector3d
		// (-2, 0, 0))) ;
		// objRoot.addChild (loadFile ("colorcube3.wrl", new Vector3d (0, 0,
		// 0))) ;

		// add virtual objects
		// TransformGroup virtualObject ;
		// int dx = 10 ;
		// int dy = 10 ;
		// int dz = 10 ;
		// int nx = 20 ;
		// int ny = 20 ;
		// int nz = 20 ;
		// for (int i = -nx ; i < nx ; i++) {
		// for (int j = -ny ; j < ny ; j++) {
		// for (int k = -nz ; k < nz ; k++) {
		// virtualObject = createCube (new Vector3d (i * dx, j * dy, k * dz),
		// new Color3f (i * 0.6f / dx, j * 0.6f / dy, k * 0.6f / dz),
		// new Color3f (i * 1.0f / dx, j * 1.0f / dy, k * 1.0f / dz)) ;
		// objRoot.addChild (virtualObject) ;
		// }
		// }
		// }
		return objRoot;
	}

	
	public void enableInteraction(BranchGroup objRoot) {
		BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100);
		PickRotateBehavior prb = new PickRotateBehavior(objRoot, canvas3D,
				bounds);
		prb.setMode(PickTool.GEOMETRY);
		prb.setTolerance(0.0f);
		objRoot.addChild(prb);
		PickTranslateBehavior ptb = new PickTranslateBehavior(objRoot,
				canvas3D, bounds);
		ptb.setMode(PickTool.GEOMETRY);
		ptb.setTolerance(0.0f);
		objRoot.addChild(ptb);
		PickZoomBehavior pzb = new PickZoomBehavior(objRoot, canvas3D, bounds);
		pzb.setMode(PickTool.GEOMETRY);
		pzb.setTolerance(0.0f);
		objRoot.addChild(pzb);
	}

	public DemoNavigation() {
		this.setSize(800, 600);
		this.setLocation(410, 0);
		this.setTitle("Navigation 3D");
		this.addWindowListener(this);
		
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		// c.setStereoEnable (true) ;
		// System.out.println (c.getGraphicsContext3D ().getStereoMode ()) ;
		getContentPane().add(canvas3D);

		universe = new VirtualUniverse();
		javax.media.j3d.Locale locale = new javax.media.j3d.Locale(universe);
		// création du ViewPlatform
		ViewPlatform viewPlatform = new ViewPlatform();
		// devrait être inutile ...
		// viewPlatform.setViewAttachPolicy (View.NOMINAL_HEAD) ;
		// viewPlatform.setActivationRadius (1000.0f) ;
		// création du PhysicalBody
		PhysicalBody physicalBody = new PhysicalBody();
		// création du PhysicalEnvironment
		PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment();
		// création du View
		View view = new View();
		view.addCanvas3D(canvas3D);
		view.setPhysicalBody(physicalBody);
		view.setPhysicalEnvironment(physicalEnvironment);
		view.attachViewPlatform(viewPlatform);
		view.setBackClipDistance(1000);
		view.setFrontClipDistance(0.001);
		// création du ViewingPlatform
		ViewingPlatform viewingPlatform = new ViewingPlatform();
		viewingPlatform.setViewPlatform(viewPlatform);
		viewpointTG = viewingPlatform.getViewPlatformTransform();
		viewpointTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		viewpointTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		// This will move the ViewPlatform back a bit so the objects in the
		// scene can be viewed.
		// viewingPlatform.setNominalViewingTransform () ;
		// KeyNavigatorBehavior knb = new KeyNavigatorBehavior (canvas3D,
		// viewpointTG) ;
		// knb.setSchedulingBounds (new BoundingSphere (new Point3d (), 1.0)) ;

		Navigator navigator = new Navigator(viewpointTG);

		NavigatorBehavior nb = new NavigatorBehavior(navigator);
		nb.setSchedulingBounds(new BoundingSphere(new Point3d(), 1.0));
		viewpointTG.addChild(nb);
		viewpointTG.addChild(ShapeFactory.createSmallBox());

		PointLight light = new PointLight(new Color3f(1.0f, 1.0f, 1.0f),
				new Point3f(0.0f, 0.0f, 0.0f), new Point3f(1.0f, 0.0f, 0.0f));
		light.setColor(new Color3f(1.0f, 1.0f, 1.0f));
		light.setBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
		light.setEnable(true);
		light.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 10));
		viewpointTG.addChild(light);
		viewingPlatform.compile();

		// position des etapes du parcours
		Vector3d[] listePositions = { new Vector3d(0, 0, -4),
				new Vector3d(0, 0, -10), new Vector3d(-20, -10, -40),
				new Vector3d(-10, 0, -40), new Vector3d(0, -10, -30),
				new Vector3d(10, 0, -20), new Vector3d(20, 0, -20),
				new Vector3d(30, 10, -10), new Vector3d(40, 10, 0) };
		
		// universe.getViewingPlatform ().setNominalViewingTransform () ;
		Measurer measurer = new Measurer(navigator);
		supervisor = new Supervisor(measurer, listePositions.length - 2);
		BranchGroup scene = createSceneGraph(listePositions);
		measurer.setTubeShapes(tubeShapes);
		
		// enableInteraction (scene) ;
		// compilation de la scène
		scene.compile();

		locale.addBranchGraph(viewingPlatform);
		locale.addBranchGraph(scene);
		PilotageServerSocket pss = new PilotageServerSocket(navigator);
		pss.start();
		pwb = new PilotageWiimoteWiiuseJ(navigator);

		setVisible(true);
		
	}

	public void destroy() {
		universe.removeAllLocales();
	}

	public static void main(String[] args) {
		new DemoNavigation();
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		
		pwb.disconnect();
		
		pwb.dispose();
		this.dispose();
		System.exit(444);		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
