package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp(name = "Pushbot: Manual Mode", group = "Linear Opmode")

public class Main extends LinearOpMode {
  private double drive;
  private double turn;
  // private double max;

  final double SPEED_MULTIPLIER = 0.81;
  final double ArmDefaultServoPosition = 0.98;

  HardwarePushbot robot = new HardwarePushbot(); // Use a Pushbot's hardware

  @Override
  public void runOpMode() {

    // Send telemetry message to signify robot waiting;
    telemetry.addData("Say", "Hello Driver"); //
    telemetry.update();

    robot.init(hardwareMap);

    // Wait for tdhe game to start (driver presses PLAY)
    this.waitForStart();
    if (this.isStopRequested()) {
        return;
    }

    while(opModeIsActive()) {
      this.drive = -this.gamepad1.left_stick_y;
      this.turn = this.gamepad1.right_stick_x;

      mecanumDrive_Cartesian(-this.gamepad1.right_stick_x, this.gamepad1.right_stick_y, this.gamepad1.left_stick_x);

      robot.duckWheel.setPower(this.gamepad1.left_trigger);

      if (this.gamepad1.right_bumper) {
        robot.intake.setPower(-1);
      } else if (this.gamepad1.left_bumper) {
        robot.intake.setPower(1);
      } else {
        robot.intake.setPower(0);
      }
      
      if (this.gamepad1.dpad_up) {
        robot.armMotor.setPower(0.5);
      }else if (this.gamepad1.dpad_down) {
        robot.armMotor.setPower(-0.5);
      }else {
        robot.armMotor.setPower(0);
      }
    }
  }

  public void resetArm() {
    robot.armServoRight.setPosition(this.ArmDefaultServoPosition);
    robot.armServoLeft.setPosition(this.ArmDefaultServoPosition);
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

    // normalize(wheelSpeeds);

    robot.frontLeft.setPower(-wheelSpeeds[0]);
    robot.frontRight.setPower(wheelSpeeds[1]);
    robot.backLeft.setPower(-wheelSpeeds[2]);
    robot.backRight.setPower(wheelSpeeds[3]);

    // telemetry.addData("front Left", robot.frontLeft.getPower());
    // telemetry.addData("front Right", robot.frontRight.getPower());
    // telemetry.addData("back Left", robot.backLeft.getPower());
    // telemetry.addData("back Right", robot.backRight.getPower());
    // telemetry.addData("x y r", x + " " + y + " " + rotation);
    // telemetry.update();

  } // end mecanumDrive_Cartesian
}