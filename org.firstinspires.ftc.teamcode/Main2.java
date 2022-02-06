package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp(name = "Pushbot: Manual Mode2", group = "Linear Opmode")

public class Main2 extends LinearOpMode {
  private double drive;
  private double turn;
  // private double max;

  private final double SPEED_MULTIPLIER = 0.55;
  
  // Arm 
  private final double ArmDefaultServoPosition = 0.95;
  private final String[] stage = {"dummy", "BucketUp", "PulleyUp", "PulleySlow", "PulleyStop", "BucketDump", "BucketUp", "PulleyDown", "PulleySlow", "PulleyStop", "BucketReset"};
  private final double[] stageTime= {0.0, 0.0, 1.9, 0.5, 0.0, 1.0, 0.5, 0.5, 1.1, 0.0, 0.0};
  private int stageIDX = 0;
  private boolean stageDone = false;
  private boolean activateArm = false;
  private ElapsedTime armTimer = new ElapsedTime();

  // duck
  private int duckStage = 0;
  private final double[] duckTime = {4.0, 4.0, 0.0};
  private boolean duckStageDone = false;
  private boolean activateDuck = false;
  private ElapsedTime duckTimer = new ElapsedTime();

  HardwarePushbot robot = new HardwarePushbot(); // Use a Pushbot's hardware

  @Override
  public void runOpMode() {

    // Send telemetry message to signify robot waiting;
    telemetry.addData("Say", "Hello Driver"); //
    telemetry.update();

    robot.init(hardwareMap);


    // Wait for the game to start (driver presses PLAY)
    this.waitForStart();

    this.resetArm();

    while(opModeIsActive()) {

      telemetry.addData("duck", "");
      telemetry.addData("done", this.duckStageDone);
      telemetry.addData("stage", this.duckStage);
      telemetry.addData("timer", this.duckTimer.seconds());
      telemetry.update();

      if (this.gamepad1.right_bumper) {
        robot.intake.setPower(-1);
      } else if (this.gamepad1.left_bumper) {
        robot.intake.setPower(1);
      } else {
        robot.intake.setPower(0);
      }
      
      // if (this.gamepad1.dpad_up) {
      //   robot.armMotor.setPower(0.5);
      // }else if (this.gamepad1.dpad_down) {
      //   robot.armMotor.setPower(-0.5);
      // }else {
      //   robot.armMotor.setPower(0);
      // }
      // robot.duckWheel.setPower(this.gamepad1.left_trigger);

      if (this.gamepad1.b && !this.activateArm) {
        this.activateArm = true;
        this.armTimer.reset();
      }
      if (this.gamepad1.x && !this.activateDuck){
        this.activateDuck = true;
        this.duckTimer.reset();
      }

      if (this.activateArm){
        this.updateArm();
      }
      
      if (this.activateDuck){
        this.updateDuck();
      }
      this.updateWheel();
    }
  }

  public void updateWheel() {
    this.drive = -this.gamepad1.left_stick_y;
    this.turn = this.gamepad1.right_stick_x;

    mecanumDrive_Cartesian(-this.gamepad1.right_stick_x, this.gamepad1.right_stick_y, this.gamepad1.left_stick_x);
  }

  public void updateArm() {
    if (this.armTimer.seconds() >= this.stageTime[this.stageIDX]) {
      this.stageIDX += 1;
      this.stageDone = false;
    }
    if (!this.stageDone) {
      switch (this.stageIDX) {
        case 1: // BucketUp
          robot.armServoLeft.setPosition(0.7);
          robot.armServoRight.setPosition(0.3);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 2: // PulleyUp
          robot.armMotor.setPower(0.5);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 3: // PulleySlow going up
          robot.armMotor.setPower(0.2);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 4: // PulleyStop
          robot.armMotor.setPower(0.0);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 5: // BucketDump
          robot.armServoRight.setPosition(0.55);
          robot.armServoLeft.setPosition(0.45);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 6: // BucketUp
          robot.armServoLeft.setPosition(0.7);
          robot.armServoRight.setPosition(0.3);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 7: // PulleyDown 
          robot.armMotor.setPower(-1);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 8: // PulleySlow going down
          robot.armMotor.setPower(-0.2);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 9: // Pulley Stop
          robot.armMotor.setPower(0.0);
          this.armTimer.reset();
          this.stageDone = true;
          break;
        case 10: // BucketReset
          resetArm();
          this.stageDone = true;
          this.stageIDX = 0;
          this.activateArm = false;
          break;
      }
    }
  }

  public void resetArm() {
    robot.armServoRight.setPosition(1-this.ArmDefaultServoPosition);
    robot.armServoLeft.setPosition(this.ArmDefaultServoPosition);

  }

  public void updateDuck(){
    if (this.duckTimer.seconds() < 0.5){
      robot.duckWheel.setPower(0.8);
    }
    else if (this.duckTimer.seconds() < 1.5){
      robot.duckWheel.setPower(1.0);
    }
    else{
      robot.duckWheel.setPower(0.0);
      this.activateDuck = false;
    }
  }

  public void mecanumDrive_Cartesian(double x, double y, double rotation) {
    double wheelSpeeds[] = new double[4];

    wheelSpeeds[0] = x + y - rotation;
    wheelSpeeds[1] = -x + y + rotation;
    wheelSpeeds[2] = -x + y - rotation;
    wheelSpeeds[3] = x + y + rotation;

    for (int i = 0; i < wheelSpeeds.length; i++) {
      wheelSpeeds[i] = wheelSpeeds[i] * this.SPEED_MULTIPLIER;
    }


    robot.frontLeft.setPower(-wheelSpeeds[0]);
    robot.frontRight.setPower(wheelSpeeds[1]);
    robot.backLeft.setPower(-wheelSpeeds[2]);
    robot.backRight.setPower(wheelSpeeds[3]);
  } // end mecanumDrive_Cartesian
}