package etma.navigator;

import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import etma.navigator.control.Navigator;
import etma.navigator.control.keyboard.NavigatorBehavior;
import etma.navigator.control.network.PilotageServerSocket;
import etma.navigator.control.wiimote.PilotageWiimoteBluetooth;
import etma.navigator.shape.Cube;
import etma.navigator.shape.ShapeFactory;
import etma.navigator.timeRecorder.Detector;
import etma.navigator.timeRecorder.IntermediateTimeCountDetector;
import etma.navigator.timeRecorder.StartTimeCountDetector;
import etma.navigator.timeRecorder.StopTimeCountDetector;
import etma.navigator.timeRecorder.Supervisor;

public class DemoNavigation extends JFrame {

	/**
    * 
    */
	private static final long serialVersionUID = -7195818365236790571L;
	private VirtualUniverse universe = null;
	private Canvas3D canvas3D = null;
	private TransformGroup viewpointTG = new TransformGroup();
	private Supervisor supervisor;

	public BranchGroup createSceneGraph(Vector3d[] listePositions) {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		TransformGroup virtualBegin = ShapeFactory.createTarget(
				listePositions[0], listePositions[1], new Color3f(0.0f, 0.0f,
						1.0f), new Color3f(1.0f, 0.0f, 0.0f),
				new StartTimeCountDetector(supervisor));
		objRoot.addChild(virtualBegin);
		for (int i = 1; i < listePositions.length - 1; i++) {
			TransformGroup virtualObject = ShapeFactory.createTarget(
					listePositions[i - 1], listePositions[i],
					listePositions[i + 1], new Color3f(0.0f, 0.0f, 1.0f),
					new Color3f(1.0f, 0.0f, 0.0f),
					new IntermediateTimeCountDetector(supervisor));
			objRoot.addChild(virtualObject);
		}
		TransformGroup virtualEnd = ShapeFactory.createTarget(
				listePositions[listePositions.length - 1],
				listePositions[listePositions.length - 2], new Color3f(0.0f,
						0.0f, 1.0f), new Color3f(1.0f, 0.0f, 0.0f),
				new StopTimeCountDetector(supervisor));
		objRoot.addChild(virtualEnd);
		for (int i = 1; i < listePositions.length; i++) {
			TransformGroup virtualObject = ShapeFactory.createLine(
					listePositions[i - 1], listePositions[i], new Color3f(0.0f,
							1.0f, 0.0f));
			objRoot.addChild(virtualObject);
		}

		objRoot.addChild(ShapeFactory.loadFile("data/niveau1.wrl",
				new Vector3d(-2, 0, 0)));
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
		setSize(800, 600);
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

		supervisor = new Supervisor(navigator, listePositions.length - 2);

		// universe.getViewingPlatform ().setNominalViewingTransform () ;
		BranchGroup scene = createSceneGraph(listePositions);
		// enableInteraction (scene) ;
		// compilation de la scène
		scene.compile();

		locale.addBranchGraph(viewingPlatform);
		locale.addBranchGraph(scene);
		PilotageServerSocket pss = new PilotageServerSocket(navigator);
		pss.start();
		PilotageWiimoteBluetooth pwb = new PilotageWiimoteBluetooth(navigator);

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void destroy() {
		universe.removeAllLocales();
	}

	public static void main(String[] args) {
		new DemoNavigation();
	}
}
