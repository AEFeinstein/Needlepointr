package needlepointer;

import java.util.ArrayList;
import java.util.Random;

public class KMeans {

	private static final int ITERATIONS = 100;

	/**
	 * Use K means clustering to find the K dominant colors in the original
	 * image, return those colors in the pixelPalette ArrayList, and return the
	 * image with each pixel as the nearest dominant color
	 * 
	 * @param original
	 *            The image to analyze
	 * @param k
	 *            The number of clusters to compute
	 * @param pixelPalette
	 *            An ArrayList to return the pixel palette through
	 * @return A new image using only the dominant colors, with each pixel's
	 *         nearest neighbor
	 */
	static Pixel[][] kmeans_helper(Pixel original[][], int k,
			ArrayList<Pixel> pixelPalette) {

		int width = original.length;
		int height = original[0].length;

		/* Start off with random cluster centers */
		Pixel clusters[] = getInitialRandomClusters(original, k);

		Pixel clusterAverages[] = new Pixel[k];
		int clusterCounts[] = new int[k];

		for (int i = 0; i < k; i++) {
			clusterAverages[i] = new Pixel(0, 0, 0);
		}

		for (int iter = 0; iter < ITERATIONS; iter++) {

			/* Reset the state for this iteration */
			for (int i = 0; i < k; i++) {
				clusterAverages[i].setValue(0, 0, 0);
				clusterCounts[i] = 0;
			}

			/*
			 * Find each pixel's cluster and keep track of that cluster's
			 * averages
			 */
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int cluster = findNearestCluster(original[x][y], clusters);
					clusterAverages[cluster].addError(original[x][y], 1);
					clusterCounts[cluster]++;
				}
			}

			/* Find the new cluster centers by averaging pixel values */
			for (int i = 0; i < k; i++) {
				if (clusterCounts[i] != 0) {
					clusterAverages[i].scalePixel(1 / (double) clusterCounts[i]);
					clusters[i].setValue(clusterAverages[i]);
				}
			}
		}

		/* Make a palette of new pixel objects, not references to the original */
		for (Pixel cluster : clusters) {
			pixelPalette.add(cluster.round());
		}

		/* Draw an image with the clustered palette values */
		Pixel[][] clusteredPx = new Pixel[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int cluster = findNearestCluster(original[x][y], clusters);
				clusteredPx[x][y] = pixelPalette.get(cluster);
			}
		}

		return clusteredPx;
	}

	/**
	 * Helper function to assign a pixel to a cluster
	 * 
	 * @param original
	 *            The pixel to assign to a cluster
	 * @param clusters
	 *            An array of the current clusters
	 * @return The index to the cluster for this pixel
	 */
	private static int findNearestCluster(Pixel original, Pixel[] clusters) {
		double minDist = Double.MAX_VALUE;
		int cluster = -1;
		for (int i = 0; i < clusters.length; i++) {
			double distToCluster = original.distance(clusters[i]);
			if (distToCluster < minDist) {
				minDist = distToCluster;
				cluster = i;
			}
		}
		return cluster;
	}

	/**
	 * Returns K initial random cluster centers
	 * 
	 * @param original
	 *            The image to pick random values from
	 * @param k
	 *            The number of random values to pick
	 * @return K random values from the original image
	 */
	static Pixel[] getInitialRandomClusters(Pixel[][] original, int k) {

		int width = original.length;
		int height = original[0].length;

		Pixel clusters[] = new Pixel[k];

		/* Get K random cluster centers, making sure not to overlap */
		Random rand = new Random(System.currentTimeMillis());
		int clusters1D[] = new int[k];
		int clusterInd = 0;
		while (clusterInd < k) {
			int randomCluster = rand.nextInt(width * height);
			for (int i = 0; i < clusterInd; i++) {
				if (clusters1D[i] == randomCluster) {
					break;
				}
			}
			clusters1D[clusterInd] = randomCluster;
			clusters[clusterInd++] = new Pixel(original[randomCluster % width]
					                                   [randomCluster / width]);
		}

		return clusters;
	}
}
