package com.textextraction;

import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * Main - Sample usage of ImageTextExtractor for OCR
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Initialize the text extractor
            ImageTextExtractor extractor = new ImageTextExtractor();
            
            // Optional: Set language (default is "eng")
            extractor.setLanguage("eng");
            
            // Example usage
            System.out.println("=== Image Text Extraction using tess4j ===\n");
            
            if (args.length > 0) {
                // Extract text from image provided as command line argument
                String imagePath = args[0];
                
                extractAndPrint(extractor, imagePath);
            } else {
                String defaultImagePath = "C:\\Users\\sudhi\\Downloads\\photo.jpg";
                extractAndPrint(extractor, defaultImagePath);
                // Display usage information
                //displayUsage();
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract text from image and print results
     * @param extractor ImageTextExtractor instance
     * @param imagePath Path to image file
     */
    private static void extractAndPrint(ImageTextExtractor extractor, String imagePath) {
        try {
            System.out.println("Processing image: " + imagePath);
            System.out.println("-".repeat(50));
            
            long startTime = System.currentTimeMillis();
            String extractedText = extractor.extractText(imagePath);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\nExtracted Text:");
            System.out.println("-".repeat(50));
            System.out.println(extractedText);
            System.out.println("-".repeat(50));
            System.out.println("\nProcessing time: " + (endTime - startTime) + " ms");
            
        } catch (TesseractException e) {
            System.err.println("OCR Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
        }
    }

    /**
     * Display usage information
     */
    private static void displayUsage() {
        System.out.println("Usage: java -jar image-processing-ocr-1.0.0.jar <image_path>");
        System.out.println("\nExample:");
        System.out.println("  java -jar image-processing-ocr-1.0.0.jar sample.png");
        System.out.println("  java -jar image-processing-ocr-1.0.0.jar path/to/document.jpg");
        System.out.println("\nSupported Formats:");
        System.out.println("  - PNG, JPG, JPEG, TIFF, GIF, BMP");
        System.out.println("\nNote:");
        System.out.println("  - Ensure tessdata folder is in the classpath or set the data path");
        System.out.println("  - Download language files from: https://github.com/UB-Mannheim/tesseract/wiki");
    }
}
