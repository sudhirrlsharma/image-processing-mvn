package com.textextraction;

import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * Main - CLI tool for extracting text from images
 * Usage:
 *   - java -cp . com.textextraction.Main <image_path>
 *   - java -cp . com.textextraction.Main <image_path> <language> <format>
 *
 * Formats: plain, json, markdown, structured
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Initialize the text extractor
            ImageTextExtractor extractor = new ImageTextExtractor();
            
            System.out.println("=== Image Text Extraction using Tesseract OCR ===\n");
            
            String imagePath = "";
            if (args.length == 0) {
                imagePath = "C:\\Users\\sudhi\\Downloads\\photo.jpg";
                // displayUsage();
                // System.exit(0);
                }else{
                imagePath = args[0];
                }
            
            String language = args.length > 1 ? args[1] : "eng";
            String format = args.length > 2 ? args[2] : "plain";
            
            // Create request and extract text
            ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
                imagePath, language, format
            );
            
            ImageTextExtractor.ExtractTextResponse response = extractor.extractTextFormatted(request);
            
            // Display results
            displayResults(response);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Display extraction results
     * @param response The extraction response
     */
    private static void displayResults(ImageTextExtractor.ExtractTextResponse response) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Extraction Results:");
        System.out.println("=".repeat(60));
        System.out.println("Image: " + response.imageName);
        System.out.println("File Size: " + response.imageSize + " bytes");
        System.out.println("Format: " + response.format);
        System.out.println("Status: " + (response.success ? "SUCCESS" : "FAILED"));
        
        if (!response.success) {
            System.out.println("Error: " + response.errorMessage);
        } else {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Formatted Output:");
            System.out.println("-".repeat(60));
            System.out.println(response.formattedText);
            System.out.println("-".repeat(60));
        }
    }

    /**
     * Display usage information
     */
    private static void displayUsage() {
        System.out.println("Usage: java com.textextraction.Main <image_path> [language] [format]");
        System.out.println("\nArguments:");
        System.out.println("  image_path  - Path to the image file (required)");
        System.out.println("  language    - OCR language code (default: eng)");
        System.out.println("                Common codes: eng, fra, deu, spa, ita, chi_sim, jpn");
        System.out.println("  format      - Output format (default: plain)");
        System.out.println("                Options: plain, json, markdown, structured");
        System.out.println("\nExamples:");
        System.out.println("  java com.textextraction.Main document.png");
        System.out.println("  java com.textextraction.Main document.png eng plain");
        System.out.println("  java com.textextraction.Main document.png deu structured");
        System.out.println("  java com.textextraction.Main scan.jpg spa json");
        System.out.println("\nSupported Image Formats:");
        System.out.println("  PNG, JPG, JPEG, TIFF, GIF, BMP");
    }
}
