package application;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

public class Camera {
	private Mat frame, binary, processed;
	private VideoCapture video;
	private double objectPixelRadius = 0;
	private double objectPixelPosX = 0;
	

	public Camera(VideoCapture video) {
		frame = new Mat();
		binary = new Mat();
		processed = new Mat();
		
		this.video = video;
	}
	
	/**
	 * Get distance from target
	 * */
	public double getDistance() {
		return (CameraSettings.FOCAL_LENGTH * CameraSettings.OBJECT_RADIUS) / objectPixelRadius;
	}
	
	/**
	 * Get yaw angle from target
	 * */
	public double getYaw() {
		double ratio = CameraSettings.HORIZONTAL_FOV / CameraSettings.RESOLUTION_X;
		return (objectPixelPosX - CameraSettings.RESOLUTION_X / 2 ) * ratio;
	}
	
	public Mat getFrame() {
		return frame;		
	}

	public Mat getBinary() {
		return binary;
	}
	
	public Mat getProcessed() {
		return processed;
	}
	
	public void updateFrame(double minH, double maxH, double minS, double maxS, double minV, double maxV, int dilate, int erode) {
		Mat frameBlur = new Mat();
		Mat hsvFrame = new Mat();
		// get threshold values 
		Scalar minValues = new Scalar(minH, minS, minV);
		Scalar maxValues = new Scalar(maxH, maxS, maxV);
			
			video.read(frame);
			if (!frame.empty()) {			      
				// convert from bgr to hsv
		        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
				// clean some noise
				Imgproc.blur(hsvFrame, frameBlur, new Size(7, 7));
				//find pixels in threshold
				Core.inRange(frameBlur, minValues, maxValues, binary);
				
				//process image
				pipeline(erode, dilate);
				//draw circle around detected object
				drawCircle();
			}
			
	}
	
	private void pipeline(int erode, int dilate) {
		 Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
		 Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
		 
		 Imgproc.erode(binary, processed, erodeElement);
		 for(int i = 1; i < erode; i++) {
		 	Imgproc.erode(processed, processed, erodeElement);
		 }
		 
		 for(int i = 0; i < dilate; i++) {
		 	Imgproc.dilate(processed, processed, dilateElement);
		 }
	}

	private void drawCircle() {
	    List<MatOfPoint> contours = new ArrayList<>();
	    Mat hierarchy = new Mat();

	    Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

	    double maxArea = 0;
	    Point maxCenter = new Point();
	    int maxRadius = 0;

	    for (int i = 0; i < contours.size(); i++) {
	        MatOfPoint contour = contours.get(i);
	        Moments moments = Imgproc.moments(contour);
	        Point center = new Point(moments.m10 / moments.m00, moments.m01 / moments.m00);
	        double area = Imgproc.contourArea(contour);
	        if (area > maxArea) {
	            maxArea = area;
	            maxCenter = center;
	            maxRadius = (int) Math.sqrt(area / Math.PI);
	        }
	    }

	    Scalar color = new Scalar(0, 255, 0); // Green
	    Imgproc.circle(frame, maxCenter, maxRadius, color, 2);
	    
	    objectPixelRadius = maxRadius;
	    objectPixelPosX = maxCenter.x;
	}

	
	
	
	
}
