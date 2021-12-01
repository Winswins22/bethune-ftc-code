package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="Pushbot: Mecanum wheels", group="mode")

public class Main2 extends LinearOpMode {

    /* Declare OpMode members. */
    HardwarePushbot robot           = new HardwarePushbot();   // Use a Pushbot's hardware
    double          clawOffset      = 0;                       // Servo mid position
    final double    CLAW_SPEED      = 0.02 ;                   // sets rate to move servo
    private ElapsedTime     runtime = new ElapsedTime();

    double speedMultiplier = 1.0;
    double turningMultiplier = 0.7;
    
    //SENSITIVITY FORMULA: ROTATION_COEFFICIENT * (JOYSTICK ^ ROTATION_EXPONENT)
    //Remember that JOYSTICK is always clamped from -1 to 1
    public static final double ROTATION_EXPONENT = 1;
    public static final double ROTATION_COEFFICIENT = 0.3;

    //ARM TICK BOUNDS 
    public static final int ARM_MOTOR_UPPER_BOUNDS = -100000000;
    public static final int ARM_MOTOR_LOWER_BOUNDS = 100000000;
    
    //ARM CLAMPED MAX VELOCITY IN TICKS/S (LINEAR)
    public static final int ARM_MOTOR_MAX_VELOCITY = 600;
    
    //The minimum measured velocity in ticks per second before an idle tick position is set;
    //this is to prevent the arm from going back to the position controls were released after 
    //it overshoots that target, since the motor will always coast a little even after controls being released
    //This might not be necessary due to the weight of the arm.
    public static final double ARM_SET_IDLE_DELAY_SECONDS = 0.2;
    
    class GamepadCustom extends Gamepad{
        public GamepadCustom(){
            joystickDeadzone = 0f;
        } 
    }
    
    @Override
    public void runOpMode() {
        
        GamepadCustom gamepad = new GamepadCustom();
        gamepad1 = gamepad;
        
        double left;
        double right;
        double drive;
        double turn;
        double max;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();
        
        //INITIALIZE THE MOTORS
        resetMotors();
        runMotorsWithEncodersAndReset(); //DEBUG; COMMENT OUT WHEN NECESSARY

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        
        //boolean flag for arm idle position and the idle position
        boolean setIdlePosOnce = false;
        boolean recordedArmControlReleaseTime = false;
        int idlePosL = 0, idlePosR = 0;
        double armReleasedTime = 0;
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            drive = gamepad1.left_stick_y;
            turn  = gamepad1.right_stick_x;
            
            //intake
            
            if (gamepad1.left_bumper || gamepad1.dpad_up){
                intakePower(-1);
            }
            else if (gamepad1.right_bumper || gamepad1.dpad_down){
                intakePower(1);
            } else {
                intakePower(0);
            } 
            
            if(gamepad1.x){
                robot.duckMotor.setPower(1);
            } else 
                robot.duckMotor.setPower(0);
            
            
            robot.armMotorL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.armMotorR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //UP
            if(gamepad1.left_trigger > 0 && !(gamepad1.right_trigger > 0)){ 
                setIdlePosOnce = false;
                recordedArmControlReleaseTime = false;
                
                robot.armMotorL.setTargetPosition(ARM_MOTOR_UPPER_BOUNDS);
                robot.armMotorL.setVelocity(gamepad1.left_trigger / 1 * ARM_MOTOR_MAX_VELOCITY);
                
                robot.armMotorR.setTargetPosition(ARM_MOTOR_UPPER_BOUNDS);
                robot.armMotorR.setVelocity(gamepad1.left_trigger / 1 * ARM_MOTOR_MAX_VELOCITY);
            } 
            else if(gamepad1.right_trigger > 0 && !(gamepad1.left_trigger > 0)){ //DOWN
                setIdlePosOnce = false;
                recordedArmControlReleaseTime = false;
                
                robot.armMotorL.setTargetPosition(ARM_MOTOR_LOWER_BOUNDS);
                robot.armMotorL.setVelocity(-gamepad1.right_trigger / 1 * ARM_MOTOR_MAX_VELOCITY);
                
                robot.armMotorR.setTargetPosition(ARM_MOTOR_LOWER_BOUNDS);
                robot.armMotorR.setVelocity(-gamepad1.right_trigger / 1 * ARM_MOTOR_MAX_VELOCITY);
                
            } else { //IDLE AND STAY
            
                if(!recordedArmControlReleaseTime){
                    armReleasedTime = runtime.seconds();
                    recordedArmControlReleaseTime = true;
                }
            
                if(!setIdlePosOnce && ((runtime.seconds() - armReleasedTime) > ARM_SET_IDLE_DELAY_SECONDS)){
                    idlePosL = robot.armMotorL.getCurrentPosition();
                    idlePosR = robot.armMotorR.getCurrentPosition();
                    setIdlePosOnce = true;
                }
                
                robot.armMotorL.setTargetPosition(idlePosL);
                robot.armMotorL.setVelocity(ARM_MOTOR_MAX_VELOCITY);    
                robot.armMotorR.setTargetPosition(idlePosR);
                robot.armMotorR.setVelocity(ARM_MOTOR_MAX_VELOCITY);
            }

            drive(drive, turn);
            telemetry.update();
            }
        }
        
    
        
        
    public void drive(double y, double rotation)
    {
        double wheelSpeeds[] = new double[4];
        
        int positiveOrNegative = 0;
        if(rotation < 0)
            positiveOrNegative = -1;
        else if(rotation > 0)
            positiveOrNegative = 1; 
        
        rotation = positiveOrNegative * ROTATION_COEFFICIENT * exp(rotation, ROTATION_EXPONENT);
    
        wheelSpeeds[0] = y - rotation;
        wheelSpeeds[1] = y + rotation;
        wheelSpeeds[2] = y - rotation;
        wheelSpeeds[3] = y + rotation;
    
        //normalize(wheelSpeeds);
        reduceSpeeds(wheelSpeeds, speedMultiplier);
    
        robot.frontLeft.setPower(-wheelSpeeds[0]); //FL
        robot.frontRight.setPower(wheelSpeeds[1]); //FR
        robot.backLeft.setPower(-wheelSpeeds[2]); //BL
        robot.backRight.setPower(wheelSpeeds[3]); //BR
        
        
    
        telemetry.addData("front Left", robot.frontLeft.getPower());
        telemetry.addData("front Right", robot.frontRight.getPower());
        telemetry.addData("back Left", robot.backLeft.getPower());
        telemetry.addData("back Right", robot.backRight.getPower());
        //telemetry.addData("speedMultiplier", speedMultiplier);
        telemetry.addData("arm motor L position: ", robot.armMotorL.getCurrentPosition());
        telemetry.addData("frontLeft motor position:", robot.frontLeft.getCurrentPosition());
        telemetry.addData("backRight motor position:", robot.backRight.getCurrentPosition());
        telemetry.addData("(FL - BR)(Forward/back): ", robot.frontLeft.getCurrentPosition() + robot.backRight.getCurrentPosition());
        
    
    }   //end drive

    private void reduceSpeeds(double[] wheelSpeeds, double multiplier){
        for (int i = 0; i < wheelSpeeds.length; i ++){
            wheelSpeeds[i] = wheelSpeeds[i] * multiplier;
        }
    }
    
    public void intakePower(double power) {
        robot.intakeMotor.setPower(power);
    } 

    private void resetMotors(){
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
        robot.armMotorL.setPower(0);
        robot.armMotorR.setPower(0);
        
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.armMotorL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.armMotorR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // reset target positions
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition());
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition());
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition());
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition());
        robot.armMotorL.setTargetPosition(robot.armMotorL.getCurrentPosition());
        robot.armMotorR.setTargetPosition(robot.armMotorR.getCurrentPosition());
    }

    public void runMotorsWithEncodersAndReset(){
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.armMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.armMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // reset target positions
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition());
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition());
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition());
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition());
        robot.armMotorL.setTargetPosition(robot.armMotorL.getCurrentPosition());
        robot.armMotorR.setTargetPosition(robot.armMotorR.getCurrentPosition());
        
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.armMotorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.armMotorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    } 

    /* private double reduceRotation(double rotation){
        return rotation * turningMultiplier; 
    } */
    
    public static double exp(double n, double e){
        
        if(e == 0) return 1;
        
        double num = n;
        for(int i = 0; i < e; i++){
            num *= num;
        }
        return num;
        
    }


// private void normalize(double[] wheelSpeeds)
// {
//     double maxMagnitude = Math.abs(wheelSpeeds[0]);

//     for (int i = 1; i < wheelSpeeds.length; i++)
//     {
//         double magnitude = Math.abs(wheelSpeeds[i]);

//         if (magnitude > maxMagnitude)
//         {
//             maxMagnitude = magnitude;
//         }
//     }

//     if (maxMagnitude > 1.0)
//     {
//         for (int i = 0; i < wheelSpeeds.length; i++)
//         {
//             wheelSpeeds[i] /= maxMagnitude;
//         }
//     }
// }   //normalize
}//end class



