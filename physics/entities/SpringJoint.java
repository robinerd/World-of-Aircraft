/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package physics.entities;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import physics.InterpolatorRK4;
import physics.Vector;

/**
 * SpringJoint är en fjädrande koppling som håller samman två RigidBody-objekt relativt varandra, i en given kopplingspunkt.
 * Denna klass är abstrakt; det är upp till underklasser att avgöra fjäderkonstanten och dämpningskoefficienten.
 * SpringJoint är en grafisk komponent och innehåller standard-grafik. 
 * Underklasser kan overrida getJointImage() och getBrokenJointImage() om annan grafik önskas.
 * @author Robin Lindh Nilsson
 */
public abstract class SpringJoint extends JPanel implements Joint {

	public static Image JOINT_IMAGE, BROKEN_JOINT_IMAGE;

	static {
		try {
			JOINT_IMAGE = Image.loadImageWhiteMask("img/joint.png");
			BROKEN_JOINT_IMAGE = Image.loadImageWhiteMask("img/broken_joint.png");
		} catch (Exception ex) {
			System.err.println("Could not load joint image\n");
			ex.printStackTrace();
		}
	}
	protected RigidBody body1, body2; //de två objekten som fästs ihop
	protected Vector radiusVector1, radiusVector2; //fästpunkterna relativt respektive center of mass
	protected Vector attachPoint1, attachPoint2; //"fjäderns" fästpunkter i världskoordinater
	protected SpringInterpolatorRK4 interpolator;
	private Image currentImage;
	private boolean broken = false;

	public SpringJoint() {
		super();
		interpolator = new SpringInterpolatorRK4();

		setOpaque(false);
	}

	public void attach(RigidBody body1, RigidBody body2, Vector attachPointWorld) {
		this.body1 = body1;
		this.body2 = body2;
		currentImage = getJointImage();

		if (body1 != null && body2 != null) {
			radiusVector1 = Vector.difference(attachPointWorld, body1.pos);
			radiusVector2 = Vector.difference(attachPointWorld, body2.pos);
		} else {
			destroy();
		}
	}

	public void update(double dt) {

		if (!isBroken()) {
			interpolator.update(dt);
		} else {
			updateAttachmentPoints(body1.rotation, body2.rotation, body1.pos, body2.pos);
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//rita ut bilden för jointen centrerat på båda jointens ändpunkter
		currentImage.drawCenteredAt(attachPoint1.toPoint(), g);
		currentImage.drawCenteredAt(attachPoint2.toPoint(), g);

		//rita en blå linje mellan fästpunkterna om jointen inte är trasig
		if (!isBroken()) {
			g.setColor(Color.cyan);
			g.drawLine(
					(int) attachPoint1.x, (int) attachPoint1.y,
					(int) attachPoint2.x, (int) attachPoint2.y);
		}

	}

	protected void updateAttachmentPoints(double rotation1, double rotation2, Vector centerMass1, Vector centerMass2) {
		//beräkna fästpunkterna i världskoordinater med hänsyn till rotation
		double sin = (double) Math.sin(rotation1);
		double cos = (double) Math.cos(rotation1);
		Vector posWithRotation1 = new Vector(cos * radiusVector1.x + sin * radiusVector1.y, -sin * radiusVector1.x + cos * radiusVector1.y);

		sin = (double) Math.sin(rotation2);
		cos = (double) Math.cos(rotation2);
		Vector posWithRotation2 = new Vector(cos * radiusVector2.x + sin * radiusVector2.y, -sin * radiusVector2.x + cos * radiusVector2.y);

		attachPoint1 = Vector.sum(centerMass1, posWithRotation1);
		attachPoint2 = Vector.sum(centerMass2, posWithRotation2);
	}

	protected abstract double getSpringCoefficient();

	protected abstract double getDampingCoefficient();

	protected abstract double getStretchLimit();

	public void destroy() {
		broken = true;
		currentImage = getBrokenJointImage();
	}

	public boolean isBroken() {
		return broken;
	}

	public Image getJointImage() {
		return JOINT_IMAGE;
	}

	public Image getBrokenJointImage() {
		return BROKEN_JOINT_IMAGE;
	}


	//==========================================================================


	protected class SpringInterpolatorRK4 extends InterpolatorRK4 {

		protected void update(double dt) {
			State[] state = new State[4];

			//Linjär rörelse
			//värde 0 för position, värde 1 för hastighet, värde 2 används ej
			state[0] = new State(); //linjär rörelse för body1
			state[1] = new State(); //linjär rörelse för body2
			state[0].setValues(new Vector[]{body1.pos, body1.vel});
			state[1].setValues(new Vector[]{body2.pos, body2.vel});

			//vinkelrörelse
			//värde 0 x-komposant för rotation, värde 1 x-komposant för vinkelhastighet
			state[2] = new State(); //vinkelrörelse för body1
			state[3] = new State(); //vinkelrörelse för body2
			state[2].setValues(new Vector[]{new Vector(body1.rotation, 0), new Vector(body1.angularVel, 0)});
			state[3].setValues(new Vector[]{new Vector(body2.rotation, 0), new Vector(body2.angularVel, 0)});

			integrate(state, dt);

			//Uppdatera ej positionen, objektet förflyttas i GravityInterpolator
			body1.vel = state[0].values[1];
			//body1.rotation = state[2].values[0].x;
			body1.angularVel = state[2].values[1].x;

			body2.vel = state[1].values[1];
			//body2.rotation = state[3].values[0].x;
			body2.angularVel = state[3].values[1].x;
		}

		@Override
		protected Vector[] derivatives0(State[] states, double time) {
			return createVectorArray(
					states[0].values[1],
					states[1].values[1],
					states[2].values[1],
					states[3].values[1]); //returnera hastigheterna för båda objekt
		}

		@Override
		protected Vector[] derivatives1(State[] states, double time) {
			//namnge state-data mer pedagogiskt
			Vector centerMass1 = states[0].values[0];
			Vector centerMass2 = states[1].values[0];
			Vector velocity1 = states[0].values[1];
			Vector velocity2 = states[1].values[1];
			double rotation1 = states[2].values[0].x;
			double rotation2 = states[3].values[0].x;
			double angularVel1 = states[2].values[1].x;
			double angularVel2 = states[3].values[1].x;

			updateAttachmentPoints(rotation1, rotation2, centerMass1, centerMass2);
			Vector deltaPoint = Vector.difference(attachPoint1, attachPoint2);

			//kolla om fjädern går sönder
			if (deltaPoint.length() > getStretchLimit()) {
				destroy();
				return createZeroVectorArray();
			}

			Vector pointVelocity1 = RigidBody.calcPointVelocity(attachPoint1, centerMass1, velocity1, angularVel1);
			Vector pointVelocity2 = RigidBody.calcPointVelocity(attachPoint2, centerMass2, velocity2, angularVel2);

			//delta hastighet
			Vector deltaVelocity = Vector.difference(pointVelocity1, pointVelocity2);

			//kraften för objekt 2, riktad mot objekt 1. För objekt 1 negeras kraften
			//F = springCoef * deltaX + dampingCoef * deltaV
			Vector springForce = deltaPoint.multiply(getSpringCoefficient());
			Vector dampingForce = deltaVelocity.multiply(getDampingCoefficient());
			Vector force2 = springForce.add(dampingForce);
			Vector force1 = new Vector(force2).negate();

			//F = m a
			Vector acceleration1 = new Vector(force1).divide(body1.getMass());
			Vector acceleration2 = new Vector(force2).divide(body2.getMass());

			double angularAcceleration1 = calcAngularAcceleration(force1, attachPoint1, body1);
			double angularAcceleration2 = calcAngularAcceleration(force2, attachPoint2, body2);

			return createVectorArray(
					acceleration1,
					acceleration2,
					new Vector(angularAcceleration1, 0), //endast x-komposanten används
					new Vector(angularAcceleration2, 0)); //endast x-komposanten används
		}

		protected double calcAngularAcceleration(Vector force, Vector attachPoint, RigidBody body) {

			//rotation axis
			Vector r1 = Vector.difference(attachPoint, body.pos);

			//calculate torque (tao = r x (m*a) )
			double torque = Vector.cross(r1, force);

			//apply angular acceleration (tao = I * alpha)
			double angularAcceleration = torque / body.getMomentInertia();

			return angularAcceleration;
		}
	}
}
