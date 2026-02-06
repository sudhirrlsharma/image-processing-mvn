package com.textextraction;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;
    
    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${spring.ai.openai.temperature:0.3}")
    private double temperature;
    
    @Value("${spring.ai.openai.max-tokens:1000}")
    private int maxTokens;


    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public boolean isAvailable() {
        return chatClient != null;
    }

    public String correctAndEnhanceText(String extractedText) {
        if (!isAvailable()) {
            return extractedText;
        }

        String promptTemplate = """
            You are an expert in OCR text correction and handwriting analysis.
            Please correct and enhance the following OCR-extracted handwritten text.
            
            Rules:
            1. Fix spelling and grammatical errors
            2. Preserve the original meaning and intent
            3. Format the text properly (add paragraphs where needed)
            4. Do not add any explanatory text - just return the corrected text
            5. If text is unclear, make your best educated guess
            
            OCR Extracted Text:
            {text}
            
            Corrected Text:""";

        PromptTemplate template = new PromptTemplate(promptTemplate);
        Prompt prompt = template.create(Map.of("text", extractedText));
        
        try {
            ChatResponse response = chatClient.call(prompt);
            return response.getResult().getOutput().getContent();
        } catch (Exception e) {
            System.err.println("AI enhancement failed: " + e.getMessage());
            return extractedText;
        }
    }

}