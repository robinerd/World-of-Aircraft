package physics;

/**
 *
 * This is my Runge Kutta order 4 interpolator. It is currently limited to
 * interpolating up to the 2nd derivative of an attribute. The interpolator has 
 * a local State class, which holds an array of three variables. If we interpolate
 * position-velocity-acceleration in an example where we have linear increasing acceleration
 * variable 0 stores position, variable 1 stores velocity, and variable 2 stores acceleration.
 * If we were to handle this example case, we would make a sub-class to this class.
 * we would first have to override the derivative methods.
 * 
 * derivative0 would return the derivative of position, which is velocity
 * return state.values[1]; since the value 1 stores velocity
 * 
 * derivative1 would return the derivative of velocity, which is acceleration
 * return state.values[2];
 * 
 * derivative2 should return the change of acceleration, which is in this case linear to time
 * return 5*time;
 * 
 * We would then execute integrate each frame of the simulation, from some update function for example.
 * Assume we have some physics object called obj.
 * The code in our interpolator subclass could look like this:
 *
 * State state = new State();
 * state.values = new Vector[]{obj.pos, obj.vel, obj.acc};
 * integrate(state, dt);
 * obj.pos = state.values[0];
 * obj.vel = state.values[1];
 * obj.acc = state.values[2];
 *
 * In this scenario we would create one pos-vel-acc interpolator for each physics object.
 * @author Robin Lindh Nilsson
 */
public abstract class InterpolatorRK4 {

	private final static int nValues = 10;
	private double t = 0;

	final protected Derivative[] evaluate(State[] states, double t, double dt, Derivative[] derivatives) {
		int length = states.length;

		State[] newStates = new State[length];
		for(int i=0; i<length; i++) {
			newStates[i] = new State();
		}
		
		//loop through all states and values, calculating new values from derivatives
		for(int i=0; i<length; i++) {
			for(int j=0; j<states[i].values.length; j++) {
				newStates[i].values[j] = Vector.sum(states[i].values[j], new Vector(derivatives[i].values[j]).multiply(dt));
			}
		}

		//calculate the new derivatives by deriving the states
		Vector[] derivatives0 = derivatives0(newStates, t+dt);
		Vector[] derivatives1 = derivatives1(newStates, t+dt);
		Vector[] derivatives2 = derivatives2(newStates, t+dt);


		Derivative[] newDerivatives = new Derivative[length];
		for(int i=0; i<length; i++) {
			newDerivatives[i] = new Derivative();
			newDerivatives[i].values[0] = derivatives0[i];
			newDerivatives[i].values[1] = derivatives1[i];
			newDerivatives[i].values[2] = derivatives2[i];
		}
		return newDerivatives;
	}

	final protected void integrate(State[] states, double dt) {
		Derivative[] initDerivatives = new Derivative[states.length];
		for(int i=0; i<initDerivatives.length; i++) {
			initDerivatives[i] = new Derivative();
		}

		Derivative[] aDerivatives = evaluate(states, t, 0.0f, initDerivatives);
		Derivative[] bDerivatives = evaluate(states, t + dt * 0.5f, dt * 0.5f, aDerivatives);
		Derivative[] cDerivatives = evaluate(states, t + dt * 0.5f, dt * 0.5f, bDerivatives);
		Derivative[] dDerivatives = evaluate(states, t + dt, dt, cDerivatives);

		for(int i=0; i<aDerivatives.length; i++) {
			Vector[] a = aDerivatives[i].values;
			Vector[] b = bDerivatives[i].values;
			Vector[] c = cDerivatives[i].values;
			Vector[] d = dDerivatives[i].values;
			Vector[] stateValues = states[i].values;

			for(int j=0; j<a.length; j++) {
				//dxdt = (a + 2*(b + c) + d) / 6.0;
				Vector dxdt = Vector.sum(b[j], c[j]).multiply(2).add(a[j]).add(d[j]).divide(6);
				stateValues[j] = Vector.sum(stateValues[j], dxdt.multiply(dt));
			}
		}
		t += dt;
	}

	protected Vector[] derivatives0(State[] state, double time) {
		return createZeroVectorArray();
	}
	protected Vector[] derivatives1(State[] state, double time) {
		return createZeroVectorArray();
	}
	protected Vector[] derivatives2(State[] state, double time) {
		return createZeroVectorArray();
	}

	final protected Vector[] createZeroVectorArray() {
		Vector[] returnValues = new Vector[nValues];
		for(int i=0; i<returnValues.length; i++) {
			returnValues[i] = new Vector(0,0);
		}
		return returnValues;
	}

	final protected Vector[] createVectorArray(Vector... vectors) {
		Vector[] vectorArray = createZeroVectorArray();
		System.arraycopy(vectors, 0, vectorArray, 0, vectors.length);
		return vectorArray;
	}

	protected static class State {
		public Vector[] values;
		public State() {
			values = new Vector[nValues];
			for(int i=0; i<values.length; i++){
				values[i] = new Vector(0,0);
			}
		}

		public void setValues(Vector[] values) {
			System.arraycopy(values, 0, this.values, 0, values.length);
		}
	}
	
	protected static class Derivative extends State{
	}
}
