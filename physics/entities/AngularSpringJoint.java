
package physics.entities;

import physics.Vector;

/**
 *
 * @author Robinerd
 */
public class AngularSpringJoint extends StiffSpringJoint{


	private double targetAngle;

	@Override
	public void attach(RigidBody body1, RigidBody body2, Vector attachPointWorld) {
		super.attach(body1, body2, attachPointWorld);
		this.targetAngle = body1.rotation - body2.rotation;
		interpolator = new AngularSpringInterpolatorRK4();
	}

	@Override
	public void update(double dt) {
		super.update(dt);
	}

	protected double getAngularSpringCoefficient() {
		return 4*10E8;
	}

	protected double getAngularDampingCoefficient() {
		return 5*10E2;
	}

	/**
	 * @return maximal vinkel kopplingen kan vridas utan att brista, i radianer
	 */
	protected double getAngularStretchLimit() {
		return Math.PI / 8;
	}

	//==========================================================================

	protected double calcRelativeAngle (double angle1, double angle2) {
		double angle = angle1 - angle2;
		while(angle < -Math.PI - 0.000001) {
			angle += 2*Math.PI;
		}
		while(angle > Math.PI + 0.000001) {
			angle -= 2*Math.PI;
		}
		return angle;
	}

	protected class AngularSpringInterpolatorRK4 extends SpringInterpolatorRK4{

		@Override
		protected Vector[] derivatives1(State[] states, double time) {
			Vector[] derivatives = super.derivatives1(states, time);
			
			//beräkna hur stor vinkeln fjädern är stretchad med
			double angleDisplacement = targetAngle -
					calcRelativeAngle(states[2].values[0].x, states[3].values[0].x);

			if(Math.abs(angleDisplacement) > getAngularStretchLimit()) {
				destroy();
				return createZeroVectorArray();
			}

			double relativeAngularVelocity = states[2].values[1].x - states[3].values[1].x;
			double springTorque = angleDisplacement * getAngularSpringCoefficient();
			double dampingTorque = -relativeAngularVelocity * getAngularDampingCoefficient();
			double torque = springTorque - dampingTorque;

			//räkna ut andra värden på vinkelacceleration, ignorera värdena från super
			//vinkelacceleration för body1
			derivatives[2].add(new Vector(torque / body1.getMomentInertia(), 0)); //y-komposanten används ej
			//vinkelacceleration för body2
			derivatives[3].add(new Vector(-torque / body2.getMomentInertia(), 0)); //y-komposanten används ej

			return derivatives;
		}

	}
}
