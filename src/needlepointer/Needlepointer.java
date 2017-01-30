package needlepointer;

/*** Image input function author :Vibhav Gogate
 The University of Texas at Dallas
 *****/
/**
 * Henry Dinh
 * CS 6375.001
 * Assignment 3
 * KMeans Clustering for Image Compression
 **/

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class Needlepointer {
	/**
	 * Simple main just calls generateNeedlepoint()
	 * 
	 * @param args
	 *            "filename" "K clusters" "width in inches
	 */
	public static void main(String[] args) {
		try {
			generateNeedlepoint(
					Integer.parseInt(args[1]), /* K clusters */
					Integer.parseInt(args[2]), /* Width in inches */
					args[0]);                  /* File to needlepoint */
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void generateNeedlepoint(int k, int wInch, String fname) throws IOException {
		String name = fname.substring(0, fname.length() - 4);
		String kmeans_output_name = String.format("%s_%02d_kmeans.png", name, k);
		String fs_dither_output_name = String.format("%s_%02d_dither_fs.png", name, k);
		String r_dither_output_name = String.format("%s_%02d_dither_r.png", name, k);
		String palette_output_name = String.format("%s_%02d_palette.png", name, k);

		BufferedImage originalImage = ImageIO.read(new File(fname));
		ArrayList<Pixel> pixelPalette = new ArrayList<>(k);

		/* Scale original image */
		int wOrig = originalImage.getWidth();
		int wNew = 18 * wInch;
		int hNew = (int) Math.round(originalImage.getHeight() * (wNew / (double) wOrig));
		ResampleOp resizeOp = new ResampleOp(wNew, hNew);
		resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
		BufferedImage scaledImage = resizeOp.filter(originalImage, null);

		/* Convert the BufferedImage to an array of Pixels */
		Pixel scaledImagePx[][] = new Pixel[wNew][hNew];
		for (int x = 0; x < wNew; x++) {
			for (int y = 0; y < hNew; y++) {
				scaledImagePx[x][y] = new Pixel(scaledImage.getRGB(x, y));
			}
		}

		/* Use K means clustering to find the dominant colors in the image */
		Pixel kmeansImagePx[][] = KMeans.kmeans_helper(scaledImagePx, k, pixelPalette);
		pxToPng(kmeans_output_name, kmeansImagePx);

		/* Print the palette, for kicks */
		Collections.sort(pixelPalette);
		Pixel paletteImg[][] = drawPalette(pixelPalette);
		pxToPng(palette_output_name, paletteImg);

		/* Dither the image two ways */
		
		/* Make a copy of the source pixels */
		Pixel scaledImagePxCopy[][] = new Pixel[wNew][hNew];
		for (int x = 0; x < wNew; x++) {
			for (int y = 0; y < hNew; y++) {
				scaledImagePxCopy[x][y] = new Pixel(scaledImagePx[x][y]);
			}
		}

		/* FS dither the original source */
		Pixel fsDitheredImagePx[][] = FSdither.dither_helper(scaledImagePx, pixelPalette);
		pxToPng(fs_dither_output_name, fsDitheredImagePx);

		/* Random dither the copy */
		Pixel rDitheredImagePx[][] = RandomDither.dither_helper(scaledImagePxCopy, pixelPalette);
		pxToPng(r_dither_output_name, rDitheredImagePx);
	}

	/**
	 * Simple function to save a PNG image defined by a 2D Pixel array
	 * 
	 * @param name
	 *            The output filename
	 * @param pixels
	 *            A 2D array of pixels to save as a PNG
	 * @throws IOException
	 *             If something terrible happens
	 */
	static void pxToPng(String name, Pixel[][] pixels) throws IOException {
		int width = pixels.length;
		int height = pixels[0].length;

		/* Convert the pixel array back to a BufferedImage */
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				output.setRGB(x, y, pixels[x][y].getRGB());
			}
		}
		ImageIO.write(output, "png", new File(name));
	}

	/**
	 * This function generates a 2D array of Pixels to display the given palette
	 * 
	 * @param pixelPalette
	 *            A palette of pixel colors to draw
	 * @return A 2D array of Pixels displaying the current palette
	 */
	static Pixel[][] drawPalette(ArrayList<Pixel> pixelPalette) {
		int k = pixelPalette.size();
		int swatchSize = 64;
		int palette_w = (int) Math.ceil(Math.sqrt(k));
		int palette_h = (int) Math.ceil(k / (double) palette_w);
		Pixel paletteImg[][] = new Pixel[palette_w * swatchSize][palette_h * swatchSize];
		for (int i = 0; i < k; i++) {
			for (int x = 0; x < swatchSize; x++) {
				for (int y = 0; y < swatchSize; y++) {
					paletteImg[((i % palette_w) * swatchSize) + x][((i / palette_w) * swatchSize) + y] = new Pixel(
							pixelPalette.get(i));
				}
			}
		}
		for (int x = 0; x < palette_w * swatchSize; x++) {
			for (int y = 0; y < palette_h * swatchSize; y++) {
				if (paletteImg[x][y] == null) {
					if (x % 3 == 0 && y % 2 == 0) {
						paletteImg[x][y] = new Pixel(0, 0, 0);
					}
					else {
						paletteImg[x][y] = new Pixel(0xFF, 0xFF, 0xFF);
					}
				}
			}
		}
		return paletteImg;
	}
}