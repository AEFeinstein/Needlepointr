package needlepointer;

import java.util.ArrayList;
import java.util.Random;

public class RandomDither {

	/**
	 * Use random order dithering to redraw the original image with only the
	 * pixels in the given palette
	 * 
	 * @param original
	 *            The image to redraw
	 * @param pixelPalette
	 *            The pixels to use to redraw
	 * @return A redrawn version of the original image using only the pixels in
	 *         the palette
	 */
	static Pixel[][] dither_helper(Pixel original[][],
			ArrayList<Pixel> pixelPalette) {

		int width = original.length;
		int height = original[0].length;

		Pixel ditheredImage[][] = new Pixel[width][height];

		/*
		 * Use Fisher–Yates_shuffle to determine a random order of pixels to
		 * quantize.
		 */
		int indices[] = new int[width * height];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
		}
		shuffleArray(indices);

		/*
		 * For every pixel in random order, quanitze it and spread the error to
		 * neighboring pixels
		 */
		for (int i = 0; i < indices.length; i++) {
			int x = indices[i] % width;
			int y = indices[i] / width;

			/*
			 * Get the closest pixel from the palette, store it in the dithered
			 * image, then calculate the quantization error
			 */
			ditheredImage[x][y] = original[x][y].findNearest(pixelPalette);
			Pixel error = original[x][y].getError(ditheredImage[x][y]);

			/*
			 * Count up how many unquantized adjacent and diagonal neighbors
			 * this pixel has
			 */
			double diagNeighbors = 0;
			double adjNeighbors = 0;

			if (isUnquantizedNeighbor(x - 1, y - 1, ditheredImage)) {
				diagNeighbors++;
			}
			if (isUnquantizedNeighbor(x + 1, y - 1, ditheredImage)) {
				diagNeighbors++;
			}
			if (isUnquantizedNeighbor(x - 1, y + 1, ditheredImage)) {
				diagNeighbors++;
			}
			if (isUnquantizedNeighbor(x + 1, y + 1, ditheredImage)) {
				diagNeighbors++;
			}
			if (isUnquantizedNeighbor(x - 1, y, ditheredImage)) {
				adjNeighbors++;
			}
			if (isUnquantizedNeighbor(x + 1, y, ditheredImage)) {
				adjNeighbors++;
			}
			if (isUnquantizedNeighbor(x, y - 1, ditheredImage)) {
				adjNeighbors++;
			}
			if (isUnquantizedNeighbor(x, y + 1, ditheredImage)) {
				adjNeighbors++;
			}

			/*
			 * Spread the error to all neighboring unquantized pixels, with
			 * twice as much error to the adjacent pixels as the diagonal ones
			 */
			double diagScalar = 1 / ((2 * adjNeighbors) + diagNeighbors);
			double adjScalar = 2 * diagScalar;

			/* spreadError checks if the pixel has been quantized already */
			spreadError(x - 1, y - 1, ditheredImage, original, error, diagScalar);
			spreadError(x - 1, y + 1, ditheredImage, original, error, diagScalar);
			spreadError(x + 1, y - 1, ditheredImage, original, error, diagScalar);
			spreadError(x + 1, y + 1, ditheredImage, original, error, diagScalar);
			spreadError(x - 1, y, ditheredImage, original, error, adjScalar);
			spreadError(x + 1, y, ditheredImage, original, error, adjScalar);
			spreadError(x, y - 1, ditheredImage, original, error, adjScalar);
			spreadError(x, y + 1, ditheredImage, original, error, adjScalar);
		}

		/* Return the dithered image */
		return ditheredImage;
	}

	/**
	 * Check if a neighboring pixel is quantized or not
	 * 
	 * @param x
	 *            The x coordinate of the pixel
	 * @param y
	 *            The y coordinate of the pixel
	 * @param ditheredImage
	 *            The image to check in
	 * @return true if the pixel isn't quantized yet, false if it is
	 */
	static boolean isUnquantizedNeighbor(int x, int y, Pixel ditheredImage[][]) {
		int width = ditheredImage.length;
		int height = ditheredImage[0].length;

		if (0 <= x && x < width &&
			0 <= y && y < height &&
				ditheredImage[x][y] == null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param ditheredImage
	 * @param originalImage
	 * @param error
	 * @param scalar
	 */
	static void spreadError(int x, int y, Pixel ditheredImage[][],
			Pixel originalImage[][], Pixel error, double scalar) {
		int width = originalImage.length;
		int height = originalImage[0].length;

		if (0 <= x && x < width &&
			0 <= y && y < height &&
				ditheredImage[x][y] == null) {
			originalImage[x][y].addError(error, scalar);
		}
	}

	/**
	 * Fisher-Yates shuffle, stolen from somewhere on the internet
	 * 
	 * @param ar
	 *            the array to shuffle
	 */
	static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}
