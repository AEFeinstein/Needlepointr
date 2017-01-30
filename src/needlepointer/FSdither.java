package needlepointer;

import java.util.ArrayList;

public class FSdither {

	/**
	 * Use Floyd-Steinberg dithering to redraw the original image with only the
	 * pixels in the given palette
	 * 
	 * @param original
	 *            The image to redraw
	 * @param pixelPalette
	 *            The pixels to use to redraw
	 * @return A redrawn version of the original image using only the pixels in
	 *         the palette
	 */
	static Pixel[][] dither_helper(Pixel original[][], ArrayList<Pixel> pixelPalette) {

		int width = original.length;
		int height = original[0].length;

		Pixel ditheredImage[][] = new Pixel[width][height];

		/* For ever pixel, traversing the rows first, then columns */
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				/*
				 * Find the pixel from the palette which is closest to the
				 * source pixel
				 */
				ditheredImage[x][y] = new Pixel(original[x][y].findNearest(pixelPalette));

				/* Find the quantization error and spread it around */
				Pixel quant_error = original[x][y].getError(ditheredImage[x][y]);
				if (x + 1 < width) {
					original[x + 1][y].addError(quant_error, 7 / (double) 16);
				}
				if (y + 1 < height) {
					if (0 <= x - 1) {
						original[x - 1][y + 1].addError(quant_error, 3 / (double) 16);
					}
					original[x][y + 1].addError(quant_error, 5 / (double) 16);
					if (x + 1 < width) {
						original[x + 1][y + 1].addError(quant_error, 1 / (double) 16);
					}
				}
			}
		}

		/* Return the dithered image */
		return ditheredImage;
	}

}
