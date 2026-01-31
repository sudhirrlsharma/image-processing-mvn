# Image Processing OCR - Command Line Tool

A Spring Boot command-line application for extracting text from images using **tess4j** (Java wrapper for Tesseract OCR).

## Project Overview

This is a modern Spring Boot application that runs as a CLI tool, leveraging Spring's CommandLineRunner for seamless command-line argument processing. It provides efficient text extraction with support for multiple languages and output formats.

## Architecture

The application has been converted from a REST service to a command-line tool with the following design:

- **Application.java**: Spring Boot entry point with embedded CommandLineRunner
- **ImageExtractionRunner**: Handles CLI argument parsing and orchestrates extraction
- **TextExtractionService**: Business logic layer for text extraction
- **ImageTextExtractor**: Core OCR functionality using Tesseract

## Project Structure

```
image-processing-mvn/
├── pom.xml                          (Maven configuration)
├── src/
│   ├── main/
│   │   ├── java/com/textextraction/
│   │   │   ├── Application.java                (Spring Boot + CommandLineRunner)
│   │   │   ├── ImageExtractionRunner.java      (CLI handler, inner class)
│   │   │   ├── TextExtractionService.java      (Business logic)
│   │   │   ├── ImageTextExtractor.java         (OCR core engine)
│   │   │   ├── TextExtractionController.java   (Legacy REST controller, unused)
│   │   │   └── CLIParser.java                  (CLI argument parser)
│   │   └── resources/
│   │       └── application.yml                 (Spring configuration)
│   └── test/
│       └── java/com/textextraction/
│           └── ImageTextExtractorTest.java     (Unit tests)
└── README.md
```

## Features

- **Command-Line Interface**: Simple CLI with argument parsing and help
- **Text Extraction**: Extract text from images (PNG, JPG, JPEG, TIFF, GIF, BMP)
- **Multi-language Support**: OCR in 100+ languages
- **Multiple Output Formats**: plain, json, markdown, structured
- **Verbose Logging**: Optional detailed output for debugging
- **Spring Boot Integration**: Leverages Spring for dependency injection and configuration
- **Exit Codes**: Proper exit codes (0 = success, 1 = failure)

## Requirements

- Java 21 or higher
- Maven 3.6+
- Tesseract OCR engine (installed on system)
  - **Windows**: Download from [Tesseract GitHub Releases](https://github.com/UB-Mannheim/tesseract/wiki)
  - **Linux**: `sudo apt-get install tesseract-ocr`
  - **macOS**: `brew install tesseract`

## Installation & Setup

### 1. Clone/Navigate to Project

```bash
cd image-processing-mvn
```

### 2. Build the Project

```bash
mvn clean package
```

This creates an executable JAR: `target/image-processing-ocr-1.0.0.jar`

### 3. Verify Tesseract Installation

```bash
tesseract --version
```

## Usage

### Using Maven Spring Boot Plugin

Show help:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--help'
```

Basic extraction:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='document.png'
```

With options:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='-l fra -f json document.png'
```

Verbose output:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='-v --format structured photo.jpg'
```

### Using Compiled JAR

```bash
java -jar target/image-processing-ocr-1.0.0.jar <image_path> [OPTIONS]
```

Basic extraction:
```bash
java -jar target/image-processing-ocr-1.0.0.jar sample.png
```

With language and format:
```bash
java -jar target/image-processing-ocr-1.0.0.jar -l deu -f json document.png
```

Verbose mode:
```bash
java -jar target/image-processing-ocr-1.0.0.jar -v --language spa scan.jpg
```

### Command-Line Options

```
Usage: java -jar image-processing-ocr-1.0.0.jar [OPTIONS] <image_path>

Arguments:
  <image_path>              Path to the image file (required)

Options:
  -l, --language <code>    OCR language code (default: eng)
                            Common codes: eng, fra, deu, spa, ita, chi_sim, jpn
  -f, --format <format>    Output format (default: plain)
                            Options: plain, json, markdown, structured
  -v, --verbose            Enable verbose output
  -h, --help               Show this help message
```

### Output Formats

- **plain**: Simple text output (default)
- **json**: JSON formatted response with metadata
- **markdown**: Markdown formatted text
- **structured**: Structured output with analysis

## Supported Languages

Common language codes:
```
eng - English              fra - French               deu - German
spa - Spanish              ita - Italian              chi_sim - Simplified Chinese
chi_tra - Traditional Ch.  jpn - Japanese             ara - Arabic
rus - Russian              por - Portuguese           kor - Korean
hin - Hindi                ben - Bengali              arb - Arabic
```

## Integration as Library

You can still use this in your code as a library:

```java
import com.textextraction.TextExtractionService;
import com.textextraction.ImageTextExtractor;

public class MyApp {
    public static void main(String[] args) {
        // Initialize components
        ImageTextExtractor extractor = new ImageTextExtractor();
        TextExtractionService service = new TextExtractionService(extractor);
        
        // Extract with custom parameters
        var response = service.extractTextCustom("image.png", "eng", "json");
        
        if (response.success) {
            System.out.println(response.formattedText);
        } else {
            System.err.println("Error: " + response.errorMessage);
        }
    }
}
```

## Dependencies

- **Spring Boot 3.2.1**: Framework for CLI and dependency injection
- **tess4j 5.10.0**: Java binding for Tesseract OCR
- **Jackson 2.16.1**: JSON serialization/deserialization
- **JUnit 4**: Testing framework

See [pom.xml](pom.xml) for complete dependency list.

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

Tesseract language data (tessdata) must be available:

1. **Default Location**: `/usr/share/tesseract-ocr` (Linux), `C:\Program Files\Tesseract-OCR\tessdata` (Windows)
2. **Environment Variable**: Set `TESSDATA_PREFIX`:
   ```bash
   export TESSDATA_PREFIX=/path/to/tessdata
   ```
3. **Download Additional Languages**: Get from [tessdata GitHub](https://github.com/tesseract-ocr/tessdata)

### Low accuracy results

- **Preprocess images**: Enhance contrast, remove noise, resize if very small
- **Verify language**: Use correct language code with `-l` option
- **Check image quality**: Ensure text is clear and readable
- **Adjust segmentation**: For poor accuracy, try different PSM modes (advanced usage)

### High memory usage

For large batch operations:
```bash
java -Xmx2g -jar target/image-processing-ocr-1.0.0.jar image.png
```

## Testing

Run unit tests:
```bash
mvn test
```

Run with verbose output:
```bash
mvn test -DargLine="-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
```

## Performance Tips

1. **Batch Processing**: Process multiple images in a loop
2. **Memory**: Allocate more heap for large images: `-Xmx2g`
3. **Language**: Specify only needed languages to reduce memory
4. **Format**: Use `plain` format for best performance

## Architecture Notes

### Design Pattern

The application uses Spring's **CommandLineRunner** pattern:
- Entry point: `Application.java` with `@SpringBootApplication`
- Runner: Inner class `ImageExtractionRunner implements CommandLineRunner`
- Service layer: `TextExtractionService` for business logic
- Core logic: `ImageTextExtractor` for Tesseract integration

### Key Features

- **Dependency Injection**: Spring manages component lifecycle
- **Configuration**: Externalized via `application.yml`
- **CLI Parsing**: Custom argument parser with proper error handling
- **Exit Codes**: Returns 0 on success, 1 on failure
- **Logging**: Configurable via Spring logging properties

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [CommandLineRunner API](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/CommandLineRunner.html)
- [tess4j Documentation](https://tess4j.sourceforge.net/)
- [Tesseract OCR GitHub](https://github.com/tesseract-ocr/tesseract)
- [tessdata Repository](https://github.com/tesseract-ocr/tessdata)

## Migration Notes

This project was originally a REST API service and has been converted to a command-line application. Key changes:
- Removed `spring-boot-starter-web` dependency
- Converted `Application.java` to implement `CommandLineRunner`
- Set `spring.main.web-application-type: none` in configuration
- Maintains backward compatibility with `TextExtractionService`

## License

This project is provided as-is for educational and development purposes.
