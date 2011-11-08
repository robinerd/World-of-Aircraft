package physics.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JPanel;
import physics.Vector;

/**
 * Denna grafiska komponent har sin nollpunkt i värld-koordinaternas nollpunkt,
 * och är så pass stor att hela planet får plats i utritningen.
 * Komponenten har alltid positionen (0,0) och storleken (x+2000, y+2000),
 * där (x,y) är planets position.
 * @author Robinerd
 */
public class Plane extends JPanel {

	public boolean thrusting = false;
	private int x, y; //planets position bestäms av vingarnas genomsnittliga position.
	private ArrayList<Wing> wings = new ArrayList<Wing>();
	private ArrayList<Structure> structures = new ArrayList<Structure>();
	private ArrayList<SpringJoint> joints = new ArrayList<SpringJoint>();
	private ArrayList<Engine> engines = new ArrayList<Engine>();
	private Wing player;

	public Plane(int startX, int startY) {
		setOpaque(false);

		setOpaque(true);
		setBackground(Color.red);
	}

	public void destroy() {
		for (Wing wing : wings) {
			remove(wing);
		}
		wings = null;

		for (Structure structure : structures) {
			remove(structure);
		}
		structures = null;

		for (SpringJoint joint : joints) {
			remove(joint);
		}
		joints = null;
	}

	public void update(double dt) {

		for (Wing wing : wings) {
			wing.update(dt); //uppdatera varje vinge
		}
		for (Structure structure : structures) {
			structure.update(dt); //uppdatera varje struktur
		}
		for (Engine engine : engines) {
			engine.update(dt);
		}
		for (SpringJoint joint : joints) {
			joint.update(dt);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		for(Structure structure : structures) {
			structure.paintComponent(g);
		}
		for(Wing wing : wings) {
			wing.paintComponent(g);
		}
		for(Engine engine : engines) {
			engine.paintComponent(g);
		}
		for(SpringJoint joint : joints) {
			joint.paintComponent(g);
		}
	}

	public static Plane assemblePlane(
			ArrayList<Wing> allWings,
			ArrayList<Structure> allStructures,
			ArrayList<Point> enginePositions,
			ArrayList<Point> jointPositions)
	{

		Plane plane = new Plane(0, 0);

		plane.player = allWings.get(0);

		//materialParts innehåller delar som motorer och hjul kan fästas på (vingar och strukturer)
		ArrayList<Part> materialParts = new ArrayList<Part>();


		//lägg till vingar -----------------------------------------------------

		for (Wing wing : allWings) {
			if (wing.getMass() / wing.density > 50) { //filtrera bort pyttesmå objekt
				plane.addWing(wing);
				materialParts.add(wing);
			}
		}

		//lägg till strukturer -------------------------------------------------

		for (Structure structure : allStructures) {
			if (structure.getMass() / structure.density > 50) { //filtrera bort pyttesmå objekt
				plane.addStructure(structure);
				materialParts.add(structure);
			}
		}

		//skapa och lägg till motorer ------------------------------------------

		for (Point enginePosition : enginePositions) {
			Engine newEngine = new Engine(enginePosition.x, enginePosition.y);
			plane.addEngine(newEngine);
			//skapa joint för att hålla fast motorn
			for (Part part : materialParts) {
				if (part.containsPointNoRot(enginePosition)) {
					SpringJoint newJoint = new AngularSpringJoint();
					newJoint.attach(newEngine, part, new Vector(enginePosition));
					plane.addJoint(newJoint);
					break; //skapa endast EN joint för motorn - den sätts fast i första bästa vinge eller struktur
				}
			}
		}

		//skapa joints ---------------------------------------------------------

		//om den utsatta jointpunkten sitter ihop med endast en eller ingen vinge/struktur skapas ingen joint
		//om jointpunkten sitter ihop med två eller fler vingar/strukturer, skapas en eller flera joints, i en kedja mellan objekten
		for (Point jointPosition : jointPositions) {
			Part previousPart = null;
			for (Part part : materialParts) {
				if (part.containsPointNoRot(jointPosition)) {
					if (previousPart == null) {
						previousPart = part;
					} else {
						//jointen får endast sitta mellan material-delar dvs vingar och strukturer
						SpringJoint newJoint = new LooseSpringJoint();
						newJoint.attach(previousPart, part, new Vector(jointPosition));
						plane.addJoint(newJoint);
						previousPart = part;
					}
				}
			}
		}

		return plane;
	}


	public void setThrusting(boolean thrusting) {
		this.thrusting = thrusting;
		for (Engine engine : engines) {
			engine.setThrusting(thrusting);
		}
	}

	public void addWing(Wing newWing) {
		wings.add(newWing);
		//add(newWing, -1);
	}

	public void addJoint(SpringJoint newJoint) {
		joints.add(newJoint);
		//add(newJoint, 0);
	}

	public void addEngine(Engine newEngine) {
		engines.add(newEngine);
		//add(newEngine, 0);
	}

	public void addStructure(Structure newStructure) {
		structures.add(newStructure);
		//add(newStructure, -1);
	}

	public void setLocation(Vector newLocation) {
		Vector deltaLocation = Vector.difference(newLocation, getPlayerLocation());
		for(Wing wing : wings) {
			wing.pos.add(deltaLocation);
		}
		for(Structure structure : structures) {
			structure.pos.add(deltaLocation);
		}
		for(Engine engine : engines) {
			engine.pos.add(deltaLocation);
		}
	}

	public Vector getPlayerLocation() {
		return new Vector(wings.get(0).pos.x, wings.get(0).pos.y);
	}
}
