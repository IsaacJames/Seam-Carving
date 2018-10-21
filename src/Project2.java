import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Project2 {
    public static int[][][] pixels;

    public static void main(String[] args) throws IOException {

        final String USAGE = "Usage: java Project2 <image> <number of pixels to remove> <'h' for horizontal or 'v' for vertical resize>";

        try {
            // checking if required amount of arguments is supplied
            if (args.length != 3) {
                System.out.println(USAGE);
                System.exit(0);
            }
            BufferedImage image = ImageIO.read(new File(args[0]));  // get image
            int pixelsRemoved = Integer.parseInt(args[1]);  // get how many pixels should be removed
            String resizeDirection = args[2];  // get whether horizontal or vertical seams should be removed
            boolean rotated = false;

            // perform vertical resize if it is possible to remove given number of pixels
            if (resizeDirection.equals("v") && pixelsRemoved <= image.getHeight()) {
                rotated = true;
            // perform horizontal resize if it is possible to remove given number of pixels
            } else if (resizeDirection.equals("h") && pixelsRemoved <= image.getWidth()) {
                rotated = false;
            } else {
                System.out.println(USAGE);
                System.exit(0);
            }

            // read image into array of RGB values
            pixels = new int[image.getHeight()][image.getWidth()][3];
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    pixels[y][x] = image.getRaster().getPixel(x, y, new int[3]);
                }
            }

            // if resizing vertically, rotate the picture
            if (rotated) {
                pixels = rotateArray(pixels);
            }

            // initial seam removal uses createEnergyMatrix method
            int[][][] expandedPixels = EnergyCalculation.expandPixels(pixels);
            double[][] energyMatrix = EnergyCalculation.createEnergyMatrix(expandedPixels);
            double[][] minEnergy = SeamIdentifier.calculateMinEnergy(energyMatrix);
            int[][] shortestPath = SeamIdentifier.calculateShortestPath(minEnergy);
            pixels = ImageWriter.removePixelSeam(pixels, shortestPath);

            // for all other times use recalculateEnergy method
            for (int j = 1; j < pixelsRemoved; j++) {
                expandedPixels = EnergyCalculation.expandPixels(pixels);
                energyMatrix = EnergyCalculation.recalculateEnergy(expandedPixels, shortestPath, energyMatrix);
                minEnergy = SeamIdentifier.calculateMinEnergy(energyMatrix);
                shortestPath = SeamIdentifier.calculateShortestPath(minEnergy);
                pixels = ImageWriter.removePixelSeam(pixels, shortestPath);
            }

            // if the image was rotated, rotate it back to original orientation
            if (rotated) {
                for (int i = 0; i < 3; i++) {
                    pixels = rotateArray(pixels);
                }
            }

            image = ImageWriter.writeImage(pixels);
            ImageIO.write(image, "jpg", new File("output.jpg"));

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println(USAGE);
        }
    }

    // rotates given array 90 degrees clockwise
    private static int[][][] rotateArray(int[][][] pixels) {
        int[][][] rotated = new int[pixels[0].length][pixels.length][3];
        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[0].length; x++) {
                rotated[x][pixels.length - 1 - y] = pixels[y][x];
            }
        }
        return rotated;
    }
}