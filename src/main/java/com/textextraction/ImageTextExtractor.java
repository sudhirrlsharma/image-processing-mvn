package com.textextraction;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * ImageTextExtractor - Tool for extracting text from images using Tesseract OCR
 * This class integrates with Spring AI as a callable tool
 */
@Component
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
     * Tool function to extract text from image with formatted output
     * @param request ExtractTextRequest containing image path and format options
     * @return ExtractTextResponse with extracted text and metadata
     */
    public ExtractTextResponse extractTextFormatted(ExtractTextRequest request) {
        try {
            File imageFile = new File(request.imagePath);
            // File imageFile = new File("C:\\Users\\sudhi\\Downloads\\photo.jpg");
            
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("Image file not found: " + request.imagePath);
            }
            
            // Set language if provided
            if (request.language != null && !request.language.isEmpty()) {
                tesseract.setLanguage(request.language);
            }
            
            // Set PSM if provided
            if (request.pageSegmentationMode > 0) {
                tesseract.setPageSegMode(request.pageSegmentationMode);
            }
            
            String extractedText = tesseract.doOCR(imageFile);
            
            // Format the text based on preference
            String formattedText = formatText(extractedText, request.format);
            
            return new ExtractTextResponse(
                extractedText,
                formattedText,
                imageFile.getName(),
                imageFile.length(),
                request.format,
                true,
                null
            );
        } catch (TesseractException e) {
            return new ExtractTextResponse(
                "",
                "",
                request.imagePath,
                0,
                request.format,
                false,
                "Tesseract OCR error: " + e.getMessage()
            );
        } catch (Exception e) {
            return new ExtractTextResponse(
                "",
                "",
                request.imagePath,
                0,
                request.format,
                false,
                "Error: " + e.getMessage()
            );
        }
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
     * Format extracted text according to specified format
     * @param text Raw extracted text
     * @param format Format type (json, markdown, plain, structured)
     * @return Formatted text
     */
    private String formatText(String text, String format) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        switch (format.toLowerCase()) {
            case "json":
                return formatAsJson(text);
            case "markdown":
                return formatAsMarkdown(text);
            case "structured":
                return formatAsStructured(text);
            case "plain":
            default:
                return text;
        }
    }

    private String formatAsJson(String text) {
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("lines", text.split("\n"));
        result.put("wordCount", text.split("\\s+").length);
        return result.toString();
    }

    private String formatAsMarkdown(String text) {
        return "# Extracted Text\n\n" + text.replace("\n", "\n\n");
    }

    private String formatAsStructured(String text) {
        StringBuilder sb = new StringBuilder();
        String[] lines = text.split("\n");
        sb.append("=== EXTRACTED TEXT ===\n");
        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].trim().isEmpty()) {
                sb.append(String.format("[Line %d] %s\n", i + 1, lines[i]));
            }
        }
        sb.append("=== END ===\n");
        return sb.toString();
    }

    /**
     * Get Tesseract instance for advanced configuration
     * @return Tesseract instance
     */
    public Tesseract getTesseract() {
        return tesseract;
    }

    /**
     * Request class for extracting text with options
     */
    public static class ExtractTextRequest {
        @JsonProperty("image_path")
        @JsonPropertyDescription("The file path to the image to extract text from")
        public String imagePath;

        @JsonProperty("language")
        @JsonPropertyDescription("Language code for OCR (e.g., 'eng', 'fra', 'deu'). Default is 'eng'")
        public String language = "eng";

        @JsonProperty("format")
        @JsonPropertyDescription("Output format: 'plain', 'json', 'markdown', 'structured'. Default is 'plain'")
        public String format = "plain";

        @JsonProperty("page_segmentation_mode")
        @JsonPropertyDescription("Tesseract page segmentation mode (0-13). Default is 3 (auto)")
        public int pageSegmentationMode = 3;

        public ExtractTextRequest() {
        }

        public ExtractTextRequest(String imagePath) {
            this.imagePath = imagePath;
        }

        public ExtractTextRequest(String imagePath, String language, String format) {
            this.imagePath = imagePath;
            this.language = language;
            this.format = format;
        }
    }

    /**
     * Response class for text extraction results
     */
    public static class ExtractTextResponse {
        public String rawText;
        public String formattedText;
        public String imageName;
        public long imageSize;
        public String format;
        public boolean success;
        public String errorMessage;

        public ExtractTextResponse(String rawText, String formattedText, String imageName,
                                  long imageSize, String format, boolean success, String errorMessage) {
            this.rawText = rawText;
            this.formattedText = formattedText;
            this.imageName = imageName;
            this.imageSize = imageSize;
            this.format = format;
            this.success = success;
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return "ExtractTextResponse{" +
                    "rawText='" + rawText + '\'' +
                    ", formattedText='" + formattedText + '\'' +
                    ", imageName='" + imageName + '\'' +
                    ", imageSize=" + imageSize +
                    ", format='" + format + '\'' +
                    ", success=" + success +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}
