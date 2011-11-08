
package physics.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import physics.Vector;
import physics.Material;

/**
 *
 * @author Robinerd
 */
public class Part extends RigidBody{

	public Part(BufferedImage image, double density, Vector position) {
		super(image, density, position);
	}

	public Part(BufferedImage image, double density) {
		super(image, density);
	}

	public Part(Part part) {
		super(part);
	}

	public static ArrayList<Part> splitImageIntoParts(BufferedImage pixelData, Material material) {
		ArrayList<Part> returnValue = new ArrayList<Part>();

		ArrayList<boolean[][]> allPartPixels = RigidBody.splitImageIntoRigidBodies(pixelData);

		//för varje boolsk matris, skapa en vinge
		for (boolean[][] partPixels : allPartPixels) {
			int width = partPixels.length;
			int height = partPixels[0].length;

			//rita vingens bild utifrån den boolska matrisen.
			BufferedImage partImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					//rita en pixel för varje true-värde i den boolska matrisen
					if (partPixels[i][j]) {
						partImage.setRGB(i, j, material.color.getRGB());
					}
				}
			}
			Part newPart = new Part(partImage, material.density);
			returnValue.add(newPart);
		}

		return returnValue;
	}
}
