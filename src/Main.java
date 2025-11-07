import java.awt.*;
import java.awt.image.BufferedImage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    MyMandelbrot  gen = new MyMandelbrot (800, 600, 200, -2.5, 1.5, -1.25, 1.25);
    float[][] colors = gen.generateMandelbrot();
    BufferedImage img = gen.paintMandelbrot(colors);
    gen.saveImage(img, "image.jpg");
    gen.showImage(img);

    int iter=100;
    int[] pixelNum = {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
    long[] pixelDuration = new long[pixelNum.length];

    for (int i = 0; i < pixelNum.length; i++) {
        double avgDuration = 0;
        System.out.println("Size " + pixelNum[i] + "x" + pixelNum[i]);
        for (int j = 0; j < iter; j++) {
            MyMandelbrot fractal = new MyMandelbrot(pixelNum[i], pixelNum[i], 200, -2.5, 1.5, -1.25, 1.25);
            long start = System.nanoTime();
            fractal.generateMandelbrot();
            long end = System.nanoTime();
            avgDuration += (end - start);
        }
        pixelDuration[i] = (long) (avgDuration / iter);
    }
    try (FileWriter writer = new FileWriter("Mandelbrot.csv")) {
        writer.write("pixels,time_ns\n");
        for (int i = 0; i < pixelNum.length; i++) {
            writer.write(pixelNum[i] + "," + pixelDuration[i] + "\n");
        }
    } catch (IOException e) {
        System.out.println("An error occurred during writing to file.");
        e.printStackTrace();
    }

}
