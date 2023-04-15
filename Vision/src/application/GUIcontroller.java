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
import javafx.scene.control.Spinner;
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
	private ImageView processedImage;
	@FXML
	private ImageView binaryImage;
	@FXML
	private Slider minH;
	@FXML
	private Slider maxH;
	@FXML
	private Slider minS;
	@FXML
	private Slider maxS;
	@FXML
	private Slider minV;
	@FXML
	private Slider maxV;
	@FXML
	private TextArea values;
	@FXML
	private Slider erode;
	@FXML
	private Slider dilate;
	
	private Thread thread;
	
	private Camera camera;

	/**
	 * The action triggered by pushing the start camera on the GUI
	 */
	@FXML
	private void startCamera() {
		VideoCapture video = new VideoCapture(0);
		camera = new Camera(video);
		thread = new Thread(() -> {
			while(true) {
				updateHSV();
				camera.updateFrame(minH.getValue(), maxH.getValue(), minS.getValue(), maxS.getValue(), minV.getValue(), maxV.getValue(), (int)erode.getValue(), (int)dilate.getValue());
				
				normalImage.setImage(Utils.matToImage(camera.getFrame()));
				processedImage.setImage(Utils.matToImage(camera.getProcessed()));
				binaryImage.setImage(Utils.matToImage(camera.getBinary()));

			}
			
		});
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
	

	//update hsvVals text
	private void updateHSV() {
		double hueStartVal = minH.getValue();
		double hueEndVal   = maxH.getValue();
		double saturationStartVal = minS.getValue();
		double saturationEndtVal = maxS.getValue();
		double valueStartVal = minV.getValue();
		double valueEndVal = maxV.getValue();
		
		values.setText("hue " + hueStartVal + "-" + hueEndVal + "\n" + 
		"saturation " + saturationStartVal + "-" + saturationEndtVal + "\n" + 
		"value " + valueStartVal + "-" + valueEndVal + "\n" + "Distance:" + camera.getDistance()
		+ "\n" + "yaw:" + camera.getYaw());
	}

}
