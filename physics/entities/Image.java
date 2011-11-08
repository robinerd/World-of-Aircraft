package physics.entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author Robinerd
 */
public class Image {

	public BufferedImage image;
	public int width;
	public int height;

	public Image(BufferedImage image) {
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
	}

	public void drawCenteredAt(Point center, Graphics g) {
		drawAt(new Point(center.x - width/2, center.y - height/2), g);
	}

	public void drawAt(Point topLeft, Graphics2D g2d) {
		g2d.drawImage(image, topLeft.x, topLeft.y, null);
	}

	public void drawAt(Point position, Graphics g) {
		drawAt(position, (Graphics2D) g);
	}

	public static Image loadImage(String path) {
		BufferedImage image = null;
		try {
			image = (BufferedImage) ImageIO.read(Image.class.getClassLoader().getResource(path));
		} catch (Exception ex) {
			System.err.println("Could not load image +" + path + "\n");
			ex.printStackTrace();
		}
		
		return new Image(image);
	}

	public static Image loadImageWhiteMask(String path) {
		BufferedImage image = loadImage(path).image;

		BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		//behåll alla pixlar utom de vita. det vita blir då genomskinligt.
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int rgb = image.getRGB(i, j);
				if (rgb != -1) {
					transparentImage.setRGB(i, j, rgb);
				}
			}
		}

		return new Image(transparentImage);
	}
}
