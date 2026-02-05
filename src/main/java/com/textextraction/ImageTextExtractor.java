package com.textextraction;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Component
public class ImageTextExtractor {
    private final Tesseract tesseract;
    private final ImagePreprocessingService imagePreprocessingService;
    private final ImagePostProcessing imagePostProcessing;
    private final AIService aiService;

    public ImageTextExtractor(ImagePreprocessingService imagePreprocessingService, ImagePostProcessing imagePostProcessing, AIService aiService) {
        this.tesseract = new Tesseract();
        this.imagePreprocessingService = imagePreprocessingService;
        this.imagePostProcessing = imagePostProcessing;
        this.aiService = aiService;
    }


    public ExtractTextResponse extractTextFormatted(ExtractTextRequest request) {
        try {
            File imageFile = new File(request.imagePath);
            
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("Image file not found: " + request.imagePath);
            }
            
            if (request.language != null && !request.language.isEmpty()) {
                tesseract.setLanguage(request.language);
            }
            
            if (request.pageSegmentationMode > 0) {
                tesseract.setPageSegMode(request.pageSegmentationMode);
            }
            
            // File preprocessedFile = this.imagePreprocessingService.preprocessImage(request.imagePath, true, true, true);
            String extractedText = tesseract.doOCR(imageFile);
            String enhancedText = this.imagePostProcessing.enhanceOcrResult(extractedText);
            
            String formattedText = formatText(enhancedText, request.format);
            
            return new ExtractTextResponse(
                enhancedText,
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

    public String extractText(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("Image file not found: " + imagePath);
        }
        
        return tesseract.doOCR(imageFile);
    }

    public String extractText(File imageFile) throws TesseractException {
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("Image file not found: " + imageFile.getAbsolutePath());
        }
        
        return tesseract.doOCR(imageFile);
    }

    public void setLanguage(String language) {
        tesseract.setLanguage(language);
    }

    public void setPageSegMode(int mode) {
        tesseract.setPageSegMode(mode);
    }

    private String formatText(String text, String format) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (aiService.isAvailable()) {
            text = aiService.correctAndEnhanceText(text);
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

    public Tesseract getTesseract() {
        return tesseract;
    }

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
