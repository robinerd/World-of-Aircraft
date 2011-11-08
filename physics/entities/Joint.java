/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package physics.entities;

/**
 *
 * @author Robinerd
 */
public interface Joint {

	public boolean isBroken();
	public void update(double dt);
	
}
