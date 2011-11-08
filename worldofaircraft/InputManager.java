
package worldofaircraft;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import physics.entities.Plane;

/**
 *
 * @author Robinerd
 */
public class InputManager implements KeyListener{

	public int keyThrust = KeyEvent.VK_SPACE;

	public Plane plane;

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(keyCode == keyThrust) {
			plane.setThrusting(true);
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(keyCode == keyThrust) {
			plane.setThrusting(false);
		}
	}

	public InputManager(Plane plane) {
		setPlane(plane);
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	}

	
}
