
package physics.entities;

import java.util.ArrayList;

/**
 * innehåller för tillfället ingen information utöver sin superklass,
 * men finns ändå för framtida implementation.
 * @author Robinerd
 */
public class Structure extends Part{
	
	public Structure(Part part) {
		super(part);
	}

	public static ArrayList<Structure> createFromParts(ArrayList<Part> parts) {
		ArrayList<Structure> structures = new ArrayList<Structure>();
		for(Part part : parts) {
			structures.add(new Structure(part));
		}
		return structures;
	}
}
