import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyMandelbrot  {

    private int width;
    private int height;
    private int max_iter = 200;
    private double x_min = -2.5;
    private double x_max = 1.5;
    private double y_max = 1.25;
    private double y_min = -1.25;

    public MyMandelbrot (int width, int height, int max_iter, double x_min, double x_max, double y_min, double y_max) {
        this.width = width;
        this.height = height;
        this.max_iter = max_iter;
        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;
    }

    public float[][] generateMandelbrot() {
        double pixelWidth = (x_max - x_min) / width;
        double pixelHeight = (y_max - y_min) / height;
        float[][] pixelColors = new float[height][width];

        for (int py = 0; py < height; py++) {
            double y0 = y_min + py * pixelHeight;
            for (int px = 0; px < width; px++) {
                double x0 = x_min + px * pixelWidth;

                double x = 0.0;
                double y = 0.0;
                int iter = 0;

                while (x * x + y * y <= 4 && iter < max_iter) {
                    double xtemp = x * x - y * y + x0;
                    y = 2 * x * y + y0;
                    x = xtemp;
                    iter++;
                }

                float value = (float) iter / max_iter;
                pixelColors[py][px] = value;
            }
        }
        return pixelColors;
    }

    public BufferedImage paintMandelbrot(float[][] colors) {

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                img.setRGB(x, y, new Color(colors[y][x], colors[y][x],colors[y][x]).getRGB());
            }
        }
        return img;
    }

    public void showImage(BufferedImage img) {
        JFrame frame = new JFrame("Mandelbrot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
    }

    public void saveImage(BufferedImage img, String filename) {
        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(img, "jpg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
