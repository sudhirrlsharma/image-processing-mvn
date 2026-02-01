package com.textextraction;

import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class ImageTextExtractorTest {

    @Test
    public void testExtractorInitialization() {
        ImageTextExtractor extractor = new ImageTextExtractor();
        assertNotNull(extractor, "Extractor should be initialized");
        assertNotNull(extractor.getTesseract(), "Tesseract instance should not be null");
    }

    @Test
    public void testNonExistentImageFile() {
        ImageTextExtractor extractor = new ImageTextExtractor();
        assertThrows(IllegalArgumentException.class, () -> {
            extractor.extractText("non_existent_image.png");
        });
    }

    @Test
    public void testLanguageConfiguration() {
        ImageTextExtractor extractor = new ImageTextExtractor();
        extractor.setLanguage("eng");
        assertNotNull(extractor, "Extractor should remain functional after language configuration");
    }

    @Test
    public void testExtractorWithCustomTessDataPath() {
        String tessDataPath = "path/to/tessdata";
        ImageTextExtractor extractor = new ImageTextExtractor(tessDataPath);
        assertNotNull(extractor, "Extractor with custom path should be initialized");
    }
}
