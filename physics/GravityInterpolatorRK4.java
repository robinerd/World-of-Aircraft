package physics;

import physics.entities.RigidBody;

/**
 *
 * @author Robinerd
 */
public class GravityInterpolatorRK4 extends InterpolatorRK4{

	private RigidBody obj;


	public GravityInterpolatorRK4(RigidBody body) {
		this.obj = body;
	}

	@Override
	protected Vector[] derivatives0(State[] state, double time) {
		return createVectorArray(
				state[0].values[1], //hastighet
				state[1].values[1]); //vinkelhastighet
	}

	@Override
	protected Vector[] derivatives1(State[] state, double time) {
		return createVectorArray(
				new Vector(0, obj.gravity), //acceleration
				new Vector(0,0)); //vinkelacceleration
	}

	public void update(double dt) {
		State[] states = new State[2];
		states[0] = new State();
		states[1] = new State();
		//använd värde 0 för position, värde 1 för hastighet
		states[0].setValues(new Vector[]{obj.pos, obj.vel});
		//använd värde 0 för position, värde 1 för hastighet
		states[1].setValues(new Vector[]{
			new Vector(obj.rotation, 0), //endast första komponenten används
			new Vector(obj.angularVel, 0)}); //endast första komponenten används

		integrate(states, dt);

		obj.pos = states[0].values[0];
		obj.vel = states[0].values[1];
		obj.rotation = states[1].values[0].x;
		obj.angularVel = states[1].values[1].x;
	}

}
