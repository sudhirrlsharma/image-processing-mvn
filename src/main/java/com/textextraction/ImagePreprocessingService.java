package com.textextraction;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImagePreprocessingService {

    static {
        // Load OpenCV native library
        OpenCV.loadLocally();
    }
    
    public File preprocessImage(String imagePath, boolean denoise, boolean sharpen, boolean enhanceContrast) {
        Mat image = loadImage(imagePath);
        Mat processed = preprocessForHandwriting(image, denoise, sharpen, enhanceContrast);
        File tempFile = matToTempFile(processed);
        return tempFile;
    }

    public Mat loadImage(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new RuntimeException("Failed to load image: " + imagePath);
        }
        return image;
    }

    public Mat preprocessForHandwriting(Mat image, boolean denoise, boolean sharpen, boolean enhanceContrast) {
        Mat processed = image.clone();

        // Convert to grayscale
        if (processed.channels() > 1) {
            Imgproc.cvtColor(processed, processed, Imgproc.COLOR_BGR2GRAY);
        }

        // Denoise for blurred images
        if (denoise) {
            processed = denoiseImage(processed);
        }

        // Enhance contrast
        if (enhanceContrast) {
            processed = enhanceContrast(processed);
        }

        // Sharpen to recover edges
        if (sharpen) {
            processed = sharpenImage(processed);
        }

        // Apply adaptive thresholding for better text separation
        processed = applyAdaptiveThreshold(processed);

        // Morphological operations to clean up
        processed = morphologicalCleaning(processed);

        return processed;
    }

    private Mat denoiseImage(Mat image) {
        Mat denoised = new Mat();
        
        // Use Non-local Means Denoising for better quality
        Photo.fastNlMeansDenoising(image, denoised, 10, 7, 21);
        
        return denoised;
    }

    private Mat sharpenImage(Mat image) {
        
        // Unsharp masking technique
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(image, blurred, new Size(0, 0), 3);
        
        Mat sharpened = new Mat();
        Core.addWeighted(image, 1.5, blurred, -0.5, 0, sharpened);
        
        return sharpened;
    }

    private Mat enhanceContrast(Mat image) {
        
        // Apply CLAHE (Contrast Limited Adaptive Histogram Equalization)
        Mat enhanced = new Mat();
        org.opencv.imgproc.CLAHE clahe = Imgproc.createCLAHE();
        clahe.setClipLimit(2.0);
        clahe.setTilesGridSize(new Size(8, 8));
        clahe.apply(image, enhanced);
        
        return enhanced;
    }

    private Mat applyAdaptiveThreshold(Mat image) {
        
        Mat threshold = new Mat();
        Imgproc.adaptiveThreshold(
            image, 
            threshold, 
            255, 
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 
            Imgproc.THRESH_BINARY, 
            11, 
            2
        );
        
        return threshold;
    }

    private Mat morphologicalCleaning(Mat image) {
        
        Mat cleaned = new Mat();
        
        // Remove small noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.morphologyEx(image, cleaned, Imgproc.MORPH_OPEN, kernel);
        
        // Close small gaps
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Imgproc.morphologyEx(cleaned, cleaned, Imgproc.MORPH_CLOSE, kernel);
        
        return cleaned;
    }

    public Mat deblurImage(Mat image) {
        
        Mat deblurred = new Mat();
        
        // Wiener filter approximation using bilateral filter
        Imgproc.bilateralFilter(image, deblurred, 9, 75, 75);
        
        // Apply additional sharpening
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        float[] kernelData = {
            -1, -1, -1,
            -1,  9, -1,
            -1, -1, -1
        };
        kernel.put(0, 0, kernelData);
        
        Mat sharpened = new Mat();
        Imgproc.filter2D(deblurred, sharpened, -1, kernel);
        
        return sharpened;
    }

    public void saveImage(Mat image, String outputPath) {
        boolean success = Imgcodecs.imwrite(outputPath, image);
        if (success) {
            System.out.println("Preprocessed image saved to: " + outputPath);
        } else {
            System.err.println("Failed to save image to: " + outputPath);
        }
    }

    public Mat applyAllFilters(Mat image) {
        System.out.println("Applying all preprocessing filters...");
        
        Mat processed = image.clone();
        
        // Convert to grayscale
        if (processed.channels() > 1) {
            Imgproc.cvtColor(processed, processed, Imgproc.COLOR_BGR2GRAY);
        }
        
        // Step 1: Denoise
        processed = denoiseImage(processed);
        
        // Step 2: Deblur
        processed = deblurImage(processed);
        
        // Step 3: Enhance contrast
        processed = enhanceContrast(processed);
        
        // Step 4: Sharpen
        processed = sharpenImage(processed);
        
        // Step 5: Adaptive threshold
        processed = applyAdaptiveThreshold(processed);
        
        // Step 6: Morphological cleaning
        processed = morphologicalCleaning(processed);
        
        return processed;
    }

    public File matToTempFile(Mat mat) {
        try {
            File tempFile = File.createTempFile("preprocessed_", ".png");
            Imgcodecs.imwrite(tempFile.getAbsolutePath(), mat);
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create temporary file", e);
        }
    }
}
