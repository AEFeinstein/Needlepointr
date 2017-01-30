# Needlepointr
Needlepointr is a tool to make needlepoint patterns from images. First it uses K Means clustering to identify the K most dominant colors in an image. Then it uses random order dithering to quantize the original image using only the K most dominant colors.

Random order dithering is something I came up with when frustrated with the results from Floyd-Steinberg dithering. To random order dither an image, iterate through all pixels randomly, quantize that pixel, and diffuse the quantization error to all unquantized neighboring pixels (up to 8). The diffused error is weighted such that adjacent pixels get twice as much of it as diagonal pixels.

Let me show you an example using The Starry Night, 9 colors, 18" wide pattern.

Here's The Starry Night:

![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/starry_night.jpg)

And here's the nine most dominant colors:

![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/starry_night_09_palette.png)

And here's what it looks like using those nine colors and Floyd-Steinberg dithering:

![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/starry_night_09_dither_fs.png)
![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/fs_comparison.gif)

And here's what it looks like using those nine colors and random order dithering:

![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/starry_night_09_dither_r.png)
![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/r_comparison.gif)

And here's a gif comparing the two dithering techniques. Note how the down and rightward error propagation in the Floyd-Steinberg dithered image is evident below the moon and in the hills. Floyd-Steinberg also has a much harder time quantizing the spire:

![text](https://raw.githubusercontent.com/AEFeinstein/Needlepointr/master/example/comparison.gif)
