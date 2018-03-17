package com.knightlore.assets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteSheetMaker {
    
    static String baseFileName;
    static String mode;
    
    static int width;
    static int height;
    static int numDirs;
    static int numFrames;
    
    static Color alphaColor = Color.green;
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if (args.length == 0) {
            System.out.println("expected more arguments, got " + args.length);
            System.out.println("Usage:");
            System.out.println("-static <dimensions> <directions> <name>");
            System.out.println("-animated <dimensions> <directions> <frames> <name>");
        }
        
        // read operation mode
        mode = args[0];
        String[] dimString;
        switch (mode) {
        case "-static":
            if (args.length != 4) {
                System.out.println("expected 4 arguments, got " + args.length);
                System.out.println("Usage:");
                System.out.println("-static <dimensions> <directions> <name>");
            }
            // calc dimensions
            dimString = args[1].split("x");
            width = Integer.parseInt(dimString[0]);
            height = Integer.parseInt(dimString[1]);
            numDirs = Integer.parseInt(args[2]);
            // get file name
            baseFileName = args[3];
            createStatic();
            break;
        
        case "-animated":
            
            if (args.length != 5) {
                System.out.println("expected 5 arguments, got " + args.length);
                System.out.println("Usage:");
                System.out.println("-animated <dimensions> <directions> <frames> <name>");
            }
            // calc dimensions
            dimString = args[1].split("x");
            width = Integer.parseInt(dimString[0]);
            height = Integer.parseInt(dimString[1]);
            numDirs = Integer.parseInt(args[2]);
            // get frames
            numFrames = Integer.parseInt(args[3]);
            // get file name
            baseFileName = args[4];
            createAnimated();
            break;
        default:
            System.err.println("Unknown mode " + mode);
        }
    }
    
    static void createStatic() {
        
        int totalWidth = width;
        int totalHeight = height * numDirs;
        
        BufferedImage outImg = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        
        // get graphics setup for fast copying
        Graphics2D g2d = outImg.createGraphics();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2d.setComposite(alpha);
        
        // load in images, and output into buffer
        BufferedImage img = null;
        String fileName = null;
        for (int i = 0; i < numDirs; i++) {
            // get file name
            fileName = baseFileName + "_" + (i) + ".png";
            File file = new File(fileName);
            // try to read the image
            try {
                img = ImageIO.read(file);
            } catch (Exception e) {
                System.err.println(e);
                System.out.println("Failed to read image " + fileName + ", see above error ^");
                return;
            }
            /*
             * // wipe alpha out of the image for (int x = 0; x < width; x++) {
             * for (int y = 0; y < height; y++) { int col = img.getRGB(x, y);
             * int r = (col & 0x00ff0000) >> 16; int g = (col & 0x0000ff00) >>
             * 8; int b = (col & 0x000000ff); // set alpha if (r == 0 && g ==
             * 255 && b == 0) { img.setRGB(x, y, 0x00ffffff); } } }
             */
            // calc position
            int yPos = i * height;
            // splat image outside
            g2d.drawImage(img, 0, yPos, null);
            System.out.println("Copied " + fileName);
            
        }
        
        String outFileName = baseFileName + "_static_sprites.png";
        File outFile = new File(outFileName);
        
        try {
            ImageIO.write(outImg, "png", outFile);
        } catch (Exception e) {
            System.err.println(e);
            System.out.println("Failed to write final sprite sheet");
        } finally {
            g2d.dispose();
        }
        
        System.out.println("Spritesheet written to: " + outFileName);
        System.out.println("Done.");
    }
    
    static void createAnimated() {
        // TODO Auto-generated method stub
        int totalWidth = width*numFrames;
        int totalHeight = height * numDirs;
        
        BufferedImage outImg = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        
        // get graphics setup for fast copying
        Graphics2D g2d = outImg.createGraphics();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2d.setComposite(alpha);
        
        // load in images, and output into buffer
        BufferedImage img = null;
        String fileName = null;
        for (int f = 0; f < numFrames; f++) {
            int xPos = f * width;
            for (int d = 0; d < numDirs; d++) {
                // get file name
                fileName = baseFileName + "_" + (d) + "_f"+(f)+".png";
                File file = new File(fileName);
                // try to read the image
                try {
                    img = ImageIO.read(file);
                } catch (Exception e) {
                    System.err.println(e);
                    System.out.println("Failed to read image " + fileName + ", see above error ^");
                    return;
                }
                /*
                 * // wipe alpha out of the image for (int x = 0; x < width;
                 * x++) { for (int y = 0; y < height; y++) { int col =
                 * img.getRGB(x, y); int r = (col & 0x00ff0000) >> 16; int g =
                 * (col & 0x0000ff00) >> 8; int b = (col & 0x000000ff); // set
                 * alpha if (r == 0 && g == 255 && b == 0) { img.setRGB(x, y,
                 * 0x00ffffff); } } }
                 */
                // calc position
                int yPos = d * height;
                // splat image outside
                g2d.drawImage(img, xPos, yPos, null);
                System.out.println("Copied " + fileName);
                
            }
        }
        
        String outFileName = baseFileName + "_anim_sprites.png";
        File outFile = new File(outFileName);
        
        try {
            ImageIO.write(outImg, "png", outFile);
        } catch (Exception e) {
            System.err.println(e);
            System.out.println("Failed to write final sprite sheet");
        } finally {
            g2d.dispose();
        }
        
        System.out.println("Spritesheet written to: " + outFileName);
        System.out.println("Done.");
    }
    
}
