package fr.etma.navigator.control;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

/**
 * Gestion du point de vue (camera)
 * 
 * @author Thierry Duval
 *
 */
public class Navigator {

	protected TransformGroup viewpointTG;
	protected Transform3D viewpointTransform = new Transform3D();
	protected Transform3D headTransform = new Transform3D();
	protected Transform3D supportTransform = new Transform3D();

	public Quat4d getHeadRotationInSupportFrame() {
		Quat4d r = new Quat4d();
		headTransform.get(r);
		return r;
	}

	public Quat4d getHeadOrientationInSupportFrame() {
		Quat4d r = new Quat4d();
		headTransform.get(r);
		return r;
	}

	public Quat4d getHeadOrientationInGlobalFrame() {
		Quat4d r = new Quat4d();
		viewpointTransform.get(r);
		return r;
	}

	public Quat4d getSupportOrientationInGlobalFrame() {
		Quat4d r = new Quat4d();
		supportTransform.get(r);
		return r;
	}

	public Vector3d getSupportPositionInGlobalFrame() {
		Vector3d p = new Vector3d();
		supportTransform.get(p);
		return p;
	}

	public Vector3d getHeadPositionInGlobalFrame() {
		Vector3d p = new Vector3d();
		viewpointTransform.get(p);
		return p;
	}

	public Vector3d getHeadPositionInSupportFrame() {
		Vector3d p = new Vector3d();
		headTransform.get(p);
		return p;
	}

	public Navigator(TransformGroup viewpointTG) {
		this.viewpointTG = viewpointTG;
		viewpointTG.getTransform(supportTransform);
	}

	protected void cumulateTransforms() {
		viewpointTransform.mul(supportTransform, headTransform);
		viewpointTG.setTransform(viewpointTransform);
	}

	/**
	 * changements absolus de points de vue pour la tête et le support
	 */
	public void goThereAndLookThatWay(double x, double y, double z, double qx,
			double qy, double qz, double qw) {
		Quat4d orientation = new Quat4d(qx, qy, qz, qw);
		orientation.normalize();
		headTransform.setRotation(orientation);
		headTransform.setTranslation(new Vector3d());
		Quat4d nullOrientation = new Quat4d();
		nullOrientation.set(new AxisAngle4d(0, 1, 0, 0));
		supportTransform.setRotation(nullOrientation);
		supportTransform.setTranslation(new Vector3d(x, y, z));
		cumulateTransforms();
	}

	/**
	 * changements absolus de points de vue : pour le support
	 */
	public void setSupportPositionInGlobalFrame(double x, double y, double z) {
		Vector3d position = new Vector3d(x, y, z);
		supportTransform.setTranslation(position);
		cumulateTransforms();
	}

	/**
	 * changements absolus de points de vue : pour la tête
	 */
	public void setHeadOrientationInSupportFrame(double qx, double qy,
			double qz, double qw) {
		Vector3d position = new Vector3d();
		headTransform.get(position);
		Quat4d orientation = new Quat4d(qx, qy, qz, qw);
		orientation.normalize();
		headTransform.setRotation(orientation);
		headTransform.setTranslation(position);
		cumulateTransforms();
	}

	// changements relatifs de points de vue : pour le support

	/**
	 * méthode appelée par la présentation : on fait un calcul et on envoie
	 * au point de vue associé
	 */
	public void supportTranslateInHeadFrame(double dx, double dy, double dz) {
		Transform3D localT3D = new Transform3D();
		localT3D.set(new Vector3d(dx, dy, dz));
		viewpointTransform.mul(localT3D);
		Vector3d newPosition = new Vector3d();
		viewpointTransform.get(newPosition);
		supportTransform.setTranslation(newPosition);
		cumulateTransforms();
	}

	/**
	 * méthode appelée par la présentation : on fait un calcul et on envoie
	 * au point de vue associé
	 */
	public void supportRotateInHeadFrame(double dqx, double dqy, double dqz,
			double dqw) {
		Transform3D localT3D = new Transform3D();
		Quat4d newRotation = new Quat4d(dqx, dqy, dqz, dqw);
		newRotation.normalize();
		localT3D.set(newRotation);
		viewpointTransform.mul(localT3D);
		Transform3D headTransformInv = new Transform3D(headTransform);
		headTransformInv.invert();
		supportTransform.mul(viewpointTransform, headTransformInv);
		cumulateTransforms();
	}

}
