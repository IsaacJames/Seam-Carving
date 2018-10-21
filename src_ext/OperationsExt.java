import java.awt.image.*;

public class OperationsExt {

    public static BufferedImage blurImage(BufferedImage image) {
        // ideally the sum of all the float numbers used in blurring kernel should add up to 1,
        // in that case the image quality is not reduced that much
        float blurriness = 1.0f / 9.0f;
        // create a blurring kernel: a new pixel is an equal mix of pixels around it
        float[] blurringKernel = {
                blurriness, blurriness, blurriness,
                blurriness, blurriness, blurriness,
                blurriness, blurriness, blurriness
        };
        // use BufferedImageOp to create blurring operation
        BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurringKernel));
        // return blurred image
        return blur.filter(image, null);
    }


    public static BufferedImage sharpenImage(BufferedImage image) {
        // index can be changed to get more or less sharpened image, other values will change accordingly
        // recommended values are from around -0.5f to -2.0f
        float index = -0.5f;
        float[] blurringKernel = {
                0.0f, index, 0.0f,
                index, -4 * index + 1, index,
                0.0f, index, 0.0f
        };
        // use BufferedImageOp to create sharpening operation
        BufferedImageOp sharpen = new ConvolveOp(new Kernel(3, 3, blurringKernel));
        // return sharpened image
        return sharpen.filter(image, null);
    }


    public static BufferedImage posterizeImage(BufferedImage image) {
        short[] posterised = new short[256];
        // levelOfPosterising can be changed to get more or less posterised image
        // recommended values are 16, 32, 40, 64, etc.
        int levelOfPosterising = 64;
        // redirects every pixel's colour to one of the few
        for (int i = 0; i < 256; i++) {
            posterised[i] = (short)(i - (i % levelOfPosterising));
        }
        // use BufferedImageOp to create posterising operation
        BufferedImageOp posterise = new LookupOp(new ShortLookupTable(0, posterised), null);
        // return posterised image
        return posterise.filter(image, null);
    }

}
