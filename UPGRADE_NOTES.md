# Image Processing OCR with Spring Boot AI

This project has been upgraded to use **Spring Boot AI** with Tesseract OCR for extracting text from images. The `ImageTextExtractor` is now integrated as a callable tool within the Spring AI framework.

## Project Upgrades

### Java Runtime
- **Previous Version**: Java 11
- **Current Version**: Java 21 LTS
- Updated Maven compiler configuration to support Java 21

### Framework Upgrade
- **Added Spring Boot 3.2.1**: Provides robust application framework
- **Added Spring AI 1.0.0-M1**: Enables AI-powered text extraction with tool integration
- **Maintained Tesseract OCR**: tess4j 5.10.0 for OCR functionality

## Architecture

### Components

1. **ImageTextExtractor** (Enhanced Tool)
   - Core OCR extraction logic using Tesseract
   - New `extractTextFormatted()` method for AI integration
   - Supports multiple output formats: plain, json, markdown, structured
   - Provides `ExtractTextRequest` and `ExtractTextResponse` classes

2. **TextExtractionService** (Spring Service)
   - Integrates ImageTextExtractor as a Spring AI tool
   - Exposes methods for AI-powered extraction
   - Handles custom language and format configurations

3. **TextExtractionController** (REST API)
   - Provides HTTP endpoints for image text extraction
   - Supports multiple extraction modes:
     - `/api/text-extraction/extract` - Basic extraction with format
     - `/api/text-extraction/extract-structured` - Structured output
     - `/api/text-extraction/extract-custom` - Custom language and format

4. **Application** (Spring Boot Entry Point)
   - Spring Boot application starter
   - Manages dependency injection and component scanning

5. **Main** (CLI Tool)
   - Command-line interface for direct OCR operations
   - Supports language selection and output formatting
   - No Spring Boot runtime required for CLI usage

## Usage

### As Spring Boot REST API

1. **Set up API Key**:
   ```bash
   export OPENAI_API_KEY=your-api-key-here
   ```

2. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Extract text via REST**:
   ```bash
   # Basic extraction
   curl "http://localhost:8080/api/text-extraction/extract?imagePath=document.png&format=plain"
   
   # Structured extraction
   curl -X POST http://localhost:8080/api/text-extraction/extract-structured \
     -H "Content-Type: application/json" \
     -d '{"imagePath": "document.png"}'
   
   # Custom language and format
   curl -X POST http://localhost:8080/api/text-extraction/extract-custom \
     -H "Content-Type: application/json" \
     -d '{"imagePath": "document.png", "language": "deu", "format": "markdown"}'
   ```

### As Command-Line Tool

```bash
# Basic extraction (English, plain format)
java com.textextraction.Main document.png

# With language selection
java com.textextraction.Main document.png fra

# With language and format
java com.textextraction.Main document.png deu structured

# With JSON output
java com.textextraction.Main scan.jpg eng json
```

## Output Formats

- **plain**: Raw extracted text
- **json**: JSON structure with text, lines array, and word count
- **markdown**: Markdown formatted text with headers
- **structured**: Line-by-line structured output with line numbers

## Supported Languages

Common language codes for Tesseract:
- `eng` - English
- `fra` - French
- `deu` - German
- `spa` - Spanish
- `ita` - Italian
- `chi_sim` - Simplified Chinese
- `jpn` - Japanese

## Configuration

Edit `src/main/resources/application.yml` to configure:
- OpenAI API endpoint and model
- Server port
- Logging levels
- AI model parameters (temperature, etc.)

## Building

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Build without tests
mvn clean package -DskipTests
```

## Dependencies

### Core Dependencies
- **spring-boot-starter-parent** 3.2.1
- **spring-ai-openai-spring-boot-starter** 1.0.0-M1
- **tess4j** 5.10.0 (Tesseract wrapper)

### Testing
- **spring-boot-starter-test** (JUnit 5, Mockito, AssertJ)

## Notes

- Ensure Tesseract language data files are available in the classpath
- Spring AI requires a valid OpenAI API key for AI-powered features
- The CLI tool works independently without Spring Boot runtime
- Java 21 LTS provides long-term support and performance improvements

## License

[Your License Here]
