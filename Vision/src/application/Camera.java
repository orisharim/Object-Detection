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

import extras.Utils;
import javafx.scene.image.Image;

public class Camera {
	private Mat frame, binary, processed;
	private VideoCapture video;
	private int dilate, erode;
	public Camera(VideoCapture video) {
		frame = new Mat();
		binary = new Mat();
		processed = new Mat();
		
		this.video = video;
	}
	
	public void updateFrame(double minH, double maxH, double minS, double maxS, double minV, double maxV, int dilate, int erode) {
		Mat frameBlur = new Mat();
		// get threshold values 
		Scalar minValues = new Scalar(minH, minS, minV);
		Scalar maxValues = new Scalar(maxH, maxS, maxV);
		
		this.dilate = dilate;
		this.erode = erode;
			
			video.read(frame);
			if (!frame.empty()) {			      
				
				// remove some noise
				Imgproc.blur(frame, frameBlur, new Size(7, 7));
				
				Core.inRange(frameBlur, minValues, maxValues, binary);
				
				pipeline(erode, dilate);
				drawRects();

			}
			
	}
	

	private void pipeline(int erode, int dilate) {
		// morphological operators
		// dilate with large element, erode with small ones
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
	
	private void drawRects() {
		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();

		// find contours
		Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

//		// if any contour exist...
//		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
//		{
//		        // for each contour, display it in blue
//		        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
//		        {
//		        	if()
//		        		Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
//		        }
//		}
	
		Scalar color = new Scalar(0, 255, 0); // Green
	    int thickness = 2;

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

	    Imgproc.rectangle(frame, maxRect.tl(), maxRect.br(), color, thickness);
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
	
	
	
	

}
