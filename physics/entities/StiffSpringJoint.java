
package physics.entities;

/**
 *
 * @author Robinerd
 */
public class StiffSpringJoint extends SpringJoint{

	@Override
	protected double getSpringCoefficient() {
		return 600000;
	}

	@Override
	protected double getDampingCoefficient() {
		return 2000f;
	}

	@Override
	protected double getStretchLimit() {
		return 35;
	}

}
