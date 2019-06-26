package needlepointer;

import java.util.ArrayList;

public class Nearest {

    public static Pixel[][] dither_helper(Pixel[][] original, ArrayList<Pixel> pixelPalette) {
        int width = original.length;
        int height = original[0].length;

        Pixel ditheredImage[][] = new Pixel[width][height];

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                ditheredImage[w][h] = findNearest(original[w][h], pixelPalette);
            }
        }

        return ditheredImage;
    }

    private static Pixel findNearest(Pixel pixel, ArrayList<Pixel> pixelPalette) {
        double minDist = Double.MAX_VALUE;
        Pixel closest = null;
        for (Pixel usableColor : pixelPalette) {
            double dist = usableColor.distance(pixel);
            if (dist < minDist) {
                minDist = dist;
                closest = usableColor;
            }
        }
        return closest;
    }

}
