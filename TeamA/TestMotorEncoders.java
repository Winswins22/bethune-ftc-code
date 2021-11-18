// /* Copyright (c) 2017 FIRST. All rights reserved.
//  *
//  * Redistribution and use in source and binary forms, with or without modification,
//  * are permitted (subject to the limitations in the disclaimer below) provided that
//  * the following conditions are met:
//  *
//  * Redistributions of source code must retain the above copyright notice, this list
//  * of conditions and the following disclaimer.
//  *
//  * Redistributions in binary form must reproduce the above copyright notice, this
//  * list of conditions and the following disclaimer in the documentation and/or
//  * other materials provided with the distribution.
//  *
//  * Neither the name of FIRST nor the names of its contributors may be used to endorse or
//  * promote products derived from this software without specific prior written permission.
//  *
//  * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
//  * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//  * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//  * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
//  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
//  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//  */
 
//  /*
 
//  */

// package org.firstinspires.ftc.teamcode;

// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.eventloop.opmode.Disabled;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.hardware.DcMotor;
// import com.qualcomm.robotcore.util.ElapsedTime;


// @Autonomous(name="Pushbot: Auto Drive By Encoder", group="Pushbot")
// public class TestMotorEncoders extends LinearOpMode {

//     /* Declare OpMode members. */
//     HardwarePushbot         robot   = new HardwarePushbot();   // Use a Pushbot's hardware
//     private ElapsedTime     runtime = new ElapsedTime();
    
//     // frontLeft, frontRight, backLeft, backRight
//     public static int[] INITIAL_POSITIONS = {
        
//     };

//     @Override
//     public void runOpMode() {

//         /*
//          * Initialize the drive system variables.
//          * The init() method of the hardware class does all the work here
//          */
//         robot.init(hardwareMap);

//         // Send telemetry message to signify robot waiting;
//         telemetry.addData("Status", "Resetting Encoders");    //
//         telemetry.update();

//         robot.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//         robot.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

//         robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//         robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

//         // Send telemetry message to indicate successful Encoder reset
//         telemetry.addData("Path0",  "Starting at %7d :%7d",
//                           robot.leftDrive.getCurrentPosition(),
//                           robot.rightDrive.getCurrentPosition());
//         telemetry.update();

//         // Wait for the game to start (driver presses PLAY)
//         waitForStart();


//         // code lesgo

//         telemetry.update();
//     }

//     /*
//      */
//     public void encoderDrive(double distanceMultiplier) {
//         int newLeftTarget;
//         int newRightTarget;

//         // Ensure that the opmode is still active
//         if (opModeIsActive()) {

//             // Determine new target position, and pass to motor controller
//             robot.leftDrive.setTargetPosition(newLeftTarget);
//             robot.rightDrive.setTargetPosition(newRightTarget);

//             // Turn On RUN_TO_POSITION
//             robot.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//             robot.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

//             // reset the timeout time and start motion.
//             robot.leftDrive.setPower(Math.abs(speed));
//             robot.rightDrive.setPower(Math.abs(speed));

//             // keep looping while we are still active, and there is time left, and both motors are running.
//             // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
//             // its target position, the motion will stop.  This is "safer" in the event that the robot will
//             // always end the motion as soon as possible.
//             // However, if you require that BOTH motors have finished their moves before the robot continues
//             // onto the next step, use (isBusy() || isBusy()) in the loop test.
//             while (opModeIsActive() &&
//                   (robot.leftDrive.isBusy() && robot.rightDrive.isBusy())) {

//                 // Display it for the driver.
//                 telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
//                 telemetry.addData("Path2",  "Running at %7d :%7d",
//                                             robot.leftDrive.getCurrentPosition(),
//                                             robot.rightDrive.getCurrentPosition());
//                 telemetry.update();
//             }

//             // Stop all motion;
//             robot.leftDrive.setPower(0);
//             robot.rightDrive.setPower(0);

//             // Turn off RUN_TO_POSITION
//             robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//             robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

//             //  sleep(250);   // optional pause after each move
//         }
//     }
// }
