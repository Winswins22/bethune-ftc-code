package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="DuckAuto", group="Concept")

public class DuckAuto extends LinearOpMode {
    
    HardwarePushbot robot = new HardwarePushbot();
    
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            AutoDuck();
        }
    }
    
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
                telemetry.addData("duckWheel progress", robot.duckWheel.getTargetPosition() - robot.duckWheel.getCurrentPosition());
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
                telemetry.addData("duckWheel progress", robot.duckWheel.getTargetPosition() - robot.duckWheel.getCurrentPosition());
                telemetry.update();
            }
        }telemetry.addData("duckWheel progress", "done");
    }
    
    public void initMotor() {
        robot.duckWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // reset target positions
        robot.duckWheel.setTargetPosition(robot.frontLeft.getCurrentPosition());
        
        robot.duckWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public int translatePowerToTicks(int speed){
        return (int)(speed * 10000);
    }
    
    public int translatePowerToTicks(double speed){
        return (int)(speed * 5000);
    }
}