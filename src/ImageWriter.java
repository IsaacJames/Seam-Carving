import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageWriter {

    // converts integer pixel array into BufferedImage
    public static BufferedImage writeImage(int[][][] pixels) throws IOException {
        // create a image with 8-bit RGB color components packed into integer pixels
        BufferedImage image = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_RGB);

        // convert RGB integer array into integer value
        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[0].length; x++) {
                int[] rgb = pixels[y][x];
                int t = rgb[0];  // bits 0-7 are the red value
                t = (t << 8) + rgb[1];  // bits 8-15 are the green value
                t = (t << 8) + rgb[2];  // bits 16-23 are the blue value
                image.setRGB(x, y, t);
            }
        }
        return image;
    }

    // removes seam in pixel array
    public static int[][][] removePixelSeam(int[][][] image, int[][] path) throws IOException {
        // create a new array, one of its dimensions is one pixel smaller than the original array
        int[][][] newImage = new int[image.length][image[0].length - 1][3];

        // for every row (y coordinate) in the array, look for the x coordinate of the path
        for (int y = 0; y < image.length; y++) {
            // before the path coordinate, add all entries from old matrix to the new one accordingly
            for (int x = 0; x < path[path.length - 1 - y][0] && x != image[0].length - 1; x++) {
                newImage[y][x] = image[y][x];
            }
            // after the x coordinate is reached, the rest of the values in that row has to move by one to
            // the left in the matrix (in this way skipping the seam that has to be removed)
            for (int x = path[path.length - 1 - y][0] + 1; x < image[0].length; x++) {
                newImage[y][x - 1] = image[y][x];
            }
        }
        return newImage;
    }
}
