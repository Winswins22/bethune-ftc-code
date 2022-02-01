package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import com.qualcomm.robotcore.util.ElapsedTime;

//import com.qualcomm.robotcore.hardware.HardwareMap;

public class BarcodeReader {

  //HardwarePushbot robot = new HardwarePushbot(); // use this object for easy reference to components like motors.
  public static final double CAMERA_SCAN_SECONDS = 4d;
  double stopTime = 0d;

  ElapsedTime runtime = new ElapsedTime();

  // IMAGE RECOGNITION VARS
  private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
  private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker"
  };

  private static final String VUFORIA_KEY = "AUA6kKH/////AAABmVTR6BBHQkD5nwba+Z0iYfwJZKfxUuzWLm0s+8KxPMAQwzIHlnEe+UXeCejxQgyw+SVfsWbZi+BH+rBEIujq35GyRV73LVm6iyAlK+SzyJDah1dBpZrhepkfHRGkdrsjG2FVA7lKV5fWJel8ysRVlwqwoq5JWr5ZVhLwyYcDT1WhQsWZBkQWcoG9L76A1dWR2RtExUOJbHnICbRaSpvL+yCt1HOg7p3EYkgIDXnwnCDaz6CmcYT9bfRsXWVNTyPyCYt9cqM5foF7odZk/MVe5sLzGwWlnurQwjbvOLSqz0cqHt/MeXH5QiT/4W3G2VrxWXMdT+Y3C8GS6Oeyf5Z0LQJ9kD2x3vI9GoFOGhK7n6I8";

  private VuforiaLocalizer vuforia;

  private TFObjectDetector tfod;

  private Recognition recognitionSpots[] = new Recognition[3]; // Recognition array to store which recogniton is in
                                                               // which of the three "slots"
  // Stores the left coordinate of each spot
  private final int LEFT_SPOT = 74;
  private final int CENTER_SPOT = 264;
  private final int RIGHT_SPOT = 468;

  // END IMAGE DETECTION VARS

  public BarcodeReader() {
    robot.init(hardwareMap);
    initVuforia();
    initTfod();
    if (tfod != null) {
      tfod.activate();
      tfod.setZoom(1, 16.0 / 9.0);
    }

  }

  // Scan for ducks
  // Returns the level that the duck is on.
  public int scan(){
    boolean duckFound = false;
    int duckLevel = 0;

    // 2.1: scan for 2 seconds
    stopTime = runtime.seconds();
    while (runtime.seconds() - stopTime < CAMERA_SCAN_SECONDS) {
      //telemetry.addData("runtime: ", runtime.seconds());
      if (tfod != null) {
        // getUpdatedRecognitions() will return null if no new information is available
        // since
        // the last time that call was made.
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
          //telemetry.addData("# Object Detected", updatedRecognitions.size());
          // step through the list of recognitions and display boundary info.
          // int i = 0;
          for (Recognition recognition : updatedRecognitions) {
            // telemetry.addData(String.format("label (%d, %.1f)", i,
            // recognition.getLeft()), recognition.getLabel());
            // telemetry.addData(String.format(" left,top (%d)", i), "%.1f , %.1f",
            // recognition.getLeft(), recognition.getTop());
            // i++;
            if (recognition.getLabel() == "Duck" || recognition.getLabel() == "Marker") {
              placeRecognitionInArray(recognition);
            }
          }
          if (updatedRecognitions.size() != 0) {
            //telemetry.update();
            try {
              Thread.sleep(1000);
            } catch (Exception e) {
            }
          }

        }
      }
      //telemetry.update();
    }
    duckLevel = calculateLevel();
    // telemetry.addData("Duck Level", duckLevel);
    // telemetry.update();

    return duckLevel;
  }

  private void initVuforia() {

    /*
     * Configure Vuforia by creating a Parameter object, and passing it to the
     * Vuforia engine.
     */
    VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

    parameters.vuforiaLicenseKey = VUFORIA_KEY;
    parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

    // Instantiate the Vuforia engine
    vuforia = ClassFactory.getInstance().createVuforia(parameters);

    // Loading trackables is not necessary for the TensorFlow Object Detection
    // engine.
  }

  /**
   * Initialize the TensorFlow Object Detection engine.
   */
  private void initTfod() {
    int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
        "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
    tfodParameters.minResultConfidence = 0.4f;
    tfodParameters.isModelTensorFlow2 = true;
    tfodParameters.inputSize = 320;
    // tfodParameters.setThreads(4);

    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
    tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
  }

  private void placeRecognitionInArray(Recognition recognition) {
    double leftDifference = Math.abs(recognition.getLeft() - LEFT_SPOT);
    double centerDifference = Math.abs(recognition.getLeft() - CENTER_SPOT);
    double rightDifference = Math.abs(recognition.getLeft() - RIGHT_SPOT);

    // Check which slot the recognition is closest to and place it in the cloeset
    // slot
    if (leftDifference < centerDifference && leftDifference < rightDifference) {
      recognitionSpots[0] = recognition;
      telemetry.addData("Spot 1", String.format("%s, %.1f", recognition.getLabel(), recognition.getLeft()));
    } else if (centerDifference < leftDifference && centerDifference < rightDifference) {
      recognitionSpots[1] = recognition;
      telemetry.addData("Spot 2", String.format("%s, %.1f", recognition.getLabel(), recognition.getLeft()));
    } else if (rightDifference < leftDifference && rightDifference < centerDifference) {
      recognitionSpots[2] = recognition;
      telemetry.addData("Spot 3", String.format("%s, %.1f", recognition.getLabel(), recognition.getLeft()));
    }
    return;
  }

  private int calculateLevel() {
    String spot1;
    String spot2;
    String spot3;

    try {
      spot1 = recognitionSpots[0].getLabel();
    } catch (Exception e) {
      spot1 = null;
    }
    try {
      spot2 = recognitionSpots[1].getLabel();
    } catch (Exception e) {
      spot2 = null;
    }
    try {
      spot3 = recognitionSpots[2].getLabel();
    } catch (Exception e) {
      spot3 = null;
    }

    // If duck is recognized - Return duck position
    if (spot1 == "Duck") {
      return 1;
    } else if (spot2 == "Duck") {
      return 2;
    } else if (spot3 == "Duck") {
      return 3;
    }
    // If two markers are detected - duck spot can be deduced
    else if (spot1 == "Marker" && spot2 == "Marker") {
      return 3;
    } else if (spot1 == "Marker" && spot3 == "Marker") {
      return 2;
    } else if (spot2 == "Marker" && spot3 == "Marker") {
      return 1;
    }
    // If only one marker is detected - return one of the other two spots
    else if (spot1 == "Marker") {
      return 2;
    } else if (spot2 == "Marker") {
      return 3;
    } else if (spot3 == "Marker") {
      return 1;
    }
    // If no spots are filled - return one of the three spots
    else {
      return 1;
    }
  }

}


// while(){
//   if(timer.seconds > x){
//     wheels();
//   }

//   if (timer2.seconds > x){
//     arm();
//   }
}