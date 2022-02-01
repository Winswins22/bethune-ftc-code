/* Copyright (c) 2017 FIRST. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted (subject to the limitations in the disclaimer below) provided that
* the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice, this list
* of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice, this
* list of conditions and the following disclaimer in the documentation and/or
* other materials provided with the distribution.
*
* Neither the name of FIRST nor the names of its contributors may be used to endorse or
* promote products derived from this software without specific prior written permission.
*
* NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
* LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.firstinspires.ftc.teamcode;

// auto imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

// tensorflow imports
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

@Autonomous(name="Auto Wheels 2.0", group="mode")

public class AutoTicks extends LinearOpMode {
    
    //Approximate field length: 358.2cm
    //approximate field width: 238.44cm
    
    // Ticks to move across the length: 10000
    // Ticks to move across the width: 6000
    // Approx ticks/m: ~ 2516-2792
    // Ticks to strafe across the length:
    // Ticks to strafe across the width:
    public static final double TICKS_PER_METER = 1500;
    
    
    public static final int TICKS_ROTATE_90_DEGREES = 660;
    // meters to move across 1 floor tile 
    public static final double METERS_PER_TILE = 0.6;
    // meters to move diagonally across 1 floor tile. Calculated by experimentation
    public static final double METERS_PER_DIAGONAL_TILE = 1.3;
    
    public boolean didOnce = false;
    boolean runArm = false;
    
    private ElapsedTime duckTimer = new ElapsedTime();
    private ElapsedTime armTimer = new ElapsedTime();
    
    private boolean duck = true;
    private boolean slept = false;
    private double defaultPosition = 0.95;

    public int duckPosition;

    /* Declare OpMode members. */
    HardwarePushbot         robot   = new HardwarePushbot();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();

  public static final double CAMERA_SCAN_SECONDS = 4d;
  double stopTime = 0d;

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


    @Override
    public void runOpMode() {

        initVuforia();
        initTfod();
        if (tfod != null) {
          tfod.activate();
          tfod.setZoom(1, 16.0 / 9.0);
        }

        /*
          * Initialize the drive system variables.
          * The init() method of the hardware class does all the work here
          */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();
        initMotors();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        robot.hand.setPosition(defaultPosition+0.5);
        
        
        while (opModeIsActive()){
            if (!didOnce){
                didOnce = true;

                // scan
                duckPosition = scan();
                telemetry.addData("duckPosition", duckPosition);
                telemetry.update();
                
                // // // move to carosel
                // moveMeters(0, 1, 0, 0.2, METERS_PER_TILE / 2);
                // move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                
                // moveMeters(0, 1, 0, 0.3, METERS_PER_TILE * 0.8);
                // move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                // moveMeters(0, 1, 0, 0.05, METERS_PER_TILE / 4);
    
                // // // duck wheel code here
                // if (duck){
                //     goForwardsSlowly();
                //     rotateDuckWheel(1, -6000);
                //     initMotors();
                //     duck = false;
                // }
                
                // // // move back a little for rotation space
                // moveMeters(0, -1, 0, 0.3, METERS_PER_TILE / 2);
                // move(0, 0, 1, 0.2, TICKS_ROTATE_90_DEGREES);
                
                // // slam into the back wall to realign
                // moveMeters(0, 1, 0, 0.3, METERS_PER_TILE / 4);
                
                
                // // move to the shipping hub
                // moveMeters(0, -1, 0, 0.3, METERS_PER_TILE * 0.2);
                
                // // """tensorflow"""
                // if (!slept){
                //   sleep(slept, 3);
                //   slept = true;
                // }
                
                // moveMeters(0, -1, 0, 0.3, METERS_PER_TILE * 1.8);
                
                // // face the shipping hub
                // move(0, 0, -1, 0.2, (int)(TICKS_ROTATE_90_DEGREES));
                // // insert arm code here
                
                // armTimer.reset();
                // while (true) {
                //     if (autoArm()) {
                //         armTimer.reset();
                //         break;
                //     }
                // }
                // // end arm code
                // // face the warehouse
                // move(0, 0, -1, 0.2, (int)(TICKS_ROTATE_90_DEGREES));
    
                // // move to the warehouse
                // moveMeters(0, 1, 0, 0.3, METERS_PER_TILE / 2);
                // move(0, 0, 1, 0.2, TICKS_ROTATE_90_DEGREES);
                // moveMeters(0, 1, 0, 0.3, METERS_PER_TILE);
                // move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                // moveMeters(1, 0, 0, 0.3, METERS_PER_TILE / 4);
                // moveMeters(0, 1, 0, 0.3, METERS_PER_TILE * 2);
            }
        }
    }
        
    public void initMotors(){
        
        robot.frontRight.setDirection(DcMotor.Direction.REVERSE); 
        robot.backRight.setDirection(DcMotor.Direction.REVERSE);
        robot.frontLeft.setDirection(DcMotor.Direction.REVERSE); 
        robot.backLeft.setDirection(DcMotor.Direction.FORWARD);
        robot.duckWheel.setDirection(DcMotor.Direction.FORWARD);
        
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.duckWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // reset target positions
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition());
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition());
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition());
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition());
        robot.duckWheel.setTargetPosition(robot.duckWheel.getCurrentPosition());
        
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.duckWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
    } 
    
    // use power to move motors very slowly during duck spinning
    public void goForwardsSlowly(){
      double power = 0.02;
      robot.frontLeft.setPower(power);
      robot.frontRight.setPower(power);
      robot.backLeft.setPower(power);
      robot.backRight.setPower(power);
    }
    
    // overidden function to accept meters as target intead of ticks
    // see move() function below for docs
    public void moveMeters(int x, int y, int rotation, double power, double metersTarget){
        move (x, y, rotation, power, (int)(metersTarget * TICKS_PER_METER), true);
    }
    
    public void moveMeters(int x, int y, int rotation, double power, double metersTarget, boolean driveQuickly){
        move (x, y, rotation, power, (int)(metersTarget * TICKS_PER_METER), driveQuickly);
    }
    
    public void move(int x, int y, int rotation, double power, int ticksTarget){
        move(x, y, rotation, power, ticksTarget, true);
    }
    
    /**
      * public void move(int x, int y, int rotation, double power, int ticksTarget)
      * 
      * Moves the robot by translating simulated gamepad instructions to autonomous movement.
      * Allows movement in 8 directions (North, Northeast, East, ...) and can rotate CW and CCW.
      * 
      * Samples:
      *   Move forwards at max speed:
      *     move(0, 1, 0, 1.0, INSERT_TICKS_HERE);
      *   Strafe right:
      *     move(1, 0, 0, 1.0, INSERT_TICKS_HERE);
      *   Rotate 90 degrees clockwise:
      *     move(0, 0, 1, 1.0, TICKS_ROTATE_90_DEGREES);
      * 
      * @param int x. The x-axis of the right gamestick. Takes in -1, 0, or 1. Changes movement 
      *               directions of the wheels to strafe to the left, stop, or strafe to the right, respectively.
      * @param int y. The y-axis of the right gamestick. Takes in -1, 0, or 1. Changes movement 
      *               directions of the wheels to move backwards, stop, or move forwards, respectively.
      * @param int rotation. The x-axis of the left gamestick. Takes in -1, 0, or 1. Changes rotation 
      *                      directions of the wheels to rotate to the left, no rotate, or to the right, respectively.
      * @param double power. The power to be delivered to the motors, a double between [-1, 1].
      *                      Setting to a negative value will invert the movement, and a bigger number
      *                      will increase the speed of the movement.
      * @param int ticksTarget. The amount of ticks for the motors to travel, given the gamepad
      *                         instructions above. If you wish to use meters instead of ticks, see 
      *                         moveMeters() and the constants at the top of AutoFunctionLib.java.
      * @param boolean driveQuickly. Whether or not the program will wait until all the motors are done
      *                              moving or not.
      */
    public void move(int x, int y, int rotation, double power, int ticksTarget, boolean driveQuickly){
        // invert
        //x = -x;
        rotation = -rotation;
        
        // the simulated gamepad instructions
        double wheelSpeeds[] = new double[4];
        // the ticks for the motors to run to
        int[] tickTargets = {0, 0, 0, 0};
    
        // translate simulated gamepad inputs into their speeds to use to caclulate ticksTarget
        wheelSpeeds[0] = (x + y - rotation);
        wheelSpeeds[1] = (-x + y + rotation);
        wheelSpeeds[2] = (-x + y - rotation);
        wheelSpeeds[3] = (x + y + rotation);
        
        // make motors travel to ticksTarget based on simulated gamepad movements
        for (int i = 0; i < wheelSpeeds.length; i ++){
            if (wheelSpeeds[i] == 0){
                tickTargets[i] = 0;
            }
            else if (wheelSpeeds[i] > 0){
                tickTargets[i] = ticksTarget;
            }
            else if (wheelSpeeds[i] < 0){
                tickTargets[i] = -ticksTarget;
            }
        }
        
        // telemetry.addData("frontLeft instructions", robot.frontLeft.getTargetPosition() - robot.frontLeft.getCurrentPosition());
        // telemetry.addData("frontRight instructions", robot.frontRight.getTargetPosition() - robot.frontRight.getCurrentPosition());
        // telemetry.addData("backLeft instructions", robot.backLeft.getTargetPosition() - robot.backLeft.getCurrentPosition());
        // telemetry.addData("backRight instructions", robot.backRight.getTargetPosition() - robot.backRight.getCurrentPosition());
        
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition() + tickTargets[0]);
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition() + tickTargets[1]);
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition() + tickTargets[2]);
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition() + tickTargets[3]);
        
        // Turn On RUN_TO_POSITION
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // Start motion;
        robot.frontLeft.setPower(power);
        robot.frontRight.setPower(power);
        robot.backLeft.setPower(power);
        robot.backRight.setPower(power);
        
        // change if condition depending on if using rotation
        //if (rotation == 0.0){
        if (driveQuickly){
            while (opModeIsActive() && 
                (robot.frontLeft.isBusy() && robot.frontRight.isBusy() && robot.backLeft.isBusy() && robot.backRight.isBusy()) ) {
                
                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", ticksTarget);
                telemetry.addData("frontLeft progress", robot.frontLeft.getTargetPosition() - robot.frontLeft.getCurrentPosition());
                telemetry.addData("frontRight progress", robot.frontRight.getTargetPosition() - robot.frontRight.getCurrentPosition());
                telemetry.addData("backLeft progress", robot.backLeft.getTargetPosition() - robot.backLeft.getCurrentPosition());
                telemetry.addData("backRight progress", robot.backRight.getTargetPosition() - robot.backRight.getCurrentPosition());
                telemetry.addData("frontLeft target", robot.frontLeft.getTargetPosition());
                telemetry.addData("frontRight target", robot.frontRight.getTargetPosition());
                telemetry.addData("backLeft target", robot.backLeft.getTargetPosition());
                telemetry.addData("backRight target", robot.backRight.getTargetPosition());
                telemetry.update();
            }
        }
        else {
            while (opModeIsActive() && 
                (robot.frontLeft.isBusy() || robot.frontRight.isBusy() || robot.backLeft.isBusy() || robot.backRight.isBusy()) ) {
                
                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", ticksTarget);
                telemetry.addData("frontLeft progress", robot.frontLeft.getTargetPosition() - robot.frontLeft.getCurrentPosition());
                telemetry.addData("frontRight progress", robot.frontRight.getTargetPosition() - robot.frontRight.getCurrentPosition());
                telemetry.addData("backLeft progress", robot.backLeft.getTargetPosition() - robot.backLeft.getCurrentPosition());
                telemetry.addData("backRight progress", robot.backRight.getTargetPosition() - robot.backRight.getCurrentPosition());
                telemetry.addData("frontLeft target", robot.frontLeft.getTargetPosition());
                telemetry.addData("frontRight target", robot.frontRight.getTargetPosition());
                telemetry.addData("backLeft target", robot.backLeft.getTargetPosition());
                telemetry.addData("backRight target", robot.backRight.getTargetPosition());
                telemetry.update();
            }
        }
        
        // Stop all motion;
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
        
        initMotors();
    }
    
    public static double ticksToMeters (int ticks){
        return TICKS_PER_METER * (double)ticks;
    }
    
    public void rotateDuckWheel(double speed, int ticksTarget){
        robot.duckWheel.setTargetPosition(robot.duckWheel.getCurrentPosition() + ticksTarget);
        // Turn On RUN_TO_POSITION
        robot.duckWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Start motion;
        robot.duckWheel.setPower(speed);
        
        while (opModeIsActive() && robot.duckWheel.isBusy()){
            telemetry.addData("Motors are running to Positions", ticksTarget);
            telemetry.addData("duckWheel progress", robot.duckWheel.getTargetPosition() - robot.duckWheel.getCurrentPosition());
            telemetry.update();
        }
        
        robot.duckWheel.setPower(0);
        duck = false;
    }
    
  // 0-0.20 go up
  // 0.20-2.0 stop motor, turn bucket
  // 2.0-2.5 default position, go back down
  // 2.5-3.0 stop motor
  public boolean autoArm() {//output arm
    if (armTimer.seconds() <= 0.3) {
      robot.hand.setPosition(0.7);
      robot.arm.setPower(1);
    }
    else if (armTimer.seconds() <= 1.5){
      robot.arm.setPower(0);
      robot.hand.setPosition(0.52);
    }
    else if (armTimer.seconds() <= 1.75) {
      robot.arm.setPower(-0.80);
      robot.hand.setPosition(defaultPosition);
    }
    else if (armTimer.seconds() <= 1.80) {
      robot.arm.setPower(0);
    }else {
      return true;
    }
    return false;
  }

  public void sleep(boolean sleptAlready, int sleepSecs){    
    if (sleptAlready){
      return;
    }
    ElapsedTime sleepTimer = new ElapsedTime();
    
    while (true){
      if (sleepTimer.seconds() > sleepSecs){
        return;
      } 
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

