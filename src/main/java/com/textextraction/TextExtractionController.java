package com.textextraction;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST API Controller for Image Text Extraction
 */
@RestController
@RequestMapping("/api/text-extraction")
public class TextExtractionController {

    private final TextExtractionService textExtractionService;

    public TextExtractionController(TextExtractionService textExtractionService) {
        this.textExtractionService = textExtractionService;
    }

    /**
     * Extract text from image with specified format
     * @param imagePath Path to the image file
     * @param format Output format (plain, json, markdown, structured)
     * @return Extracted text
     */
    @GetMapping("/extract")
    public Map<String, Object> extractText(
            @RequestParam String imagePath,
            @RequestParam(defaultValue = "plain") String format) {
        try {
            ImageTextExtractor.ExtractTextResponse result = textExtractionService.extractText(imagePath, format);
            return Map.of(
                "success", result.success,
                "imagePath", imagePath,
                "format", format,
                "rawText", result.rawText,
                "formattedText", result.formattedText
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * Extract text with structured output
     * @param imagePath Path to the image file
     * @return Structured extraction response
     */
    @PostMapping("/extract-structured")
    public ImageTextExtractor.ExtractTextResponse extractStructured(@RequestBody ExtractionRequest request) {
        return textExtractionService.extractTextStructured(request.imagePath);
    }

    /**
     * Extract text with custom language and format
     * @param request Extraction request with custom parameters
     * @return Extraction response
     */
    @PostMapping("/extract-custom")
    public ImageTextExtractor.ExtractTextResponse extractCustom(@RequestBody ExtractionRequest request) {
        return textExtractionService.extractTextCustom(
            request.imagePath,
            request.language != null ? request.language : "eng",
            request.format != null ? request.format : "plain"
        );
    }

    /**
     * Request class for extraction parameters
     */
    public static class ExtractionRequest {
        public String imagePath;
        public String language;
        public String format;

        public ExtractionRequest() {
        }

        public ExtractionRequest(String imagePath, String language, String format) {
            this.imagePath = imagePath;
            this.language = language;
            this.format = format;
        }
    }
}
