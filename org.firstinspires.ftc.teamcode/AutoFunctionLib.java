/*
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.List;
import java.util.Random;
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
    //approximate ticks to traverse width (DRIVE): basically 3000 every time
    //TODO: Measure using driving mode instead
    //approximate ticks to traverse length (DRIVE): basically 
    public static final double TICKS_PER_METER_FB = 1313;
    
    public static final double CAMERA_SCAN_SECONDS = 1.4d;
    
    //at least this many wheels have to be idle for the wheelsAreBusy() to be true.
    public static final int WHEELS_IDLE_MIN_COUNT = 3;
    
    public static final int TICKS_PER_180 = 820;
    
    HardwarePushbot robot = new HardwarePushbot(); //use this object for easy reference to components like motors.
    
    ElapsedTime runtime = new ElapsedTime();
    
    //Assumes that the bottom-left (red markers on left side of field) is 0,0. Measured in meters since ticks do not
    //necessarily correspond to distance (e.g. ticks/m is different when strafing).
    //Manipulated by moveTickFB and moveTickLR functions (yes, it'd be more intuitive if those functions were correspondingly
    //in meters instead of ticks, but ticks gives us more precision) 
    public static double estimatedX = 0;
    public static double estimatedY = 0;
    //In degrees, relative to cartesian plane; 0 degrees means facign straighr right, 90 degrees is up, -90 degrees is down.
    public static double estimatedR = 0;
    
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

        robot.init(hardwareMap);
        
        initVuforia();
        initTfod();
        tfod.activate();
        tfod.setZoom(2.5, 16.0/9.0);
        
        initial = robot.frontLeft.getCurrentPosition();
        int randint = ThreadLocalRandom.current().nextInt(1, 4); 
        telemetry.addData("Randint: ", randint);
        telemetry.addData("Robot", "Initialized");
        telemetry.update();
 
        waitForStart();
        
        initWheelMotors();
        
        Random random = new Random();
        
        boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false, flag5 = false, flag6 = false;
        boolean flag7 = false, flag8 = false, flag9 = false, flag10 = false, flag11 = false, flag12 = false;
        boolean flag13 = false, flag14 = false, flag15 = false, flag16 = false, flag17 = false, flag18 = false;
        // run until the end of the match (driver presses STOP)
        double stopTime = 0d;
        boolean duckFound = false;
        int duckLevel = 0;
        while (opModeIsActive()) {
            
            telemetry.addData("WheelsAreBusy: ", wheelsAreBusy());
            telemetry.addData("WheelsBusy: ", DEBUGBusy());
                
            //1: Raise arm to prevent dragging, reverse into duck wheel, and spin duck whee, then stop duck wheel
            if (!flag1){
                setArmPosition(-50, 600); //move arm up
                moveTickFB(25, 150); //reverse into duck wheel 
                duckMotor(1); //spin duck wheel
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", 1);
                    telemetry.addData("Randint: ", randint);
                    telemetry.update();
                }
                stopTime = runtime.seconds();
                while(runtime.seconds() - stopTime < 1.1d){
                    telemetry.addData("Spinning duck wheel... ", runtime.seconds() - stopTime);
                    telemetry.update();
                }
                duckMotor(0); //shut off duck wheel
                flag1 = true;
            }
            initWheelMotors();
            
            //2.1: drive forwards and align with first marker
            if(!flag2 && !wheelsAreBusy()){
                moveTickFB(-265, 450);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", "2.1: aligning");
                    telemetry.addData("Randint: ", randint);
                    telemetry.addData("Duck found: ", duckFound);
                    telemetry.addData("Duck level: ", duckLevel);
                    telemetry.update();
                }
                flag2 = true;
            }
            initWheelMotors();
            
            //2.1: scan for 2 seconds
            if(!flag9 && !wheelsAreBusy()){
                stopTime = runtime.seconds();
                flag9 = true;
                while(runtime.seconds() - stopTime < CAMERA_SCAN_SECONDS){
                    telemetry.addData("Stage: ", "2.1: scanning");
                    boolean detected = false;
                    telemetry.addData("runtime: ", runtime.seconds());                    
                    if (tfod != null) {
                        // getUpdatedRecognitions() will return null if no new information is available since
                        // the last time that call was made.
                        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                        if (updatedRecognitions != null) {
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
                            if(recognition.getLabel().equals("Duck")){
                                detected = true;
                            }
                          }
                        }
                    }
                    if(detected){
                        duckFound = true;
                        duckLevel = 1;
                    }
                    telemetry.update();
                }
            }
            initWheelMotors();
            
            //2.2: drive forwards and align with second marker
            if(!flag10 && !wheelsAreBusy() && !duckFound){
                moveTickFB(-265,450);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", "2.2: aligning");
                    telemetry.addData("Duck found: ", duckFound);
                    telemetry.addData("Duck level: ", duckLevel);
                    telemetry.update();
                }
                flag10 = true;
            }
            initWheelMotors();
            
            //2.2: scan for 2 seconds
            if(!flag11 && !wheelsAreBusy() && !duckFound){
                stopTime = runtime.seconds();
                flag11 = true;
                while(runtime.seconds() - stopTime < CAMERA_SCAN_SECONDS){
                    telemetry.addData("Stage: ", "2.2: scanning");
                    telemetry.addData("runtime: ", runtime.seconds());
                    boolean detected = false;
                    if (tfod != null) {
                        
                        // getUpdatedRecognitions() will return null if no new information is available since
                        // the last time that call was made.
                        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                        if (updatedRecognitions != null) {
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
                            if(recognition.getLabel().equals("Duck")){
                                detected = true;
                            }
                          }
                        }
                    }
                    if(detected){
                        duckFound = true;
                        duckLevel = 2;
                    }
                    telemetry.update();
                }
            }
            initWheelMotors();
            
            //2.3: drive forwards and align with third marker
            if(!flag12 && !wheelsAreBusy() && !duckFound){
                moveTickFB(-265, 450);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", "2.3: aligning");
                    telemetry.addData("Duck found: ", duckFound);
                    telemetry.addData("Duck level: ", duckLevel);
                    telemetry.update();
                }
                flag12 = true;
            }
            initWheelMotors();
            
            //2.3: scan for 2 seconds
            if(!flag13 && !wheelsAreBusy() && !duckFound){
                stopTime = runtime.seconds();
                flag13 = true;
                while(runtime.seconds() - stopTime < CAMERA_SCAN_SECONDS){
                    telemetry.addData("Stage: ", "2.3: scanning");
                    telemetry.addData("runtime: ", runtime.seconds());
                    boolean detected = false;
                    if (tfod != null) {
                        
                        // getUpdatedRecognitions() will return null if no new information is available since
                        // the last time that call was made.
                        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                        if (updatedRecognitions != null) {
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
                            if(recognition.getLabel().equals("Duck")){
                                detected = true;
                            }
                          }
                        }
                    }
                    if(detected){
                        duckFound = true;
                        duckLevel = 3;
                    }
                    telemetry.update();
                }
            }
            initWheelMotors();
            
            //2.4: move and align broadside with tower
            if (!flag14 && !wheelsAreBusy()){
                if(duckFound){
                    if(duckLevel == 1){
                        moveTickFB(-1200, 650);
                    }
                    if(duckLevel == 2){
                        moveTickFB(-900, 650);
                    }
                    if(duckLevel == 3){
                        moveTickFB(-600, 650);
                    }
                } else {
                    moveTickFB(-600, 650);
                }
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", "2.4: aligning for turn");
                    telemetry.addData("Randint: ", randint);
                    telemetry.addData("Duck found: ", duckFound);
                    telemetry.addData("Duck level: ", duckLevel);
                    telemetry.update();
                }
                flag14 = true;
            }
            initWheelMotors();
            
            //3: rotate 90 degrees left to face storage tower
            if (!flag3 && !wheelsAreBusy()){
                rotateOnSpot(90, 200);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", 3);
                    telemetry.addData("Randint: ", randint);
                    telemetry.addData("Duck found: ", duckFound);
                    telemetry.addData("Duck level: ", duckLevel);
                    telemetry.update();
                }
                flag3 = true;
            } 
            initWheelMotors();
            
            //4: drive towards tower
            if (!flag4 && !wheelsAreBusy()){
                moveTickFB(-620, 650);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", 4);
                    telemetry.addData("Randint: ", randint);
                    telemetry.update();
                }
                flag4 = true;
            } 
            initWheelMotors();
            
            //-47, -71, -107
            //5: set arms to right position
            if (!flag5 && !wheelsAreBusy()){
                
                int pos = 0;
                if(duckFound == false){
                    if(randint == 1){
                        pos = -47;
                    } else if(randint == 2){
                        pos = -71;
                    } else if(randint == 3){
                        pos = -107;
                    }
                } else {
                    if(duckLevel == 1){
                        pos = -47;
                    } else if(duckLevel == 2){
                        pos = -71;
                    } else if(duckLevel == 3){
                        pos = -107;
                    }
                }
                
                
                setArmPosition(pos, 200);
                while(robot.armMotorL.isBusy()){
                    telemetry.addData("Target arm Pos: ", pos);
                    telemetry.addData("Randint: ", randint);
                    telemetry.addData("Stage: ", 5);
                    telemetry.update();
                }
                flag5 = true;
            } 
            initWheelMotors();
            
            //6: eject item
            if (!flag6 && !wheelsAreBusy()){
                intakeVelocity(-295);
                telemetry.addData("Stage: ", 6);
                telemetry.update();
                stopTime = runtime.seconds();
                while(runtime.seconds() - stopTime < 1.5d){
                    telemetry.addData("Ejecting... ", runtime.seconds() - stopTime);
                    telemetry.update();
                }
                intakeVelocity(0);
                flag6 = true;
            } 
            initWheelMotors();
            
            //7: turn 90 degrees left towards warehouse
            if (!flag7 && !wheelsAreBusy()){
                rotateOnSpot(-100, 200);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", 7);
                    telemetry.update();
                }
                flag7 = true;
            } 
            initWheelMotors();
            
            //8: drive towards warehouse
            if (!flag8 && !wheelsAreBusy()){
                moveTickFB(-3000, 3000);
                while(wheelsAreBusy()){
                    telemetry.addData("Stage: ", 8);
                    telemetry.update();
                }
                flag8 = true;
            } 
            initWheelMotors();
            
            telemetry.addData("Stage: ", "Finished");
            telemetry.addData("frontLeft motor position: ", robot.frontLeft.getCurrentPosition());
            telemetry.addData("backRight motor position: ", robot.backRight.getCurrentPosition());
            telemetry.update();
            
        }
    }
*/
    /**
     * resets motor tick positions and target positions
     */
/*
    public void initWheelMotors(){
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
*/
/*
    public void rotateOnSpot(double degrees, double degreesPerSecond){
        
        degrees = -degrees;
        
        double ticks = (degrees / 180d) * (double)TICKS_PER_180;
            
        double velocityTicks = (degreesPerSecond / 180d) * (double)TICKS_PER_180;
        
        /*
        if(degrees < 0){
            ticks = -ticks;
        }
        

        robot.frontLeft.setTargetPosition((int)Math.round( robot.frontLeft.getCurrentPosition() + ticks)); 
        robot.frontRight.setTargetPosition((int)Math.round( robot.frontRight.getCurrentPosition() + ticks)); 
        robot.backLeft.setTargetPosition((int)Math.round(robot.backLeft.getCurrentPosition() + ticks)); 
        robot.backRight.setTargetPosition((int)Math.round( robot.frontRight.getCurrentPosition() + ticks)); 
    
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        robot.frontLeft.setVelocity(velocityTicks);
        robot.frontRight.setVelocity(velocityTicks);
        robot.backLeft.setVelocity(velocityTicks);
        robot.backRight.setVelocity(velocityTicks);
        
    }*/
    
    //positive ticks for forward, negative ticks for backward
    /*public void moveTickFB(int ticks, int velocityTicks){
    
        robot.frontLeft.setTargetPosition(- ticks); //FL
        robot.frontRight.setTargetPosition(ticks); //FR
        robot.backLeft.setTargetPosition(- ticks); //BL
        robot.backRight.setTargetPosition(ticks); //BR`
    
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
    
    public void intakeMotor(int power){
        robot.intakeMotor.setPower(power);
    }
    
    public void duckMotor(int power){
        robot.duckMotor.setPower(power);
    }
    
    public void intakeVelocity(int ticksVelocity){
        robot.intakeMotor.setVelocity(ticksVelocity);
    }
    
    public boolean wheelsAreBusy(){
        boolean busy = false;
        if(robot.frontLeft.isBusy()) busy = true;
        if(robot.frontRight.isBusy()) busy = true;
        if(robot.backLeft.isBusy()) busy = true;
        if(robot.backRight.isBusy()) busy = true;
        
        return busy;
        
    }
    
    public int DEBUGBusy(){
        int busy = 0;
        if(robot.frontLeft.isBusy()) busy++;
        if(robot.frontRight.isBusy()) busy++;
        if(robot.backLeft.isBusy()) busy++;
        if(robot.backRight.isBusy()) busy++;
        return busy;
    }
    
    public void setArmPosition(int position, int ticksVelocity){
        robot.armMotorL.setTargetPosition(position);
        robot.armMotorR.setTargetPosition(position);
        
        robot.armMotorL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.armMotorR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        robot.armMotorL.setVelocity(ticksVelocity);
        robot.armMotorR.setVelocity(ticksVelocity);
    }*/
    /*
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }
    */
    
    /*
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
    */
//}

