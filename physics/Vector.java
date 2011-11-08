/*
 * Copyright Robin Lindh Nilsson 2010
 */

package physics;

import java.awt.Point;

/**
 * Representerar en tvådimensionell vektor.
 * Tillhandahåller stöd för några vanliga räkneoperationer.
 *
 * @author Robin Lindh Nilsson
 */
public class Vector {

    public double x,y;

    /**
     * Skapar en nollvektor
     */
    public Vector() {
        this(0,0);
    }

    /**
     * Skapar vektorn (x,y)
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Skapar en kopia av den inskickade vektorn
     * @param v Vektor att kopiera
     */
    public Vector(Vector v) {
        x = v.x;
        y = v.y;
    }

	/**
	 * Skapar vektorn från origo till punktens koordinater
	 * @param p
	 */
	public Vector(Point p) {
		x = p.x;
		y = p.y;
	}

    /**
     * Returnerar vektorns koordinater omvandlade till ett Point-objekt.
     * Eventuella decimaler trunkeras, eftersom en Point lagrar heltal.
     * @return vektorn representerad som en Point
     */
    public Point toPoint() {
        return new Point((int) x, (int) y);
    }

    /**
     * Beräknar vektorns absolutbelopp i kvadrat
     * @return vektorns absolutbelopp i kvadrat
     */
    public double lengthSquare() {
        return x*x + y*y;
    }

    /**
     * Beräknar vektorns absolutbelopp
     * @return vektorns absolutbelopp
     */
    public double length() {
        return (double) Math.sqrt(lengthSquare());
    }

    /**
     * Ökar denna vektor med (dx,dy)
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param dx ökning i x-led
     * @param dy ökning i y-led
     * @return denna vektor (INTE en kopia)
     */
    public Vector add(double dx, double dy){
        x += dx;
        y += dy;
        return this;
    }

    /**
     * Ökar denna vektor med v. Den inskickade vektorn ändras ej.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param v vektorn att öka med
     * @return denna vektor (INTE en kopia)
     */
    public Vector add(Vector v){
        add(v.x, v.y);
        return this;
    }

    /**
     * Minskar denna vektor med (dx,dy)
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param dx minskning i x-led
     * @param dy minskning i y-led
     * @return denna vektor (INTE en kopia)
     */
    public Vector subtract(double dx, double dy){
        x -= dx;
        y -= dy;
        return this;
    }

    /**
     * Minskar denna vektor med v. Den inskickade vektorn ändras ej.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param v vektorn att minska med
     * @return denna vektor (INTE en kopia)
     */
    public Vector subtract(Vector v){
        subtract(v.x, v.y);
        return this;
    }

    /**
     * Multiplicerar denna vektor med en skalär.
     * Vektorns x- och y-komposanter multipliceras var för sig.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param scalar skalär att multiplicera med
     * @return denna vektor (INTE en kopia)
     */
    public Vector multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    /**
     * Dividerar denna vektor med en skalär.
     * Vektorns x- och y-komposanter divideras var för sig.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @param scalar skalär att dividera med
     * @return denna vektor (INTE en kopia)
     */
    public Vector divide(double scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    /**
     * Negerar denna vektor.
     * Vektorns x- och y-komposanter negeras.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @return denna vektor (INTE en kopia)
     */
    public Vector negate() {
        multiply(-1);
        return this;
    }

    /**
     * Skapar och returnerar en negerad kopia av denna vektorn.
     * @return en negerad kopia.
     */
    public Vector getNegated() {
        return new Vector(this).negate();
    }

    /**
     * Normaliserar vektorn så att absolutbeloppet blir 1, utan att förändra riktningen.
     * Vektorn själv returneras för att underlätta stackade operationer.
     * @return denna vektor (INTE en kopia)
     */
    public Vector normalize() {
        divide(length());
        return this;
    }

    /**
     * Beräknar skalärprodukten mellan denna vektorn och den inskickade vektorn.
     * Den inskickade vektorn påverkas ej.
     * @return skalärprodukten
     */
    public double dot(Vector v) {
        return dot(this,v);
    }

    /**
     * Beräknar skalärprodukten mellan två vektorer.
     * De inskickade vektorerna påverkas ej.
     * @return skalärprodukten
     */
    public static double dot(Vector v1, Vector v2){
        return v1.x * v2.x + v1.y * v2.y;
    }

    /**
     * Beräknar summan v1 + v2.
     * De inskickade vektorerna påverkas ej.
     * @return summan, som en ny vektor
     */
    public static Vector sum(Vector v1, Vector v2) {
        Vector result = new Vector(v1);
        result.add(v2);
        return result;
    }
    
    /**
     * Beräknar differensen v1 - v2.
     * De inskickade vektorerna påverkas ej.
     * @return differensen, som en ny vektor
     */
    public static Vector difference(Vector v1, Vector v2) {
        Vector result = new Vector(v1);
        result.subtract(v2);
        return result;
    }

    /**
     * Beräknar kryss-produkten mellan denna vektorn och den inskickade vektorn.
     * (Även känd som perpendicular dot product)
     * Detta är likvärdigt med en skalärprodukt mellan vektorerna
     * då den ena vektorn vridits 90°.
     * Den inskickade vektorn påverkas ej.
     * @return kryssprodukten
     */
    public double cross(Vector v) {
        return cross(this, v);
    }

    /**
     * Beräknar kryss-produkten mellan två vektorer.
     * (Även känd som perpendicular dot product)
     * Detta är likvärdigt med en skalärprodukt mellan vektorerna
     * då den ena vektorn vridits 90°.
     * Den inskickade vektorn påverkas ej.
     * @return kryssprodukten
     */
    public static double cross(Vector v1, Vector v2) {
        return v2.x * v1.y - v2.y * v1.x;
    }

    /**
     * Beräknar projektionen av v1 på v2.
     * De inskickade vektorerna påverkas ej.
     * @return projektionen v1 på v2, som en ny vektor.
     */
    public static Vector projection(Vector v1, Vector v2) {
        return new Vector(v2).normalize().multiply(dot(v1, v2));
    }

    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Vector(this);
    }
    
}
