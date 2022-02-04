package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.List;


@TeleOp(name="Pushbot: Mecanum wheels", group="mode")

public class Main2 extends LinearOpMode { 

    HardwarePushbot robot           = new HardwarePushbot();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();
    
    //SENSITIVITY FORMULA: ROTATION_COEFFICIENT * (JOYSTICK ^ ROTATION_EXPONENT) + ROTATION_OFFSET;
    //Remember that JOYSTICK is always clamped from -1 to 1
    //public static final double ROTATION_EXPONENT = 1.75;
    public static final double ROTATION_EXPONENT = 2.5d;
    //public static final double ROTATION_COEFFICIENT = 0.3d; //acts as max value
    public static final double ROTATION_COEFFICIENT = 0.5d; //acts as max value
    public static final double ROTATION_COEFFICIENT_SHIFT = 0.15d; //when slow-down is pressed
    public static final double ROTATION_OFFSET = 0.12d;
    
    //Same formula for back or forward
    public static final double FB_EXPONENT = 2.5d;
    //public static final double FB_COEFFICIENT = 0.6d; //acts as max value
    public static final double FB_COEFFICIENT = 1.0d; //acts as max value
    public static final double FB_COEFFICIENT_SHIFT = 0.25d; //when slow-down is pressed
    public static final double FB_OFFSET = 0.12d;
    
    //Same formula as well
    public static final double LR_EXPONENT = 1.0d;
    //public static final double LR_COEFFICIENT = 0.5d; //acts as max value
    public static final double LR_COEFFICIENT = 1.0d; //acts as max value
    public static final double LR_COEFFICIENT_SHIFT = 0.2d; //when slow-down is pressed
    public static final double LR_OFFSET = 0.12d;
    
    //MAX VERTICAL SLIDER VELOCITY IN TICKS/S
    public static final int MAX_VERTICAL_SLIDER_VELOCITY = 400;
    //HORIZONTAL SLIDER VELOCITY
    public static final int HORIZONTAL_SLIDER_VELOCITY = 500;
    
    //SLIDER LIMITS
    public static final int HORIZONTAL_SLIDER_LIMIT = -415;
    public static final int VERTICAL_SLIDER_LIMIT = 430;
    
    public static final int VERTICAL_SLIDER_ARM_CLEAR_TOP_MIN_LIMIT = 210;
    public static final int HORIZONTAL_SLIDER_END_THRESHOLD = 35;
    public static final int BUCKET_CLEAR_CHASSIS_MIN_LIMIT = 350;
    //BUCKET SERVO LIMITS
    public static final double BUCKET_ARM_LIMIT = 1.0d;
    public static final double BUCKET_SWIVEL_OFFSET_LIMIT = 1.0d;
    public static final double BUCKET_ARM_MIN_LIMIT = 0.10d;
    public static final double BUCKET_SWIVEL_OFFSET_MIN_LIMIT = -1.0d;
    //BUCKET SPEEDS
    public static final double BUCKET_SWIVEL_SPEED = 0.010d;
    public static final double BUCKET_ARM_SPEED = 0.010d;
    
    public static final double BUCKET_SWIVEL_COLLECTING_IDLE_POS = 0.830; //initial pos for collection mode
    public static final double BUCKET_SWIVEL_INTAKE_ANGLE = 20d; //when the intake is running
    public static final double BUCKET_ARM_INTAKE_ANGLE = -62d; //when the intake is running
    
    //180 degrees: 0.9588888888
    //0.64 ticks per 180
    public static final double SERVO_ROTATION_PER_180 = 0.64;
    //(RELATIVE TO GROUND) when the arm and swivel are both facing outwards parallel to the ground.
    public static final double BUCKET_SWIVEL_LEVEL_POS = 0.74055;
    public static final double BUCKET_ARM_LEVEL_POS = 0.322777;
    
    //Starting positions for stuff
    public static final int HORIZONTAL_SLIDER_START_POS = -200;
    public static final int VERTICAL_SLIDER_START_POS = 220;
    public static final double BUCKET_ARM_START_POS = BUCKET_ARM_MIN_LIMIT;
    public static final double BUCKET_SWIVEL_START_POS = BUCKET_SWIVEL_COLLECTING_IDLE_POS;
    
    //arm, slider, and swivel angles according to level for ejection
    public static final double BUCKET_SWIVEL_ANGLE_L1 = -165;
    public static final double BUCKET_ARM_POS_L1 = 1;
    public static final int VERTICAL_SLIDER_POS_L1 = 0;
    public static final double BUCKET_SWIVEL_ANGLE_L2 = -165;
    public static final double BUCKET_ARM_POS_L2 = 1;
    public static final int VERTICAL_SLIDER_POS_L2 = 270;
    public static final double BUCKET_SWIVEL_ANGLE_L3 = -165;
    public static final double BUCKET_ARM_POS_L3 = 0.85;
    public static final int VERTICAL_SLIDER_POS_L3 = 370;
    
    
    //RELATIVE TO GROUND
    public double getBucketArmAngle(){
        return ((robot.bucketArm.getPosition() - BUCKET_ARM_LEVEL_POS) / SERVO_ROTATION_PER_180) * 180d;
    }
    //RELATIVE TO PARALLEL TO ARM
    public double getBucketSwivelAngle(){
        return ((robot.bucketSwivel.getPosition() - BUCKET_SWIVEL_LEVEL_POS) / SERVO_ROTATION_PER_180) * 180d;
    }
    public double bucketArmPosToAngle(double pos){
        return ((pos - BUCKET_ARM_LEVEL_POS) / SERVO_ROTATION_PER_180) * 180d;
    }
    public double bucketSwivelPosToAngle(double pos){
        return ((pos - BUCKET_SWIVEL_LEVEL_POS) / SERVO_ROTATION_PER_180) * 180d;
    }
    public double angleToBucketArmPos(double angle){
        return ((SERVO_ROTATION_PER_180 * angle) / 180d) + BUCKET_ARM_LEVEL_POS;
    }
    public double angleToBucketSwivelPos(double angle){
        return ((SERVO_ROTATION_PER_180 * angle) / 180d) + BUCKET_SWIVEL_LEVEL_POS;
    }
    
    enum AssistState{
        Collecting, //lower the scoop to pick a block up
        Delivering, //position the arm to drop off at the tower 
        Standby
    }
    
    //speed shift mode; slow down the bot drive
    boolean speedShift = false;
    
    @Override
    public void runOpMode() {
        
        GamepadCustom gamepad = new GamepadCustom();
        gamepad1 = gamepad;
        
        //you gotta have this here
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();
        
        //Initialize drive motors
        resetDriveMotors();
        
        //some flag booleans for controller usability
        boolean m_dpadDown = false;
        boolean m_dpadUp = false;
        boolean m_startDown = false;
        boolean m_rightShoulderDown = false;
        
        //for intake assist 
        boolean m_intake = false; 
        
        //boolean m_switchedLevelMode = false;
        //double lastTimeDpadDown = 0.0d;
        
        //for assist mode
        boolean m_lowering = false; 
        
        //initial variables for bucket 
        double bucketArmTarget = BUCKET_ARM_START_POS;
        double bucketSwivelTargetOffset = BUCKET_SWIVEL_START_POS;
        
        //for horizontal slider
        int horizontalTarget = 0;
        
        //initial variables for driving mecanum wheels
        double left;
        double right;
        double drive = 0;
        double turn = 0;
        double strafe = 0;
        
        //assist on/off
        //false = automatic arm/bucket levels, automatic slider movement from rear to front
        //true = manual control 
        boolean manual = false; 
        AssistState assistState = AssistState.Standby;
        
        //target level for automatic level selection 
        int targetLevel = 3;
        boolean usingArmLevels = false;
        
        //starting positions for sliders and bucket and arm 
        //also set some motors to run to position
        robot.bucketArm.setPosition(BUCKET_ARM_START_POS);
        robot.bucketSwivel.setPosition(BUCKET_SWIVEL_START_POS);
        
        setVerticalSliderPos(VERTICAL_SLIDER_START_POS, 400);
        /* while(robot.verticalSlider.getCurrentPosition() < 200){ //wait for vert slider to go up
            telemetry.addData("target: ", robot.verticalSlider.getTargetPosition());
            telemetry.update();
        } */
        
        setHorizontalSliderPos(HORIZONTAL_SLIDER_START_POS, 400);
        
        // Wait for the game to start (driver presses PLAY)
        //MAKE SURE THIS IS THE LAST STATEMENT (else code before loop won't execute)
        waitForStart();
        
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            drive = gamepad1.left_stick_y;
            strafe = -gamepad1.left_stick_x;
            turn  = gamepad1.right_stick_x;
            
            if(gamepad1.start){
                if(!m_startDown){
                    if(manual == true) manual = false;
                    else if (manual == false) manual = true; 
                }
                m_startDown = true;
            } else m_startDown = false;
            
            if(manual){
                if(gamepad1.left_bumper){
                    horizontalTarget = 0;
                    setHorizontalSliderPos(horizontalTarget, HORIZONTAL_SLIDER_VELOCITY);
                }
                else if(gamepad1.right_bumper){
                    horizontalTarget = HORIZONTAL_SLIDER_LIMIT;
                    setHorizontalSliderPos(horizontalTarget, HORIZONTAL_SLIDER_VELOCITY);
                } else {
                    robot.horizontalSlider.setVelocity(0);
                }
            }
            else {
                if(gamepad1.left_bumper || gamepad1.left_stick_button){
                    speedShift = true; //hold to slow the bot down
                } else speedShift = false;
                
                if(gamepad1.right_bumper){
                    if(!m_rightShoulderDown){
                        m_rightShoulderDown = true;
                        if(Math.abs(robot.horizontalSlider.getCurrentPosition()) > Math.abs((double)HORIZONTAL_SLIDER_LIMIT) / 2d
                            || assistState == AssistState.Delivering
                        ){
                            assistState = AssistState.Collecting;    
                            usingArmLevels = false;
                        } else if(Math.abs(robot.horizontalSlider.getCurrentPosition()) < Math.abs((double)HORIZONTAL_SLIDER_LIMIT) / 2d
                            || assistState == AssistState.Collecting    
                        ){
                            assistState = AssistState.Delivering;    
                        }
                        resetAssistFlags();
                        m_lowering = false; 
                    }
                } else m_rightShoulderDown = false;
                
                if(assistState == AssistState.Standby)
                    setHorizontalSliderPos(horizontalTarget, HORIZONTAL_SLIDER_VELOCITY);
            }
            telemetry.addData("collecting ", Math.abs(robot.horizontalSlider.getCurrentPosition()) > (double)HORIZONTAL_SLIDER_LIMIT / 2d);
            
            int verticalSliderVelocity = 0;
            int verticalTarget = 0;
            if(gamepad1.right_trigger > 0){ //move it down
                verticalSliderVelocity -= Math.round((double)MAX_VERTICAL_SLIDER_VELOCITY *  gamepad1.right_trigger);
                verticalTarget = VERTICAL_SLIDER_LIMIT;
                usingArmLevels = false; 
            }
            else if(gamepad1.left_trigger > 0){ //move it up
                verticalSliderVelocity += Math.round((double)MAX_VERTICAL_SLIDER_VELOCITY *  gamepad1.left_trigger);
                verticalTarget= 0;
                usingArmLevels = false; 
            }
            if(assistState == AssistState.Standby && !usingArmLevels)
                setVerticalSliderPos(verticalTarget, verticalSliderVelocity);
            
            //change the bucket arm target position (pay attention: this is not the only code manipulating them)
            if(gamepad1.dpad_left){
                if(bucketArmTarget > BUCKET_ARM_MIN_LIMIT)
                    bucketArmTarget -= BUCKET_ARM_SPEED;
                else if (bucketArmTarget < BUCKET_ARM_MIN_LIMIT)
                    bucketArmTarget = BUCKET_ARM_MIN_LIMIT;
                
                usingArmLevels = false;
            }
            if(gamepad1.dpad_right){
                if(bucketArmTarget < BUCKET_ARM_LIMIT)
                    bucketArmTarget += BUCKET_ARM_SPEED;
                else if (bucketArmTarget > BUCKET_ARM_LIMIT)
                    bucketArmTarget = BUCKET_ARM_LIMIT;
                
                usingArmLevels = false;
            }
            
            if(usingArmLevels && assistState == AssistState.Standby){
                if(getBucketArmAngle() < 90d && !canClearVerticalSlider()){
                    setVerticalSliderPos(VERTICAL_SLIDER_ARM_CLEAR_TOP_MIN_LIMIT + 20, MAX_VERTICAL_SLIDER_VELOCITY);
                } else {
                    if(targetLevel == 1){
                    bucketArmTarget = BUCKET_ARM_POS_L1;
                    //robot.bucketArm.setPosition(bucketArmTarget);
                    robot.bucketSwivel.setPosition(angleToBucketSwivelPos(BUCKET_SWIVEL_ANGLE_L1));
                    setVerticalSliderPos(VERTICAL_SLIDER_POS_L1, MAX_VERTICAL_SLIDER_VELOCITY);
                    }
                    if(targetLevel == 2){
                        bucketArmTarget = BUCKET_ARM_POS_L2;
                        //robot.bucketArm.setPosition(bucketArmTarget);
                        robot.bucketSwivel.setPosition(angleToBucketSwivelPos(BUCKET_SWIVEL_ANGLE_L2));
                        setVerticalSliderPos(VERTICAL_SLIDER_POS_L2, MAX_VERTICAL_SLIDER_VELOCITY);
                    }
                    if(targetLevel == 3){
                        bucketArmTarget = BUCKET_ARM_POS_L3;
                        //robot.bucketArm.setPosition(bucketArmTarget);
                        robot.bucketSwivel.setPosition(angleToBucketSwivelPos(BUCKET_SWIVEL_ANGLE_L3));
                        setVerticalSliderPos(VERTICAL_SLIDER_POS_L3, MAX_VERTICAL_SLIDER_VELOCITY);
                    }
                }
            }
            
            //prevent the bucket arm from hitting the vertical slider riser by limiting arc
            if(!canClearVerticalSlider()){
                if(getBucketArmAngle() < 90d && bucketArmPosToAngle(bucketArmTarget) > 55d){
                    bucketArmTarget = angleToBucketArmPos(55d);
                } else if(getBucketArmAngle() > 90d && bucketArmPosToAngle(bucketArmTarget) < 105d){
                    bucketArmTarget = angleToBucketArmPos(105d);
                }
            }
            
            //the bucket must also swivel to keep a constant level if the arm moves (related Z-angles)
            double relativeBucketPosOffset = getRelativeBucketPosOffset();
            //QOL; since the sum of the constant angle offset and target offset can be more than 1, this causes
            //the target sum to be more than 1 and cause controls to be weird
            double availableSwivelRange = 1.0d - relativeBucketPosOffset;
            double availableSwivelRangeNegative = -relativeBucketPosOffset;
            
            //you can adjust the bucket swivel in manual mode OR when the bucket is on the rear
            //side of the bot 
            //otherwise, it adjusts the target level 
            if(manual || getBucketArmAngle() < 90d){
                usingArmLevels = false; 
                if(gamepad1.dpad_down){
                if(bucketSwivelTargetOffset > BUCKET_SWIVEL_OFFSET_MIN_LIMIT)
                    bucketSwivelTargetOffset -= BUCKET_SWIVEL_SPEED;
                else if (bucketSwivelTargetOffset < BUCKET_SWIVEL_OFFSET_MIN_LIMIT)
                    bucketSwivelTargetOffset = BUCKET_SWIVEL_OFFSET_MIN_LIMIT;
                }
                if(gamepad1.dpad_up){
                    if(bucketSwivelTargetOffset < BUCKET_SWIVEL_OFFSET_LIMIT)
                        bucketSwivelTargetOffset += BUCKET_SWIVEL_SPEED;
                    else if (bucketSwivelTargetOffset > BUCKET_SWIVEL_OFFSET_LIMIT)
                        bucketSwivelTargetOffset = BUCKET_SWIVEL_OFFSET_LIMIT;
                } 
            } else {
                if(!gamepad1.dpad_down) m_dpadDown = false;
                if(!gamepad1.dpad_up) m_dpadUp = false;
                
                if(!m_dpadDown && gamepad1.dpad_down){
                    targetLevel--;
                    if(targetLevel < 1) targetLevel = 1;
                    usingArmLevels = true; 
                    m_dpadDown = true;
                }
                else if(!m_dpadUp && gamepad1.dpad_up){
                    targetLevel++;
                    if(targetLevel > 3) targetLevel = 3;
                    usingArmLevels = true; 
                    m_dpadUp = true;
                }
            }
            
            //also prevent the bucket from catching on chassis, preventing the horizontal slider from moving
            int horizontalSliderTarget = robot.horizontalSlider.getTargetPosition();
            int horizontalSliderPos = robot.horizontalSlider.getCurrentPosition();
            //if the horizontal slider's current position is not within defined threshold ticks of target
            if(Math.abs(horizontalSliderTarget) - horizontalSliderPos > HORIZONTAL_SLIDER_END_THRESHOLD){ 
                //we can skip this if vert slider is already high enough 
                if(robot.verticalSlider.getCurrentPosition() < BUCKET_CLEAR_CHASSIS_MIN_LIMIT) {
                   if(getBucketArmAngle() < 0d){
                        bucketArmTarget = angleToBucketArmPos(0d);
                    } else if(getBucketArmAngle() > 180d){
                        bucketArmTarget = angleToBucketArmPos(180d);
                    } 
                }
            }
            
            //finally set the arm position
            if(assistState == AssistState.Standby)
                robot.bucketArm.setPosition(bucketArmTarget);
            
            //refer to comments above the two if blocks; limits the target offset
            if(bucketSwivelTargetOffset > availableSwivelRange) bucketSwivelTargetOffset = availableSwivelRange;
            if(bucketSwivelTargetOffset < availableSwivelRangeNegative) bucketSwivelTargetOffset = availableSwivelRangeNegative;
            
            //Actions in assist mode happens here
            if(!manual){
                if(assistState == AssistState.Collecting){
                    //if the vertical slider isn't high enough, move it up 
                    if((!canClearVerticalSlider() || !canClearChassis()) && !m_lowering) 
                        setVerticalSliderPos(BUCKET_CLEAR_CHASSIS_MIN_LIMIT, MAX_VERTICAL_SLIDER_VELOCITY);
                    else { //once it's high enough, move it to the back (intake)
                        horizontalTarget = 0; 
                        setHorizontalSliderPos(0, HORIZONTAL_SLIDER_VELOCITY); 
                    }
                    
                    //if near the end and the previous steps are complete, put the arm in the position
                    if(Math.abs(horizontalSliderTarget) - horizontalSliderPos < HORIZONTAL_SLIDER_END_THRESHOLD)
                    {
                        m_lowering = true;
                        robot.bucketArm.setPosition(BUCKET_ARM_MIN_LIMIT);
                        bucketArmTarget = BUCKET_ARM_MIN_LIMIT;
                        robot.bucketSwivel.setPosition(BUCKET_SWIVEL_COLLECTING_IDLE_POS);
                        if(getBucketArmAngle() < 90d) //wait until the arm rotates over before it starts going down 
                            setVerticalSliderPos(0, MAX_VERTICAL_SLIDER_VELOCITY);
                        //go back to regular controls once the bucket has been lowered 
                        if(robot.verticalSlider.getCurrentPosition() < 30)
                            assistState = AssistState.Standby;
                    }
                }
                if(assistState == AssistState.Delivering){
                    //if the vertical slider isn't high enough, move it up 
                    if((!canClearVerticalSlider() || !canClearChassis()) ) 
                        setVerticalSliderPos(BUCKET_CLEAR_CHASSIS_MIN_LIMIT + 15, MAX_VERTICAL_SLIDER_VELOCITY);
                    else{
                       //once it's high enough, move it to the front and move the arms to corresponding level 
                        horizontalTarget = HORIZONTAL_SLIDER_LIMIT; //this has gotta be here to prevent it from moving back
                        setHorizontalSliderPos(HORIZONTAL_SLIDER_LIMIT, HORIZONTAL_SLIDER_VELOCITY);  
                        targetLevel = 3; 
                        usingArmLevels = true; 
                    }  
                    //if near the end and the previous steps are complete, put the arm in the position
                    //note that this uses HORIZONTAL_SLIDER_LIMIT instead of horizontalTarget otherwise
                    //it'd just prematurely end the AssistState because the horizontal target hasn't
                    //been set, is at 0, and 0 is within 30 of 0, meaning this if block would trigger before
                    //anything has actually been done...
                    if(Math.abs(HORIZONTAL_SLIDER_LIMIT) - Math.abs(horizontalSliderPos) < HORIZONTAL_SLIDER_END_THRESHOLD)
                    {
                        assistState = AssistState.Standby;
                    }
                }
            }
            
            if(gamepad1.x){
                //robot.duckMotor.setPower(1);
            } else {}
                //robot.duckMotor.setPower(0);
                
            if(gamepad1.y){
                robot.intake.setPower(1);
                robot.intake.setDirection(CRServo.Direction.FORWARD);
            } else if (gamepad1.a){
                robot.intake.setPower(1);
                robot.intake.setDirection(CRServo.Direction.REVERSE);
            } else {
                robot.intake.setPower(0);
            }
            
            //assist in intake; automatically tilt the bucket down 
            if(gamepad1.a 
                && getBucketArmAngle() < -45d 
                && 0 - horizontalSliderPos < HORIZONTAL_SLIDER_END_THRESHOLD
                )
            {
                if(!manual){
                    m_intake = true; 
                    robot.bucketArm.setPosition(BUCKET_ARM_MIN_LIMIT);
                    robot.bucketSwivel.setPosition(angleToBucketSwivelPos(BUCKET_SWIVEL_INTAKE_ANGLE));
                }   
            } else if(m_intake) {
                m_intake = false; //we want to only do this once after we release the intake button 
                robot.bucketSwivel.setPosition(angleToBucketSwivelPos(BUCKET_SWIVEL_COLLECTING_IDLE_POS));
            }
            
            //finally set the bucket position (if it isn't being overriden by the intake assist)
            if(!m_intake)
            robot.bucketSwivel.setPosition(getRelativeBucketPosOffset() + bucketSwivelTargetOffset);
            
            //resets tick values in case starting positions are messed up 
            if(gamepad1.left_stick_button && gamepad1.right_stick_button){
                resetSliderMotors();
                resetDriveMotors();
            }
            
            telemetry.addData("Bucket arm target: ", bucketArmTarget);
            telemetry.addData("Bucket swivel target: ", bucketSwivelTargetOffset);
            telemetry.addData("Bucket arm position: ", robot.bucketArm.getPosition());
            telemetry.addData("Bucket swivel position: ", robot.bucketSwivel.getPosition());
            telemetry.addData("Bucket arm angle: ", getBucketArmAngle());
            telemetry.addData("Bucket swivel angle: ", getBucketSwivelAngle());
            telemetry.addData("Bucket swivel relative target: ", relativeBucketPosOffset);
            telemetry.addData("Bucket swivel target sum: ", relativeBucketPosOffset + bucketSwivelTargetOffset);
            
            telemetry.addData("Horizontal slider target: ", robot.horizontalSlider.getTargetPosition());
            telemetry.addData("Vertical slider target: ", robot.verticalSlider.getTargetPosition());
            telemetry.addData("Horizontal slider position: ", robot.horizontalSlider.getCurrentPosition());
            telemetry.addData("Vertical slider position: ", robot.verticalSlider.getCurrentPosition());
            telemetry.addData("Manual: ", manual);
            telemetry.addData("DEBUG m_lowering", m_lowering);
            if(!manual){
                telemetry.addData("Current AssistState: ", assistState);    
                telemetry.addData("Using arm levels: ", usingArmLevels);   
                telemetry.addData("Current target level: ", targetLevel);    
            }
            
            //telemetry.addData("algebruh: ", bucketArmPosToAngle(angleToBucketArmPos(90d)));
            
            mecanumDrive_Cartesian(strafe, drive, turn);
            //drive(drive, turn);
            telemetry.update();
        }
    }
    
    double getRelativeBucketPosOffset(){
        return angleToBucketSwivelPos(-getBucketArmAngle());
    }
    
    void resetAssistFlags(){
        boolean m_lowering = false; 
    }

    public void mecanumDrive_Cartesian(double x, double y, double rotation)
    {
        
        double wheelSpeeds[] = new double[4];
        
        int positiveOrNegativeRotation = 0;
        if(rotation < 0)
            positiveOrNegativeRotation = -1;
        else if(rotation > 0)
            positiveOrNegativeRotation  = 1; 
            
        int positiveOrNegativeX = 0;
        if(x < 0)
            positiveOrNegativeX = -1;
        else if(x > 0)
            positiveOrNegativeX  = 1; 
            
        int positiveOrNegativeY = 0;
        if(y < 0)
            positiveOrNegativeY = -1;
        else if(y > 0)
            positiveOrNegativeY  = 1; 
        
        //we need these statements otherwise Math.pow() gives us NaN
        rotation = Math.abs(rotation);
        x = Math.abs(x);
        y = Math.abs(y);
        
        rotation = (double)positiveOrNegativeRotation * ((speedShift ? ROTATION_COEFFICIENT_SHIFT : ROTATION_COEFFICIENT)  * exp(rotation, ROTATION_EXPONENT) + (rotation>0 ? ROTATION_OFFSET : 0));
        
        x = (double)positiveOrNegativeX * ((speedShift ? FB_COEFFICIENT_SHIFT : FB_COEFFICIENT)  * exp(x, FB_EXPONENT) + (x>0 ? FB_OFFSET : 0));    
        
        y = (double)positiveOrNegativeY * ((speedShift ? LR_COEFFICIENT_SHIFT : LR_COEFFICIENT) * exp(y, LR_EXPONENT) + (y>0 ? LR_OFFSET : 0));    
            
    
        wheelSpeeds[0] = x + y - rotation;
        wheelSpeeds[1] = -x + y + rotation;
        wheelSpeeds[2] = -x + y - rotation;
        wheelSpeeds[3] = x + y + rotation;
    
        robot.frontLeft.setPower(-wheelSpeeds[0]);
        robot.frontRight.setPower(wheelSpeeds[1]);
        robot.backLeft.setPower(-wheelSpeeds[2]);
        robot.backRight.setPower(wheelSpeeds[3]);
    
        telemetry.addData("front Left", robot.frontLeft.getPower());
        telemetry.addData("front Right", robot.frontRight.getPower());
        telemetry.addData("back Left", robot.backLeft.getPower());
        telemetry.addData("back Right", robot.backRight.getPower());
        telemetry.update();
    
    }   //end mecanumDrive_Cartesian
    
    public void setHorizontalSliderPos(int position, int ticksVelocity){
        robot.horizontalSlider.setTargetPosition(position);
        robot.horizontalSlider.setVelocity(ticksVelocity);
        robot.horizontalSlider.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    } 
    public void setVerticalSliderPos(int position, int ticksVelocity){
        robot.verticalSlider.setTargetPosition(position);
        robot.verticalSlider.setVelocity(ticksVelocity);
        robot.verticalSlider.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    } 
    
    private void resetSliderMotors(){
        robot.horizontalSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.verticalSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    
    boolean canClearVerticalSlider(){
        return robot.verticalSlider.getCurrentPosition() > VERTICAL_SLIDER_ARM_CLEAR_TOP_MIN_LIMIT;
    }
    
    boolean canClearChassis(){
        return BUCKET_CLEAR_CHASSIS_MIN_LIMIT < robot.verticalSlider.getCurrentPosition();
    }
    
    private void resetDriveMotors(){
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
        
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // reset target positions
        robot.frontLeft.setTargetPosition(robot.frontLeft.getCurrentPosition());
        robot.frontRight.setTargetPosition(robot.frontRight.getCurrentPosition());
        robot.backLeft.setTargetPosition(robot.backLeft.getCurrentPosition());
        robot.backRight.setTargetPosition(robot.backRight.getCurrentPosition());
    }
    
    public static double exp(double n, double e){
        return Math.pow(n, e);
    }
    
    class GamepadCustom extends Gamepad{
        public GamepadCustom(){
            joystickDeadzone = 0f;
        } 
    }

}//end class
