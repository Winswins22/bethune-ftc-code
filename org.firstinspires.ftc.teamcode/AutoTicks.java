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
  import com.qualcomm.robotcore.hardware.DcMotor;
  import com.qualcomm.robotcore.eventloop.opmode.Disabled;
  import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
  import com.qualcomm.robotcore.util.ElapsedTime;
  
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
      private double defaultPosition = 0.95;
  
      /* Declare OpMode members. */
      HardwarePushbot         robot   = new HardwarePushbot();   // Use a Pushbot's hardware
      private ElapsedTime     runtime = new ElapsedTime();
  
      @Override
      public void runOpMode() {
  
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
                  
                  // // move to carosel
                  moveMeters(0, 1, 0, 0.2, METERS_PER_TILE / 2);
                  move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                  
                  moveMeters(0, 1, 0, 0.3, METERS_PER_TILE * 0.8);
                  move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                  moveMeters(0, 1, 0, 0.05, METERS_PER_TILE / 4);
      
                  // // duck wheel code here
                  if (duck){
                      goForwardsSlowly();
                      rotateDuckWheel(1, 6000);
                      initMotors();
                      duck = false;
                  }
                  
                  // // move back a little for rotation space
                  moveMeters(0, -1, 0, 0.3, METERS_PER_TILE / 2);
                  move(0, 0, 1, 0.2, TICKS_ROTATE_90_DEGREES);
                  
                  // slam into the back wall to realign
                  moveMeters(0, 1, 0, 0.3, METERS_PER_TILE / 4);
                  
                  
                  // move to the shipping hub
                  moveMeters(0, -1, 0, 0.3, METERS_PER_TILE * 2.2);
                  
                  // face the shipping hub
                  move(0, 0, -1, 0.2, (int)(TICKS_ROTATE_90_DEGREES));
                //   // insert arm code here
                  
                //   armTimer.reset();
                //   while (true) {
                //       if (autoArm()) {
                //           armTimer.reset();
                //           break;
                //       }
                //   }
                //   // end arm code
                  // face the warehouse
                  move(0, 0, -1, 0.2, (int)(TICKS_ROTATE_90_DEGREES));
      
                  // move to the warehouse
                  moveMeters(0, 1, 0, 0.3, METERS_PER_TILE / 2);
                  move(0, 0, 1, 0.2, TICKS_ROTATE_90_DEGREES);
                  moveMeters(0, 1, 0, 0.3, METERS_PER_TILE);
                  move(0, 0, -1, 0.2, TICKS_ROTATE_90_DEGREES);
                  moveMeters(1, 0, 0, 0.3, METERS_PER_TILE / 4);
                  moveMeters(0, 1, 0, 0.3, METERS_PER_TILE * 2);
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
      
      public boolean autoArm() {
        if (armTimer.seconds() <= 0.5) {
          robot.hand.setPosition(0.70);
        } else if (armTimer.seconds() <= 1.0) {
          robot.arm.setPower(1);
        } else if (armTimer.seconds() <= 2.0) {
          robot.arm.setPower(0);
          robot.hand.setPosition(0.15);
        } else if (armTimer.seconds() <= 2.5) {
          robot.arm.setPower(-0.70);
          robot.hand.setPosition(defaultPosition);
        } else if (armTimer.seconds() <= 3.0) {
          robot.arm.setPower(0);
        } else {
          return true;
        }
        return false;
      }
          
  }
  