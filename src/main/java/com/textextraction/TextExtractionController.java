package com.textextraction;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/text-extraction")
public class TextExtractionController {

    private final TextExtractionService textExtractionService;

    public TextExtractionController(TextExtractionService textExtractionService) {
        this.textExtractionService = textExtractionService;
    }

    @GetMapping("/extract")
    public Map<String, Object> extractText(
            @RequestParam String imagePath,
            @RequestParam(defaultValue = "plain") String format) {
        try {
            String result = textExtractionService.extractText(imagePath, format).errorMessage;
            return Map.of(
                "success", true,
                "imagePath", imagePath,
                "format", format,
                "result", result
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    @PostMapping("/extract-structured")
    public ImageTextExtractor.ExtractTextResponse extractStructured(@RequestBody ExtractionRequest request) {
        return textExtractionService.extractTextStructured(request.imagePath);
    }

    @PostMapping("/extract-custom")
    public ImageTextExtractor.ExtractTextResponse extractCustom(@RequestBody ExtractionRequest request) {
        return textExtractionService.extractTextCustom(
            request.imagePath,
            request.language != null ? request.language : "eng",
            request.format != null ? request.format : "plain"
        );
    }

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
