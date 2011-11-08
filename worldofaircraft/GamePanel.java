/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldofaircraft;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import physics.Vector;
import physics.entities.Plane;
import physics.entities.RigidBody;

/**
 *
 * @author Robin
 */
public class GamePanel extends JPanel implements Runnable {

	public static int fpsLimit = 300;
	public static double SCALE = 0.5;
	public static int SCROLL_INTERVAL = 100;
	public static double SCROLL_SPEED = 0.5;
	private Vector cameraPos = new Vector(0, 0); //världskoordinater
	private Rectangle scrollInterval; //världskoordinater
	private Plane plane;
	private AffineTransform scale = AffineTransform.getScaleInstance(SCALE, SCALE);
	private InputManager inputManager;
	private long lastUpdate;
	private double sleepTimer;
	private boolean quit; //sätt till true för att stoppa spelrundan

	public GamePanel() {
		setBackground(Color.green);
		inputManager = new InputManager(plane);
	}

	public void startFlight(Plane plane) {
		this.plane = plane;
		inputManager.setPlane(plane);

		//ställ upp ett interval för kamerapunkten att röra sig i
		//kamerapunkten är världskoordinaterna för den utritade bildens övre vänstra hörn
		setSize(getParent().getSize());
		scrollInterval = new Rectangle();
		scrollInterval.width = (int) Math.max(0, Level.levelWidth - getWidth()/SCALE);
		scrollInterval.height = Level.levelHeight;
		scrollInterval.x = 0;
		scrollInterval.y = (int) (-getHeight()/SCALE);
		cameraPos.x = scrollInterval.x;
		cameraPos.y = scrollInterval.y;

		Vector location = plane.getPlayerLocation();
		plane.setLocation(new Vector(
				location.x / SCALE,
				plane.getPlayerLocation().y - getHeight()));
		RigidBody.groundLevel = 0;
		plane.update(0); //initiera alla värden innan för att undvika null pointer exceptions
		new Thread(this).start();
	}

	public void run() {
		lastUpdate = System.currentTimeMillis();
		sleepTimer = 0;
		quit = false;
		while (!quit) {
			update();
			repaint();
		}
		//förstör och ta bort planet
		plane.destroy();
		remove(plane);
		plane = null;
	}

	public void stop() {
		quit = true;
	}

	private void update() {
		long currTime = System.currentTimeMillis();
		double dt = (currTime - lastUpdate) / 1000.0;
		lastUpdate = currTime;

		if (dt > 0.2) // skydd mot laggiga buggar om FPS blir mindre än 5
		{
			dt = 0.01;
		}
		plane.update(dt);

		//följ efter med kameran
		int displacementX = (int)((plane.getPlayerLocation().x - cameraPos.x)*SCALE) - getWidth()/2;
		if(displacementX > SCROLL_INTERVAL) {
			double tmp = displacementX - SCROLL_INTERVAL;
			//cameraPos.x += tmp*tmp/SCALE * SCROLL_SPEED * dt;
		}

		sleepTimer += (long) Math.max(0, 1000.0 / fpsLimit - 1000.0 * dt);
		try {
			if (sleepTimer >= 1) {
				Thread.sleep((long) sleepTimer);
				sleepTimer -= (long) sleepTimer;
			}
		} catch (InterruptedException ex) {
		}
	}

	public InputManager getInputManager()
	{
		return inputManager;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		AffineTransform original = g2d.getTransform();
		AffineTransform transform = new AffineTransform();
		transform.translate(-cameraPos.x, -cameraPos.y);
		g2d.transform(scale);
		g2d.transform(transform);
		plane.paintComponent(g2d);
		g2d.setTransform(original);

	}
}
