package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Pushbot: Mecanum wheels", group = "mode")

public class Main2 extends LinearOpMode {

    /* Declare OpMode members. */
    HardwarePushbot robot = new HardwarePushbot(); // Use a Pushbot's hardware
    double clawOffset = 0; // Servo mid position
    final double CLAW_SPEED = 0.02; // sets rate to move servo

    @Override
    public void runOpMode() {
        double left;
        double right;
        double drive;
        double turn;
        double max;

        /*
         * Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver"); //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed
            // forwards, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick
            // turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            drive = -gamepad1.left_stick_y;
            turn = gamepad1.right_stick_x;

            mecanumDrive_Cartesian(-gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);

            // if (gamepad1.right_trigger > 0.0) {
            // robot.duckWheel.setPower(-1);
            // } else if (gamepad1.left_trigger > 0.0) {
            // robot.duckWheel.setPower(1);
            // } else {
            // robot.duckWheel.setPower(0);
            // }

            // if (gamepad1.right_bumper) {
            // robot.intake.setPower(-1);
            // } else if (gamepad1.left_bumper) {
            // robot.intake.setPower(1);
            // } else {
            // robot.intake.setPower(0);
            // }

            AutoDuck();
            AutoIntake();

        }
    }

    public void mecanumDrive_Cartesian(double x, double y, double rotation) {
        double wheelSpeeds[] = new double[4];

        wheelSpeeds[0] = x + y - rotation;
        wheelSpeeds[1] = -x + y + rotation;
        wheelSpeeds[2] = -x + y - rotation;
        wheelSpeeds[3] = x + y + rotation;

        // normalize(wheelSpeeds);

        robot.frontLeft.setPower(-wheelSpeeds[0]);
        robot.frontRight.setPower(wheelSpeeds[1]);
        robot.backLeft.setPower(-wheelSpeeds[2]);
        robot.backRight.setPower(wheelSpeeds[3]);

        telemetry.addData("front Left", robot.frontLeft.getPower());
        telemetry.addData("front Right", robot.frontRight.getPower());
        telemetry.addData("back Left", robot.backLeft.getPower());
        telemetry.addData("back Right", robot.backRight.getPower());
        telemetry.addData("x y r", x + " " + y + " " + rotation);
        telemetry.update();

    } // end mecanumDrive_Cartesian

    public void AutoDuck() {
        int ticksToIncreaseBy;
        if (gamepad1.a) {
            // Slow for 50000tick
            double Dpower = 0.5;

            ticksToIncreaseBy = translatePowerToTicks(Dpower);
            robot.duckWheel.setTargetPosition(robot.duckWheel.getCurrentPosition() + ticksToIncreaseBy);
            robot.duckWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.duckWheel.setPower(Dpower);

            while (robot.duckWheel.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", "");
                telemetry.addData("duckWheel progress",
                        robot.duckWheel.getTargetPosition() - robot.duckWheel.getCurrentPosition());
                telemetry.update();
            }

            // Fast for 100000tick
            int Ipower = 1;

            ticksToIncreaseBy = translatePowerToTicks(Ipower);
            robot.duckWheel.setTargetPosition(robot.duckWheel.getCurrentPosition() + ticksToIncreaseBy);
            robot.duckWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.duckWheel.setPower(Ipower);

            while (robot.duckWheel.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", "");
                telemetry.addData("duckWheel progress",
                        robot.duckWheel.getTargetPosition() - robot.duckWheel.getCurrentPosition());
                telemetry.update();
            }
        }
        telemetry.addData("duckWheel progress", "done");
    }

    public void AutoIntake() {
        int ticksToIncreaseBy;
        if (gamepad1.b) {
            // fast for 5000tick
            int Dpower = 1;

            ticksToIncreaseBy = translatePowerToTicks(Dpower);
            robot.intake.setTargetPosition(robot.intake.getCurrentPosition() + ticksToIncreaseBy);
            robot.intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.intake.setPower(Dpower);

            while (robot.intake.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", "");
                telemetry.addData("Intake progress",
                        robot.intake.getTargetPosition() - robot.intake.getCurrentPosition());
                telemetry.update();
            }
        }
        telemetry.addData("Intake progress", "done");
    }

    public void initMotor() {
        robot.duckWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // reset target positions
        robot.duckWheel.setTargetPosition(robot.frontLeft.getCurrentPosition());

        robot.duckWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int translatePowerToTicks(int speed) {
        return (int) (speed * 10000);
    }

    public int translatePowerToTicks(double speed) {
        return (int) (speed * 5000);
    }

    // private void normalize(double[] wheelSpeeds)
    // {
    // double maxMagnitude = Math.abs(wheelSpeeds[0]);

    // for (int i = 1; i < wheelSpeeds.length; i++)
    // {
    // double magnitude = Math.abs(wheelSpeeds[i]);

    // if (magnitude > maxMagnitude)
    // {
    // maxMagnitude = magnitude;
    // }
    // }

    // if (maxMagnitude > 1.0)
    // {
    // for (int i = 0; i < wheelSpeeds.length; i++)
    // {
    // wheelSpeeds[i] /= maxMagnitude;
    // }
    // }
    // } //normalize
}// end class