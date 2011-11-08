
package physics.entities;

import physics.Vector;

/**
 *
 * @author Robinerd
 */
public class LooseSpringJoint extends SpringJoint{

	@Override
	protected double getSpringCoefficient() {
		return 300000;
	}

	@Override
	protected double getDampingCoefficient() {
		return 10000;
	}

	@Override
	protected double getStretchLimit() {
		return 30;
	}

}
