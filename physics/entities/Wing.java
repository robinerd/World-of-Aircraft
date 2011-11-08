package physics.entities;

import java.util.ArrayList;


/**
 *
 * @author Robinerd
 */
public class Wing extends Part {

	// innehåller information om lyftkoefficient
	// och metoder för att beräkna lyftkraften

	public Wing(Part part) {
		super(part);
	}

	public static ArrayList<Wing> createFromParts(ArrayList<Part> parts) {
		ArrayList<Wing> wings = new ArrayList<Wing>();
		for(Part part : parts) {
			wings.add(new Wing(part));
		}
		return wings;
	}
}
