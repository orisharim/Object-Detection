package application;

import extras.Utils;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUIcontroller
{
	
	@FXML
	private Button startCamera;
	@FXML
	private Button stopCamera;
	@FXML
	private ImageView normalImage;
	@FXML
	private ImageView maskImage;
	@FXML
	private ImageView morphImage;
	@FXML
	private Slider hueStart;
	@FXML
	private Slider hueEnd;
	@FXML
	private Slider saturationStart;
	@FXML
	private Slider saturationEnd;
	@FXML
	private Slider valueStart;
	@FXML
	private Slider valueEnd;
	@FXML
	private TextArea hsvVals;

	private Thread thread;

	/**
	 * The action triggered by pushing the start camera on the GUI
	 */
	@FXML
	private void startCamera() {
		VideoCapture video = new VideoCapture(0);

		thread = new Thread(() -> {showCameraScreen(video);});
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * The action triggered by pushing the stop camera on the GUI
	 */
	@FXML
	private void stopCamera() {
		thread.stop();
	}
	

	private void showCameraScreen(VideoCapture video) {
		Mat frame = new Mat();
		Mat gray = new Mat();
		Mat binary = new Mat();
		Mat frameBlur = new Mat();
		Mat frameHSV = new Mat();
		while (true) {
			updateHSV();
			
			// get threshold values 
			Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
					this.valueStart.getValue());
			Scalar maxValues = new Scalar(this.hueEnd.getValue(), this.saturationEnd.getValue(),
					this.valueEnd.getValue());
			
			video.read(frame);
			if (!frame.empty()) {			      

				
//				Imgproc.threshold(gray, binary, minValues, maxValues, Imgproc.THRESH_BINARY_INV);
				
				// remove some noise
				Imgproc.blur(frame, frameBlur, new Size(7, 7));
				
				// convert the frame to HSV
				Imgproc.cvtColor(frameBlur, frameHSV, Imgproc.COLOR_BGR2HSV);
				
				Core.inRange(frameHSV, minValues, maxValues, binary);
				
				Image frameImage = Utils.matToImage(frame);
				normalImage.setImage(frameImage);
				
				Image maskFrameImage = Utils.matToImage(frameHSV);
				maskImage.setImage(maskFrameImage);
				
				Image binaryImage = Utils.matToImage(binary);
				morphImage.setImage(binaryImage);
			}
			
		}
	}
	
	private void updateHSV() {
		double hueStartVal = hueStart.getValue();
		double hueEndVal   = hueEnd.getValue();
		double saturationStartVal = saturationStart.getValue();
		double saturationEndtVal = saturationEnd.getValue();
		double valueStartVal = valueStart.getValue();
		double valueEndVal = valueEnd.getValue();
		
		hsvVals.setText("hue " + hueStartVal + "-" + hueEndVal + "\n" + 
		"saturation " + saturationStartVal + "-" + saturationEndtVal + "\n" + 
		"value " + valueStartVal + "-" + valueEndVal);
	}
	
//	private Mat markContours(Mat frame , double minH, double maxH, double minS, double maxS, double minV, double maxV) {
//        
//	
//	
//	}
	
//	private Mat getGrayMat(Mat frame) {
//		Mat gray = new Mat(frame.rows(), frame.cols(), frame.type());
//		Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
//		return gray;
//	}
//	
//	private Mat getBinaryMat(Mat frame) {
//		 Mat binary = new Mat(frame.rows(), frame.cols(), frame.type(), new Scalar(0));
//		 return binary;
//	}
	
	
}
