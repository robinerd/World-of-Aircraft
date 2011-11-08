/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package physics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 *
 * @author Robin
 */
public class MaterialShape{

	public Shape shape;
	public Material material;
	public BasicStroke stroke;

	public MaterialShape(Shape shape, Material material, int paintDiameter) {
		stroke = new BasicStroke(paintDiameter, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		this.shape = shape;
		this.material = material;
	}

	public void paint(Graphics2D g2d) {
		g2d.setColor(material.color);
		g2d.setStroke(stroke);
		g2d.draw(shape);
	}
}
