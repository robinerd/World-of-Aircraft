/*
 * Copyright Robin Lindh Nilsson 2010
 */
package worldofaircraft;

import physics.MaterialShape;
import physics.Material;
import physics.entities.Wing;
import physics.entities.Part;
import physics.entities.Plane;
import physics.entities.SpringJoint;
import physics.entities.Structure;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import physics.entities.Engine;

/**
 * Tillhandahåller en rityta där användaren kan rita med musen.
 * Det man ritar lagras som en mängd linjer.
 * @author Robin Lindh Nilsson
 */
public class PaintLayer extends JPanel implements MouseListener, MouseMotionListener {

	public static final int PLACE_JOINT = 0;
	public static final int PLACE_ENGINE = 1;
	private ArrayList<MaterialShape> shapes = new ArrayList<MaterialShape>();
	private ArrayList<Point> jointPositions = new ArrayList<Point>();
	private ArrayList<Point> enginePositions = new ArrayList<Point>();
	private Point paintPos = null;
	private Material paintMaterial = Material.WING_MATERIAL;
	private int rightClickAction = PLACE_JOINT;
	private Point cursorLocation = new Point(0, 0);
	private boolean showCursor = false;
	private int paintDiameter = 7;
	private boolean isPressed_leftMouseButton = false;

	public PaintLayer() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		//clear
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		for (MaterialShape shape : shapes) {
			shape.paint(g2d);
		}

		for (Point jointPosition : jointPositions) {
			SpringJoint.JOINT_IMAGE.drawCenteredAt(jointPosition, g2d);
		}

		for (Point enginePosition : enginePositions) {
			Engine.ENGINE_IMAGE.drawCenteredAt(enginePosition, g2d);
		}

		if (showCursor) {
			if (paintMaterial != Material.NULL_MATERIAL) {
				//pencil
				g2d.setColor(paintMaterial.color);
				g2d.fillOval(cursorLocation.x - paintDiameter / 2,
						cursorLocation.y - paintDiameter / 2,
						paintDiameter, paintDiameter);
			} else {
				//Rubber
				g2d.setStroke(new BasicStroke(2));
				g2d.setColor(Color.black);
				g2d.drawOval(cursorLocation.x - paintDiameter / 2,
						cursorLocation.y - paintDiameter / 2,
						paintDiameter, paintDiameter);
			}
		}
	}

	public void paintOneMaterial(Graphics2D g2d, Material material) {
		//clear
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		for (MaterialShape shape : shapes) {
			//rita nuvarande material + null-materialet för utsuddning
			if (shape.material == material || shape.material == Material.NULL_MATERIAL) {
				shape.paint(g2d);
			}
		}
	}

	private void drawDot(Point location) {
		if (paintPos != null) {
			Shape newShape = new Line2D.Double(paintPos.x, paintPos.y, location.x, location.y);
			shapes.add(new MaterialShape(newShape, paintMaterial, paintDiameter));

			if (paintMaterial == Material.NULL_MATERIAL) {
				removeInvalidParts();
			}

			repaint();
			paintPos.setLocation(location.x, location.y);
		}
	}

	public void mouseEntered(MouseEvent e) {
		showCursor = true;
		cursorLocation = e.getPoint();
		repaint();
	}

	public void mouseExited(MouseEvent e) {
		showCursor = false;
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		cursorLocation = e.getPoint();
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		cursorLocation = e.getPoint();
		if (isPressed_leftMouseButton) {
			drawDot(e.getPoint());
		}
	}

	public void mousePressed(MouseEvent e) {
		Point clickPoint = new Point(e.getX(), e.getY());

		switch (e.getButton()) {

			case MouseEvent.BUTTON1:
				paintPos = clickPoint;
				isPressed_leftMouseButton = true;
				drawDot(paintPos);

				break;

			case MouseEvent.BUTTON3:
				switch (rightClickAction) {
					case PLACE_JOINT:
						jointPositions.add(new Point(e.getX(), e.getY()));
						break;
					case PLACE_ENGINE:
						enginePositions.add(new Point(e.getX(), e.getY()));
						break;
				}
				removeInvalidParts();
				repaint();
				break;
		}
	}

	public void mouseReleased(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			paintPos = null;
			isPressed_leftMouseButton = false;
		}
	}

	public void removeInvalidParts() {
		//definierar lager för jointkontrollen
		//en joint måste överlappa minst 2 lager för att vara giltig
		Material[] drawMaterials = {Material.WING_MATERIAL, Material.STRUCTURE_MATERIAL};

		BufferedImage[] paintedMaterials = new BufferedImage[drawMaterials.length];

		for (int i = 0; i < drawMaterials.length; i++) {
			paintedMaterials[i] = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			paintOneMaterial(paintedMaterials[i].createGraphics(), drawMaterials[i]);
		}

		//remove invalid joints
		ArrayList<Point> toRemove = new ArrayList<Point>();
		for (Point jointPosition : jointPositions) {
			for (BufferedImage materialImage : paintedMaterials) {
				if (materialImage.getRGB(jointPosition.x, jointPosition.y) == -1) {
					toRemove.add(jointPosition);
				}
			}
		}
		for (Point pointToRemove : toRemove) {
			jointPositions.remove(pointToRemove);
		}


		//remove invalid engines
		toRemove = new ArrayList<Point>();
		for (Point enginePosition : enginePositions) {
			boolean isAttached = false;
			for (BufferedImage materialImage : paintedMaterials) {
				if (materialImage.getRGB(enginePosition.x, enginePosition.y) != -1) {
					isAttached = true;
				}
			}
			if (!isAttached) {
				toRemove.add(enginePosition);
			}
		}
		for (Point pointToRemove : toRemove) {
			enginePositions.remove(pointToRemove);
		}
	}

	public Plane assemblePlane() {
		// Skapa för vänsterklick ----------------------------------------------

		// skapa vingar
		ArrayList<Wing> allWings = Wing.createFromParts(createParts(Material.WING_MATERIAL));

		// skapa strukturer
		ArrayList<Structure> allStructures = Structure.createFromParts(createParts(Material.STRUCTURE_MATERIAL));

		Plane plane = Plane.assemblePlane(allWings, allStructures, enginePositions, jointPositions);

		return plane;
	}

	private ArrayList<Part> createParts(Material material) {
		//måla ritytan till en bildbuffert, vingarnas bilddata
		BufferedImage partImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		paintOneMaterial(
				partImage.createGraphics(), material);
		ArrayList<Part> allParts = Part.splitImageIntoParts(partImage, material);

		return allParts;
	}

	public void clear() {
		enginePositions.clear();
		jointPositions.clear();
		shapes.clear();
		repaint();
	}

	public void setPaintMaterial(Material paintMaterial) {
		this.paintMaterial = paintMaterial;
	}

	public void setRightClickAction(int rightClickAction) {
		this.rightClickAction = rightClickAction;
	}

	public void setBrushSize(int brushSize) {
		paintDiameter = brushSize;
	}

	public void mouseClicked(MouseEvent e) {
	}
}
