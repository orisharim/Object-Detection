package application;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Camera {
	private Mat frame, binary, processed;
	private VideoCapture video;
	

	public Camera(VideoCapture video) {
		frame = new Mat();
		binary = new Mat();
		processed = new Mat();
		
		this.video = video;
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
		// get threshold values 
		Scalar minValues = new Scalar(minH, minS, minV);
		Scalar maxValues = new Scalar(maxH, maxS, maxV);
			
			video.read(frame);
			if (!frame.empty()) {			      
				
				// clean some noise
				Imgproc.blur(frame, frameBlur, new Size(7, 7));
				
				Core.inRange(frameBlur, minValues, maxValues, binary);
				
				pipeline(erode, dilate);
				drawRect();
				
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
	
	private void drawRect() {
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();

		Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

	    double maxArea = 0;
	    Rect maxRect = new Rect();

	    for (int i = 0; i < contours.size(); i++) {
	        Rect rect = Imgproc.boundingRect(contours.get(i));
	        double area = Imgproc.contourArea(contours.get(i));
	        if (area > maxArea) {
	            maxArea = area;
	            maxRect = rect;
	        }
	    }
	    
		Scalar color = new Scalar(0, 255, 0); // Green

	    Imgproc.rectangle(frame, maxRect.tl(), maxRect.br(), color, 2);

	    System.out.println(getLength(7400/7.5, 19, 20, Math.abs(maxRect.tl().x - maxRect.br().x)));
	    
	}
	
	
	private double getLength(double focalLength, double height , double width, double pixels) {
		return Math.max((focalLength * width) / pixels, (focalLength * height) / pixels);
	}
	
	private double getYaw(double targetPixelPosX, double horizontalFOV, int resolutionX) {
		double ratio = horizontalFOV / resolutionX;
		return (targetPixelPosX - resolutionX /2 ) * ratio;
	}
	
	
	
	
}
