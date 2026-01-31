package com.textextraction;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * ImageTextExtractor - Utility class for extracting text from images using Tesseract OCR
 */
public class ImageTextExtractor {
    private final Tesseract tesseract;

    /**
     * Constructor - Initializes Tesseract instance
     */
    public ImageTextExtractor() {
        this.tesseract = new Tesseract();
        // Set path to tessdata folder (optional - if not in default location)
        // this.tesseract.setDatapath("path/to/tessdata");
    }

    /**
     * Constructor - Initializes Tesseract with custom tessdata path
     * @param tessDataPath Path to tessdata directory containing language files
     */
    public ImageTextExtractor(String tessDataPath) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(tessDataPath);
    }

    /**
     * Extract text from an image file
     * @param imagePath Path to the image file
     * @return Extracted text as String
     * @throws TesseractException if OCR processing fails
     */
    public String extractText(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("Image file not found: " + imagePath);
        }
        
        return tesseract.doOCR(imageFile);
    }

    /**
     * Extract text from an image file
     * @param imageFile Image File object
     * @return Extracted text as String
     * @throws TesseractException if OCR processing fails
     */
    public String extractText(File imageFile) throws TesseractException {
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("Image file not found: " + imageFile.getAbsolutePath());
        }
        
        return tesseract.doOCR(imageFile);
    }

    /**
     * Set language for OCR recognition
     * @param language Language code (e.g., "eng", "fra", "deu", "chi_sim")
     */
    public void setLanguage(String language) {
        tesseract.setLanguage(language);
    }

    /**
     * Set page segmentation mode for OCR
     * @param mode PSM value (0-13)
     */
    public void setPageSegMode(int mode) {
        tesseract.setPageSegMode(mode);
    }

    /**
     * Set confidence level for OCR
     * @param confidence Confidence threshold (0-100)
     */
    public void setConfidenceThreshold(float confidence) {
        // This can be used to filter results based on confidence
    }

    /**
     * Get Tesseract instance for advanced configuration
     * @return Tesseract instance
     */
    public Tesseract getTesseract() {
        return tesseract;
    }
}
