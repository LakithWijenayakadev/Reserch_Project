package com.exammonitoring.service;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComputerVisionService {
    
    @Value("${app.monitoring.face-detection-threshold:15.0}")
    private double faceDetectionThreshold;
    
    @Value("${app.monitoring.blur-detection-threshold:15.0}")
    private double blurDetectionThreshold;
    
    private CascadeClassifier faceCascade;
    private CascadeClassifier profileCascade;
    private CascadeClassifier eyeCascade;
    
    private Mat prevFrame;
    private long lastFaceDetected;
    private long absenceStartTime;
    
    public ComputerVisionService() {
        // Load OpenCV classifiers
        try {
            faceCascade = new CascadeClassifier();
            profileCascade = new CascadeClassifier();
            eyeCascade = new CascadeClassifier();
            
            // Load the cascade classifiers
            faceCascade.load("haarcascade_frontalface_default.xml");
            profileCascade.load("haarcascade_profileface.xml");
            eyeCascade.load("haarcascade_eye.xml");
        } catch (Exception e) {
            System.err.println("Error loading OpenCV classifiers: " + e.getMessage());
        }
    }
    
    public String analyzeFrame(Mat imageBgr) {
        if (imageBgr == null || imageBgr.empty()) {
            return "no_face";
        }
        
        // Resize image if too large
        Size originalSize = imageBgr.size();
        if (Math.max(originalSize.width, originalSize.height) > 640) {
            double scale = 640.0 / Math.max(originalSize.width, originalSize.height);
            Size newSize = new Size((int)(originalSize.width * scale), (int)(originalSize.height * scale));
            Imgproc.resize(imageBgr, imageBgr, newSize);
        }
        
        Mat gray = new Mat();
        Imgproc.cvtColor(imageBgr, gray, Imgproc.COLOR_BGR2GRAY);
        
        // Store current frame for next comparison
        prevFrame = gray.clone();
        
        // Calculate blur
        double blur = calculateLaplacianVariance(gray);
        
        // Detect faces
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces, 1.3, 8, 0, new Size(80, 80), new Size(300, 300));
        
        List<Rect> faceList = faces.toList();
        List<Rect> filteredFaces = filterOverlappingFaces(faceList);
        
        int faceCount = filteredFaces.size();
        long currentTime = System.currentTimeMillis();
        
        if (faceCount == 0) {
            // Track absence duration
            if (absenceStartTime == 0) {
                absenceStartTime = currentTime;
            }
            
            return analyzeNoFaceDetection(gray, currentTime);
        } else if (faceCount >= 2) {
            // Reset absence tracking
            lastFaceDetected = currentTime;
            absenceStartTime = 0;
            return "multiple_people";
        } else {
            // Reset absence tracking
            lastFaceDetected = currentTime;
            absenceStartTime = 0;
            
            // One face - check for looking away
            return analyzeLookingAway(gray, filteredFaces.get(0));
        }
    }
    
    private String analyzeNoFaceDetection(Mat gray, long currentTime) {
        // Analyze image characteristics
        Scalar meanScalar = Core.mean(gray);
        double meanBrightness = meanScalar.val[0];
        
        Mat stdDev = new Mat();
        Core.meanStdDev(gray, new Scalar(), stdDev);
        double stdBrightness = stdDev.get(0, 0)[0];
        
        // Check for movement from previous frame
        boolean movementDetected = false;
        if (prevFrame != null && !prevFrame.empty()) {
            Mat diff = new Mat();
            Core.absdiff(gray, prevFrame, diff);
            Scalar meanDiff = Core.mean(diff);
            double movementScore = meanDiff.val[0];
            movementDetected = movementScore > 10;
        }
        
        // Time-based analysis
        long absenceDuration = currentTime - absenceStartTime;
        
        // Determine if person is absent or looking away
        boolean isVeryDark = meanBrightness < 30;
        boolean isLowContrast = stdBrightness < 15;
        boolean isEmptyBrightRoom = meanBrightness > 100 && stdBrightness < 25;
        boolean isLongAbsence = absenceDuration > 5000; // 5 seconds
        
        if (isVeryDark || isLowContrast || isEmptyBrightRoom || isLongAbsence) {
            return "no_face";
        } else if (movementDetected) {
            // Check for profile faces
            MatOfRect profiles = new MatOfRect();
            profileCascade.detectMultiScale(gray, profiles, 1.1, 3, 0, new Size(30, 30), new Size());
            if (profiles.toList().size() > 0) {
                return "looking_away";
            }
            return "looking_away";
        } else {
            // Check for profile faces
            MatOfRect profiles = new MatOfRect();
            profileCascade.detectMultiScale(gray, profiles, 1.1, 3, 0, new Size(30, 30), new Size());
            if (profiles.toList().size() > 0) {
                return "looking_away";
            }
            return "looking_away";
        }
    }
    
    private String analyzeLookingAway(Mat gray, Rect face) {
        // Extract face region
        Mat faceROI = new Mat(gray, face);
        
        // Detect eyes
        MatOfRect eyes = new MatOfRect();
        eyeCascade.detectMultiScale(faceROI, eyes, 1.05, 2, 0, new Size(), new Size());
        
        int eyesCount = eyes.toList().size();
        
        // Calculate face characteristics
        double aspect = (double) face.width / face.height;
        double faceArea = face.width * face.height;
        double imageArea = gray.rows() * gray.cols();
        double faceRatio = faceArea / imageArea;
        
        // Check face position
        double faceCenterX = face.x + face.width / 2.0;
        double imageCenterX = gray.cols() / 2.0;
        double faceOffset = Math.abs(faceCenterX - imageCenterX) / imageCenterX;
        
        // Determine if looking away
        boolean isSideProfile = aspect > 1.1;
        boolean isPartialFace = faceRatio < 0.08;
        boolean isOffCenter = faceOffset > 0.3;
        boolean noEyes = eyesCount == 0;
        
        if ((isSideProfile || isPartialFace || isOffCenter) && noEyes || noEyes) {
            return "looking_away";
        }
        
        return "none";
    }
    
    private List<Rect> filterOverlappingFaces(List<Rect> faces) {
        List<Rect> filtered = new ArrayList<>();
        
        for (Rect face1 : faces) {
            boolean isDuplicate = false;
            
            for (Rect face2 : filtered) {
                // Calculate overlap
                int overlapX = Math.max(0, Math.min(face1.x + face1.width, face2.x + face2.width) - Math.max(face1.x, face2.x));
                int overlapY = Math.max(0, Math.min(face1.y + face1.height, face2.y + face2.height) - Math.max(face1.y, face2.y));
                int overlapArea = overlapX * overlapY;
                int face1Area = face1.width * face1.height;
                int face2Area = face2.width * face2.height;
                
                // Check for significant overlap
                if (overlapArea > 0.1 * Math.min(face1Area, face2Area)) {
                    isDuplicate = true;
                    break;
                }
                
                // Check distance between centers
                double center1X = face1.x + face1.width / 2.0;
                double center1Y = face1.y + face1.height / 2.0;
                double center2X = face2.x + face2.width / 2.0;
                double center2Y = face2.y + face2.height / 2.0;
                double distance = Math.sqrt(Math.pow(center1X - center2X, 2) + Math.pow(center1Y - center2Y, 2));
                double minDistance = Math.max(Math.max(face1.width, face1.height), Math.max(face2.width, face2.height)) * 1.2;
                
                if (distance < minDistance) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                // Additional quality checks
                if (face1.width >= 80 && face1.height >= 80 && 
                    face1.width <= 300 && face1.height <= 300 &&
                    face1.x >= 20 && face1.y >= 20 &&
                    face1.x + face1.width <= gray.cols() - 20 && 
                    face1.y + face1.height <= gray.rows() - 20) {
                    filtered.add(face1);
                }
            }
        }
        
        return filtered;
    }
    
    private double calculateLaplacianVariance(Mat gray) {
        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, CvType.CV_64F);
        
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();
        Core.meanStdDev(laplacian, mean, stdDev);
        
        return stdDev.get(0, 0)[0] * stdDev.get(0, 0)[0];
    }
    
    public void resetTracking() {
        prevFrame = null;
        lastFaceDetected = 0;
        absenceStartTime = 0;
    }
}
