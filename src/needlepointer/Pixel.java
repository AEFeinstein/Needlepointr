package needlepointer;

import java.awt.Color;
import java.util.ArrayList;

public class Pixel implements Comparable<Pixel>{

	private double r, g, b;

	public Pixel(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Pixel(int rgb) {
		this.r = (rgb >> 16) & 0xFF;
		this.g = (rgb >>  8) & 0xFF;
		this.b = (rgb >>  0) & 0xFF;
	}

	public Pixel(Pixel pixel) {
		this.r = pixel.r;
		this.g = pixel.g;
		this.b = pixel.b;
	}

	public double distance(Pixel other) {
		return  ((r - other.r) * (r - other.r)) +
				((g - other.g) * (g - other.g)) +
				((b - other.b) * (b - other.b));
	}

	public Pixel findNearest(ArrayList<Pixel> palette) {
		double minDist = Double.MAX_VALUE;
		Pixel nearestPixel = new Pixel(0,0,0);

		for (Pixel p : palette) {
			if (distance(p) < minDist) {
				minDist = distance(p);
				nearestPixel.setValue(p);
			}
		}
		return nearestPixel;
	}
	
	public Pixel getError(Pixel p) {
		return new Pixel(
				r - p.r,
				g - p.g,
				b - p.b);
	}

	public void addError(Pixel error, double scale) {
		r += error.r * scale;
		g += error.g * scale;
		b += error.b * scale;
	}

	public int getRGB() {
		return  ((0xFF)     << 24) |
				((((int)Math.round(r)) & 0xFF) << 16) |
				((((int)Math.round(g)) & 0xFF) <<  8) |
				((((int)Math.round(b)) & 0xFF) <<  0);
	}

	public void scalePixel(double d) {
		r *= d;
		g *= d;
		b *= d;
	}

	@Override
	public String toString() {
		return String.format("<%02X,%02X,%02X>",
				(int)Math.round(r),
				(int)Math.round(g),
				(int)Math.round(b));
	}

	public void setValue(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setValue(Pixel pixel) {
		this.r = pixel.r;
		this.g = pixel.g;
		this.b = pixel.b;
	}

	public Pixel round() {
		this.r = Math.round(r);
		this.g = Math.round(g);
		this.b = Math.round(b);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pixel)) {
			return false;
		}
		
		if( ((Pixel)obj).r == this.r &&
			((Pixel)obj).g == this.g &&
			((Pixel)obj).b == this.b) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(Pixel o) {
		float hsv[] = new float[3];
		Color.RGBtoHSB((int)Math.round(r), (int)Math.round(g), (int)Math.round(b), hsv);
		Float thisHue = hsv[0];
		Color.RGBtoHSB((int)Math.round(o.r), (int)Math.round(o.g), (int)Math.round(o.b), hsv);
		Float thatHue = hsv[0];
		return thisHue.compareTo(thatHue);
	}
}