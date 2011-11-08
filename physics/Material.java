
package physics;

import java.awt.Color;

/**
 *
 * @author Robinerd
 */
public enum Material {
	WING_MATERIAL(1, Color.BLACK),
	STRUCTURE_MATERIAL(0.2f, Color.lightGray),
	NULL_MATERIAL(0, Color.white);

	public double density;
	public Color color;

	private Material(double density, Color color) {
		this.density = density;
		this.color = color;
	}

}
