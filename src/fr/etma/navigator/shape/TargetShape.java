package fr.etma.navigator.shape;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;

import fr.etma.navigator.CollisionDetector;
import fr.etma.navigator.timeRecorder.Detector;

public class TargetShape extends TransformGroup {

	Transform3D translation = new Transform3D();
	Color3f collisionColor;
	Appearance shapeAppearance;
	private int id;

	public TargetShape(int id, Vector3d v3d, Vector3d nextV3d, Color3f color,
			Color3f collisionColor, Detector detector) {
		
		super();

		this.id = id;
		
		translation.setRotation(getRotation1(v3d, nextV3d));

		translation.setTranslation(v3d);
		translation.setScale(new Vector3d(1.3, 0.1, 1.3));

		this.setTransform(translation);

		init(color, collisionColor, detector);
	}

	public TargetShape(int id, Vector3d previousV3d, Vector3d v3d, Vector3d nextV3d,
			Color3f color, Color3f collisionColor, Detector detector) {

		this.id=id;
		
		Quat4d rotation1 = getRotation1(v3d, nextV3d);

		Quat4d rotation2 = getRotation2(v3d, previousV3d);
		Quat4d rotation = new Quat4d(rotation1);
		rotation.interpolate(rotation2, 0.5);

		translation.setRotation(rotation);

		translation.setTranslation(v3d);
		translation.setScale(new Vector3d(1, 0.05, 1));
		
		this.setTransform(translation);

		init(color, collisionColor, detector);
		
		
	}

	private Quat4d getRotation1(Vector3d v3d, Vector3d nextV3d) {

		Vector3d y = new Vector3d(0, 1, 0);
		Vector3d length = new Vector3d();
		length.sub(nextV3d, v3d);
		Vector3d rotationAxis = new Vector3d();
		rotationAxis.cross(y, length);
		Quat4d rotation = new Quat4d();
		rotation.set(new AxisAngle4d(rotationAxis.getX(), rotationAxis.getY(),
				rotationAxis.getZ(), y.angle(length)));
		return rotation;
	}

	private Quat4d getRotation2(Vector3d v3d, Vector3d previousV3d) {

		Vector3d y = new Vector3d(0, 1, 0);

		Vector3d length = new Vector3d();
		length.sub(v3d, previousV3d);
		Vector3d rotationAxis = new Vector3d();
		rotationAxis.cross(y, length);
		Quat4d rotation2 = new Quat4d();
		rotation2.set(new AxisAngle4d(rotationAxis.getX(), rotationAxis.getY(),
				rotationAxis.getZ(), y.angle(length)));

		return rotation2;
	}

	private void init(Color3f color, Color3f collisionColor, Detector detector) {

		this.collisionColor = collisionColor;
		
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.setCapability(Node.ENABLE_PICK_REPORTING);
		Cube box = new Cube();
		box.getGeometry().setCapability(Geometry.ALLOW_INTERSECT);
		shapeAppearance = new Appearance();
		box.setAppearance(shapeAppearance);
		this.addChild(box);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(color);
		shapeAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		shapeAppearance.setColoringAttributes(ca);
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparency(0.5f);
		ta.setTransparencyMode(TransparencyAttributes.FASTEST);
		shapeAppearance.setTransparencyAttributes(ta);
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		shapeAppearance.setPolygonAttributes(pa);
		CollisionDetector cd = new CollisionDetector(box, collisionColor, detector);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1.0);
		cd.setSchedulingBounds(bounds);
		Appearance smallApp = new Appearance();
		ColoringAttributes smallca = new ColoringAttributes();
		smallca.setColor(collisionColor);
		Box smallBox = new Box(0.2f, 0.1f, 0.2f, smallApp);
		smallApp.setColoringAttributes(smallca);
		this.addChild(smallBox);
		// Add the behavior to the scene graph
		this.addChild(cd);
	}

	public void select() {
	    ColoringAttributes highlight = new ColoringAttributes (collisionColor, ColoringAttributes.SHADE_GOURAUD);
	    //ColoringAttributes highlight = new ColoringAttributes (new Color3f(0,1,0), ColoringAttributes.SHADE_GOURAUD) ;
		//ColoringAttributes shapeColoring = shapeAppearance.getColoringAttributes () ;
		shapeAppearance.setColoringAttributes (highlight) ;
		
	}

	public int getId() {
		return id;
	}
}
