###### This program was set as coursework for a partnered programming projects module. The code was written in my first year of university study in March 2018.

# seam-carving

This is a program for re-sizing images using seam carving. Pixel seams with the lowest energy contiguous path through the image are added or removed. The result is an image resizing tool which is content-aware and is less likely to distort image content than a traditional scaling method.

### Build instructions:
#### Basic:
From src folder: *java Project2 \<image> <number of seams to remove> <’h’ for horizontal or ’v’ for vertical resize>*

The src folder contains the basic program. The program requires three command line arguments: an image file, an integer denoting the number pixels dimensions should be decreased by and a character ('h' or 'v') corresponding to the options of horizontal or vertical re-sizing. This basic program only allows for decrease in image dimensions. The seam carving method consists of three steps:
1. Calculating the pixel energy matrix of the image
2. Identifying a seam (lowest energy contiguous path) through the energy matrix
3. Removing the corresponding pixel seam from the image

The steps are then repeated for each seam that is removed. However, when repeating step 1 the pixels adjacent to the removed pixels are the only energies that require re-calculation. This is due to the fact that a pixel's energy is calculated using the RGB values of it's neighbouring pixels.

#### Extended:

From src folder: 

*java Project2Ext \<image> \<output file type> <number of seams to remove/add> <’r’ for removal or ’a’ for addition> <’h’ for horizontal or ’v’ for vertical resize>*

The src_ext folder contains an extended version of the basic program. Additional functionality includes:
+ The addition of an output file type argument which allows the user to specify the image extension used for the re-sized image.
+ The user now has the option to increase the image dimensions by typing 'a' for the fourth argument instead of 'r' for a decrease in dimensions.
+ The user is also prompted to select an image processing option after re-sizing is finished (drifted from basic functionality a little here, but some interested techniques involved in blurring, sharpenening and posterising).
