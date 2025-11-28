import java.awt.*;
import java.awt.image.BufferedImage;

void main() throws ExecutionException, InterruptedException {
//    MyMandelbrot  gen = new MyMandelbrot (800, 600, 200, -2.5, 1.5, -1.25, 1.25);
//    float[][] colors = gen.generateMandelbrotThreads();
//    BufferedImage img = gen.paintMandelbrot(colors);
//    gen.saveImage(img, "image.jpg");
//    gen.showImage(img);

    int iter=20;
    int[] pixelNum = {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
    long[] pixelDuration = new long[pixelNum.length];

    int numThreads = Runtime.getRuntime().availableProcessors();

    // THREADS
    System.out.println("Threads");
    for (int i = 0; i < pixelNum.length; i++) {
        double avgDuration = 0;
        System.out.println("Size " + pixelNum[i] + "x" + pixelNum[i]);
        for (int j = 0; j < iter; j++) {
            MyMandelbrot fractal = new MyMandelbrot(pixelNum[i], pixelNum[i], 200, -2.5, 1.5, -1.25, 1.25);
            long start = System.nanoTime();
            fractal.generateMandelbrotThreads();
            long end = System.nanoTime();
            avgDuration += (end - start);
        }
        pixelDuration[i] = (long) (avgDuration / iter);
    }
    write_to_csv(pixelDuration, pixelNum, "Mandelbrot_threads.csv");

    // POOL
    System.out.println("One Pool");
    ExecutorService ex = Executors.newFixedThreadPool(numThreads);
    CompletionService<String> cs = new ExecutorCompletionService<>(ex);

    for (int i = 0; i < pixelNum.length; i++) {
        double avgDuration = 0;
        System.out.println("Size " + pixelNum[i] + "x" + pixelNum[i]);
        int rowsPerThread = pixelNum[i] / numThreads;

        for (int j = 0; j < iter; j++) {
            MyMandelbrot fractal = new MyMandelbrot(pixelNum[i], pixelNum[i], 200, -2.5, 1.5, -1.25, 1.25);

            long start = System.nanoTime();

            for (int k = 0; k < numThreads; k++) {
                int startY = rowsPerThread * k;
                int endY = rowsPerThread * k + rowsPerThread;
                cs.submit(() -> {
                    fractal.generateMandelbrotJob(startY, endY);
                    return "";
                });
            }
            for (int k = 0; k < numThreads; k++) {
                cs.take();
            }

            long end = System.nanoTime();
            avgDuration += (end - start);

        }
        pixelDuration[i] = (long) (avgDuration / iter);
    }
    ex.shutdown();
    ex.awaitTermination(1, TimeUnit.DAYS);
    write_to_csv(pixelDuration, pixelNum, "Mandelbrot_pool.csv");

// MANY POOLS
    System.out.println("Many Pools with max_rows");

//  inny rozmiar niż max. liczba wątków  4, 8, 16, 32, 64, 128 pikseli
    int[] rows = {4, 8, 16, 32, 64, 128};
    for (int t = 0; t < rows.length; t++) {
        for (int i = 0; i < pixelNum.length; i++) {
            int rowsPerThread = rows[t];
            if (rowsPerThread > pixelNum[i])
                rowsPerThread = pixelNum[i];

            double avgDuration = 0;
            System.out.println("Size " + pixelNum[i] + "x" + pixelNum[i] + " max size "+ rowsPerThread);

            for (int j = 0; j < iter; j++) {
                MyMandelbrot fractal = new MyMandelbrot(pixelNum[i], pixelNum[i], 200, -2.5, 1.5, -1.25, 1.25);
                numThreads = pixelNum[i] / rowsPerThread;

                long start = System.nanoTime();
                ex = Executors.newFixedThreadPool(numThreads);
                cs = new ExecutorCompletionService<>(ex);

                for (int k = 0; k < numThreads; k++) {
                    int startY = rowsPerThread * k;
                    int endY = rowsPerThread * k + rowsPerThread;
                    cs.submit(() -> {
                        fractal.generateMandelbrotJob(startY, endY);
                        return "";
                    });
                }
                for (int k = 0; k < numThreads; k++) {
                    cs.take();
                }
                ex.shutdown();
                ex.awaitTermination(1, TimeUnit.DAYS);
                long end = System.nanoTime();
                avgDuration += (end - start);

            }
            pixelDuration[i] = (long) (avgDuration / iter);
        }
        write_to_csv(pixelDuration, pixelNum, "Mandelbrot_pool_size"+rows[t]+".csv");

    }
}

void write_to_csv(long[] pixelDuration, int [] pixelNum, String fileName){
    try (FileWriter writer = new FileWriter(fileName)) {
        writer.write("pixels,time_ns\n");
        for (int i = 0; i < pixelNum.length; i++) {
            writer.write(pixelNum[i] + "," + pixelDuration[i] + "\n");
        }
    } catch (IOException e) {
        System.out.println("An error occurred during writing to file.");
        e.printStackTrace();
    }
}