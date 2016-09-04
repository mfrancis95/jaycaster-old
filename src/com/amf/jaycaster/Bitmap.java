package com.amf.jaycaster;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Bitmap {
    
    public static Bitmap fallback;
    
    private static final HashMap<String, Bitmap> bitmaps;
    
    static {
        bitmaps = new HashMap<>();
    }
    
    public static Bitmap get(String name) {
        Bitmap bitmap = bitmaps.get(name);
        if (bitmap == null) {
            bitmap = fallback;
        }
        return bitmap;
    }
    
    private static void load(URL url) throws IOException, URISyntaxException {
        BufferedImage loaded = ImageIO.read(url);
        BufferedImage image = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(loaded, 0, 0, null);
        String name = Paths.get(url.toURI()).getFileName().toString();
        bitmaps.put(name.substring(0, name.indexOf(".")), new Bitmap(image));
    }
    
    public static void loadFile(String file) throws IOException, MalformedURLException, URISyntaxException {
        load(new URL(file));
    }
    
    public static void loadResource(String resource) throws IOException, URISyntaxException {
        load(Bitmap.class.getResource(resource));
    }
    
    public static void put(String name, Bitmap bitmap) {
        bitmaps.put(name, bitmap);
    }
    
    public final int[] pixels;
    
    public final int height, width;
    
    public Bitmap(BufferedImage image) {
        this(image.getWidth(), image.getHeight(), ((DataBufferInt) image.getRaster().getDataBuffer()).getData());
    }
    
    public Bitmap(int width, int height) {
        pixels = new int[width * height];
        this.width = width;
        this.height = height;
    }
    
    public Bitmap(int width, int height, int[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }
    
    public void fill(int color) {
        Arrays.fill(pixels, color);
    }
    
    public int getPixel(int x, int y) {
        return pixels[x + y * width];
    }
    
    public void setPixel(int x, int y, int pixel) {
        pixels[x + y * width] = pixel;
    }
    
}