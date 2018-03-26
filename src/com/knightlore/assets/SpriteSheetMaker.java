package com.knightlore.assets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * The second half of the graphics pipeline. This should be used as a command
 * line tool for creating sprite sheets.
 * 
 * @author James
 *
 */
public class SpriteSheetMaker {
    
    private static String baseFileName;
    private static int width;
    private static int height;
    private static int numDirs;
    private static int numFrames;
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("expected more arguments, got " + args.length);
            System.out.println("Usage:");
            System.out.println("-static <dimensions> <directions> <name>");
            System.out.println("-animated <dimensions> <directions> <frames> <name>");
            return;
        }
        
        // read operation mode
        String mode = args[0];
        String[] dimString;
        switch (mode) {
        case "-static":
            if (args.length != 4) {
                System.out.println("expected 4 arguments, got " + args.length);
                System.out.println("Usage:");
                System.out.println("-static <dimensions> <directions> <name>");
                return;
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
                return;
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
    
    private static void createStatic() {
        
        int totalWidth = width;
        int totalHeight = height * numDirs;
        
        BufferedImage outImg = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        
        // get graphics setup for fast copying
        Graphics2D g2d = outImg.createGraphics();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2d.setComposite(alpha);
        
        // load in images, and output into buffer
        BufferedImage img;
        String fileName;
        for (int i = 0; i < numDirs; i++) {
            // get file name
            fileName = baseFileName + "_" + (i) + ".png";
            File file = new File(fileName);
            // try to read the image
            try {
                img = ImageIO.read(file);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to read image " + fileName + ", see above error ^");
                return;
            }
            
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
            e.printStackTrace();
            System.out.println("Failed to write final sprite sheet");
        } finally {
            g2d.dispose();
        }
        
        System.out.println("Spritesheet written to: " + outFileName);
        System.out.println("Removing source files...");
        for (int d = 0; d < numDirs; d++) {
            // get file name
            fileName = baseFileName + "_" + (d) + ".png";
            File file = new File(fileName);
            // try to read the image
            try {
                file.delete();
            } catch (Exception e) {
                System.err.println(e);
                System.out.println("Failed to delete " + fileName + ", see above error ^");
                return;
            }
        }
        System.out.println("Done.");
    }
    
    private static void createAnimated() {
        int totalWidth = width * numFrames;
        int totalHeight = height * numDirs;
        
        BufferedImage outImg = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        
        // get graphics setup for fast copying
        Graphics2D g2d = outImg.createGraphics();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2d.setComposite(alpha);
        
        // load in images, and output into buffer
        BufferedImage img;
        String fileName;
        for (int f = 0; f < numFrames; f++) {
            int xPos = f * width;
            for (int d = 0; d < numDirs; d++) {
                // get file name
                fileName = baseFileName + "_" + (d) + "_f" + (f) + ".png";
                File file = new File(fileName);
                // try to read the image
                try {
                    img = ImageIO.read(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to read image " + fileName + ", see above error ^");
                    return;
                }
                
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
            e.printStackTrace();
            System.out.println("Failed to write final sprite sheet");
        } finally {
            g2d.dispose();
        }
        
        System.out.println("Spritesheet written to: " + outFileName);
        System.out.println("Removing source files...");
        for (int f = 0; f < numFrames; f++) {
            for (int d = 0; d < numDirs; d++) {
                // get file name
                fileName = baseFileName + "_" + (d) + "_f" + (f) + ".png";
                File file = new File(fileName);
                // try to read the image
                try {
                    file.delete();
                } catch (Exception e) {
                    System.err.println(e);
                    System.out.println("Failed to delete " + fileName + ", see above error ^");
                    return;
                }
            }
        }
        System.out.println("Done.");
    }
    
}
