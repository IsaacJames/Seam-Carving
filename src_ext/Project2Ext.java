import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Project2Ext {
    public static int[][][] pixels;

    public static void main(String[] args) throws IOException {

        final String USAGE = "Usage: java Project2Ext <image> <image extension> <number of pixels to remove/add> " +
                "<'r' for pixel removal or 'a' for pixel addition> <'h' for horizontal or 'v' for vertical resize>";

        try {
            // checking if required amount of arguments is supplied
            if (args.length != 5) {
                System.out.println(USAGE);
                System.exit(0);
            }
            BufferedImage image = ImageIO.read(new File(args[0]));  // get image
            int pixelsChanged = Integer.parseInt(args[2]);  // get how many pixels should be removed
            String resizeType = args[3];  // get choice for pixel removal/addition
            String resizeDirection = args[4];  // get choice for resize direction
            boolean rotated = false;
            boolean removingPixels = false;

            // perform pixel addition for any value larger than 0
            if (resizeType.equals("a") && pixelsChanged > 0) {
                removingPixels = false;
            // perform pixel removal if it is possible to remove given number of pixels
            } else if (resizeType.equals("r") && pixelsChanged <= image.getWidth()) {
                removingPixels = true;
            } else {
                System.out.println(USAGE);
                System.exit(0);
            }

            // perform vertical resize if it is possible to remove given number of pixels
            if (resizeDirection.equals("v") && pixelsChanged <= image.getHeight() && pixelsChanged > 0) {
                rotated = true;
            // perform horizontal resize if it is possible to remove given number of pixels
            } else if (resizeDirection.equals("h") && pixelsChanged <= image.getWidth() && pixelsChanged > 0) {
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

            // initial seam removal/addition uses createEnergyMatrix method
            int[][][] expandedPixels = EnergyCalculationExt.expandPixels(pixels);
            double[][] energyMatrix = EnergyCalculationExt.createEnergyMatrix(expandedPixels);
            double[][] minEnergy = SeamIdentifierExt.calculateMinEnergy(energyMatrix);
            int[][] shortestPath = SeamIdentifierExt.calculateShortestPath(minEnergy);

            // for removing pixels
            if (removingPixels) {
                pixels = ImageWriterExt.removePixelSeam(pixels, shortestPath);
            // for adding pixels
            } else {
                pixels = ImageWriterExt.duplicatePixelSeam(pixels, shortestPath);
            }

            // further seam removal/addition uses recalculateEnergy method
            for (int j = 1; j < pixelsChanged; j++) {
                expandedPixels = EnergyCalculationExt.expandPixels(pixels);

                // for removing pixels
                if (removingPixels) {
                    energyMatrix = EnergyCalculationExt.removeEnergySeam(energyMatrix, shortestPath);
                    energyMatrix = EnergyCalculationExt.recalculateEnergy(expandedPixels, shortestPath, energyMatrix);
                    minEnergy = SeamIdentifierExt.calculateMinEnergy(energyMatrix);
                    shortestPath = SeamIdentifierExt.calculateShortestPath(minEnergy);
                    pixels = ImageWriterExt.removePixelSeam(pixels, shortestPath);
                // for adding pixels
                } else {
                    energyMatrix = EnergyCalculationExt.expandEnergyMatrix(energyMatrix, shortestPath);
                    energyMatrix = EnergyCalculationExt.markEnergySeam(energyMatrix, shortestPath);
                    minEnergy = SeamIdentifierExt.calculateMinEnergy(energyMatrix);
                    shortestPath = SeamIdentifierExt.calculateShortestPath(minEnergy);
                    pixels = ImageWriterExt.duplicatePixelSeam(pixels, shortestPath);
                }
            }

            // if the image was rotated, rotate it back to original orientation
            if (rotated) {
                for (int i = 0; i < 3; i++) {
                    pixels = rotateArray(pixels);
                }
            }

            image = ImageWriterExt.writeImage(pixels);

            Scanner reader = new Scanner(System.in);
            System.out.println("Would you like to perform additional image processing? Type 'y' for yes or 'n' for no.");
            String answer = reader.next();
            if (answer.equals("y")) {
                System.out.println("Type 1 for blurring, 2 for sharpening and 3 for posterising.");
                int choice = reader.nextInt();
                switch (choice) {
                    case 1: image = OperationsExt.blurImage(image);
                    break;
                    case 2: image = OperationsExt.sharpenImage(image);
                    break;
                    case 3: image = OperationsExt.posterizeImage(image);
                    break;
                    default: System.out.println("Command not recognised. The output image is created but it is not processed in any way.");
                }
            } else if (!answer.equals("n")) {
                System.out.println("Command not recognised. The output image is created but it is not processed in any way.");
            }
            // write image using user's chosen format name
            ImageIO.write(image, args[1], new File("output." + args[1]));

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