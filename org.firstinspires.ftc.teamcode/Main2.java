package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="Pushbot: Mecanum wheels", group="mode")

public class Main2 extends LinearOpMode {

    /* Declare OpMode members. */
    HardwarePushbot robot           = new HardwarePushbot();   // Use a Pushbot's hardware
    double          clawOffset      = 0;                       // Servo mid position
    final double    CLAW_SPEED      = 0.02 ;                   // sets rate to move servo
    
    int initial = 0;
    
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

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();
        
        initial = robot.frontLeft.getCurrentPosition();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            drive = -gamepad1.left_stick_y;
            turn  =  gamepad1.right_stick_x;

            mecanumDrive_Cartesian(-gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);
            
            
            robot.duckWheel.setPower(gamepad1.right_trigger);
            robot.duckWheel.setPower(-(gamepad1.left_trigger));
            
            //if (gamepad1.right_bumper) {
                //robot.intake.setPower(-1); 
            //}
            //else if (gamepad1.left_bumper) {
               // robot.intake.setPower(1);
            //}
            //else {
               // robot.intake.setPower(0);
            //}
            
        }
        }
    public void mecanumDrive_Cartesian(double x, double y, double rotation)
{
    double wheelSpeeds[] = new double[4];

    wheelSpeeds[0] = x + y - rotation;
    wheelSpeeds[1] = -x + y + rotation;
    wheelSpeeds[2] = -x + y - rotation;
    wheelSpeeds[3] = x + y + rotation;

    //normalize(wheelSpeeds);

    robot.frontLeft.setPower(-wheelSpeeds[0]);
    robot.frontRight.setPower(wheelSpeeds[1]);
    robot.backLeft.setPower(-wheelSpeeds[2]);
    robot.backRight.setPower(wheelSpeeds[3]);
    
    telemetry.addData("Ticks from Initial", robot.frontLeft.getCurrentPosition() - initial);
    telemetry.addData("front Left", robot.frontLeft.getPower());
    telemetry.addData("front Right", robot.frontRight.getPower()); 
    telemetry.addData("back Left", robot.backLeft.getPower()); 
    telemetry.addData("back Right", robot.backRight.getPower()); 
    telemetry.addData("right Trigger", gamepad1.right_trigger); 
    telemetry.addData("left Trigger", gamepad1.left_trigger); 
    telemetry.addData("left_bumper", gamepad1.left_bumper); 
    telemetry.addData("right_bumper", gamepad1.right_bumper); 
    telemetry.addData("x y r", x + " " + y + " " + rotation);
    telemetry.update();
    
}   //end mecanumDrive_Cartesian

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
}//end classss