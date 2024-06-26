// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.subsystems;

// import com.pathplanner.lib.auto.AutoBuilder;
// import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
// import com.pathplanner.lib.util.PIDConstants;
// import com.pathplanner.lib.util.ReplanningConfig;

// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.kinematics.ChassisSpeeds;
// import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
// import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
// import edu.wpi.first.math.kinematics.SwerveModulePosition;
// import edu.wpi.first.math.kinematics.SwerveModuleState;
// import edu.wpi.first.math.util.Units;
// import edu.wpi.first.util.WPIUtilJNI;
// import edu.wpi.first.wpilibj.ADIS16470_IMU;
// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.ADIS16448_IMU.IMUAxis;
// import frc.robot.Constants.DriveConstants;
// import frc.utils.SwerveUtils;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// public class DriveSubsystem extends SubsystemBase {
//   // Create MAXSwerveModules
//   private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
//       DriveConstants.kFrontLeftDrivingCanId,
//       DriveConstants.kFrontLeftTurningCanId,
//       DriveConstants.kFrontLeftChassisAngularOffset);

//   private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
//       DriveConstants.kFrontRightDrivingCanId,
//       DriveConstants.kFrontRightTurningCanId,
//       DriveConstants.kFrontRightChassisAngularOffset);

//   private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
//       DriveConstants.kRearLeftDrivingCanId,
//       DriveConstants.kRearLeftTurningCanId,
//       DriveConstants.kBackLeftChassisAngularOffset);

//   private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
//       DriveConstants.kRearRightDrivingCanId,
//       DriveConstants.kRearRightTurningCanId,
//       DriveConstants.kBackRightChassisAngularOffset);

//   // The gyro sensor
//   public ADIS16470_IMU m_gyro = new ADIS16470_IMU();

//   // Slew rate filter variables for controlling lateral acceleration
//   private double m_currentRotation = 0.0;
//   private double m_currentTranslationDir = 0.0;
//   private double m_currentTranslationMag = 0.0;

//   private SlewRateLimiter m_magLimiter = new SlewRateLimiter(DriveConstants.kMagnitudeSlewRate);
//   private SlewRateLimiter m_rotLimiter = new SlewRateLimiter(DriveConstants.kRotationalSlewRate);
//   private double m_prevTime = WPIUtilJNI.now() * 1e-6;

//   // Odometry class for tracking robot pose
//   SwerveDriveOdometry m_odometry = new SwerveDriveOdometry(
//       DriveConstants.kDriveKinematics,
//       Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)),
//       new SwerveModulePosition[] {
//           m_frontLeft.getPosition(),
//           m_frontRight.getPosition(),
//           m_rearLeft.getPosition(),
//           m_rearRight.getPosition()
//       });

//   /** Creates a new DriveSubsystem. */
//   public DriveSubsystem() {
//   //  AutoBuilder.configureHolonomic(
//   //           this::getPose, // Robot pose supplier
//   //           this::resetOdometry,
//   //           this::getRobotRelativeSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
//   //           this::driveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
//   //           new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
//   //                   new PIDConstants(5.0, 0.0, 0.0), // Translation PID constants
//   //                   new PIDConstants(5.0, 0.0, 0.0), // Rotation PID constants
//   //                   4.5, // Max module speed, in m/s
//   //                   0.4, // Drive base radius in meters. Distance from robot center to furthest module.
//   //                   new ReplanningConfig() // Default path replanning config. See the API for the options here
//   //           ),
//   //           () -> {
//   //             // Boolean supplier that controls when the path will be mirrored for the red alliance
//   //             // This will flip the path being followed to the red side of the field.
//   //             // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

//   //             var alliance = DriverStation.getAlliance();
//   //             if (alliance.isPresent()) {
//   //               return alliance.get() == DriverStation.Alliance.Red;
//   //             }
//   //             return false;
//   //           },
//   //           this // Reference to this subsystem to set requirements
//   //   );
//   }

//   @Override
//   public void periodic() {
//     // Update the odometry in the periodic block
//     m_odometry.update(
//         Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)),
//         new SwerveModulePosition[] {
//             m_frontLeft.getPosition(),
//             m_frontRight.getPosition(),
//             m_rearLeft.getPosition(),
//             m_rearRight.getPosition()
//         });
//   }

//   /**
//    * Returns the currently-estimated pose of the robot.
//    *
//    * @return The pose.
//    */
//   public Pose2d getPose() {
//     return m_odometry.getPoseMeters();
//   }

//   /**
//    * Resets the odometry to the specified pose.
//    *
//    * @param pose The pose to which to set the odometry.
//    */
//   public void resetOdometry(Pose2d pose) {
//     m_odometry.resetPosition(
//         Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)),
//         new SwerveModulePosition[] {
//             m_frontLeft.getPosition(),
//             m_frontRight.getPosition(),
//             m_rearLeft.getPosition(),
//             m_rearRight.getPosition()
//         },
//         pose);
//   }

//   /**
//    * Method to drive the robot using joystick info.
//    *
//    * @param xSpeed        Speed of the robot in the x direction (forward).
//    * @param ySpeed        Speed of the robot in the y direction (sideways).
//    * @param rot           Angular rate of the robot.
//    * @param fieldRelative Whether the provided x and y speeds are relative to the
//    *                      field.
//    * @param rateLimit     Whether to enable rate limiting for smoother control.
//    */
//   public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative, boolean rateLimit) {
    
//     double xSpeedCommanded;
//     double ySpeedCommanded;

//     if (rateLimit) {
//       // Convert XY to polar for rate limiting
//       double inputTranslationDir = Math.atan2(ySpeed, xSpeed);
//       double inputTranslationMag = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));

//       // Calculate the direction slew rate based on an estimate of the lateral acceleration
//       double directionSlewRate;
//       if (m_currentTranslationMag != 0.0) {
//         directionSlewRate = Math.abs(DriveConstants.kDirectionSlewRate / m_currentTranslationMag);
//       } else {
//         directionSlewRate = 500.0; //some high number that means the slew rate is effectively instantaneous
//       }
      

//       double currentTime = WPIUtilJNI.now() * 1e-6;
//       double elapsedTime = currentTime - m_prevTime;
//       double angleDif = SwerveUtils.AngleDifference(inputTranslationDir, m_currentTranslationDir);
//       if (angleDif < 0.45*Math.PI) {
//         m_currentTranslationDir = SwerveUtils.StepTowardsCircular(m_currentTranslationDir, inputTranslationDir, directionSlewRate * elapsedTime);
//         m_currentTranslationMag = m_magLimiter.calculate(inputTranslationMag);
//       }
//       else if (angleDif > 0.85*Math.PI) {
//         if (m_currentTranslationMag > 1e-4) { //some small number to avoid floating-point errors with equality checking
//           // keep currentTranslationDir unchanged
//           m_currentTranslationMag = m_magLimiter.calculate(0.0);
//         }
//         else {
//           m_currentTranslationDir = SwerveUtils.WrapAngle(m_currentTranslationDir + Math.PI);
//           m_currentTranslationMag = m_magLimiter.calculate(inputTranslationMag);
//         }
//       }
//       else {
//         m_currentTranslationDir = SwerveUtils.StepTowardsCircular(m_currentTranslationDir, inputTranslationDir, directionSlewRate * elapsedTime);
//         m_currentTranslationMag = m_magLimiter.calculate(0.0);
//       }
//       m_prevTime = currentTime;
      
//       xSpeedCommanded = m_currentTranslationMag * Math.cos(m_currentTranslationDir);
//       ySpeedCommanded = m_currentTranslationMag * Math.sin(m_currentTranslationDir);
//       m_currentRotation = m_rotLimiter.calculate(rot);


//     } else {
//       xSpeedCommanded = xSpeed;
//       ySpeedCommanded = ySpeed;
//       m_currentRotation = rot;
//     }

//     // Convert the commanded speeds into the correct units for the drivetrain
//     double xSpeedDelivered = xSpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
//     double ySpeedDelivered = ySpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
//     double rotDelivered = m_currentRotation * DriveConstants.kMaxAngularSpeed;

//     var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
//         fieldRelative
//             ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered, Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)))
//             : new ChassisSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered));
//     SwerveDriveKinematics.desaturateWheelSpeeds(
//         swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
//     m_frontLeft.setDesiredState(swerveModuleStates[0]);
//     m_frontRight.setDesiredState(swerveModuleStates[1]);
//     m_rearLeft.setDesiredState(swerveModuleStates[2]);
//     m_rearRight.setDesiredState(swerveModuleStates[3]);
//   }

//   /**
//    * Sets the wheels into an X formation to prevent movement.
//    */
//   public void setX() {
//     m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
//     m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
//     m_rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
//     m_rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
//   }
//   public void driveX(){
//     drive(0.3, 0, 0, false,true);
    
//   }
//   public void drivedistance(double distancetodrive,int direction){
//     if (direction==0){ //x-direction
      
//       SmartDashboard.putNumber("Front left movement",m_frontLeft.getPosition().distanceMeters);
//       double errorx=distancetodrive-m_frontLeft.getPosition().distanceMeters;
//       SmartDashboard.putNumber("Distancex",Units.metersToInches(distancetodrive));
//       SmartDashboard.putNumber("Errorx",Units.metersToInches(errorx));
//       PIDController xController= new PIDController(1.3, 0, 0);
//       double speedx=xController.calculate(errorx);
//       drive(0, speedx, 0, false, true);
//     }
//     else if (direction==1){ //y-direction
//       // m_frontLeft.resetEncoders();
//       SmartDashboard.putNumber("Front left movement",Units.metersToInches(m_frontLeft.getPosition().distanceMeters));
//       double errory=distancetodrive+m_frontLeft.getPosition().distanceMeters;
//       SmartDashboard.putNumber("Distance",Units.metersToInches(distancetodrive));
//       SmartDashboard.putNumber("Error",Units.metersToInches(errory));
//       PIDController yController= new PIDController(1.3, 2, 0);
//       double speedy=yController.calculate(errory);
//       drive(speedy, 0, 0, false, true);
     
      
//     }
//   }
//   // public void drivemeters(double distance,MAXSwerveModule motor,int direction){
//   //   drive(0, 0, 0, false, true);
    
//   //   if (direction==0){
//   //     double errorx=distance-motor.getPosition().distanceMeters;
//   //     SmartDashboard.putNumber("Front left movement",motor.getPosition().distanceMeters);
//   //     PIDController xController= new PIDController(1, 0, 0);
//   //     double speedx=xController.calculate(errorx);
//   //     drive(0, speedx, 0, false, true);
//   //   }
//   //   if (direction==1){ //y-direction
//   //     SmartDashboard.putNumber("Front left movement",motor.getPosition().distanceMeters);
//   //     double errory=distance-motor.getPosition().distanceMeters;
//   //     PIDController yController= new PIDController(1, 0, 0);
//   //     double speedy=yController.calculate(errory);
//   //     drive(speedy, 0, 0, false, true);
//   //   }
    
//   // }
//   /**
//    * Sets the swerve ModuleStates.
//    *
//    * @param desiredStates The desired SwerveModule states.
//    */
//   public void setModuleStates(SwerveModuleState[] desiredStates) {
//     SwerveDriveKinematics.desaturateWheelSpeeds(
//         desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
//     m_frontLeft.setDesiredState(desiredStates[0]);
//     m_frontRight.setDesiredState(desiredStates[1]);
//     m_rearLeft.setDesiredState(desiredStates[2]);
//     m_rearRight.setDesiredState(desiredStates[3]);
//   }

//   /** Resets the drive encoders to currently read a position of 0. */


//   /** Zeroes the heading of the robot. */
//   public void zeroHeading() {
//     m_gyro.reset();
//   }

//   /**
//    * Returns the heading of the robot.
//    *
//    * @return the robot's heading in degrees, from -180 to 180
//    */
//   public double getHeading() {
//     return Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)).getDegrees();
//   }
//   public void getHeadings() {
//     SmartDashboard.putNumber(
//     "angle",Rotation2d.fromDegrees(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ)).getDegrees()
//     );
//   }


//   /**
//    * Returns the turn rate of the robot.
//    *
//    * @return The turn rate of the robot, in degrees per second
//    */
//   public double getTurnRate() {
//     return m_gyro.getRate(null) * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
//   }
// }
package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

// import org.littletonrobotics.junction.LoggedRobot;
// import org.littletonrobotics.junction.Logger;
// import org.photonvision.EstimatedRobotPose;
// import org.photonvision.PhotonPoseEstimator;

// import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import com.pathplanner.lib.util.ReplanningConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.DriveConstants;
import frc.utils.SwerveUtils;

public class DriveSubsystem extends SubsystemBase {
    // Create MAXSwerveModules
    private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
            DriveConstants.kFrontLeftDrivingCanId,
            DriveConstants.kFrontLeftTurningCanId,
            DriveConstants.kFrontLeftChassisAngularOffset);

    private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
            DriveConstants.kFrontRightDrivingCanId,
            DriveConstants.kFrontRightTurningCanId,
            DriveConstants.kFrontRightChassisAngularOffset);

    private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
            DriveConstants.kRearLeftDrivingCanId,
            DriveConstants.kRearLeftTurningCanId,
            DriveConstants.kBackLeftChassisAngularOffset);

    private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
            DriveConstants.kRearRightDrivingCanId,
            DriveConstants.kRearRightTurningCanId,
            DriveConstants.kBackRightChassisAngularOffset);

    private final Field2d m_field = new Field2d();

    // The gyro sensor
    public ADIS16470_IMU m_gyro = new ADIS16470_IMU();

    // Slew rate filter variables for controlling lateral acceleration
    private double m_currentRotation = 0.0;
    private double xSpeed;

    // Pose estimation class for tracking robot pose
    SwerveDrivePoseEstimator m_poseEstimator = new SwerveDrivePoseEstimator(
            DriveConstants.kDriveKinematics,
            getHeading(),
            new SwerveModulePosition[] {
                    m_frontLeft.getPosition(),
                    m_frontRight.getPosition(),
                    m_rearLeft.getPosition(),
                    m_rearRight.getPosition()
            }, 
            new Pose2d());

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem() {
        SmartDashboard.putData("Field", m_field);
        m_gyro.reset();
        AutoBuilder.configureHolonomic(
                this::getPose, // Robot pose supplier
                this::resetOdometry, // Method to reset odometry (will be called if your auto has a starting pose)
                this::getRobotRelativeSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
                this::driveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
                new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your
                                                 // Constants class
                        new PIDConstants(6.0, 0.0, 0.0), // Translation PID constants
                        new PIDConstants(4.0, 0.0, 0.0), // Rotation PID constants
                        Constants.DriveConstants.kMaxSpeedMetersPerSecond, // Max module speed, in m/s
                        0.354, // Drive base radius in meters. Distance from robot center to furthest module.
                        new ReplanningConfig() // Default path replanning config. See the API for the options here
                ),
                () -> {
                    // Boolean supplier that controls when the path will be mirrored for the red
                    // alliance
                    // This will flip the path being followed to the red side of the field.
                    // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

                    var alliance = DriverStation.getAlliance();
                    if (alliance.isPresent()) {
                        return alliance.get() == DriverStation.Alliance.Red;
                    }
                    return false;
                },
                this // Reference to this subsystem to set requirements
        );
    }

    @Override
    public void periodic() {
        // zeroHeading();
        m_field.setRobotPose(getPose());
        // Update the odometry in the periodic block
        m_poseEstimator.update(
                getHeading(),
                new SwerveModulePosition[] {
                        m_frontLeft.getPosition(),
                        m_frontRight.getPosition(),
                        m_rearLeft.getPosition(),
                        m_rearRight.getPosition()
                });
                
        
    }

    /**
     * Returns the currently-estimated pose of the robot.
     *
     * @return The pose.
     */
    public Pose2d getPose() {
        return m_poseEstimator.getEstimatedPosition();
    }

    /**
     * Resets the odometry to the specified pose.
     *
     * @param pose The pose to which to set the odometry.
     */
    public void resetOdometry(Pose2d pose) {
        m_poseEstimator.resetPosition(
                getHeading(),
                new SwerveModulePosition[] {
                        m_frontLeft.getPosition(),
                        m_frontRight.getPosition(),
                        m_rearLeft.getPosition(),
                        m_rearRight.getPosition()
                },
                pose);
    }




    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        double xSpeedCommanded;
        double ySpeedCommanded;

        // var alliance = DriverStation.getAlliance();
        double invert = 1;
        // if (alliance.isPresent()) {
        //   if ( alliance.get() == DriverStation.Alliance.Red){
        //     invert = -1;
        //   }
        // }

        xSpeedCommanded = invert*xSpeed;
        ySpeedCommanded = invert*ySpeed;
        m_currentRotation = rot;

        // Convert the commanded speeds into the correct units for the drivetrain
        double xSpeedDelivered = xSpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
        xSpeed = xSpeedDelivered;
        double ySpeedDelivered = ySpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
        double rotDelivered = m_currentRotation * DriveConstants.kMaxAngularSpeed;

        drive(new ChassisSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered), fieldRelative);
    }


    private void driveRobotRelative(ChassisSpeeds speeds) {
        drive(speeds, false);
    }

    
    private void drive(ChassisSpeeds speeds, boolean fieldRelative) {
        if (fieldRelative)
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, getPose().getRotation());
        var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
        setModuleStates(swerveModuleStates);
    }

    private ChassisSpeeds getRobotRelativeSpeeds() {
        return DriveConstants.kDriveKinematics.toChassisSpeeds(getModuleStates());
    }

    private SwerveModuleState[] getModuleStates() {
        return new SwerveModuleState[] {
                m_frontLeft.getState(),
                m_frontRight.getState(),
                m_rearLeft.getState(),
                m_rearRight.getState()
        };
    }

    //rotSetpoint is in degrees
    public void drive(double xSpeed, double ySpeed, double rotSetpoint) {
        PIDController anglePID = new PIDController(.02, 0, 0);
        double vel_multiplier = anglePID.calculate(getHeading().getRadians(), rotSetpoint);

        if (Math.abs(vel_multiplier) > .2){
            if (vel_multiplier < 0){
                vel_multiplier = -.2;
            }
            if (vel_multiplier > 0){
                vel_multiplier = .2;
            }
        }

        drive(xSpeed, ySpeed, vel_multiplier, true);
        anglePID.close();
    }
    public void drivedistance(double distancetodrive,int direction){
            if (direction==0){ //x-direction
                // m_frontLeft.resetEncoders();
              SmartDashboard.putNumber("Front left movement",m_frontLeft.getPosition().distanceMeters);
              double errorx=(distancetodrive*-1)-(m_frontLeft.getPosition().distanceMeters);
              SmartDashboard.putNumber("Distancex",Units.metersToInches(distancetodrive));
              SmartDashboard.putNumber("Errorx",Units.metersToInches(errorx));
              PIDController xController= new PIDController(1.3, 2, 0);
              double speedx=xController.calculate(errorx);
              drive(0, -speedx, 0, false);
            }
            else if (direction==1){ //y-direction
              // m_frontLeft.resetEncoders();
              SmartDashboard.putNumber("Front left movement",Units.metersToInches(m_frontLeft.getPosition().distanceMeters));
              double errory=distancetodrive+m_frontLeft.getPosition().distanceMeters;
              SmartDashboard.putNumber("Distance",Units.metersToInches(distancetodrive));
              SmartDashboard.putNumber("Error",Units.metersToInches(errory));
              PIDController yController= new PIDController(1.3, 2, 0);
              double speedy=yController.calculate(errory);
              drive(speedy, 0, 0, false);
             
              
            }
          }
    /**
     * Sets the wheels into an X formation to prevent movement.
     */
    public void setX() {
        SwerveModuleState[] states = new SwerveModuleState[4];
        states[0] = new SwerveModuleState(0, Rotation2d.fromDegrees(45));
        states[1] = new SwerveModuleState(0, Rotation2d.fromDegrees(-45));
        states[2] = new SwerveModuleState(0, Rotation2d.fromDegrees(-45));
        states[3] = new SwerveModuleState(0, Rotation2d.fromDegrees(45));
        setModuleStates(states);
    }

    /**
     * Sets the swerve ModuleStates.
     *
     * @param desiredStates The desired SwerveModule states.
     */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(
                desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
        m_frontLeft.setDesiredState(desiredStates[0]);
        m_frontRight.setDesiredState(desiredStates[1]);
        m_rearLeft.setDesiredState(desiredStates[2]);
        m_rearRight.setDesiredState(desiredStates[3]);
    }

    /** Zeroes the heading of the robot. */
    public void zeroHeading() {
        m_gyro.reset();
        System.out.println("Heading Zeroed");
    }

public void getRobotAngle() {
        m_gyro.getAngle();
        System.out.println(m_gyro.getAngle(ADIS16470_IMU.IMUAxis.kZ));
    }
    /**
     * Returns the heading of the robot.
     *
     * @return the robot's heading
     */
    private Rotation2d getHeading() {
        return Rotation2d.fromDegrees(m_gyro.getAngle() * (DriveConstants.kGyroReversed ? -1.0 : 1.0));
    }
    // private Pose2d getSomething(){
    //     return Pose2d.get
    // }
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }


}
