package com.textextraction;

import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

/**
 * ImageTextExtractorTest - Unit tests for ImageTextExtractor
 */
public class ImageTextExtractorTest {

    @Test
    public void testExtractorInitialization() {
        ImageTextExtractor extractor = new ImageTextExtractor();
        assertNotNull("Extractor should be initialized", extractor);
        assertNotNull("Tesseract instance should not be null", extractor.getTesseract());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistentImageFile() throws TesseractException {
        ImageTextExtractor extractor = new ImageTextExtractor();
        extractor.extractText("non_existent_image.png");
    }

    @Test
    public void testLanguageConfiguration() {
        ImageTextExtractor extractor = new ImageTextExtractor();
        extractor.setLanguage("eng");
        assertNotNull("Extractor should remain functional after language configuration", extractor);
    }

    @Test
    public void testExtractorWithCustomTessDataPath() {
        String tessDataPath = "path/to/tessdata";
        ImageTextExtractor extractor = new ImageTextExtractor(tessDataPath);
        assertNotNull("Extractor with custom path should be initialized", extractor);
    }
}
