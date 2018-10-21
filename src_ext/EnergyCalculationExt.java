import java.io.IOException;

public class EnergyCalculationExt {

    // calculates the energy of a pixel given its 2D array coordinates, x and y
    private static double calculatePixelEnergy(int[][][] pixels, int x, int y) {
        // create arrays that store differences between each of rgb values of two neighbouring pixels of the current one
        int[] xDifferences = new int[3];
        int xGradient = 0;
        int[] yDifferences = new int[3];
        int yGradient = 0;
        for (int i = 0; i < 3; i++) {
            // finds absolute difference between rgb values of the pixels to the left and right of the wanted pixel
            xDifferences[i] = Math.abs(pixels[y][x - 1][i] - pixels[y][x + 1][i]);
            // squares that difference
            xGradient += Math.pow(xDifferences[i], 2);
            // finds absolute difference between rgb values of the pixels above and below the wanted pixel
            yDifferences[i] = Math.abs(pixels[y - 1][x][i] - pixels[y + 1][x][i]);
            // squares the difference
            yGradient += Math.pow(yDifferences[i], 2);
        }
        // returns the total energy of the pixel - square root of the sum of gradients
        return Math.sqrt(xGradient + yGradient);
    }

    // creates a 2D array of all pixel energies in a RGB pixel array
    public static double[][] createEnergyMatrix(int[][][] pixels) throws IOException {
        // create a matrix to store energy values
        double[][] energyMatrix = new double[pixels.length][pixels[0].length];
        // calculate energy values for every pixel using the expanded matrix of the original image
        for (int y = 1; y < pixels.length - 1; y++) {
            for (int x = 1; x < pixels[0].length - 1; x++) {
                energyMatrix[y][x] = calculatePixelEnergy(pixels, x, y);
            }
        }
        return energyMatrix;
    }

    // updates energy matrix with recalculated energies of pixels affected by seam removal
    public static double[][] recalculateEnergy(int[][][] expandedPixels, int[][] path, double[][] energyMatrix) throws IOException {
        // remove the seam from the energy matrix
        energyMatrix = removeEnergySeam(energyMatrix, path);

        // for every pixel in the removed path, recalculate energy values for the two pixels which were next to it
        for (int[] xy : path) {
            // making sure that pixel in the path is not the one on the left most column
            if (xy[0] - 2 > 0) {
                energyMatrix[xy[1]][xy[0] - 1] = calculatePixelEnergy(expandedPixels, xy[0] - 1, xy[1]);
                energyMatrix[xy[1]][xy[0] - 2] = calculatePixelEnergy(expandedPixels, xy[0] - 2, xy[1]);
            }
        }
        return energyMatrix;
    }

    // expands RGB pixel array with buffers to allow for energy calculation
    public static int[][][] expandPixels(int[][][] pixels) {
        // create an expanded array
        int[][][] expanded = new int[pixels.length + 2][pixels[0].length + 2][3];
        System.arraycopy(pixels[pixels.length - 1], 0, expanded[0], 1, pixels[0].length);
        System.arraycopy(pixels[0], 0, expanded[expanded.length - 1], 1, pixels[0].length);

        // fill the left most column of the expanded array with the right most column of the original array and vice versa
        for (int y = 0; y < pixels.length; y++) {
            expanded[y + 1][0] = pixels[y][pixels[0].length - 1];
            expanded[y + 1][expanded[0].length - 1] = pixels[y][0];
        }

        // fill in the rest of the expanded array with the pixels from the original array
        for (int y = 1; y < expanded.length - 1; y++) {
            for (int x = 1; x < expanded[0].length - 1; x++) {
                expanded[y][x] = pixels[y - 1][x - 1];
            }
        }
        return expanded;
    }

    // expands energy matrix so its size corresponds to that of an image with a duplicated seam
    public static double[][] expandEnergyMatrix(double[][] energyMatrix, int[][] path) throws IOException {
        // create a new array with dimensions corresponding to an image with a width increased by 1 pixel
        double[][] newEnergyMatrix = new double[energyMatrix.length][energyMatrix[0].length + 1];

        // for every row (y coordinate) in the array, look for the x coordinate of the path
        for (int y = 1; y < energyMatrix.length - 1; y++) {
            // up to and including the path coordinate, add all entries from old matrix to the new one accordingly
            for(int x = 0; x <= path[path.length - y][0]; x++) {
                newEnergyMatrix[y][x] = energyMatrix[y][x];
            }
            // after the x coordinate of the path is reached add the corresponding energy again, then continue
            // copying the old array (effectively duplicating the energy value)
            for (int x = path[path.length - y][0]; x < energyMatrix[0].length; x++) {
                newEnergyMatrix[y][x + 1] = energyMatrix[y][x];
            }
        }
        return newEnergyMatrix;
    }

    // removes the seam from the energy matrix
    public static double[][] removeEnergySeam(double[][] energyMatrix, int[][] path) throws IOException {
        // create a new array, one of its dimensions is one pixel smaller than the original array
        double[][] newEnergyMatrix = new double[energyMatrix.length][energyMatrix[0].length - 1];

        // for every row (y coordinate) in the array, look for the x coordinate of the path
        for (int y = 1; y < energyMatrix.length - 1; y++) {
            // before the path coordinate, add all entries from old matrix to the new one accordingly
            for (int x = 0; x < path[path.length - y][0]; x++) {
                newEnergyMatrix[y][x] = energyMatrix[y][x];
            }
            // after the x coordinate of the path is reached, the rest of the values in that row have to move by one to
            // the left in the matrix (in this way skipping the seam that has to be removed)
            for (int x = path[path.length - y][0] + 1; x < energyMatrix[0].length; x++) {
                newEnergyMatrix[y][x - 1] = energyMatrix[y][x];
            }
        }
        return newEnergyMatrix;
    }

    // marks the seam (and its duplicate) in the energy matrix with a high energy
    public static double[][] markEnergySeam(double[][] energyMatrix, int[][] path) throws IOException {
        for (int[] xy : path) {
            energyMatrix[xy[1]][xy[0]] = 700;  // mark the seam
            energyMatrix[xy[1]][xy[0] + 1] = 700;  // mark the duplicated seam
        }
        return energyMatrix;
    }
}
