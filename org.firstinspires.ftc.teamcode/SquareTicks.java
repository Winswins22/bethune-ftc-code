// package org.firstinspires.ftc.teamcode;

// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
// import com.qualcomm.robotcore.eventloop.opmode.Disabled;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// import com.qualcomm.robotcore.hardware.DcMotor;
// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
// import com.qualcomm.robotcore.hardware.HardwareMap;
// import com.qualcomm.robotcore.hardware.Servo;
// import com.qualcomm.robotcore.util.ElapsedTime;
// @Autonomous

// public class SquareTicks {
//     // time
//     private ElapsedTime     runtime = new ElapsedTime();
    
//     /* Declare OpMode members. */
//     static HardwarePushbot robot           = new HardwarePushbot();   // Use a Pushbot's hardware
//     double          clawOffset      = 0;                       // Servo mid position
//     final double    CLAW_SPEED      = 0.02 ;                   // sets rate to move servo
    


//     @Override
//     public void runOpMode() {
        
//         double left;
//         double right;
//         double drive;
//         double turn;
//         double max;

//         /* Initialize the hardware variables.
//          * The init() method of the hardware class does all the work here
//          */
//         robot.init(hardwareMap);
//         robot.frontRight.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
//         robot.backRight.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
//         robot.frontLeft.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
//         robot.backLeft.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors


//         // Send telemetry message to signify robot waiting;
//         telemetry.addData("Say", "Hello Driver");    //
//         telemetry.update();

//         // Wait for the game to start (driver presses PLAY)
//         waitForStart();
//         runtime.reset(); 
        
        
//         // run until the end of the match (driver presses STOP)
//         while (opModeIsActive()) {
//         }
        
//         // run until the end of the match (driver presses STOP)
//         //             
//         // }
//     }

// }