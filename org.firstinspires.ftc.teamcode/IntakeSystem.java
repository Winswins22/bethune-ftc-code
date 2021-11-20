package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="IntakeAuto", group="Concept")

public class IntakeSystem extends LinearOpMode {
    
    HardwarePushbot robot = new HardwarePushbot();
    
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            AutoIntake();
        }
    }
    
    public void AutoIntake() {
        int ticksToIncreaseBy;
        if (gamepad1.b) {
            // fast for 5000tick
            int Dpower = 1;
        
            ticksToIncreaseBy = translatePowerToTicks(Dpower);
            robot.Intake.setTargetPosition(robot.Intake.getCurrentPosition() + ticksToIncreaseBy);
            robot.Intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            
            robot.Intake.setPower(Dpower);
            
            while (robot.Intake.isBusy()) {
    
                // Display it for the driver.
                telemetry.addData("Motors are running to Positions", "");
                telemetry.addData("Intake progress", robot.Intake.getTargetPosition() - robot.Intake.getCurrentPosition());
                telemetry.update();
            }
        }telemetry.addData("Intake progress", "done");
    }
    
    public void initMotor() {
        robot.duckWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // reset target positions
        robot.duckWheel.setTargetPosition(robot.frontLeft.getCurrentPosition());
        
        robot.duckWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public int translatePowerToTicks(int speed){
        return (int)(speed * 5000);
    }
}