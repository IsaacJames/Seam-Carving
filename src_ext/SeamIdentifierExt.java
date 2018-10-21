import java.io.IOException;

public class SeamIdentifierExt {

    // creates a matrix of the minimum cumulative energies in an energy matrix
    public static double[][] calculateMinEnergy(double[][] energyMatrix) throws IOException {
        double[][] minEnergies = new double[energyMatrix.length][energyMatrix[0].length];

        // copy the array
        for (int y = 0; y < energyMatrix.length; y++) {
            for(int x = 0; x < energyMatrix[0].length; x++) {
                minEnergies[y][x] = energyMatrix[y][x];
            }
        }
        // add edge buffers with maximum value so that they are not included to the path
        for (int y = 0; y < minEnergies.length - 1; y++) {
            minEnergies[y + 1][0] = Integer.MAX_VALUE;
            minEnergies[y + 1][minEnergies[0].length - 1] = Integer.MAX_VALUE;
        }
        // for each pixel, calculate its minimum energy value by adding its energy to the one smallest from three pixels
        // above it
        for (int y = 2; y < minEnergies.length - 1; y++) {
            for (int x = 1; x < minEnergies[0].length - 1; x++) {
                double minValue = Math.min(Math.min(minEnergies[y - 1][x - 1], minEnergies[y - 1][x]), minEnergies[y - 1][x + 1]);
                minEnergies[y][x] += minValue;
            }
        }
        return minEnergies;
    }

    // calculates the shortest path in a matrix of minimum cumulative energies
    public static int[][] calculateShortestPath(double[][] energyMatrix) {
        // create an array that holds the coordinates for the shortest path
        int[][] path = new int[energyMatrix.length - 2][2];
        double minValue = Double.MAX_VALUE;
        // keep track of the x value of the path
        int prevX = 0;

        // find the smallest energy in the bottom row
        for (int x = 1; x < energyMatrix[0].length - 1; x++) {
            if (energyMatrix[energyMatrix.length - 2][x] <= minValue) {
                minValue = energyMatrix[energyMatrix.length - 2][x];
                prevX = x;
            }
        }
        // add the smallest energy pixel from the bottom row to the path
        path[0] = new int[]{prevX, energyMatrix.length - 2};

        // in every row find the smallest energy value of the three above the previous x
        for (int y = energyMatrix.length - 2; y > 1; y--) {
            // check for the cases of diagonal pixels from the current one being minimum value, otherwise
            // prevX does not change
            if (energyMatrix[y - 1][prevX - 1] < energyMatrix[y - 1][prevX] &&
                    energyMatrix[y - 1][prevX - 1] < energyMatrix[y - 1][prevX + 1]) {
                prevX = prevX - 1;
            }

            else if (energyMatrix[y - 1][prevX + 1] < energyMatrix[y - 1][prevX - 1] &&
                    energyMatrix[y - 1][prevX + 1] < energyMatrix[y - 1][prevX]) {
                prevX = prevX + 1;
            }
            // add it to the path
            path[energyMatrix.length - 1 - y] = new int[]{prevX, y - 1};
        }
        return path;
    }
}
