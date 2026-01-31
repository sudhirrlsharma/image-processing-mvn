# Image Processing OCR Project

A Java Maven project for extracting text from images using **tess4j** (Java wrapper for Tesseract OCR).

## Project Structure

```
image-processing-mvn/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/textextraction/
│   │   │   ├── ImageTextExtractor.java    (Core OCR utility class)
│   │   │   └── Main.java                  (Sample usage)
│   │   └── resources/
│   └── test/
│       └── java/com/textextraction/
│           └── ImageTextExtractorTest.java (Unit tests)
└── README.md
```

## Features

- **Text Extraction**: Extract text from various image formats (PNG, JPG, JPEG, TIFF, GIF, BMP)
- **Multi-language Support**: Configure OCR for different languages
- **Easy Integration**: Simple API for integrating OCR into your applications
- **Configurable**: Set language, page segmentation mode, and other Tesseract parameters
- **Unit Tests**: Basic test coverage for core functionality

## Requirements

- Java 11 or higher
- Maven 3.6+
- Tesseract OCR engine (installed on system)
  - **Windows**: Download from [Tesseract GitHub Releases](https://github.com/UB-Mannheim/tesseract/wiki)
  - **Linux**: `sudo apt-get install tesseract-ocr`
  - **macOS**: `brew install tesseract`

## Installation & Setup

### 1. Clone/Create the Project

Navigate to the project directory:
```bash
cd image-processing-mvn
```

### 2. Download Tesseract Language Data

The project requires language data files. You can:

- **Option A**: Download from [tessdata GitHub](https://github.com/UB-Mannheim/tesseract/wiki)
- **Option B**: Place tessdata folder in project root or specify path in code

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run Tests

```bash
mvn test
```

## Usage

### As a Standalone Application

```bash
java -jar target/image-processing-ocr-1.0.0.jar <image_path>
```

Example:
```bash
java -jar target/image-processing-ocr-1.0.0.jar sample.png
```

### In Your Java Code

```java
import com.textextraction.ImageTextExtractor;
import net.sourceforge.tess4j.TesseractException;

public class MyApp {
    public static void main(String[] args) throws TesseractException {
        // Initialize extractor
        ImageTextExtractor extractor = new ImageTextExtractor();
        
        // Set language (optional, default is "eng")
        extractor.setLanguage("eng");
        
        // Extract text from image
        String text = extractor.extractText("path/to/image.png");
        
        System.out.println(text);
    }
}
```

### Advanced Usage

```java
// With custom tessdata path
ImageTextExtractor extractor = new ImageTextExtractor("C:\\tessdata");

// Set language for multi-language support
extractor.setLanguage("eng+fra");  // English + French

// Set page segmentation mode (PSM)
// PSM modes: 0-13 (see Tesseract documentation)
extractor.setPageSegMode(3);  // Default: automatic page segmentation

// Get Tesseract instance for advanced configuration
Tesseract tess = extractor.getTesseract();
tess.setOcrEngineMode(1);  // TessEngine mode
```

## Dependencies

- **tess4j 5.10.0**: Java binding for Tesseract OCR
- **slf4j**: Logging framework
- **JUnit 4**: Testing framework

See [pom.xml](pom.xml) for complete dependency list.

## Supported Languages

Common language codes:
- `eng` - English
- `fra` - French
- `deu` - German
- `spa` - Spanish
- `chi_sim` - Simplified Chinese
- `chi_tra` - Traditional Chinese
- `jpn` - Japanese
- `ara` - Arabic
- `rus` - Russian

## Troubleshooting

### "Tesseract is not installed"
Ensure Tesseract OCR is installed and in your system PATH.

### "tessdata not found"
Specify the tessdata path explicitly:
```java
ImageTextExtractor extractor = new ImageTextExtractor("C:\\Program Files\\Tesseract-OCR\\tessdata");
```

### Low accuracy results
- Preprocess images (enhance contrast, remove noise)
- Set appropriate language for your content
- Adjust page segmentation mode

## References

- [tess4j Documentation](https://tess4j.sourceforge.net/)
- [Tesseract OCR Wiki](https://github.com/UB-Mannheim/tesseract/wiki)
- [Tesseract Manual](https://github.com/tesseract-ocr/tesseract/wiki)

## License

This project is provided as-is for educational and development purposes.
