package fr.etma.navigator.shape;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

public class TubeShape extends TransformGroup {

	private int id;
	float diameter = 2.0f;
	Vector3d x1, x2;  //cylinder extremity points

	public TubeShape(int id, Vector3d from, Vector3d to, Color3f color) {

		this.id = id;
		this.x1 = from;
		this.x2 = to;
		
		Transform3D translation = new Transform3D();
		Vector3d middle = new Vector3d(from);
		middle.add(to);
		middle.set(middle.getX() / 2, middle.getY() / 2, middle.getZ() / 2);
		Vector3d y = new Vector3d(0, 1, 0);
		Vector3d length = new Vector3d();
		length.sub(to, from);
		Vector3d rotationAxis = new Vector3d();
		rotationAxis.cross(y, length);
		double angle = y.angle(length);
		Quat4d rotation = new Quat4d();
		rotation.set(new AxisAngle4d(rotationAxis.getX(), rotationAxis.getY(),
				rotationAxis.getZ(), angle));
		translation.setRotation(rotation);
		translation.setTranslation(middle);
		// translation.setScale (new Vector3d (1, length.length (), 1)) ;

		this.setTransform(translation);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		// objTrans.setCapability (TransformGroup.ENABLE_PICK_REPORTING) ;
		Cylinder cylinder = new Cylinder(diameter, (float) length.length());
		// cylinder.getShape (Cylinder.BODY).getGeometry ().setCapability
		// (Geometry.ALLOW_INTERSECT) ;
		cylinder.getShape(Cylinder.BOTTOM).removeAllGeometries();
		cylinder.getShape(Cylinder.TOP).removeAllGeometries();
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(new Color3f(0.8f, 0.8f, 0.8f));
		ColoringAttributes ca2 = new ColoringAttributes();
		ca2.setColor(new Color3f(1.0f, 0.50f, 0.0f));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setColoringAttributes(ca);
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparency(0.80f);
		ta.setTransparencyMode(TransparencyAttributes.FASTEST);
		app.setTransparencyAttributes(ta);
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		cylinder.setAppearance(app);
		this.addChild(cylinder);
		Appearance app2 = new Appearance();
		app2.setColoringAttributes(ca2);
		app2.setTransparencyAttributes(ta);
		app2.setPolygonAttributes(pa);
		Box box = new Box(0.025f, (float) length.length() / 2.0f - 0.6f,
				0.025f, app2);
		this.addChild(box);

	}

	public double distanceToPoint(Vector3d x0) {
		//capped cylinder centered at the origin with its central axis aligned along the Y-axis.
		Vector3d xx1 = new Vector3d(x1);
		Vector3d xx2 = new Vector3d(x2);
		Vector3d x = new Vector3d();
		xx2.sub(x1); // xx2 = x2 - x1;
		xx1.sub(x0); // xx1 = x1 - x0
		x.cross(xx2, xx1); // x = (x2-x1) x (x1-x0)
		double distance = x.length() / xx2.length();
		
		return distance - diameter;
	}
	
	public boolean isInside(Vector3d x0) {
		if (distanceToPoint(x0) > 0)
			return true;
		else
			return false;
	}
}
