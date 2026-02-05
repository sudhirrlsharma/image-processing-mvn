package com.textextraction;

import org.springframework.stereotype.Service;

@Service
public class ImagePostProcessing {

    // Note: Spring AI integration would require API key configuration
    // For demonstration, this shows the structure
    
    public String enhanceOcrResult(String rawText) {
        System.out.println("AI enhancement requested for OCR result");
        
    
        String enhanced = rawText;
        
        // Remove excessive whitespace
        enhanced = enhanced.replaceAll("\\s+", " ");
        
        // Fix common OCR mistakes
        enhanced = fixCommonOcrErrors(enhanced);
        
        // Improve formatting
        enhanced = improveFormatting(enhanced);
        
        System.out.println("Basic text enhancement applied");
        return enhanced;
    }

    private String fixCommonOcrErrors(String text) {
        // Common OCR confusions
        return text
            // Number/Letter confusions
            .replaceAll("(?<=\\s)0(?=\\s)", "O")  // 0 -> O in words
            .replaceAll("(?<=\\s)1(?=\\s)", "I")  // 1 -> I in words
            .replaceAll("(?<=\\d)l(?=\\d)", "1")  // l -> 1 in numbers
            .replaceAll("(?<=\\d)O(?=\\d)", "0")  // O -> 0 in numbers
            
            // Common character confusions
            .replace(" rn ", " m ")
            .replace(" vv ", " w ")
            
            // Fix double spaces
            .replaceAll("\\s+", " ");
    }

    private String improveFormatting(String text) {
        // Capitalize first letter of sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder formatted = new StringBuilder();
        
        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                String capitalized = sentence.substring(0, 1).toUpperCase() + 
                                   sentence.substring(1);
                formatted.append(capitalized).append(" ");
            }
        }
        
        return formatted.toString().trim();
    }

    // TODO: Integrate with Spring AI for advanced enhancement
    public String enhanceWithVisionAI(String rawText, byte[] imageBytes) {
        System.out.println("Vision AI enhancement requested");
        
        // In a production implementation, this would:
        // 1. Send both the image and OCR text to a vision AI model
        // 2. Let the AI verify and correct the OCR output
        // 3. Handle context-aware corrections
        
        // Example prompt structure:
        // "Here is text extracted from a handwritten image using OCR. 
        //  Please correct any errors, improve readability, and format properly:
        //  [raw text]"
        
        System.out.println("Vision AI not configured. Using basic enhancement.");
        return enhanceOcrResult(rawText);
    }
}
