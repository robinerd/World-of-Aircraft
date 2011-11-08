
package physics.entities;

import java.awt.image.BufferedImage;
import physics.Vector;

/**
 *
 * @author Robinerd
 */
public class Engine extends Part {

	public static Image ENGINE_IMAGE;

	static {
		ENGINE_IMAGE = Image.loadImageWhiteMask("img/engine.png");
	}

	private double engineForce = 8000000;
	private boolean thrusting = false;

	public Engine(double x, double y) {
		super(ENGINE_IMAGE.image, 2, new Vector(x, y));
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		if(thrusting) {
			Vector engineImpulse = new Vector((double)Math.cos(rotation), -(double)Math.sin(rotation)).multiply(engineForce * dt);
			applyImpulse(engineImpulse, pos);
		}
		
	}

	public void setThrusting(boolean thrusting) {
		this.thrusting = thrusting;
	}

}
