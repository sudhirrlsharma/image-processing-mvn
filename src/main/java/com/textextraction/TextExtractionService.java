package com.textextraction;

import org.springframework.stereotype.Service;

/**
 * Service for processing image text extraction
 */
@Service
public class TextExtractionService {

    private final ImageTextExtractor imageTextExtractor;

    public TextExtractionService(ImageTextExtractor imageTextExtractor) {
        this.imageTextExtractor = imageTextExtractor;
    }

    /**
     * Extract text from an image using the tool
     * @param imagePath Path to the image
     * @param format Output format (plain, json, markdown, structured)
     * @return Extraction response
     */
    public ImageTextExtractor.ExtractTextResponse extractText(String imagePath, String format) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, "eng", format != null ? format : "plain"
        );
        return imageTextExtractor.extractTextFormatted(request);
    }

    /**
     * Extract text from an image with structured analysis
     * @param imagePath Path to the image
     * @return Extraction response with metadata
     */
    public ImageTextExtractor.ExtractTextResponse extractTextStructured(String imagePath) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, "eng", "structured"
        );
        return imageTextExtractor.extractTextFormatted(request);
    }

    /**
     * Extract text with custom language and format
     * @param imagePath Path to the image
     * @param language OCR language code
     * @param format Output format
     * @return Extraction response
     */
    public ImageTextExtractor.ExtractTextResponse extractTextCustom(String imagePath, String language, String format) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, language, format
        );
        return imageTextExtractor.extractTextFormatted(request);
    }
}
