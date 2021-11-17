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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
 
 @Autonomous(name="MAuto", group="mode")
 
public class MecanumAuto extends LinearOpMode
{
    
    // time
    private ElapsedTime     runtime = new ElapsedTime();
    
    /* Declare OpMode members. */
    static HardwarePushbot robot           = new HardwarePushbot();   // Use a Pushbot's hardware
    double          clawOffset      = 0;                       // Servo mid position
    final double    CLAW_SPEED      = 0.02 ;                   // sets rate to move servo
    


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

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset(); 
        
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            
            //F 1
            if (runtime.seconds() < 2.0){
                driveForwards();
               
               telemetry.addData("time forward", runtime.seconds() );    //
               telemetry.update();
            }
            
            //R 1sec
            else if (runtime.seconds() < 4.0){
                strafeRight();
              
                telemetry.addData("time right", runtime.seconds() );    //
              telemetry.update();
              
            }
            
            //B 1sec
            else if (runtime.seconds() < 6.0){
                driveBackwards();
              
                telemetry.addData("time backward", runtime.seconds() );    //
              telemetry.update();
            }
            
            //L 1sec
            else if (runtime.seconds() < 8.0){
                strafeLeft();
                
                telemetry.addData("time left", runtime.seconds() );    //
              telemetry.update();
              
            }
            else {
                break;
            }
            
        }
    }
    
    public static void driveForwards(){
        robot.frontLeft.setPower(1.0);
        robot.frontRight.setPower(1.0);
        robot.backLeft.setPower(1.0);
        robot.backRight.setPower(1.0);
    }
    
    public static void driveBackwards(){
        robot.frontLeft.setPower(-1.0);
        robot.frontRight.setPower(-1.0);
        robot.backLeft.setPower(-1.0);
        robot.backRight.setPower(-1.0);
    }
    
    public static void strafeLeft(){
        robot.frontLeft.setPower(-1.0);
        robot.frontRight.setPower(1.0);
        robot.backLeft.setPower(1.0);
        robot.backRight.setPower(-1.0); 
    }
    
    public static void strafeRight(){
        robot.frontLeft.setPower(1.0);
        robot.frontRight.setPower(-1.0);
        robot.backLeft.setPower(-1.0);
        robot.backRight.setPower(1.0); 

    }

 }

