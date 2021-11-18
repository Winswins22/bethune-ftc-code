package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="AutoFunctionLib", group="mode")

public class AutoFunctionLib extends LinearOpMode {
    
    //Approximate field length: 358.2cm
    //approximate field width: 238.44cm
    //approximate ticks to traverse width: trial 1: 3866 || trial 2: 3100
    //TODO: Measure using driving mode instead
    //approximate ticks to traverse length (DRIVE): trial 1: 3856 trial 2: 3449 trial 3:
    public static final double TICKS_PER_METER_FB = 1624.37;
    
    //TODO
    public static final double TICKS_PER_METER_LR = 0;
    
    HardwarePushbot robot = new HardwarePushbot(); //use this object for easy reference to components like motors.
    
    ElapsedTime elapsedTime = new ElapsedTime();
    
    //Assumes that the bottom-left (red markers on left side of field) is 0,0. Measured in meters since ticks do not
    //necessarily correspond to distance (e.g. ticks/m is different when strafing).
    //Manipulated by moveTickFB and moveTickLR functions (yes, it'd be more intuitive if those functions were correspondingly
    //in meters instead of ticks, but ticks gives us more precision) 
    public static double estimatedX = 0;
    public static double estimatedY = 0;
    
    //IMAGE RECOGNITION VARS
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker"
    };

    private static final String VUFORIA_KEY =
            "AUA6kKH/////AAABmVTR6BBHQkD5nwba+Z0iYfwJZKfxUuzWLm0s+8KxPMAQwzIHlnEe+UXeCejxQgyw+SVfsWbZi+BH+rBEIujq35GyRV73LVm6iyAlK+SzyJDah1dBpZrhepkfHRGkdrsjG2FVA7lKV5fWJel8ysRVlwqwoq5JWr5ZVhLwyYcDT1WhQsWZBkQWcoG9L76A1dWR2RtExUOJbHnICbRaSpvL+yCt1HOg7p3EYkgIDXnwnCDaz6CmcYT9bfRsXWVNTyPyCYt9cqM5foF7odZk/MVe5sLzGwWlnurQwjbvOLSqz0cqHt/MeXH5QiT/4W3G2VrxWXMdT+Y3C8GS6Oeyf5Z0LQJ9kD2x3vI9GoFOGhK7n6I8";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;
    //END IMAGE DETECTION VARS

    double initial = 0;
    @Override
    public void runOpMode() {
        double left;
        double right;
        double drive;
        double turn;
        double max;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        
        initial = robot.frontLeft.getCurrentPosition();
        
        initVuforia();
        initTfod();
        
        tfod.activate();

        // The TensorFlow software will scale the input images from the camera to a lower resolution.
        // This can result in lower detection accuracy at longer distances (> 55cm or 22").
        // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
        // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
        // should be set to the value of the images used to create the TensorFlow Object Detection model
        // (typically 16/9).
        tfod.setZoom(2.5, 16.0/9.0);

        telemetry.addData("Robot", "Initialized");    //
        telemetry.update();
 
        waitForStart();
        
        initMotors();
        
        boolean didOnce = false; 
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            
            boolean detected = false;

            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            /*drive = -gamepad1.left_stick_y;
            turn  =  gamepad1.right_stick_x; */
                
            if (tfod != null) {
                
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    detected = true;
                  telemetry.addData("# Object Detected", updatedRecognitions.size());
                  // step through the list of recognitions and display boundary info.
                  int i = 0;
                  for (Recognition recognition : updatedRecognitions) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());
                    i++;
                  }
                } else {
                    detected = false;
                }
            }
                //move(-gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);
                
            // do it only once to make it easier to debug
            if (!didOnce){
                //move(0.0, 1.0, 0.0, 0.1);
                //move(1.0, 0.0, 0.0, 0.2);
                //move(0.0, -1.0, 0.0, 0.2);
                //move(-1.0, 0.0, 0.0, 0.2);
                //didOnce = true;
            }
            
            telemetry.addData("Something is detected: ", detected);
            telemetry.addData("frontLeft motor position: ", robot.frontLeft.getCurrentPosition());
            telemetry.addData("backRight motor position: ", robot.backRight.getCurrentPosition());
            telemetry.addData("(2 * FL - BR)(Forward/back): ", 2 * robot.frontLeft.getCurrentPosition() - robot.backRight.getCurrentPosition());
            //telemetry.addData("frontLeft target pos", robot.frontLeft.getTargetPosition());
            telemetry.addData("ticksDiff", robot.frontLeft.getCurrentPosition() - initial);
            telemetry.update();
            
        }
    }
    
    /**
     * resets motor tick positions and target positions
     */
    public void initMotors(){
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // reset target positions
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition());
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition());
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition());
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition());
        
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    } 
    
    //Arbitrary; used in move() function
    public int[] translatePowerToTicks(double[] speeds){
        int[] ticksToIncreaseBy = {0, 0, 0, 0};  
        
        for (int i = 0; i < speeds.length; i ++){
            ticksToIncreaseBy[i] = (int)(speeds[i] * 500);
        }
        
        return ticksToIncreaseBy;
    }
    
    /**
     * Avoid using this in the autonomous mode since we want the bot to track its position
     * in the field
     */
    public void move(double x, double y, double rotation, double power){
        double wheelSpeeds[] = new double[4];
        int[] ticksToIncreaseBy = {0, 0, 0, 0};
        // negate x and y to reverse travel directions so that it travels properly
        x = -x;
        y = -y;

        wheelSpeeds[0] = -(x + y - rotation); //FL
        wheelSpeeds[1] = -x + y + rotation; //FR
        wheelSpeeds[2] = -(-x + y - rotation); //BL
        wheelSpeeds[3] = x + y + rotation; //BR
    
        ticksToIncreaseBy = translatePowerToTicks(wheelSpeeds);
    
        //setAllVelocity(velocity);
    
        // telemetry.addData("frontLeft instructions", robot.frontLeft.getTargetPosition() - robot.frontLeft.getCurrentPosition());
        // telemetry.addData("frontRight instructions", robot.frontRight.getTargetPosition() - robot.frontRight.getCurrentPosition());
        // telemetry.addData("backLeft instructions", robot.backLeft.getTargetPosition() - robot.backLeft.getCurrentPosition());
        // telemetry.addData("backRight instructions", robot.backRight.getTargetPosition() - robot.backRight.getCurrentPosition());
    
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition() + ticksToIncreaseBy[0]);
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition() + ticksToIncreaseBy[1]);
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition() + ticksToIncreaseBy[2]);
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition() + ticksToIncreaseBy[3]);
    
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
    
        while (opModeIsActive() && 
            (robot.frontLeft.isBusy() || robot.frontRight.isBusy() || robot.backLeft.isBusy() || robot.backRight.isBusy()) ) {

                // Display it for the driver.
            telemetry.addData("Motors are running to Positions", "");
            telemetry.addData("frontLeft progress", robot.frontLeft.getTargetPosition() - robot.frontLeft.getCurrentPosition());
            telemetry.addData("frontRight progress", robot.frontRight.getTargetPosition() - robot.frontRight.getCurrentPosition());
            telemetry.addData("backLeft progress", robot.backLeft.getTargetPosition() - robot.backLeft.getCurrentPosition());
            telemetry.addData("backRight progress", robot.backRight.getTargetPosition() - robot.backRight.getCurrentPosition());
            telemetry.addData("ticksDiff from start pos", robot.frontLeft.getCurrentPosition() - initial);
            telemetry.update();
        }
    
        // Stop all motion;
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
        
        initMotors();
    
        // TODO add encoder stuffs
        
        //normalize(wheelSpeeds);
    
        // robot.frontLeft.setPower(-wheelSpeeds[0]);
        // robot.frontRight.setPower(wheelSpeeds[1]);
        // robot.backLeft.setPower(-wheelSpeeds[2]);
        // robot.backRight.setPower(wheelSpeeds[3]);
        
        // telemetry.addData("front Left", robot.frontLeft.getPower());
        // telemetry.addData("front Right", robot.frontRight.getPower()); 
        // telemetry.addData("back Left", robot.backLeft.getPower()); 
        // telemetry.addData("back Right", robot.backRight.getPower()); 
        // telemetry.update();
    }
    
    public void setAllVelocity(double velocity){
        robot.frontLeft.setVelocity(velocity);
        robot.frontRight.setVelocity(velocity);
        robot.backLeft.setVelocity(velocity);
        robot.backRight.setVelocity(velocity);
    }
    
    //positive ticks for forward, negative ticks for backward
    public void moveTickFB(int ticks, int velocityTicks){
    
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition() + ticks); //FL
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition() + ticks); //FR
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition() + ticks); //BL
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition() + ticks); //BR`
    
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        robot.frontLeft.setVelocity(velocityTicks);
        robot.frontRight.setVelocity(velocityTicks);
        robot.backLeft.setVelocity(velocityTicks);
        robot.backRight.setVelocity(velocityTicks);
    }
    
    //positive ticks for strafe right, negative ticks for strafe left
    public void moveTickLR(int ticks, int velocityTicks){
    
        //TODO: unfinished; need to adjust for motor orientation; this currently assumes that 
        //the wheels rotate forward when positive, and rotate backward when negative
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition() + ticks); //FL
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition() - ticks); //FR
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition() - ticks); //BL
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition() + ticks); //BR`
    
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        robot.frontLeft.setVelocity(velocityTicks);
        robot.frontRight.setVelocity(velocityTicks);
        robot.backLeft.setVelocity(velocityTicks);
        robot.backRight.setVelocity(velocityTicks);
    }
    
    
    public static double ticksToMetersFB (int ticks){
        return TICKS_PER_METER_FB * ticks;
    }
    
    public static int metersToTicksFB (double meters){
        return (int)(meters * TICKS_PER_METER_FB);
    }
    
    public static double ticksToMetersLR (int ticks){
        return TICKS_PER_METER_LR * ticks;
    }
    
    public static int metersToTicksLR (double meters){
        return (int)(meters * TICKS_PER_METER_LR);
    }
    
    
    
    
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.8f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       //tfodParameters.setThreads(4);
       
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
    
    
    
}
