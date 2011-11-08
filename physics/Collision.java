/*
 * Copyright Robin Lindh Nilsson 2010
 */

package physics;

import physics.entities.RigidBody;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Robinerd
 */
public class Collision {

    public Vector point; //kollisionspunkten
    public Vector normal; //2D-vektor med kollisionens normal
    public double overlap; // hur mycket det överlappar mätt i pixlar längs normalen

    /**Hittar om det finns något överlapp mellan den givna kroppen och rektangeln.
     * Först görs en enkel kontroll med hjälp av kroppens radie.<br/>
     * Om denna cirkel inte överlappar returneras null. Om den överlappar görs en ytterligare
     * kontroll inom en rektangel som omfattar det överlappande området. Överlapp innebär
     * att kroppen har minst en icke-genomskinlig pixel inom rektangeln.
     *
     * @param body Kroppen
     * @param rect Den rektangulära kollisionsytan
     * @return - Om inget överlapp finns returneras null<br/>
     *         - Om en eller flera pixlar överlappar returneras en Collision
     *           med kollisionsdata
     */
    public static Collision checkCollision(RigidBody body, Rectangle rect) {
        if(body.isEmpty())
            return null;
        
        int radius = (int) body.collisionRadius + 5; // +5 gör att objekt hamnar bättre i vila, av någon anledning
        int L = (int) body.pos.x - radius;
        int U = (int) body.pos.y - radius;
        int R = L + radius*2;
        int D = U + radius*2;
        //kontrollera kollisions-cirkeln mot rektangeln
        if(D <= rect.y || U >= rect.y + rect.getHeight() ||
                R <= rect.x || L >= rect.x + rect.getWidth()){
            return null;
        }

        //intervallet för pixel-kollision i globala koordinater
        int limitL = Math.max(L, rect.x);
        int limitR = Math.min(R, rect.x + (int)rect.getWidth());
        int limitU = Math.max(U, rect.y);
        int limitD = Math.min(D, rect.y + (int)rect.getHeight());

        ArrayList<Vector> colPoints = new ArrayList<Vector>();

		//räkna ut det exakta intervallet som ska kollisionskontrolleras per-pixel
		int intersectionWidth = limitR-limitL+1;
		int intersectionHeight = limitD-limitU+1;

		BufferedImage intersection = new BufferedImage(intersectionWidth, intersectionHeight, BufferedImage.TYPE_INT_ARGB);
		//translatera bildbuffertens grafik så att övre vänstra hörnet av
		//kollisions-kontroll-intervallet (kollisionskontrollens lokala origo)
		//hamnar i bildbuffertens origo. 
		//Därefter omfattas området som ska testas per-pixel exakt av bilden.
		Graphics2D g2d = intersection.createGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, intersection.getWidth(), intersection.getHeight());
		body.paintAtCoordinate(g2d, new Point((int)body.pos.x - limitL, (int)body.pos.y - limitU));

		for(int i=0; i<intersection.getWidth(); i++){
			for(int j=0; j<intersection.getHeight(); j++){
				if(intersection.getRGB(i, j) != -1) {
					//translatera tillbaka punkten till världskoordinater, och lägg till som kollision
					colPoints.add(new Vector(i+limitL, j+limitU));
				}
			}
		}
		
        if(colPoints.isEmpty())
            return null;
        
        Collision col = new Collision();
        col.point = avgPoint(colPoints);

        //beräkna kollisionsnormalen
        int w = (int)rect.getWidth();
        int h = (int)rect.getHeight();
        double dx = col.point.x - rect.x-w/2;
        double dy = col.point.y - rect.y-h/2;

        //jämför k-värde på:
        //1. en vektor från rektangelcentrum till kollisionspunkten
        //2. en vektor från ö.v. hörnet till n.h. hörnet (rektangelns proportioner)
        if(Math.abs(dy/dx) < h/w){ //lägre lutning än proportionerna, dvs antingen från höger eller från vänster
            if(dx > 0)
                col.normal = new Vector(1, 0);
            else
                col.normal = new Vector(-1,0);
        } else { //högre lutning än proportionerna, dvs antingen uppifrån eller nerifrån
            if(dy > 0)
                col.normal = new Vector(0,1);
            else
                col.normal = new Vector(0,-1);
        }
		//ingen kollision om objektet rör sig bort från rektangeln
		if(Vector.dot(body.getPointVelocity(col.point), col.normal) >= 0)
			return null;

        //beräkna överlapp
        double dotMax = 1 - Double.MAX_VALUE;
        double dotMin = Double.MAX_VALUE;
        double dot;
        for(Vector v : colPoints) {
            dot = Vector.dot(v, col.normal);
            dotMax = Math.max(dot, dotMax);
            dotMin = Math.min(dot, dotMin);
        }
        col.overlap = dotMax - dotMin;

        return col;
    }

    /**
     * Beräknar medelvärdet av ett antal vektorer i 2D
     *
     * @param points Lista av vektorer
     * @return Medelvärdet, som en vektor
     */
    private static Vector avgPoint(ArrayList<Vector> points){

        Vector sum = new Vector();//summa för att beräkna medelvärdet

        for(Vector v : points) {
            sum = sum.add(v);
        }

        return sum.divide(points.size());
    }
}
