package com.textextraction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring Boot Application for Image Text Extraction
 * Runs as a command-line application using CommandLineRunner
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * CommandLineRunner implementation for executing image text extraction from command line
     */
    @Component
    public class ImageExtractionRunner implements CommandLineRunner {
        private final TextExtractionService textExtractionService;

        public ImageExtractionRunner(TextExtractionService textExtractionService) {
            this.textExtractionService = textExtractionService;
        }

        @Override
        public void run(String... args) throws Exception {
            try {
                // Parse command line arguments
                CLIArgs cliArgs = parseArguments(args);

                if (cliArgs.showHelp || cliArgs.imagePath == null) {
                    displayHelp();
                    if (cliArgs.imagePath == null && args.length > 0) {
                        System.exit(1);
                    }
                    System.exit(0);
                }

                if (cliArgs.verbose) {
                    System.out.println("[INFO] Starting image text extraction...");
                    System.out.println("[INFO] Image: " + cliArgs.imagePath);
                    System.out.println("[INFO] Language: " + cliArgs.language);
                    System.out.println("[INFO] Format: " + cliArgs.format);
                }

                // Extract text using the service
                ImageTextExtractor.ExtractTextResponse response = textExtractionService.extractTextCustom(
                    cliArgs.imagePath,
                    cliArgs.language,
                    cliArgs.format
                );

                // Display results
                displayResults(response, cliArgs.verbose);

                // Exit with appropriate code
                System.exit(response.success ? 0 : 1);

            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("[CAUSE] " + e.getCause().getMessage());
                }
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }

        /**
         * Parse command-line arguments
         */
        private CLIArgs parseArguments(String[] args) {
            CLIArgs result = new CLIArgs();

            if (args.length == 0) {
                result.showHelp = true;
                return result;
            }

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.equals("-h") || arg.equals("--help")) {
                    result.showHelp = true;
                } else if (arg.equals("-v") || arg.equals("--verbose")) {
                    result.verbose = true;
                } else if (arg.equals("-l") || arg.equals("--language")) {
                    if (i + 1 < args.length) {
                        result.language = args[++i];
                    }
                } else if (arg.equals("-f") || arg.equals("--format")) {
                    if (i + 1 < args.length) {
                        result.format = args[++i];
                    }
                } else if (!arg.startsWith("-")) {
                    result.imagePath = arg;
                }
            }

            return result;
        }

        /**
         * Display help information
         */
        private void displayHelp() {
            System.out.println("Image Text Extraction Tool - Spring Boot CLI");
            System.out.println("============================================");
            System.out.println();
            System.out.println("Usage: mvn spring-boot:run -Dspring-boot.run.arguments='<OPTIONS> <image_path>'");
            System.out.println();
            System.out.println("Arguments:");
            System.out.println("  <image_path>              Path to the image file (required)");
            System.out.println();
            System.out.println("Options:");
            System.out.println("  -l, --language <code>    OCR language code (default: eng)");
            System.out.println("                            Common codes: eng, fra, deu, spa, ita, chi_sim, jpn");
            System.out.println("  -f, --format <format>    Output format (default: plain)");
            System.out.println("                            Options: plain, json, markdown, structured");
            System.out.println("  -v, --verbose            Enable verbose output");
            System.out.println("  -h, --help               Show this help message");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  mvn spring-boot:run -Dspring-boot.run.arguments='document.png'");
            System.out.println("  mvn spring-boot:run -Dspring-boot.run.arguments='-l fra document.png'");
            System.out.println("  mvn spring-boot:run -Dspring-boot.run.arguments='-f json --language deu scan.jpg'");
            System.out.println("  java -jar app.jar --format structured photo.jpg");
            System.out.println();
            System.out.println("Supported Image Formats:");
            System.out.println("  PNG, JPG, JPEG, TIFF, GIF, BMP");
            System.out.println();
        }

        /**
         * Display extraction results
         */
        private void displayResults(ImageTextExtractor.ExtractTextResponse response, boolean verbose) {
            if (verbose) {
                System.out.println("\n[INFO] Extraction completed");
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("Extraction Results");
            System.out.println("=".repeat(60));

            if (!response.success) {
                System.out.println("Status: FAILED");
                System.out.println("Error: " + response.errorMessage);
            } else {
                System.out.println("Status: SUCCESS");
                System.out.println("Image: " + response.imageName);
                System.out.println("File Size: " + formatBytes(response.imageSize));
                System.out.println("Format: " + response.format);
                System.out.println("-".repeat(60));
                System.out.println(response.formattedText);
                System.out.println("-".repeat(60));
            }
        }

        /**
         * Format bytes to human-readable format
         */
        private String formatBytes(long bytes) {
            if (bytes <= 0) return "0 B";
            final String[] units = new String[]{"B", "KB", "MB", "GB"};
            int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
            return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
        }
    }

    /**
     * CLI Arguments holder class
     */
    public static class CLIArgs {
        public String imagePath;
        public String language = "eng";
        public String format = "plain";
        public boolean showHelp = false;
        public boolean verbose = false;
    }
}
