package com.textextraction;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class TextExtractionService {

    private final ImageTextExtractor imageTextExtractor;

    public TextExtractionService(ImageTextExtractor imageTextExtractor, ImagePreprocessingService imagePreprocessingService) {
        this.imageTextExtractor = imageTextExtractor;
    }

    public ImageTextExtractor.ExtractTextResponse extractText(String imagePath, String format) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, "eng", format != null ? format : "plain"
        );
        return imageTextExtractor.extractTextFormatted(request);
    }

    public ImageTextExtractor.ExtractTextResponse extractTextStructured(String imagePath) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, "eng", "structured"
        );
        return imageTextExtractor.extractTextFormatted(request);
    }

    public ImageTextExtractor.ExtractTextResponse extractTextCustom(String imagePath, String language, String format) {
        ImageTextExtractor.ExtractTextRequest request = new ImageTextExtractor.ExtractTextRequest(
            imagePath, language, format
        );
        return imageTextExtractor.extractTextFormatted(request);
    }
}
